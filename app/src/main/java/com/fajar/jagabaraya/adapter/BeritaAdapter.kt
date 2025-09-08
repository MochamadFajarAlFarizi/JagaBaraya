package com.fajar.jagabaraya.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fajar.jagabaraya.R
import com.fajar.jagabaraya.model.Berita

class BeritaAdapter(
    private val listBerita: List<Berita>,
    private val onItemClick: (Berita) -> Unit
) : RecyclerView.Adapter<BeritaAdapter.BeritaViewHolder>() {

    // ViewHolder
    class BeritaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBerita: ImageView = itemView.findViewById(R.id.ivBerita)
        val tvJudul: TextView = itemView.findViewById(R.id.tvJudulBerita)
        val tvKategori1: TextView = itemView.findViewById(R.id.tvKategori1)
        val tvKategori2: TextView = itemView.findViewById(R.id.tvKategori2)
        val tvSumber: TextView = itemView.findViewById(R.id.tvSumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeritaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_berita, parent, false)
        return BeritaViewHolder(view)
    }

    override fun onBindViewHolder(holder: BeritaViewHolder, position: Int) {
        val berita = listBerita[position]

        // Judul
        holder.tvJudul.text = berita.judul


        // Gambar
        Glide.with(holder.itemView.context)
            .load(berita.gambarUrl)
            .placeholder(R.color.gray_light)
            .into(holder.ivBerita)

        // Sumber
        holder.tvSumber.text = berita.sumber

        // Kategori
        if (berita.kategori.isNotEmpty()) {
            holder.tvKategori1.text = berita.kategori[0]
            holder.tvKategori1.visibility = View.VISIBLE
        } else {
            holder.tvKategori1.visibility = View.GONE
        }

        if (berita.kategori.size > 1) {
            holder.tvKategori2.text = berita.kategori[1]
            holder.tvKategori2.visibility = View.VISIBLE
        } else {
            holder.tvKategori2.visibility = View.GONE
        }

        // Klik item → kirim berita ke listener
        holder.itemView.setOnClickListener {
            onItemClick(berita)
        }
    }

    override fun getItemCount(): Int = listBerita.size
}
