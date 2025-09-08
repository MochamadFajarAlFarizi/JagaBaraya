package com.fajar.jagabaraya


import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import java.io.IOException
import java.util.Locale

class LaporForumFragment : Fragment() {

    private lateinit var etJudul: EditText
    private lateinit var etDeskripsi: EditText
    private lateinit var etLokasi: EditText
    private lateinit var layoutUpload: FrameLayout
    private lateinit var ivPreview: ImageView
    private lateinit var btnLaporkan: Button
    private lateinit var switchAnonymous: SwitchMaterial
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var imageUri: Uri? = null

    private val REQUEST_CAMERA = 1001
    private val REQUEST_GALLERY = 1002

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_lapor_forum, container, false)

        etJudul = view.findViewById(R.id.etJudul)
        etDeskripsi = view.findViewById(R.id.etDeskripsi)
        etLokasi = view.findViewById(R.id.etLokasi)
        layoutUpload = view.findViewById(R.id.layoutUpload)
        btnLaporkan = view.findViewById(R.id.btnLaporkan)
        switchAnonymous = view.findViewById(R.id.switchAnonymous)

        // Preview image di upload box
        ivPreview = ImageView(requireContext())
        ivPreview.layoutParams =
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        ivPreview.scaleType = ImageView.ScaleType.CENTER_CROP
        ivPreview.visibility = View.GONE
        layoutUpload.addView(ivPreview)

        // Klik Upload
        layoutUpload.setOnClickListener {
            showImagePickerDialog()
        }

        // Klik Laporkan
        btnLaporkan.setOnClickListener {
            if (validateForm()) {
                val isAnonymous = switchAnonymous.isChecked
                val namaUser = if (isAnonymous) "Anonymous" else getUserName()

                Toast.makeText(requireContext(), "Lapor sebagai: $namaUser", Toast.LENGTH_SHORT).show()
                showCustomProgress()
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        getCurrentLocation() // panggil otomatis saat buka form


        return view
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Kamera", "Galeri")
        AlertDialog.Builder(requireContext())
            .setTitle("Pilih Sumber Gambar")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }.show()
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Minta izin kamera
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_CAMERA
            )
        } else {
            // Jika sudah diizinkan, buka kamera
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CAMERA)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 2001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "Izin lokasi diperlukan untuk mengisi otomatis", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == REQUEST_CAMERA) {
            // kode kamera tetap sama
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_CAMERA)
            } else {
                Toast.makeText(requireContext(), "Izin kamera diperlukan", Toast.LENGTH_SHORT).show()
            }
        }
    }



    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CAMERA -> {
                    val bitmap = data?.extras?.get("data") as? android.graphics.Bitmap
                    ivPreview.setImageBitmap(bitmap)
                    ivPreview.visibility = View.VISIBLE
                }
                REQUEST_GALLERY -> {
                    imageUri = data?.data
                    try {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, imageUri)
                        ivPreview.setImageBitmap(bitmap)
                        ivPreview.visibility = View.VISIBLE
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    private fun validateForm(): Boolean {
        if (etJudul.text.isEmpty() || etDeskripsi.text.isEmpty() || etLokasi.text.isEmpty()) {
            Toast.makeText(requireContext(), "Lengkapi semua data!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun showCustomProgress() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_progress, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.show()

        // Simulasi kirim data 3 detik
        dialogView.postDelayed({
            dialog.dismiss()

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, LaporanFragment())
                .addToBackStack(null)
                .commit()

            val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            bottomNav.selectedItemId = R.id.laporanku
        }, 3000)
    }

    private fun getUserName(): String {
        return "Fajar Al Farizi" // misalnya ambil dari user login
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                2001
            )
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    if (addresses != null && addresses.isNotEmpty()) {
                        val address = addresses[0].getAddressLine(0) // alamat lengkap
                        etLokasi.setText(address)
                    } else {
                        etLokasi.setText("${location.latitude}, ${location.longitude}")
                    }
                } else {
                    Toast.makeText(requireContext(), "Lokasi tidak tersedia", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}
