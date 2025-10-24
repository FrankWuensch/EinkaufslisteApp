package com.frankwuensch.einkaufslisteapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class ShopSelectorDataSource(context: Context) {
    private val LOG_TAG = ShopSelectorDataSource::class.java.simpleName
    private var database: SQLiteDatabase? = null
    private var dbHelper: ShopSelectorDbHelper = ShopSelectorDbHelper(context)

    init {
        Log.d(LOG_TAG, "Unsere DataSource erzeugt jetzt den dbHelper.")
        getWriteableDatabase()
    }

    fun getWriteableDatabase(): SQLiteDatabase? {
        dbHelper.writableDatabase.also {
            database = it
            Log.d(LOG_TAG, "Datenbank erfolgreich zum Schreiben geöffnet.")
        }
        return database
    }
}
