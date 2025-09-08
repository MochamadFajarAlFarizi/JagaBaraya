package com.fajar.jagabaraya.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Laporan(
    val id: String = "",
    val judul: String = "",
    val isi: String = "",
    val lokasi: String = "",
    val tanggal: String = "",
    val imageUrl: String? = null,
    val likeCount: Long = 0,
    val likedBy: List<String> = emptyList()
) : Parcelable


