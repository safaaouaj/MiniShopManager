
package com.example.minishopmanager

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class PaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val tvTotal = findViewById<TextView>(R.id.tvPaymentTotal)
        val rgMethods = findViewById<RadioGroup>(R.id.rgPaymentMethods)
        val btnConfirm = findViewById<Button>(R.id.btnConfirmPayment)
        val btnBack = findViewById<Button>(R.id.btnBackPayment)

        val total = intent.getDoubleExtra("TOTAL_COMMANDE", 0.0)

        tvTotal.text = String.format(
            "Total à payer : %.2f DT",
            total
        )

        btnConfirm.setOnClickListener {

            if (total <= 0.0) {
                Toast.makeText(
                    this,
                    "Montant de commande invalide.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val modePaiement = when (rgMethods.checkedRadioButtonId) {
                R.id.rbCash -> "Paiement à la livraison"
                R.id.rbSimulation -> "Paiement de démonstration"
                else -> ""
            }

            if (modePaiement.isEmpty()) {
                Toast.makeText(
                    this,
                    "Choisissez un mode de paiement.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage(
                    "Mode : $modePaiement\n\n" +
                            String.format("Montant : %.2f DT", total) +
                            "\n\nConfirmer cette opération de démonstration ?"
                )
                .setPositiveButton("Confirmer") { _, _ ->

                    viderPanier()

                    AlertDialog.Builder(this)
                        .setTitle("Commande confirmée !")
                        .setMessage(
                            "Votre commande de démonstration est confirmée.\n\n" +
                                    "Mode : $modePaiement\n" +
                                    String.format("Total : %.2f DT", total) +
                                    "\n\nMerci pour votre visite !"
                        )
                        .setCancelable(false)
                        .setPositiveButton("Terminer") { _, _ ->

                            val intent = Intent(
                                this,
                                MainActivity::class.java
                            )

                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_SINGLE_TOP

                            startActivity(intent)
                            finish()
                        }
                        .show()
                }
                .setNegativeButton("Annuler", null)
                .show()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun viderPanier() {

        val dbHelper = DBHelper(this)
        val db = dbHelper.writableDatabase

        db.delete("panier", null, null)

        dbHelper.close()
    }
}
