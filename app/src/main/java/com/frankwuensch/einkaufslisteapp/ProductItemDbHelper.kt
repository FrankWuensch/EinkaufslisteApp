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
        } catch (ex: Exception) {
            Log.e(LOG_TAG, "Fehler beim Anlegen der Tabelle: ${ex.message}")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        Log.d(LOG_TAG, "Upgrade der Datenbank von Version $oldVersion auf $newVersion.")
    }

    fun insertInitialProductsAlways(db: SQLiteDatabase?) {
        val initialProducts = listOf(
            ProductItem("Milch", 1, false, 1, 1L),
            ProductItem("Eier", 10, false, 1, 2L),
            ProductItem("Brot", 1, false, 1, 3L),
            ProductItem("Käse", 1, false, 1, 4L),
            ProductItem("Butter", 1, false, 1, 5L),
            ProductItem("Äpfel", 5, false, 1, 6L),
            ProductItem("Tomaten", 1, false, 1, 7L),
            ProductItem("Birnen", 5, false, 1, 8L),
            ProductItem("Orangen", 5, false, 1, 9L),
            ProductItem("Avokado", 3, false, 1, 10L),
            ProductItem("Jogurt", 2, false, 1, 11L)
        )

        initialProducts.forEach { product ->
            val cursor = db?.query(
                TABLE_SHOPPING_LIST,
                arrayOf(COLUMN_ID),
                "$COLUMN_PRODUCT = ?",
                arrayOf(product.product),
                null,
                null,
                null
            )
            val exists: Boolean = cursor?.count!! > 0
            cursor.close()

            if (!exists) {
                val values = ContentValues().apply {
                    put(COLUMN_PRODUCT, product.product)
                    put(COLUMN_QUANTITY, product.quantity)
                    put(COLUMN_IS_INITIAL, product.isInitial)
                    put(COLUMN_ID, product.id)
                    put(COLUMN_BROUGHT, 0)
                }
                db.insert(TABLE_SHOPPING_LIST, null, values)
            }
        }
    }
}
