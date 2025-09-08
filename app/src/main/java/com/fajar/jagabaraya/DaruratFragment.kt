package com.fajar.jagabaraya

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class DaruratFragment : Fragment() {

    private val nomorDarurat = "081210876438"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_darurat, container, false)

        val btnPolisi: Button = view.findViewById(R.id.btn_callCenter)
        val btnWhatsapp: Button = view.findViewById(R.id.btn_Whatsapp)
        val btnPMI: Button = view.findViewById(R.id.btn_PMI)

        // Tombol Call Center
        btnPolisi.setOnClickListener {
            showConfirmDialog("110") {
                callNumber(nomorDarurat)
            }
        }

        // Tombol WhatsApp
        btnWhatsapp.setOnClickListener {
            showConfirmDialog("WhatsApp") {
                sendWhatsapp(nomorDarurat, "Halo, saya membutuhkan bantuan.")
            }
        }

        // Tombol Ambulance / PMI
        btnPMI.setOnClickListener {
            showConfirmDialog("Ambulance") {
                callNumber(nomorDarurat)
            }
        }

        return view
    }

    // Dialog konfirmasi umum
    private fun showConfirmDialog(jenis: String, onYes: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi")
            .setMessage("Apakah Anda yakin ingin $jenis?")
            .setPositiveButton("Ya") { _, _ ->
                onYes()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    // Fungsi menelpon
    private fun callNumber(number: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
    }

    // Fungsi kirim WhatsApp
    private fun sendWhatsapp(number: String, message: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            // Nomor harus pakai kode negara, contoh +62 tanpa tanda +
            val url = "https://api.whatsapp.com/send?phone=$number&text=${Uri.encode(message)}"
            intent.data = Uri.parse(url)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            AlertDialog.Builder(requireContext())
                .setTitle("Error")
                .setMessage("WhatsApp tidak terpasang di perangkat ini.")
                .setPositiveButton("OK", null)
                .show()
        }
    }
}
