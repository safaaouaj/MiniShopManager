package com.example.minishopmanager

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etStudentName = findViewById<EditText>(R.id.etStudentName)
        val cbRemember = findViewById<CheckBox>(R.id.cbRemember)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        prefs = getSharedPreferences("MiniShopPrefs", MODE_PRIVATE)

        // Charger les préférences sauvegardées
        etStudentName.setText(prefs.getString("STUDENT", ""))
        cbRemember.isChecked = prefs.getBoolean("REMEMBER", false)

        btnLogin.setOnClickListener {

            val nom = etStudentName.text.toString().trim()

            if (nom.isEmpty()) {
                Toast.makeText(
                    this,
                    "Veuillez saisir votre nom",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                val editor = prefs.edit()

                if (cbRemember.isChecked) {
                    editor.putString("STUDENT", nom)
                    editor.putBoolean("REMEMBER", true)
                } else {
                    editor.remove("STUDENT")
                    editor.putBoolean("REMEMBER", false)
                }

                editor.apply()

                // Ouvrir le catalogue
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}