package com.fajar.jagabaraya.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fajar.jagabaraya.R
import com.fajar.jagabaraya.model.Komentar

class KomentarAdapter(private val listKomentar: List<Komentar>) :
    RecyclerView.Adapter<KomentarAdapter.KomentarViewHolder>() {

    inner class KomentarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivFotoProfil: ImageView = itemView.findViewById(R.id.ivFotoProfilKomentar)
        val tvNama: TextView = itemView.findViewById(R.id.tvNamaKomentar)
        val tvWaktu: TextView = itemView.findViewById(R.id.tvWaktuKomentar)
        val tvIsi: TextView = itemView.findViewById(R.id.tvKomentarItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KomentarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_komentar, parent, false)
        return KomentarViewHolder(view)
    }

    override fun onBindViewHolder(holder: KomentarViewHolder, position: Int) {
        val komentar = listKomentar[position]

        holder.tvNama.text = komentar.nama
        holder.tvWaktu.text = komentar.waktu
        holder.tvIsi.text = komentar.isi

        if (!komentar.fotoProfilUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(komentar.fotoProfilUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_account_circle)
                .into(holder.ivFotoProfil)
        } else {
            holder.ivFotoProfil.setImageResource(R.drawable.ic_account_circle)
        }
    }

    override fun getItemCount(): Int = listKomentar.size
}
