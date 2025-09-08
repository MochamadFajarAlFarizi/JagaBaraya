package com.fajar.jagabaraya.model

import java.io.Serializable

data class Berita(
    val id: String = "",
    val judul: String = "",
    val isi: String = "",
    val kategori: List<String> = listOf(),
    val gambarUrl: String = "",
    val sumber: String = ""
) : Serializable
