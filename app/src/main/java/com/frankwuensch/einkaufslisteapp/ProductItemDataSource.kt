package com.frankwuensch.einkaufslisteapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log

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