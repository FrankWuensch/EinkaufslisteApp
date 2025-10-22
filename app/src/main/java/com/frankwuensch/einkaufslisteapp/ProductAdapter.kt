package com.frankwuensch.einkaufslisteapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView

class ProductAdapter(
    context: Context,
    private val items: MutableList<ProductItem>,
    private val dataSource: ProductItemDataSource
) : ArrayAdapter<ProductItem>(context, 0, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_product, parent, false)

        val textView = view.findViewById<TextView>(R.id.text)
        val checkBox = view.findViewById<CheckBox>(R.id.checkbox_brought)

        val item = getItem(position)

        // Text anzeigen
        textView.text = "${item?.product} (${item?.quantity})"

        // Checkbox-Status anzeigen
        checkBox.isChecked = item?.isChecked ?: false

        // Checkbox nicht klickbar, wir nutzen den ListView-Click
        checkBox.isClickable = false
        checkBox.isFocusable = false

        return view
    }
}
