package com.fajar.jagabaraya.model


data class Komentar(
    val nama: String,
    val isi: String,
    val waktu: String,
    val fotoProfilUrl: String? = null
)
