package com.fajar.jagabaraya

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.fajar.jagabaraya.adapter.CarouselAdapter
import com.fajar.jagabaraya.adapter.LaporanAdapter
import com.fajar.jagabaraya.model.Berita
import com.fajar.jagabaraya.model.Laporan
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private lateinit var recyclerViewLaporan: RecyclerView
    private lateinit var laporanAdapter: LaporanAdapter
    private val listLaporan = mutableListOf<Laporan>()

    private lateinit var bannerViewPager: ViewPager2
    private lateinit var beritaAdapter: CarouselAdapter
    private val listBerita = mutableListOf<Berita>()

    private val handler = Handler(Looper.getMainLooper())

    private val slideRunnable = object : Runnable {
        override fun run() {
            if (beritaAdapter.itemCount > 0) {
                val nextItem = (bannerViewPager.currentItem + 1) % beritaAdapter.itemCount
                bannerViewPager.setCurrentItem(nextItem, true)
                handler.postDelayed(this, 3000) // ganti tiap 3 detik
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // === RecyclerView untuk laporan ===
        recyclerViewLaporan = view.findViewById(R.id.recyclerViewLaporan)
        recyclerViewLaporan.layoutManager = LinearLayoutManager(requireContext())

        isiDummyLaporan()

        laporanAdapter = LaporanAdapter(listLaporan, object : LaporanAdapter.OnItemClickListener {
            override fun onItemClicked(laporan: Laporan) {
                val intent = Intent(requireContext(), DetailLaporanActivity::class.java)
                intent.putExtra("laporan", laporan)
                startActivity(intent)
            }

            override fun onLikeClicked(laporan: Laporan, position: Int) {
                val isLiked = laporan.likedBy.contains("dummyUser")
                val updated = laporan.copy(
                    likeCount = if (isLiked) laporan.likeCount - 1 else laporan.likeCount + 1,
                    likedBy = if (isLiked) emptyList() else listOf("dummyUser")
                )
                laporanAdapter.updateLaporan(updated, position)
            }

            override fun onCommentClicked(laporan: Laporan) {
                val intent = Intent(requireContext(), DetailLaporanActivity::class.java).apply {
                    putExtra("laporan", laporan)
                }
                startActivity(intent)
            }

            override fun onShareClicked(laporan: Laporan) {
                val shareText = "Laporan: ${laporan.judul}\n${laporan.isi}\nLokasi: ${laporan.lokasi}"
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Bagikan Laporan")
                    putExtra(Intent.EXTRA_TEXT, shareText)
                }
                startActivity(Intent.createChooser(intent, "Bagikan via"))
            }
        })

        recyclerViewLaporan.adapter = laporanAdapter

        // === ViewPager2 untuk berita ===
        bannerViewPager = view.findViewById(R.id.bannerViewPager)
        isiDummyBerita()

        beritaAdapter = CarouselAdapter(listBerita) { berita ->
            val intent = Intent(requireContext(), DetailBeritaActivity::class.java)
            intent.putExtra("berita", berita)
            startActivity(intent)
        }

        bannerViewPager.adapter = beritaAdapter
        bannerViewPager.offscreenPageLimit = 3

        // set ke slide pertama (tanpa animasi)
        bannerViewPager.setCurrentItem(0, false)

        // mulai auto slide
        handler.postDelayed(slideRunnable, 3000)

        return view
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(slideRunnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(slideRunnable, 3000)
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
        fab.show()
    }

    private fun isiDummyLaporan() {
        listLaporan.add(
            Laporan(
                id = "1",
                judul = "Jalan Rusak Parah",
                isi = "Jalan utama di desa mengalami kerusakan parah hingga sulit dilalui.",
                lokasi = "Jl. Raya Cikoneng",
                tanggal = "7 Sep 2025",
                imageUrl = "https://picsum.photos/600/400?random=2",
                likeCount = 5,
                likedBy = emptyList()
            )
        )
        listLaporan.add(
            Laporan(
                id = "2",
                judul = "Sampah Menumpuk",
                isi = "Sampah menumpuk di pinggir jalan karena truk sampah jarang datang.",
                lokasi = "Jl. Sukasari",
                tanggal = "6 Sep 2025",
                imageUrl = "https://picsum.photos/600/400?random=3",
                likeCount = 2,
                likedBy = emptyList()
            )
        )
        listLaporan.add(
            Laporan(
                id = "3",
                judul = "Lampu Jalan Mati",
                isi = "Beberapa lampu jalan di perempatan padam sehingga rawan kecelakaan.",
                lokasi = "Jl. Raya Pameungpeuk",
                tanggal = "5 Sep 2025",
                imageUrl = "https://picsum.photos/600/400?random=4",
                likeCount = 8,
                likedBy = emptyList()
            )
        )
    }

    private fun isiDummyBerita() {
        listBerita.add(
            Berita(
                id = "1",
                judul = "Operasi Anti Pungli",
                isi = "ini isi dari card",
                kategori = listOf("Premanisme", "Pungli"),
                gambarUrl = "https://picsum.photos/600/300?random=1",
                sumber = "Polsek Pameungpeuk"
            )
        )
        listBerita.add(
            Berita(
                id = "2",
                judul = "Gotong Royong Perbaikan Jalan",
                isi = "ini isi dari card",
                kategori = listOf("Infrastruktur"),
                gambarUrl = "https://picsum.photos/600/300?random=2",
                sumber = "Desa Sukamaju"
            )
        )
        listBerita.add(
            Berita(
                id = "3",
                judul = "Sosialisasi Anti Narkoba",
                isi = "ini isi dari card",
                kategori = listOf("Kesehatan", "Edukasi"),
                gambarUrl = "https://picsum.photos/600/300?random=3",
                sumber = "BNN Kabupaten"
            )
        )
    }
}
