
package com.example.minishopmanager

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, "minishop.db", null, 3) {

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE panier(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT,
                quantite INTEGER NOT NULL DEFAULT 1
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE utilisateurs(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE COLLATE NOCASE,
                password_hash TEXT NOT NULL,
                salt TEXT NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(
        db: SQLiteDatabase,
        oldVersion: Int,
        newVersion: Int
    ) {

        if (oldVersion < 2) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS utilisateurs(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE COLLATE NOCASE,
                    password_hash TEXT NOT NULL,
                    salt TEXT NOT NULL
                )
                """.trimIndent()
            )
        }

        if (oldVersion < 3) {
            db.execSQL(
                "ALTER TABLE panier ADD COLUMN quantite INTEGER NOT NULL DEFAULT 1"
            )
        }
    }

    fun registerUser(
        nom: String,
        email: String,
        password: String
    ): Boolean {

        val salt = generateSalt()
        val hash = hashPassword(password, salt)

        val db = writableDatabase
        val values = ContentValues()

        values.put("nom", nom)
        values.put("email", email)
        values.put("password_hash", hash)
        values.put("salt", salt)

        val result = db.insertWithOnConflict(
            "utilisateurs",
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        )

        return result != -1L
    }

    fun checkUser(email: String, password: String): Boolean {

        val db = readableDatabase

        val cursor = db.rawQuery(
            """
            SELECT password_hash, salt
            FROM utilisateurs
            WHERE email = ? COLLATE NOCASE
            """.trimIndent(),
            arrayOf(email)
        )

        cursor.use {

            if (it.moveToFirst()) {

                val savedHash = it.getString(0)
                val salt = it.getString(1)
                val enteredHash = hashPassword(password, salt)

                return MessageDigest.isEqual(
                    Base64.decode(savedHash, Base64.NO_WRAP),
                    Base64.decode(enteredHash, Base64.NO_WRAP)
                )
            }
        }

        return false
    }

    private fun generateSalt(): String {

        val random = SecureRandom()
        val salt = ByteArray(16)

        random.nextBytes(salt)

        return Base64.encodeToString(
            salt,
            Base64.NO_WRAP
        )
    }

    private fun hashPassword(
        password: String,
        salt: String
    ): String {

        val saltBytes = Base64.decode(
            salt,
            Base64.NO_WRAP
        )

        val spec = PBEKeySpec(
            password.toCharArray(),
            saltBytes,
            120000,
            256
        )

        val factory = SecretKeyFactory.getInstance(
            "PBKDF2WithHmacSHA1"
        )

        val hash = factory.generateSecret(spec).encoded

        spec.clearPassword()

        return Base64.encodeToString(
            hash,
            Base64.NO_WRAP
        )
    }
}
