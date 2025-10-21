package com.frankwuensch.einkaufslisteapp

data class ProductItem(
    var product: String,
    var quantity: Int,
    var isChecked: Boolean = false,
    var id: Long? = null
) {
    override fun toString(): String {
        return "$quantity x $product"
    }
}
