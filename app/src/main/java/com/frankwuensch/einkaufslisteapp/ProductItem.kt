package com.frankwuensch.einkaufslisteapp

class ProductItem(
    product: String,
    quantity: Int,
    id: Long
) {
    var product: String = product
        get() = field
        set(value) {
            field = value
        }

    var quantity: Int = quantity
        get() = field
        set(value) {
            field = value
        }

    var id: Long = id
        get() = field
        set(value) {
            field = value
        }

    override fun toString(): String {
        return "$quantity x $product"
    }
}
