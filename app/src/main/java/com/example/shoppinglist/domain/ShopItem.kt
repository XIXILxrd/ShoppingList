package com.example.shoppinglist.domain

data class ShopItem(
    val name: String,
    val count: Int,
    val isEnabled: Boolean,
    var id: Int = UNDEFINED_ID
) {
    companion object {
        const val UNDEFINED_ID = -1
    }
}