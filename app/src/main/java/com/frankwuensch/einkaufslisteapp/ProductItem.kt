package com.frankwuensch.einkaufslisteapp

data class ProductItem(
    var product: String,
    var quantity: Int = 1,
    var isChecked: Boolean = false,
    var isInitial: Int = 0,
    var id: Long? = null
) {
    override fun toString(): String {
        return "$quantity x $product"
    }
}
