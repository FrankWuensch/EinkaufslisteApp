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

    fun insertOrUpdateProduct(productItem: ProductItem, newQuantity: Int): Long {
        val db = database ?: return -1L

        val cursor = db.query(
            ProductItemDbHelper.TABLE_SHOPPING_LIST,
            arrayOf(ProductItemDbHelper.COLUMN_ID, ProductItemDbHelper.COLUMN_QUANTITY),
            "${ProductItemDbHelper.COLUMN_PRODUCT} = ?",
            arrayOf(productItem.product),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val existingId = cursor.getLong(cursor.getColumnIndexOrThrow(ProductItemDbHelper.COLUMN_ID))

            val values = ContentValues().apply {
                put(ProductItemDbHelper.COLUMN_QUANTITY, newQuantity)
            }

            db.update(
                ProductItemDbHelper.TABLE_SHOPPING_LIST,
                values,
                "${ProductItemDbHelper.COLUMN_ID} = ?",
                arrayOf(existingId.toString())
            )

            cursor.close()
            existingId
        } else {
            val values = ContentValues().apply {
                put(ProductItemDbHelper.COLUMN_PRODUCT, productItem.product)
                put(ProductItemDbHelper.COLUMN_QUANTITY, newQuantity)
            }
            val id = db.insert(ProductItemDbHelper.TABLE_SHOPPING_LIST, null, values)
            cursor.close()
            id
        }
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

    fun deleteProduct(id: Long?): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            ProductItemDbHelper.TABLE_SHOPPING_LIST,
            "${ProductItemDbHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    fun getProductSuggestions(query: String): List<String> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            ProductItemDbHelper.TABLE_SHOPPING_LIST,
            arrayOf(ProductItemDbHelper.COLUMN_PRODUCT),
            "${ProductItemDbHelper.COLUMN_PRODUCT} LIKE ?",
            arrayOf("%$query%"),
            null, null,
            "${ProductItemDbHelper.COLUMN_PRODUCT} ASC"
        )

        val suggestions = mutableListOf<String>()
        with(cursor) {
            while (moveToNext()) {
                val product = getString(getColumnIndexOrThrow(ProductItemDbHelper.COLUMN_PRODUCT))
                suggestions.add(product)
            }
        }

        cursor.close()
        return suggestions.distinct()
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