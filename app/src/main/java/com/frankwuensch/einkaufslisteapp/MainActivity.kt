package com.frankwuensch.einkaufslisteapp

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    private lateinit var dataSource: ProductItemDataSource
    private lateinit var dbHelper: ProductItemDbHelper
    private lateinit var listView: ListView
    private lateinit var adapter: ProductAdapter
    private var productItemsList = mutableListOf<ProductItem>()

    private lateinit var textEditQuantity: EditText
    private lateinit var textEditProduct: EditText
    private lateinit var buttonAddProduct: Button
    private lateinit var checkboxBought: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = ProductItemDbHelper(this)
        dataSource = ProductItemDataSource(this)

        listView = findViewById(R.id.listview_product_items)
        textEditQuantity = findViewById(R.id.editText_quantity)
        textEditProduct = findViewById(R.id.editText_product)
        buttonAddProduct = findViewById(R.id.button_add_product)

        adapter = ProductAdapter(this, productItemsList, dataSource)
        listView.adapter = adapter

        buttonAddProduct.setOnClickListener {
            val quantity = textEditQuantity.text.toString().toIntOrNull() ?: 0
            val product = textEditProduct.text.toString()

            if (quantity >= 1) {
                val productItem = ProductItem(product, quantity)
                val id = dataSource.insertProduct(productItem)

                if (id != -1L) {
                    refreshProductList()
                } else {
                    Log.e("MainActivity", "Einfügen in Datenbank fehlgeschlagen")
                }
            } else {
                Toast.makeText(this, "Anzahl muss größer oder gleich 1 sein.", Toast.LENGTH_LONG).show()
            }
        }

        listView.setOnItemLongClickListener { _, _, position, _ ->
            val productItem = adapter.getItem(position)

            if (productItem != null) {
                // Show a confirmation dialog before deleting
                AlertDialog.Builder(this)
                    .setTitle("Produkt löschen")
                    .setMessage("Soll(en) '${productItem.toString()}' wirklich von der Liste gelöscht werden?")
                    .setPositiveButton("Löschen") { _, _ ->
                        val deletedRows = dataSource.deleteProduct(productItem.id)
                        if (deletedRows > 0) {
                            // Remove from adapter and refresh
                            adapter.remove(productItem)
                            adapter.notifyDataSetChanged()
                            Toast.makeText(
                                this,
                                "'${productItem.product}' gelöscht.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                "Produkt konnte nicht gelöscht werden.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

            true // indicate the long click was handled
        }
    }

    override fun onResume() {
        super.onResume()
        refreshProductList()
    }

    private fun refreshProductList() {
        val productsFromDb = dataSource.readAllProducts(dbHelper)
        Log.d("debug", "items of db: $productsFromDb")

        adapter.clear()
        productItemsList.clear()
        productItemsList.addAll(productsFromDb)
        adapter.notifyDataSetChanged()
        Log.d(
            "debug",
            "items of productItemsList: $productItemsList; type of productItemsList: ${productItemsList.javaClass.toString()}"
        )
        textEditQuantity.text.clear()
        textEditProduct.text.clear()

        textEditQuantity.requestFocus()
    }

    override fun onStart() {
        super.onStart()
        dataSource.getWriteableDatabase()
    }

    override fun onStop() {
        super.onStop()
        dataSource.close()
    }
}
