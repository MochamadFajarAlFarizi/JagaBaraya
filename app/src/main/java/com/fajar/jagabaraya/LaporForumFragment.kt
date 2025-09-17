package com.fajar.jagabaraya

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

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
    private var progressDialog: Dialog? = null

    private val REQUEST_CAMERA = 1001
    private val REQUEST_GALLERY = 1002

    private val cloudName = "dzofhsgzp"
    private val uploadPreset = "infracare"

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


        ivPreview = ImageView(requireContext())
        ivPreview.layoutParams =
            FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        ivPreview.scaleType = ImageView.ScaleType.CENTER_CROP
        ivPreview.visibility = View.GONE
        layoutUpload.addView(ivPreview)


        layoutUpload.setOnClickListener {
            showImagePickerDialog()
        }


        btnLaporkan.setOnClickListener {
            if (validateForm()) {
                val isAnonymous = switchAnonymous.isChecked
                val namaUser = if (isAnonymous) "Anonymous" else getUserName()

                showLoadingDialog()

                if (imageUri != null) {
                    uploadImageToCloudinary(imageUri!!) { url ->
                        if (url != null) {
                            requireActivity().runOnUiThread {
                                saveReportToFirestore(namaUser, url)
                            }
                        } else {
                            requireActivity().runOnUiThread {
                                hideLoadingDialog()
                                Toast.makeText(requireContext(), "Upload gambar gagal", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    saveReportToFirestore(namaUser, "")
                }
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getCurrentLocation()

        return view
    }


    private fun uploadImageToCloudinary(uri: Uri, onResult: (String?) -> Unit) {
        val url = "https://api.cloudinary.com/v1_1/$cloudName/image/upload"

        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val requestBody = inputStream?.readBytes()?.toRequestBody("image/*".toMediaTypeOrNull())

        val multipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "upload.jpg", requestBody!!)
            .addFormDataPart("upload_preset", uploadPreset)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(multipartBody)
            .build()

        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                onResult(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
                val imageUrl = JSONObject(json!!).getString("secure_url")
                onResult(imageUrl)
            }
        })
    }


    private fun saveReportToFirestore(userName: String, imageUrl: String) {
        val db = FirebaseFirestore.getInstance()
        val tanggalString = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            .format(System.currentTimeMillis())

        val laporan = hashMapOf(
            "createdBy" to userName,
            "judul" to etJudul.text.toString(),
            "isi" to etDeskripsi.text.toString(),
            "kategori" to "forum",
            "lokasi" to etLokasi.text.toString(),
            "imageUrl" to imageUrl,
            "likeCount" to 0,
            "likedBy" to listOf<String>(),
            "status" to "Diterima",
            "tanggal" to tanggalString
        )

        db.collection("laporan")
            .add(laporan)
            .addOnSuccessListener {
                hideLoadingDialog()
                Toast.makeText(requireContext(), "Laporan berhasil disimpan", Toast.LENGTH_SHORT).show()
                val bottomNav = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
                bottomNav.selectedItemId = R.id.home
            }
            .addOnFailureListener { e ->
                hideLoadingDialog()
                Toast.makeText(requireContext(), "Gagal simpan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
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
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_CAMERA
            )
        } else {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_CAMERA)
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

    private fun getUserName(): String {
        return "Fajar Al Farizi"
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
                        val address = addresses[0].getAddressLine(0)
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


    private fun showLoadingDialog() {
        progressDialog = Dialog(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_progress, null)
        progressDialog?.setContentView(view)
        progressDialog?.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        progressDialog?.setCancelable(false)
        progressDialog?.show()
    }

    private fun hideLoadingDialog() {
        progressDialog?.dismiss()
    }
}
