package com.fajar.jagabaraya.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fajar.jagabaraya.R
import com.fajar.jagabaraya.model.Laporan

class LaporanAdapter(
    private val listLaporan: MutableList<Laporan>,
    private val listener: OnItemClickListener
) : RecyclerView.Adapter<LaporanAdapter.LaporanViewHolder>() {

    private val currentUserId = "dummyUser"

    interface OnItemClickListener {
        fun onItemClicked(laporan: Laporan)
        fun onLikeClicked(laporan: Laporan, position: Int)
        fun onCommentClicked(laporan: Laporan)
        fun onShareClicked(laporan: Laporan)
    }

    inner class LaporanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNama)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvTanggal)
        val tvJudul: TextView = itemView.findViewById(R.id.tvJudul)
        val tvIsi: TextView = itemView.findViewById(R.id.tvIsi)
        val tvLokasi: TextView = itemView.findViewById(R.id.tvLokasi)
        val ivLaporan: ImageView = itemView.findViewById(R.id.ivLaporan)

        val btnLike: LinearLayout = itemView.findViewById(R.id.btnLike)
        val ivLike: ImageView = itemView.findViewById(R.id.ivLike)
        val tvLikeCount: TextView = itemView.findViewById(R.id.tvLikeCount)

        val btnComment: LinearLayout = itemView.findViewById(R.id.btnComment)
        val btnShare: LinearLayout = itemView.findViewById(R.id.btnShare)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaporanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_laporan, parent, false)
        return LaporanViewHolder(view)
    }

    override fun onBindViewHolder(holder: LaporanViewHolder, position: Int) {
        val laporan = listLaporan[position]

        holder.tvNama.text = "Anonymous" // sementara statis
        holder.tvTanggal.text = laporan.tanggal
        holder.tvJudul.text = laporan.judul
        holder.tvIsi.text = laporan.isi
        holder.tvLokasi.text = laporan.lokasi

        // Gambar laporan
        if (!laporan.imageUrl.isNullOrEmpty()) {
            Glide.with(holder.itemView.context)
                .load(laporan.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_image_placeholder)
                .into(holder.ivLaporan)
        } else {
            holder.ivLaporan.setImageResource(R.drawable.ic_image_placeholder)
        }

        // Hitung like
        val isLiked = laporan.likedBy.contains(currentUserId)
        updateLikeUI(holder, laporan.likeCount, isLiked)

        // Aksi klik
        holder.itemView.setOnClickListener {
            listener.onItemClicked(laporan)
        }

        holder.btnLike.setOnClickListener {
            listener.onLikeClicked(laporan, position)
        }

        holder.btnComment.setOnClickListener {
            listener.onCommentClicked(laporan)
        }

        holder.btnShare.setOnClickListener {
            listener.onShareClicked(laporan)
        }
    }

    override fun getItemCount(): Int = listLaporan.size

    private fun updateLikeUI(holder: LaporanViewHolder, likeCount: Long, isLiked: Boolean) {
        holder.tvLikeCount.text = "$likeCount Like"

        if (isLiked) {
            holder.ivLike.setImageResource(R.drawable.ic_like_fill)
            holder.ivLike.setColorFilter(
                ContextCompat.getColor(holder.itemView.context, R.color.black)
            )
        } else {
            holder.ivLike.setImageResource(R.drawable.ic_like_outline)
            holder.ivLike.setColorFilter(
                ContextCompat.getColor(holder.itemView.context, R.color.black)
            )
        }
    }

    fun updateLaporan(updatedLaporan: Laporan, position: Int) {
        listLaporan[position] = updatedLaporan
        notifyItemChanged(position)
    }
}
