package com.fajar.jagabaraya

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val inputPhone = findViewById<TextInputEditText>(R.id.input_phone)
        val inputPassword = findViewById<TextInputEditText>(R.id.input_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val tvRegister = findViewById<TextView>(R.id.tv_register)
        val tvForgot = findViewById<TextView>(R.id.tv_forgot)

        btnLogin.setOnClickListener {
            val phone = inputPhone.text.toString().trim()
            val password = inputPassword.text.toString().trim()

            if(phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Nomor telepon dan kata sandi wajib diisi", Toast.LENGTH_SHORT).show()
            } else {
                // Logika autentikasi bisa di sini
                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(this, "Login berhasil (sementara)", Toast.LENGTH_SHORT).show()
            }
        }

        tvRegister.setOnClickListener {
            // Pindah ke halaman registrasi
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        tvForgot.setOnClickListener {
            // Tambahkan logika lupa password
            Toast.makeText(this, "Lupa kata sandi ditekan", Toast.LENGTH_SHORT).show()
        }
    }
}
