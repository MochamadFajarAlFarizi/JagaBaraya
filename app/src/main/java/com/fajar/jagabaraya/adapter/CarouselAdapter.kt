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

class CarouselAdapter(
    private val listBerita: List<Berita>,
    private val onItemClick: (Berita) -> Unit
) : RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder>() {

    inner class CarouselViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBanner: ImageView = itemView.findViewById(R.id.ivBerita)
        val tvJudul: TextView = itemView.findViewById(R.id.tvJudulBerita)
        val tvSumber: TextView = itemView.findViewById(R.id.tvSumber)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carousel, parent, false)
        return CarouselViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val berita = listBerita[position]

        holder.tvJudul.text = berita.judul
        holder.tvSumber.text = berita.sumber

        Glide.with(holder.itemView.context)
            .load(berita.gambarUrl)
            .placeholder(R.color.gray_light)
            .into(holder.ivBanner)

        holder.itemView.setOnClickListener {
            onItemClick(berita)
        }
    }

    override fun getItemCount(): Int = listBerita.size
}
