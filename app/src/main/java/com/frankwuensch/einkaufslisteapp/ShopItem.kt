package com.frankwuensch.einkaufslisteapp

data class ShopItem(
    var shopName: String,
    var shopId: String,
    var plz: String,
    var city: String? = "",
    var street: String? = "",
    var houseNumber: String? = "",
    var id: Long? = null
) {
    override fun toString(): String {
        var str: String = ""
        if (city.isNullOrEmpty()) {
            str = "$shopName ($plz)"
        } else if
            (!city.isNullOrEmpty() && !street.isNullOrEmpty() && !houseNumber.isNullOrEmpty()) {
            str = "$shopName ($plz $city, $street $houseNumber)"
        }
        return str
    }
}