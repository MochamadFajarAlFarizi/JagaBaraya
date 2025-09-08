package com.fajar.jagabaraya

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fajar.jagabaraya.adapter.LaporanAdapter
import com.fajar.jagabaraya.model.Laporan
import com.google.android.material.floatingactionbutton.FloatingActionButton

class LaporanFragment : Fragment(), LaporanAdapter.OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LaporanAdapter
    private val listLaporan = ArrayList<Laporan>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_laporan, container, false)

        recyclerView = view.findViewById(R.id.rvLaporan)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // isi data dummy
        isiDummyLaporan()

        // passing data + listener
        adapter = LaporanAdapter(listLaporan, this)
        recyclerView.adapter = adapter

        return view
    }

    override fun onResume() {
        super.onResume()
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
        fab.show()
    }

    private fun isiDummyLaporan() {
        listLaporan.clear()
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
                imageUrl = "https://picsum.photos/600/400?random=2",
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
                imageUrl = "https://picsum.photos/600/400?random=2",
                likeCount = 8,
                likedBy = emptyList()
            )
        )
    }

    // implement listener dari adapter
    override fun onItemClicked(laporan: Laporan) {
        // buka detail laporan
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
        adapter.updateLaporan(updated, position)
    }

    override fun onCommentClicked(laporan: Laporan) {
        val intent = Intent(requireContext(), DetailLaporanActivity::class.java).apply {
            putExtra("laporan", laporan)
        }
        startActivity(intent)
    }


    override fun onShareClicked(laporan: Laporan) {
        // share laporan via intent
        val shareText = "Laporan: ${laporan.judul}\n${laporan.isi}\nLokasi: ${laporan.lokasi}"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Bagikan Laporan")
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, "Bagikan via"))
    }
}
