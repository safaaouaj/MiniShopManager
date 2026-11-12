
package com.example.minishopmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray

class CheckoutActivity : AppCompatActivity() {

    private val prixProduits = HashMap<String, Double>()
    private var totalCommande = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        val tvSummary = findViewById<TextView>(R.id.tvCheckoutSummary)
        val tvTotal = findViewById<TextView>(R.id.tvCheckoutTotal)

        val etName = findViewById<EditText>(R.id.etDeliveryName)
        val etPhone = findViewById<EditText>(R.id.etDeliveryPhone)
        val etAddress = findViewById<EditText>(R.id.etDeliveryAddress)

        val btnConfirm = findViewById<Button>(R.id.btnConfirmOrder)
        val btnBack = findViewById<Button>(R.id.btnBackCart)

        lirePrixProduits()

        val dbHelper = DBHelper(this)
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery(
            "SELECT nom, quantite FROM panier ORDER BY id DESC",
            null
        )

        val resume = StringBuilder()
        var nombreArticles = 0
        totalCommande = 0.0

        cursor.use {
            while (it.moveToNext()) {
                val nom = it.getString(0)
                val quantite = it.getInt(1)
                val prix = prixProduits[nom] ?: 0.0
                val sousTotal = prix * quantite

                resume.append(nom)
                resume.append(" x ")
                resume.append(quantite)
                resume.append(" : ")
                resume.append(String.format("%.2f DT", sousTotal))
                resume.append("\n\n")

                nombreArticles += quantite
                totalCommande += sousTotal
            }
        }

        dbHelper.close()

        if (nombreArticles == 0) {
            tvSummary.text = "Votre panier est vide."
            btnConfirm.isEnabled = false
        } else {
            tvSummary.text = resume.toString()
        }

        tvTotal.text = String.format(
            "Total : %.2f DT",
            totalCommande
        )

        btnConfirm.setOnClickListener {

            val nom = etName.text.toString().trim()
            val telephone = etPhone.text.toString().trim()
            val adresse = etAddress.text.toString().trim()

            if (nom.isEmpty()) {
                etName.error = "Saisissez un nom"
            } else if (telephone.isEmpty()) {
                etPhone.error = "Saisissez un numéro"
            } else if (adresse.isEmpty()) {
                etAddress.error = "Saisissez une adresse"
            } else if (totalCommande <= 0.0) {
                Toast.makeText(
                    this,
                    "Le montant de la commande est invalide.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Confirmer la commande")
                    .setMessage(
                        "Nom : $nom\n" +
                                "Téléphone : $telephone\n" +
                                "Adresse : $adresse\n\n" +
                                String.format(
                                    "Total à payer : %.2f DT",
                                    totalCommande
                                ) +
                                "\n\nVoulez-vous continuer ?"
                    )
                    .setPositiveButton("Continuer") { _, _ ->

                        val intent = Intent(
                            this,
                            PaymentActivity::class.java
                        )

                        intent.putExtra(
                            "TOTAL_COMMANDE",
                            totalCommande
                        )

                        startActivity(intent)
                    }
                    .setNegativeButton("Annuler", null)
                    .show()
            }
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun lirePrixProduits() {
        try {
            val inputStream = assets.open("produits.json")

            val jsonString = inputStream.bufferedReader().use {
                it.readText()
            }

            val jsonArray = JSONArray(jsonString)

            for (i in 0 until jsonArray.length()) {
                val produit = jsonArray.getJSONObject(i)

                val nom = produit.getString("nom")
                val prix = produit.getDouble("prix")

                prixProduits[nom] = prix
            }

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Impossible de lire les prix des produits.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
