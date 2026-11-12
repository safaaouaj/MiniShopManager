package com.example.minishopmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val tvName = findViewById<TextView>(R.id.tvProfileName)
        val tvEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val email = getSharedPreferences(
            "MiniShopPrefs",
            MODE_PRIVATE
        ).getString("EMAIL", "")

        val dbHelper = DBHelper(this)

        val cursor = dbHelper.readableDatabase.rawQuery(
            "SELECT nom, email FROM utilisateurs WHERE email = ? COLLATE NOCASE",
            arrayOf(email)
        )

        if (cursor.moveToFirst()) {
            tvName.text = cursor.getString(0)
            tvEmail.text = cursor.getString(1)
        }

        cursor.close()

        btnLogout.setOnClickListener {
            getSharedPreferences("MiniShopPrefs", MODE_PRIVATE)
                .edit()
                .clear()
                .apply()

            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}