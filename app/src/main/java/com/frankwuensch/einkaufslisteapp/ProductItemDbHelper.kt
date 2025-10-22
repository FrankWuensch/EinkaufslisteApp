package com.frankwuensch.einkaufslisteapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class ProductItemDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME: String = "einkaufsliste.db"
        private const val DATABASE_VERSION: Int = 1
        private val LOG_TAG: String = ProductItemDbHelper::class.java.simpleName
        const val TABLE_SHOPPING_LIST: String = "einkaufsliste"

        // definition of variables for database fields
        const val COLUMN_ID: String = "_id"
        const val COLUMN_PRODUCT: String = "product"
        const val COLUMN_QUANTITY: String = "quantity"
        const val COLUMN_BROUGHT: String = "brought"
        const val COLUMN_IS_INITIAL: String = "is_initial"

        // SQL command to create the table
        private val SQL_CREATE: String =
            """
            CREATE TABLE $TABLE_SHOPPING_LIST (
                $COLUMN_ID INTEGER PRIMARY KEY, 
                $COLUMN_PRODUCT TEXT NOT NULL, 
                $COLUMN_QUANTITY INTEGER DEFAULT 1, 
                $COLUMN_BROUGHT INTEGER DEFAULT 0,
                $COLUMN_IS_INITIAL INTEGER DEFAULT 0
            )
            """.trimIndent()
    }

    init {
        Log.d(LOG_TAG, "DbHelper hat eine Datenbank: $databaseName erzeugt.")
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {
            Log.d(LOG_TAG, "Die Tabelle wird mit dem SQL-Befehl: $SQL_CREATE angelegt.")
            db.execSQL(SQL_CREATE)
            insertInitialProducts(db)
        } catch (ex: Exception) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: ${ex.message}")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(LOG_TAG, "Upgrade der Datenbank von Version $oldVersion auf $newVersion.")
    }

    fun insertInitialProducts(db: SQLiteDatabase?) {
        val initialProducts = listOf(
            "Milch" to 1,
            "Eier" to 10,
            "Brot" to 1,
            "Käse" to 1,
            "Butter" to 1,
            "Äpfel" to 5,
            "Tomaten" to 1,
            "Birnen" to 5,
            "Orangen" to 5,
            "Avokado" to 3,
            "Jogurt" to 2,
            "Saure Sahne" to 2,
            "Süße Sahne" to 2,
            "Sauerrahm" to 2,
            "Speisequark" to 1
        )

        for ((product, quantity) in initialProducts) {
            val values = ContentValues().apply {
                put(COLUMN_PRODUCT, product)
                put(COLUMN_QUANTITY, quantity)
                put(COLUMN_IS_INITIAL, 1)
            }
            db?.insert(TABLE_SHOPPING_LIST, null, values)
        }
    }
}
