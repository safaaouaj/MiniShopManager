
package com.example.minishopmanager

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

data class ProduitPanier(
    val id: Int,
    val nom: String,
    var quantite: Int
)

class CartActivity : AppCompatActivity() {

    private val produits = ArrayList<ProduitPanier>()

    private lateinit var adapter: PanierAdapter
    private lateinit var tvCount: TextView
    private lateinit var tvEmpty: TextView
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        tvCount = findViewById(R.id.tvCartCount)
        tvEmpty = findViewById(R.id.tvEmptyCart)
        listView = findViewById(R.id.lstCart)

        val btnClear = findViewById<Button>(R.id.btnClearCart)
        val btnBack = findViewById<Button>(R.id.btnBackShop)
        val btnCheckout = findViewById<Button>(R.id.btnCheckout)

        adapter = PanierAdapter()
        listView.adapter = adapter

        btnClear.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Vider le panier")
                .setMessage("Voulez-vous supprimer tous les produits ?")
                .setPositiveButton("Oui") { _, _ ->
                    viderPanier()
                }
                .setNegativeButton("Non", null)
                .show()
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnCheckout.setOnClickListener {

            if (produits.isEmpty()) {
                Toast.makeText(
                    this,
                    "Votre panier est vide.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = Intent(this, CheckoutActivity::class.java)
                startActivity(intent)
            }
        }

        chargerPanier()
    }

    override fun onResume() {
        super.onResume()
        chargerPanier()
    }

    private fun chargerPanier() {

        produits.clear()

        val dbHelper = DBHelper(this)
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT id, nom, quantite FROM panier ORDER BY id DESC",
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                produits.add(
                    ProduitPanier(
                        it.getInt(0),
                        it.getString(1),
                        it.getInt(2)
                    )
                )
            }
        }

        dbHelper.close()

        adapter.notifyDataSetChanged()

        var totalArticles = 0

        for (produit in produits) {
            totalArticles += produit.quantite
        }

        tvCount.text = "Total : $totalArticles article(s)"

        if (produits.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            listView.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            listView.visibility = View.VISIBLE
        }
    }

    private fun modifierQuantite(id: Int, nouvelleQuantite: Int) {

        if (nouvelleQuantite < 1) {
            Toast.makeText(
                this,
                "La quantité minimale est 1.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val dbHelper = DBHelper(this)
        val db = dbHelper.writableDatabase

        val values = ContentValues()
        values.put("quantite", nouvelleQuantite)

        db.update(
            "panier",
            values,
            "id = ?",
            arrayOf(id.toString())
        )

        dbHelper.close()
        chargerPanier()
    }

    private fun supprimerProduit(id: Int) {

        val dbHelper = DBHelper(this)
        val db = dbHelper.writableDatabase

        db.delete(
            "panier",
            "id = ?",
            arrayOf(id.toString())
        )

        dbHelper.close()

        Toast.makeText(
            this,
            "Produit supprimé du panier.",
            Toast.LENGTH_SHORT
        ).show()

        chargerPanier()
    }

    private fun viderPanier() {

        val dbHelper = DBHelper(this)
        val db = dbHelper.writableDatabase

        db.delete("panier", null, null)

        dbHelper.close()

        Toast.makeText(
            this,
            "Le panier est vide.",
            Toast.LENGTH_SHORT
        ).show()

        chargerPanier()
    }

    private inner class PanierAdapter : BaseAdapter() {

        override fun getCount(): Int {
            return produits.size
        }

        override fun getItem(position: Int): ProduitPanier {
            return produits[position]
        }

        override fun getItemId(position: Int): Long {
            return produits[position].id.toLong()
        }

        override fun getView(
            position: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {

            val view = convertView ?: LayoutInflater.from(this@CartActivity)
                .inflate(R.layout.item_cart, parent, false)

            val produit = produits[position]

            val tvNom = view.findViewById<TextView>(
                R.id.tvCartProduct
            )

            val tvQuantite = view.findViewById<TextView>(
                R.id.tvCartQuantity
            )

            val btnDecrease = view.findViewById<Button>(
                R.id.btnDecrease
            )

            val btnIncrease = view.findViewById<Button>(
                R.id.btnIncrease
            )

            val btnDelete = view.findViewById<Button>(
                R.id.btnDelete
            )

            tvNom.text = produit.nom
            tvQuantite.text = produit.quantite.toString()

            btnIncrease.setOnClickListener {
                modifierQuantite(
                    produit.id,
                    produit.quantite + 1
                )
            }

            btnDecrease.setOnClickListener {
                modifierQuantite(
                    produit.id,
                    produit.quantite - 1
                )
            }

            btnDelete.setOnClickListener {
                supprimerProduit(produit.id)
            }

            return view
        }
    }
}
