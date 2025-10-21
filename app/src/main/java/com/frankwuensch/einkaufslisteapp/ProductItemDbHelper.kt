package com.frankwuensch.einkaufslisteapp

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
        private const val TABLE_SHOPPING_LIST: String = "einkaufsliste"

        // definition of variables for database fields
        private const val COLUMN_ID: String = "_id"
        private const val COLUMN_PRODUCT: String = "product"
        private const val COLUMN_QUANTITY: String = "quantity"

        // create string for database
        private const val SQL_CREATE: String =
            "CREATE TABLE $TABLE_SHOPPING_LIST($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_PRODUCT TEXT NOT NULL, $COLUMN_QUANTITY INTEGER NOT NULL);"
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

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}