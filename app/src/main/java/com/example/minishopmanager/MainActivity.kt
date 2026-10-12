package com.example.minishopmanager

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private val nomsProduits = ArrayList<String>()
    private val imagesProduits = ArrayList<Int>()

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

        val listView = findViewById<ListView>(R.id.lstViewProduits)

        // Lire les produits depuis le fichier JSON
        lireProduitsDepuisJSON()

        // Adaptateur personnalisé avec les images
        val adapter = object : ArrayAdapter<String>(
            this,
            R.layout.item_product,
            R.id.tvProduit,
            nomsProduits
        ) {
            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {

                val view = super.getView(position, convertView, parent)
                val imageView =
                    view.findViewById<ImageView>(R.id.imgProduit)

                imageView.setImageResource(imagesProduits[position])

                return view
            }
        }

        listView.adapter = adapter

        // Ajouter le produit sélectionné au panier SQLite
        listView.setOnItemClickListener { _, _, position, _ ->

            val produit = nomsProduits[position]

            val dbHelper = DBHelper(this)
            val db = dbHelper.writableDatabase

            val values = ContentValues()
            values.put("nom", produit)

            db.insert("panier", null, values)
            db.close()
            dbHelper.close()

            Toast.makeText(
                this,
                "Produit ajouté au panier : $produit",
                Toast.LENGTH_SHORT
            ).show()
        }

        Log.d("LIFECYCLE", "onCreate appelé")
    }

    private fun lireProduitsDepuisJSON() {

        nomsProduits.clear()
        imagesProduits.clear()

        try {
            val inputStream = assets.open("produits.json")
            val jsonString =
                inputStream.bufferedReader().use { it.readText() }

            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {

                val produit = jsonArray.getJSONObject(i)

                val nom = produit.getString("nom")
                val nomImage = produit.getString("image")

                val imageId = resources.getIdentifier(
                    nomImage,
                    "drawable",
                    packageName
                )

                nomsProduits.add(nom)

                if (imageId != 0) {
                    imagesProduits.add(imageId)
                } else {
                    imagesProduits.add(R.drawable.telephone)
                }
            }

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Erreur de lecture du fichier JSON",
                Toast.LENGTH_LONG
            ).show()

            Log.e("JSON", "Erreur de lecture", e)
        }
    }
}