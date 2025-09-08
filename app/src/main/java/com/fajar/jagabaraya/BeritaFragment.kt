package com.fajar.jagabaraya


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fajar.jagabaraya.adapter.BeritaAdapter
import com.fajar.jagabaraya.model.Berita
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BeritaFragment : Fragment() {

    private lateinit var rvBerita: RecyclerView
    private lateinit var beritaAdapter: BeritaAdapter
    private val listBerita = mutableListOf<Berita>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_berita, container, false)

        rvBerita = view.findViewById(R.id.rvBerita)

        // Inisialisasi adapter
        beritaAdapter = BeritaAdapter(listBerita) { berita ->
            val intent = Intent(requireContext(), DetailBeritaActivity::class.java)
            intent.putExtra("berita", berita) // kirim object berita
            startActivity(intent)
        }


        rvBerita.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = beritaAdapter
        }

        // Muat data dummy sementara
        loadDummyBerita()

        return view
    }

    override fun onResume() {
        super.onResume()
        val fab = requireActivity().findViewById<FloatingActionButton>(R.id.fab)
        fab.show()
    }

    private fun loadDummyBerita() {
        val dataDummy = listOf(
            Berita(
                id = "1",
                judul = "Petani di Bandung Panen Raya",
                isi = "ini isi berita asli bingung mau ngeik apa jadi asal aja yangpenting ada isinya",
                kategori = listOf("Pertanian", "Ekonomi"),
                gambarUrl = "https://picsum.photos/600/400?random=1",
                sumber = "Detik News"
            ),
            Berita(
                id = "2",
                judul = "Teknologi IoT Dukung Pertanian Cerdas",
                isi = "ini isi berita asli bingung mau ngeik apa jadi asal aja yangpenting ada isinya",
                kategori = listOf("Teknologi"),
                gambarUrl = "https://picsum.photos/600/400?random=2",
                sumber = "Kompas"
            )
        )

        listBerita.clear()
        listBerita.addAll(dataDummy)
        beritaAdapter.notifyDataSetChanged()
    }
}

