
package com.example.minishopmanager

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private val nomsProduits = ArrayList<String>()
    private val imagesParNom = HashMap<String, Int>()

    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnNext = findViewById<Button>(R.id.btnNext)
        val btnCart = findViewById<Button>(R.id.btnCart)
        val listView = findViewById<ListView>(R.id.lstViewProduits)
        val etSearch = findViewById<EditText>(R.id.etSearch)

        btnNext.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        btnCart.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }

        lireProduitsDepuisJSON()

        adapter = object : ArrayAdapter<String>(
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

                val imageView = view.findViewById<ImageView>(
                    R.id.imgProduit
                )

                val nom = getItem(position) ?: ""

                imageView.setImageResource(
                    imagesParNom[nom] ?: R.drawable.telephone
                )

                return view
            }
        }

        listView.adapter = adapter

        // Ajouter ou augmenter la quantité
        listView.setOnItemClickListener { _, _, position, _ ->

            val produit = adapter.getItem(position) ?: return@setOnItemClickListener

            ajouterAuPanier(produit)
        }

        // Recherche
        etSearch.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                adapter.filter.filter(s)
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    private fun ajouterAuPanier(nomProduit: String) {

        val dbHelper = DBHelper(this)
        val db = dbHelper.writableDatabase

        val cursor = db.rawQuery(
            "SELECT id, quantite FROM panier WHERE nom = ?",
            arrayOf(nomProduit)
        )

        if (cursor.moveToFirst()) {

            val id = cursor.getInt(0)
            val ancienneQuantite = cursor.getInt(1)

            cursor.close()

            val values = ContentValues()
            values.put("quantite", ancienneQuantite + 1)

            db.update(
                "panier",
                values,
                "id = ?",
                arrayOf(id.toString())
            )

            Toast.makeText(
                this,
                "Quantité de $nomProduit augmentée !",
                Toast.LENGTH_SHORT
            ).show()

        } else {

            cursor.close()

            val values = ContentValues()
            values.put("nom", nomProduit)
            values.put("quantite", 1)

            val result = db.insert("panier", null, values)

            if (result != -1L) {
                Toast.makeText(
                    this,
                    "$nomProduit ajouté au panier !",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Erreur lors de l'ajout.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        dbHelper.close()
    }

    private fun lireProduitsDepuisJSON() {

        nomsProduits.clear()
        imagesParNom.clear()

        try {

            val inputStream = assets.open("produits.json")

            val jsonString = inputStream.bufferedReader().use {
                it.readText()
            }

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

                imagesParNom[nom] = if (imageId != 0) {
                    imageId
                } else {
                    R.drawable.telephone
                }
            }

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Erreur de lecture du fichier JSON.",
                Toast.LENGTH_LONG
            ).show()

            Log.e("JSON", "Erreur de lecture", e)
        }
    }
}
