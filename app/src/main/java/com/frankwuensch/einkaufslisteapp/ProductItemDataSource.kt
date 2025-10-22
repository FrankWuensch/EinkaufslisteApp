package com.frankwuensch.einkaufslisteapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.frankwuensch.einkaufslisteapp.ProductItemDbHelper.Companion.COLUMN_ID
import com.frankwuensch.einkaufslisteapp.ProductItemDbHelper.Companion.COLUMN_PRODUCT
import com.frankwuensch.einkaufslisteapp.ProductItemDbHelper.Companion.COLUMN_QUANTITY
import com.frankwuensch.einkaufslisteapp.ProductItemDbHelper.Companion.COLUMN_BROUGHT

class ProductItemDataSource(context: Context) {
    private val LOG_TAG = ProductItemDataSource::class.java.simpleName
    private var database: SQLiteDatabase? = null
    private var dbHelper: ProductItemDbHelper = ProductItemDbHelper(context)

    init {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.")
        getWriteableDatabase()
    }

    fun getWriteableDatabase(): SQLiteDatabase? {
        return try {
            dbHelper.writableDatabase.also {
                database = it
                Log.d(LOG_TAG, "Datenbank erfolgreich zum Schreiben geöffnet.")
            }
        } catch (ex: Exception) {
            Log.e(LOG_TAG, "Fehler beim Öffnen der Datenbank: ${ex.message}")
            null
        }
    }

    fun readAllProducts(dbHelper: ProductItemDbHelper): MutableList<ProductItem> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            ProductItemDbHelper.TABLE_SHOPPING_LIST,
            null, null, null, null, null, null
        )

        val productList = mutableListOf<ProductItem>()
        with(cursor) {
            while (moveToNext()) {
                val id = getLong(getColumnIndexOrThrow(COLUMN_ID))
                val product = getString(getColumnIndexOrThrow(COLUMN_PRODUCT))
                val quantity = getInt(getColumnIndexOrThrow(COLUMN_QUANTITY))
                val checkedState = getInt(getColumnIndexOrThrow(COLUMN_BROUGHT)) == 1
                productList.add(ProductItem(product, quantity, checkedState.toString().toBoolean(), id))
            }
        }
        cursor.close()
        db.close()
        return productList
    }

    fun insertProduct(productItem: ProductItem): Long? {
        val db = database ?: return -1L

        val values = ContentValues().apply {
            put(ProductItemDbHelper.COLUMN_PRODUCT, productItem.product)
            put(ProductItemDbHelper.COLUMN_QUANTITY, productItem.quantity)
        }

        return try {
            db.insert(ProductItemDbHelper.TABLE_SHOPPING_LIST, null, values)
            Log.d(LOG_TAG, "Produkt ${productItem.product} erfolgreich gespeichert.")
            productItem.id
        } catch (ex: Exception) {
            Log.e(LOG_TAG, "Fehler beim Einfügen: ${ex.message}.")
            -1L
        }
    }

    fun deleteProduct(id: Long?): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            ProductItemDbHelper.TABLE_SHOPPING_LIST,
            "${ProductItemDbHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun updateProductChecked(id: Long?, isChecked: Boolean): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ProductItemDbHelper.COLUMN_BROUGHT, if (isChecked) 1 else 0)
        }
        return db.update(
            ProductItemDbHelper.TABLE_SHOPPING_LIST,
            values,
            "${ProductItemDbHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun close() {
        database?.let {
            if (it.isOpen) {
                it.close()
                Log.d(LOG_TAG, "Datenbank wurde erfolgreich geschlossen.")
            }
        }
        dbHelper.close()
    }
}