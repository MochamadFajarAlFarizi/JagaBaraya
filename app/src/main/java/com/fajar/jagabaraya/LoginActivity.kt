package com.fajar.jagabaraya

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val layoutPhone = findViewById<TextInputLayout>(R.id.layout_phone)
        val layoutPassword = findViewById<TextInputLayout>(R.id.layout_password)
        val inputPhone = findViewById<TextInputEditText>(R.id.input_phone)
        val inputPassword = findViewById<TextInputEditText>(R.id.input_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val tvRegister = findViewById<TextView>(R.id.tv_register)
        val tvForgot = findViewById<TextView>(R.id.tv_forgot)

        val passwordRegex = Regex("^(?=.*[A-Z])(?=.*[0-9])(?=\\S+$).{8,}$")


        inputPhone.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val phone = s.toString().trim()
                when {
                    phone.isEmpty() -> layoutPhone.error = "Nomor telepon wajib diisi"
                    phone.length < 9 -> layoutPhone.error = "Nomor telepon minimal 9 digit"
                    else -> layoutPhone.error = null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Validasi real-time password
        inputPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val password = s.toString().trim()
                when {
                    password.isEmpty() -> layoutPassword.error = "Kata sandi wajib diisi"
                    !password.matches(passwordRegex) ->
                        layoutPassword.error = "Minimal 8 karakter, 1 huruf besar, 1 angka, tanpa spasi"
                    else -> layoutPassword.error = null
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnLogin.setOnClickListener {
            val phone = inputPhone.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            val phoneValid = layoutPhone.error == null && phone.isNotEmpty()
            val passwordValid = layoutPassword.error == null && password.isNotEmpty()

            if (phoneValid && passwordValid) {
                val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_progress, null)
                val dialogBuilder = AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setCancelable(false)

                val dialog = dialogBuilder.create()
                dialog.show()

                val tvLoading = dialogView.findViewById<TextView>(R.id.tvMessage)
                tvLoading.text = "Sedang memproses login..."

                // Hash password input
                val hashedPassword = hashPassword(password)

                db.collection("users").document(phone).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val storedPassword = document.getString("KataSandi")
                            if (storedPassword == hashedPassword) {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    dialog.dismiss()
                                    startActivity(Intent(this, MainActivity::class.java))
                                    finish()
                                }, 1000)
                            } else {
                                dialog.dismiss()
                                layoutPassword.error = "Kata sandi salah"
                            }
                        } else {
                            dialog.dismiss()
                            layoutPhone.error = "Nomor tidak terdaftar"
                        }
                    }
                    .addOnFailureListener { e ->
                        dialog.dismiss()
                        Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Periksa kembali input Anda", Toast.LENGTH_SHORT).show()
            }
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        tvForgot.setOnClickListener {
            Toast.makeText(this, "Lupa kata sandi ditekan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}
