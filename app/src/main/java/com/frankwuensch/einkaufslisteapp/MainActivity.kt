package com.frankwuensch.einkaufslisteapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class MainActivity : AppCompatActivity() {
    companion object {
        private val LOG_TAG = MainActivity::class.java.simpleName
    }

    private lateinit var dataSource: ProductItemDataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val testProductItem = ProductItem("Birnen", 5, 102)
        Log.d(LOG_TAG, "Inhalt der Testmemo: $testProductItem")

        dataSource = ProductItemDataSource(this)
    }

    override fun onStart() {
        dataSource.getWriteableDatabase()
        super.onStart()
    }

    override fun onStop() {
        dataSource.close()
        super.onStop()
    }
}