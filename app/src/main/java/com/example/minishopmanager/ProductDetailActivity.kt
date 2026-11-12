
package com.example.minishopmanager

import android.content.ContentValues
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ProductDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        val imgProduit = findViewById<ImageView>(R.id.imgDetailProduit)
        val tvNom = findViewById<TextView>(R.id.tvDetailNom)
        val tvDescription = findViewById<TextView>(R.id.tvDetailDescription)
        val tvPrix = findViewById<TextView>(R.id.tvDetailPrix)
        val btnAdd = findViewById<Button>(R.id.btnAddToCart)
        val btnBack = findViewById<Button>(R.id.btnBackDetail)

        val nom = intent.getStringExtra("NOM_PRODUIT") ?: ""
        val description = intent.getStringExtra("DESCRIPTION_PRODUIT") ?: ""
        val prix = intent.getDoubleExtra("PRIX_PRODUIT", 0.0)
        val image = intent.getIntExtra(
            "IMAGE_PRODUIT",
            R.drawable.telephone
        )

        tvNom.text = nom
        tvDescription.text = description
        tvPrix.text = String.format("%.2f DT", prix)
        imgProduit.setImageResource(image)

        btnAdd.setOnClickListener {

            val dbHelper = DBHelper(this)
            val db = dbHelper.writableDatabase

            val values = ContentValues()
            values.put("nom", nom)

            val result = db.insert("panier", null, values)

            dbHelper.close()

            if (result != -1L) {
                Toast.makeText(
                    this,
                    "$nom ajouté au panier !",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Erreur lors de l'ajout",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}
