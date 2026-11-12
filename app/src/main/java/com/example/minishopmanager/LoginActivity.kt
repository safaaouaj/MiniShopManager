
package com.example.minishopmanager

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etStudentName)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val cbRemember = findViewById<CheckBox>(R.id.cbRemember)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)

        prefs = getSharedPreferences(
            "MiniShopPrefs",
            MODE_PRIVATE
        )

        val dbHelper = DBHelper(this)

        etEmail.setText(prefs.getString("EMAIL", ""))
        cbRemember.isChecked =
            prefs.getBoolean("REMEMBER", false)

        btnLogin.setOnClickListener {

            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty()) {

                etEmail.error = "Veuillez saisir votre e-mail"

            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                etEmail.error = "Adresse e-mail invalide"

            } else if (password.isEmpty()) {

                etPassword.error = "Veuillez saisir votre mot de passe"

            } else {

                val connected = dbHelper.checkUser(
                    email,
                    password
                )

                if (connected) {

                    val editor = prefs.edit()

                    if (cbRemember.isChecked) {
                        editor.putString("EMAIL", email)
                        editor.putBoolean("REMEMBER", true)
                    } else {
                        editor.remove("EMAIL")
                        editor.putBoolean("REMEMBER", false)
                    }

                    editor.apply()

                    Toast.makeText(
                        this,
                        "Bienvenue !",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(
                        this,
                        MainActivity::class.java
                    )

                    startActivity(intent)
                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "E-mail ou mot de passe incorrect.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        tvRegister.setOnClickListener {

            val intent = Intent(
                this,
                RegisterActivity::class.java
            )

            startActivity(intent)
        }
    }
}
