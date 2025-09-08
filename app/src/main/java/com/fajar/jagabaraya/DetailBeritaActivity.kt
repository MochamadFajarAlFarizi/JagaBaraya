package com.fajar.jagabaraya

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.fajar.jagabaraya.model.Berita

class DetailBeritaActivity : AppCompatActivity() {

    private lateinit var ivBerita: ImageView
    private lateinit var tvJudul: TextView
    private lateinit var tvKategori1: TextView
    private lateinit var tvKategori2: TextView
    private lateinit var tvSumber: TextView
    private lateinit var btnShare: TextView
    private lateinit var tvIsi: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_berita)

        ivBerita = findViewById(R.id.ivDetailBerita)
        tvJudul = findViewById(R.id.tvJudulDetailBerita)
        tvKategori1 = findViewById(R.id.tvKategoriDetail1)
        tvKategori2 = findViewById(R.id.tvKategoriDetail2)
        tvSumber = findViewById(R.id.tvSumberDetailBerita)
        btnShare = findViewById(R.id.btnShareBerita)
        tvIsi = findViewById(R.id.tvIsi)

        val berita = intent.getSerializableExtra("berita") as? Berita

        berita?.let {
            tvJudul.text = it.judul
            tvSumber.text = "Sumber: ${it.sumber}"


            tvIsi.text= it.isi

            Glide.with(this)
                .load(it.gambarUrl)
                .placeholder(R.color.gray_light)
                .into(ivBerita)

            // kategori
            if (it.kategori.isNotEmpty()) {
                tvKategori1.text = it.kategori[0]
                tvKategori1.visibility = View.VISIBLE
            } else {
                tvKategori1.visibility = View.GONE
            }

            if (it.kategori.size > 1) {
                tvKategori2.text = it.kategori[1]
                tvKategori2.visibility = View.VISIBLE
            } else {
                tvKategori2.visibility = View.GONE
            }


            // share
            btnShare.setOnClickListener { _ ->
                val shareText = "${it.judul}\n\nSumber: ${it.sumber}"
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Bagikan Berita")
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                startActivity(Intent.createChooser(intent, "Bagikan via"))
            }
        }
    }
}
