package com.example.minishopmanager

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnNext = findViewById<Button>(R.id.btnNext)

        btnNext.setOnClickListener {
            Toast.makeText(
                this,
                "Bonjour Asma AYARI !",
                Toast.LENGTH_SHORT
            ).show()

            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        // ListView des produits
        val listView = findViewById<ListView>(R.id.lstViewProduits)

        // Liste des produits
        val products = resources.getStringArray(R.array.products)

        // Images des produits
        val images = arrayOf(
            R.drawable.telephone,
            R.drawable.casque,
            R.drawable.montre,
            R.drawable.chargeurr,
            R.drawable.coque,
            R.drawable.ecouteurs,
            R.drawable.powerbank,
            R.drawable.cable
        )

        // Adaptateur personnalisé
        val adapter = object : ArrayAdapter<String>(
            this,
            R.layout.item_product,
            R.id.tvProduit,
            products
        ) {

            override fun getView(
                position: Int,
                convertView: View?,
                parent: android.view.ViewGroup
            ): View {

                val view = super.getView(position, convertView, parent)

                val imageView = view.findViewById<ImageView>(R.id.imgProduit)

                imageView.setImageResource(images[position])

                return view
            }
        }

        // Afficher les produits
        listView.adapter = adapter

        // Gérer le clic sur un produit
        listView.setOnItemClickListener { parent, _, position, _ ->

            val item = parent.getItemAtPosition(position).toString()

            Toast.makeText(
                this,
                "Produit sélectionné : $item",
                Toast.LENGTH_SHORT
            ).show()
        }

        Log.d("LIFECYCLE", "onCreate appelé")
    }
}

