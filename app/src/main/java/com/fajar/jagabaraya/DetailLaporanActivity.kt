package com.fajar.jagabaraya

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fajar.jagabaraya.adapter.KomentarAdapter
import com.fajar.jagabaraya.model.Komentar
import com.fajar.jagabaraya.model.Laporan
import java.text.SimpleDateFormat
import java.util.*

class DetailLaporanActivity : AppCompatActivity() {

    private lateinit var tvNama: TextView
    private lateinit var tvTanggal: TextView
    private lateinit var tvJudul: TextView
    private lateinit var tvIsi: TextView
    private lateinit var tvLokasi: TextView
    private lateinit var ivDetail: ImageView
    private lateinit var rvKomentar: RecyclerView
    private lateinit var etKomentar: EditText
    private lateinit var btnKirim: TextView
    private lateinit var btnLike: LinearLayout
    private lateinit var btnShare: LinearLayout
    private lateinit var tvLikeCount: TextView
    private lateinit var ivLike: ImageView

    private val listKomentar = ArrayList<Komentar>()
    private lateinit var adapterKomentar: KomentarAdapter

    private var likeCount = 35
    private var isLiked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_laporan)

        val laporan = intent.getParcelableExtra<Laporan>("laporan")

        tvNama = findViewById(R.id.tvNamaDetail)
        tvTanggal = findViewById(R.id.tvTanggalDetail)
        tvJudul = findViewById(R.id.tvJudulDetail)
        tvIsi = findViewById(R.id.tvIsiDetail)
        tvLokasi = findViewById(R.id.tvLokasiDetail)
        ivDetail = findViewById(R.id.ivDetail)
        rvKomentar = findViewById(R.id.rvKomentar)
        etKomentar = findViewById(R.id.etKomentar)
        btnKirim = findViewById(R.id.btnKirimKomentar)
        btnLike = findViewById(R.id.btnLike)
        btnShare = findViewById(R.id.btnShare)
        tvLikeCount = findViewById(R.id.tvLikeCount)
        ivLike = findViewById(R.id.ivLike)

        laporan?.let {
            tvNama.text = "Anonymous"
            tvTanggal.text = it.tanggal
            tvJudul.text = it.judul
            tvIsi.text = it.isi
            tvLokasi.text = it.lokasi

            if (!it.imageUrl.isNullOrEmpty()) {
                Glide.with(this).load(it.imageUrl).into(ivDetail)
            }
        }

        // setup komentar
        adapterKomentar = KomentarAdapter(listKomentar)
        rvKomentar.layoutManager = LinearLayoutManager(this)
        rvKomentar.adapter = adapterKomentar

        btnKirim.setOnClickListener {
            val isiKomentar = etKomentar.text.toString().trim()
            if (isiKomentar.isNotEmpty()) {
                val waktu = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                val komentarBaru = Komentar(
                    nama = "Anonymous",
                    isi = isiKomentar,
                    waktu = waktu,
                    fotoProfilUrl = null
                )
                listKomentar.add(komentarBaru)
                adapterKomentar.notifyItemInserted(listKomentar.size - 1)
                etKomentar.text.clear()
                rvKomentar.scrollToPosition(listKomentar.size - 1)
            }
        }

        // setup like
        tvLikeCount.text = "$likeCount Like"
        btnLike.setOnClickListener {
            if (isLiked) {
                likeCount--
                ivLike.setImageResource(R.drawable.ic_like_outline)
            } else {
                likeCount++
                ivLike.setImageResource(R.drawable.ic_like_fill)
            }
            isLiked = !isLiked
            tvLikeCount.text = "$likeCount Like"
        }

        // setup share
        btnShare.setOnClickListener {
            val shareText = "Laporan: ${tvJudul.text}\n${tvIsi.text}\nLokasi: ${tvLokasi.text}"
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Bagikan Laporan")
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(intent, "Bagikan via"))
        }
    }
}
