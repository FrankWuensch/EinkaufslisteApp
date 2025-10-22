package com.frankwuensch.einkaufslisteapp

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener

class MainActivity : AppCompatActivity() {

    private lateinit var dataSource: ProductItemDataSource
    private lateinit var dbHelper: ProductItemDbHelper
    private lateinit var listViewProductItems: ListView
    private lateinit var listViewDoneItems: ListView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var doneAdapter: ProductAdapter
    private var productItemsList = mutableListOf<ProductItem>()
    private var doneItemsList = mutableListOf<ProductItem>()

    private lateinit var textEditQuantity: EditText
    private lateinit var textEditProduct: AutoCompleteTextView
    private lateinit var buttonAddProduct: Button

    // Autocomplete Adapter einmal erstellen
    private lateinit var autoCompleteAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            AlertDialog.Builder(this).setTitle("Info")
                .setMessage("Lange auf einen Listeneintrag klicken, um diesen zu löschen.")
                .setIcon(R.drawable.twotone_info_24)
                .setPositiveButton("OK") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }

        dbHelper = ProductItemDbHelper(this)
        dataSource = ProductItemDataSource(this)

        listViewProductItems = findViewById(R.id.listview_product_items)
        listViewDoneItems = findViewById(R.id.listview_done_items)
        textEditQuantity = findViewById(R.id.editText_quantity)
        textEditProduct = findViewById(R.id.editText_product)
        buttonAddProduct = findViewById(R.id.button_add_product)

        // Autocomplete Adapter initialisieren
        autoCompleteAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf()
        )
        textEditProduct.setAdapter(autoCompleteAdapter)

        textEditProduct.addTextChangedListener { editable ->
            val query = editable?.toString()?.trim() ?: ""
            if (query.isNotEmpty()) {
                val suggestions = dataSource.getProductSuggestions(query)
                autoCompleteAdapter.clear()
                autoCompleteAdapter.addAll(suggestions)
                autoCompleteAdapter.notifyDataSetChanged()
                textEditProduct.showDropDown()
            }
        }

        productAdapter = ProductAdapter(this, productItemsList, dataSource)
        doneAdapter = ProductAdapter(this, doneItemsList, dataSource)
        listViewProductItems.adapter = productAdapter
        listViewDoneItems.adapter = doneAdapter

        buttonAddProduct.setOnClickListener {
            val quantity = textEditQuantity.text.toString().toIntOrNull() ?: 0
            val product = textEditProduct.text.toString().trim()

            if (quantity >= 1 && product.isNotEmpty()) {
                val productItem = ProductItem(product, quantity)

                // Insert/Update ohne unnötige if-Bedingung
                dataSource.insertOrUpdateProduct(productItem, quantity, 0)

                refreshProductList()
            } else {
                Toast.makeText(
                    this,
                    "Anzahl muss größer oder gleich 1 sein und Produkttext darf nicht leer sein.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        listViewProductItems.setOnItemClickListener { _, _, position, _ ->
            moveItemBetweenLists(productItemsList, doneItemsList, position, true)
        }

        listViewDoneItems.setOnItemClickListener { _, _, position, _ ->
            moveItemBetweenLists(doneItemsList, productItemsList, position, false)
        }

        setupDeleteOnLongClick(listViewProductItems, productAdapter, dataSource)
        setupDeleteOnLongClick(listViewDoneItems, doneAdapter, dataSource)
    }

    private fun moveItemBetweenLists(
        fromList: MutableList<ProductItem>,
        toList: MutableList<ProductItem>,
        position: Int,
        isChecked: Boolean
    ) {
        val item = fromList.removeAt(position)
        productAdapter.notifyDataSetChanged()
        doneAdapter.notifyDataSetChanged()

        item.isChecked = isChecked
        dataSource.updateProductChecked(item.id, isChecked)

        toList.add(item)
        toList.sortWith(compareBy<ProductItem> { it.product.lowercase() }.thenBy { it.quantity })
        if (isChecked) doneAdapter.notifyDataSetChanged() else productAdapter.notifyDataSetChanged()
    }

    private fun setupDeleteOnLongClick(
        listView: ListView,
        adapter: ArrayAdapter<ProductItem>,
        dataSource: ProductItemDataSource
    ) {
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val productItem = adapter.getItem(position)
            if (productItem != null) {
                deleteListItem(productItem, adapter, dataSource)
            }
            true
        }
    }

    private fun deleteListItem(
        productItem: ProductItem,
        adapter: ArrayAdapter<ProductItem>,
        dataSource: ProductItemDataSource
    ) {
        if (productItem.isInitial != 0) {
            Toast.makeText(this, "Initialprodukte können nicht gelöscht werden.", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Produkt löschen")
            .setMessage("Soll '${productItem}' wirklich von der Liste gelöscht werden?")
            .setPositiveButton("Löschen") { _, _ ->
                val deletedRows = dataSource.deleteProduct(productItem.id)
                if (deletedRows > 0) {
                    adapter.remove(productItem)
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "'${productItem}' gelöscht.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Produkt konnte nicht gelöscht werden.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Abbrechen", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        refreshProductList()
    }

    private fun refreshProductList() {
        val productsFromDb = dataSource.readAllProducts(dbHelper)
        productItemsList.clear()
        doneItemsList.clear()

        productsFromDb.forEach {
            if (it.isChecked) doneItemsList.add(it) else productItemsList.add(it)
        }

        productItemsList.sortWith(compareBy<ProductItem> { it.product.lowercase() }.thenBy { it.quantity })
        doneItemsList.sortWith(compareBy<ProductItem> { it.product.lowercase() }.thenBy { it.quantity })

        productAdapter.notifyDataSetChanged()
        doneAdapter.notifyDataSetChanged()

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
