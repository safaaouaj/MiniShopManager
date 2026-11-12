
package com.example.minishopmanager

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword =
            findViewById<EditText>(R.id.etConfirmPassword)

        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvBackLogin = findViewById<TextView>(R.id.tvBackLogin)

        val dbHelper = DBHelper(this)

        btnRegister.setOnClickListener {

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword =
                etConfirmPassword.text.toString()

            if (name.isEmpty()) {

                etName.error = "Saisissez votre nom"

            } else if (email.isEmpty()) {

                etEmail.error = "Saisissez votre e-mail"

            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                etEmail.error = "E-mail invalide"

            } else if (password.length < 8) {

                etPassword.error =
                    "8 caractères minimum"

            } else if (password != confirmPassword) {

                etConfirmPassword.error =
                    "Les mots de passe ne correspondent pas"

            } else {

                val success = dbHelper.registerUser(
                    name,
                    email,
                    password
                )

                if (success) {

                    Toast.makeText(
                        this,
                        "Compte créé avec succès !",
                        Toast.LENGTH_LONG
                    ).show()

                    val intent = Intent(
                        this,
                        LoginActivity::class.java
                    )

                    startActivity(intent)
                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "Cette adresse e-mail existe déjà.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        tvBackLogin.setOnClickListener {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
