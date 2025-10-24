package com.frankwuensch.einkaufslisteapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class ShopSelectorDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME: String = "laden.db"
        private const val DATABASE_VERSION: Int = 1
        private val LOG_TAG: String = ShopSelectorDbHelper::class.java.simpleName
        const val TABLE_SELECTED_SHOP: String = "selected_shop"

        // definition of variables for database fields
        const val COLUMN_ID: String = "_id"
        const val COLUMN_SHOP_NAME: String = "shop_name"
        const val COLUMN_SHOP_ID: String = "shop_id"
        const val COLUMN_PLZ: String = "plz"
        const val COLUMN_CITY: String = "city"
        const val COLUMN_STREET: String = "street"
        const val COLUMN_HOUSE_NUMBER: String = "house_number"

        // SQL command to create the table
        private val SQL_CREATE: String =
            """
            CREATE TABLE $TABLE_SELECTED_SHOP (
                $COLUMN_ID INTEGER PRIMARY KEY, 
                $COLUMN_SHOP_NAME TEXT NOT NULL, 
                $COLUMN_SHOP_ID TEXT NOT NULL,
                $COLUMN_PLZ TEXT NOT NULL,
                $COLUMN_CITY TEXT,
                $COLUMN_STREET TEXT,
                $COLUMN_HOUSE_NUMBER TEXT
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
}