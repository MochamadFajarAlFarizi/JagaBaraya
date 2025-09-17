package com.fajar.jagabaraya

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

class RegisterActivity : AppCompatActivity() {

    private lateinit var layoutPhone: TextInputLayout
    private lateinit var layoutPassword: TextInputLayout
    private lateinit var layoutConfirmPassword: TextInputLayout

    private lateinit var inputName: TextInputEditText
    private lateinit var inputPhone: TextInputEditText
    private lateinit var inputPassword: TextInputEditText
    private lateinit var inputConfirmPassword: TextInputEditText
    private lateinit var cbTerms: CheckBox
    private lateinit var btnRegister: Button
    private lateinit var tvLogin: TextView

    private val db = FirebaseFirestore.getInstance()

    private var progressDialog: AlertDialog? = null
    private var tvMessage: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        layoutPhone = findViewById(R.id.layout_phone)
        layoutPassword = findViewById(R.id.layout_password)
        layoutConfirmPassword = findViewById(R.id.layout_confirm_password)

        inputName = findViewById(R.id.input_name)
        inputPhone = findViewById(R.id.input_phone)
        inputPassword = findViewById(R.id.input_password)
        inputConfirmPassword = findViewById(R.id.input_confirm_password)
        cbTerms = findViewById(R.id.cb_terms)
        btnRegister = findViewById(R.id.btn_register)
        tvLogin = findViewById(R.id.tv_login)


        inputPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isValidPhone(s.toString())) {
                    layoutPhone.error = "Nomor telepon minimal 9 digit angka"
                } else {
                    layoutPhone.error = null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        inputPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!isValidPassword(s.toString())) {
                    layoutPassword.error =
                        "Minimal 8 karakter, huruf besar, kecil, angka, tanpa spasi"
                } else {
                    layoutPassword.error = null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        inputConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != inputPassword.text.toString()) {
                    layoutConfirmPassword.error = "Konfirmasi kata sandi tidak sama"
                } else {
                    layoutConfirmPassword.error = null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnRegister.setOnClickListener {
            val name = inputName.text.toString().trim()
            val phone = inputPhone.text.toString().trim()
            val password = inputPassword.text.toString()
            val confirmPassword = inputConfirmPassword.text.toString()

            when {
                name.isEmpty() || phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                    toast("Harap isi semua field")
                }
                layoutPhone.error != null || layoutPassword.error != null || layoutConfirmPassword.error != null -> {
                    toast("Periksa kembali input yang salah")
                }
                !cbTerms.isChecked -> {
                    toast("Harap setujui syarat dan ketentuan")
                }
                else -> {
                    showLoadingDialog("Sedang memproses registrasi...")
                    registerUser(name, phone, password)
                }
            }
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun isValidPhone(phone: String): Boolean {
        return phone.length >= 9 && phone.all { it.isDigit() }
    }

    private fun isValidPassword(password: String): Boolean {
        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{8,}$")
        return regex.matches(password) && !password.contains(" ")
    }

    private fun registerUser(name: String, phone: String, password: String) {
        val hashedPassword = hashPassword(password)

        val userData = hashMapOf(
            "namaLengkap" to name,
            "notelepon" to phone,
            "KataSandi" to hashedPassword,
            "email" to "",
            "fotoProfil" to "https://www.gstatic.com/images/branding/product/2x/avatar_circle_blue_512dp.png"
        )

        db.collection("users")
            .document(phone)
            .set(userData)
            .addOnSuccessListener {
                updateLoadingMessage("Registrasi berhasil, mengarahkan ke login...")
                inputPhone.postDelayed({
                    hideLoadingDialog()
                    toast("Registrasi berhasil")
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }, 1500)
            }
            .addOnFailureListener { e ->
                hideLoadingDialog()
                toast("Gagal registrasi: ${e.message}")
            }
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    private fun showLoadingDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_progress, null)

        tvMessage = view.findViewById(R.id.tvMessage)
        tvMessage?.text = message

        builder.setView(view)
        builder.setCancelable(false)

        progressDialog = builder.create()
        progressDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        progressDialog?.show()
    }

    private fun updateLoadingMessage(message: String) {
        tvMessage?.text = message
    }

    private fun hideLoadingDialog() {
        progressDialog?.dismiss()
        progressDialog = null
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
