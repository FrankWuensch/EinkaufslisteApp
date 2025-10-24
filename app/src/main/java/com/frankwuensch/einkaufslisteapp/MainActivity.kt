package com.frankwuensch.einkaufslisteapp

import android.app.AlertDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

    private lateinit var autoCompleteAdapter: ArrayAdapter<String>

    /**
     * Initializes the activity, sets up the user interface, and registers event listeners.
     *
     * This is the main entry point for the activity when it is first created. It handles:
     * - Inflating the layout (`R.layout.activity_main`).
     * - Initializing the database helper and data source.
     * - Setting up adapters for the shopping and "done" lists.
     * - Configuring click listeners for adding, moving, and deleting items.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down, this Bundle contains the data it most
     *     recently supplied in [onSaveInstanceState]. Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // just for testing
        startScraping()

        dbHelper = ProductItemDbHelper(this)
        dataSource = ProductItemDataSource(this)

        listViewProductItems = findViewById(R.id.listview_product_items)
        listViewDoneItems = findViewById(R.id.listview_done_items)
        textEditQuantity = findViewById(R.id.editText_quantity)
        textEditProduct = findViewById(R.id.editText_product)
        buttonAddProduct = findViewById(R.id.button_add_product)

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

        val db = dataSource.getWriteableDatabase()!!
        dbHelper.insertInitialProductsAlways(db)

        refreshProductList()

        buttonAddProduct.setOnClickListener {
            val quantity = textEditQuantity.text.toString().toIntOrNull() ?: 0
            val product = textEditProduct.text.toString().trim()

            if (quantity >= 1 && product.isNotEmpty()) {
                val productItem = ProductItem(product, quantity)

                val values = ContentValues().apply {
                    put(ProductItemDbHelper.COLUMN_PRODUCT, productItem.product)
                    put(ProductItemDbHelper.COLUMN_QUANTITY, productItem.quantity)
                    put(ProductItemDbHelper.COLUMN_IS_INITIAL, 0)
                    put(ProductItemDbHelper.COLUMN_BROUGHT, 0)
                }

                db.insert(ProductItemDbHelper.TABLE_SHOPPING_LIST, null, values)
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

        if (savedInstanceState == null) {
            AlertDialog.Builder(this)
                .setTitle("Info")
                .setMessage("Lange auf einen Listeneintrag klicken, um diesen zu löschen.")
                .setIcon(R.drawable.twotone_info_24)
                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                .show()
        }
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
        val allProducts = dataSource.readAllProducts(dbHelper)

        productItemsList.clear()
        doneItemsList.clear()

        allProducts.filter { it.isInitial == 0 }.forEach { item ->
            if (item.isChecked) doneItemsList.add(item) else productItemsList.add(item)
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

    private fun startScraping() {
        // lifecycleScope ensures this process is cancelled if the user leaves the screen.
        lifecycleScope.launch {
            try {
                Log.d("Scraping", "Coroutine in MainActivity has started.")
                // Switch to a background thread to perform the network call.
                val links = withContext(Dispatchers.IO) {
                    // ACTUALLY CALL the new suspend function.
                    fetchAllLinks("https://kotlinlang.org/docs/reference/")
                }

                // --- Back on the main thread, update the UI ---

                // Process the raw links to get clean product names
                val productNames = links
                    .map { it.substringAfterLast('/').replace("-", " ").trim() }
                    .filter { it.isNotEmpty() }

                // Update the AutoCompleteTextView's adapter with the new data
                autoCompleteAdapter.clear()
                autoCompleteAdapter.addAll(productNames)
                autoCompleteAdapter.notifyDataSetChanged()

                Toast.makeText(this@MainActivity, "Scraping successful: ${productNames.size} names loaded.", Toast.LENGTH_LONG).show()

            } catch (e: Exception) {
                // This will now catch actual network errors (e.g., no internet).
                e.printStackTrace()
                Toast.makeText(this@MainActivity, "Scraping failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        dataSource.close()
    }
}
