package com.fajar.jagabaraya

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment

class EditProfilFragment : Fragment() {

    private lateinit var ivFotoProfil: ImageView
    private lateinit var etNamaLengkap: EditText
    private lateinit var etNoTelepon: EditText
    private lateinit var btnSimpan: Button

    companion object {
        private const val REQ_CAMERA = 100
        private const val REQ_GALLERY = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_profil, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivFotoProfil = view.findViewById(R.id.ivFotoProfil)
        etNamaLengkap = view.findViewById(R.id.etNamaLengkap)
        etNoTelepon = view.findViewById(R.id.etNoTelepon)
        btnSimpan = view.findViewById(R.id.btnSimpan)

        // Contoh data awal
        etNamaLengkap.setText("Fajar Ganteng")
        etNoTelepon.setText("08123456789")

        // Klik foto profil -> pilih sumber foto
        ivFotoProfil.setOnClickListener {
            showImagePickerDialog()
        }

        // Tombol simpan
        btnSimpan.setOnClickListener {
            val namaBaru = etNamaLengkap.text.toString()

            if (namaBaru.isBlank()) {
                Toast.makeText(requireContext(), "Nama lengkap tidak boleh kosong", Toast.LENGTH_SHORT).show()
            } else {
                // TODO: Simpan data ke database / API
                Toast.makeText(requireContext(), "Profil berhasil disimpan", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Kamera", "Galeri")
        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Foto Profil")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQ_CAMERA)
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQ_GALLERY)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_CAMERA -> {
                    val bitmap = data?.extras?.get("data") as? Bitmap
                    bitmap?.let {
                        ivFotoProfil.setImageBitmap(it)
                    }
                }
                REQ_GALLERY -> {
                    val imageUri: Uri? = data?.data
                    imageUri?.let {
                        ivFotoProfil.setImageURI(it)
                    }
                }
            }
        }
    }
}
