package com.example.shoppinglist.data

import com.example.shoppinglist.domain.ShopItem
import javax.inject.Inject

class ShopListMapper @Inject constructor() {

    fun mapEntityToDbModel(shopItem: ShopItem): ShopItemDBModel = ShopItemDBModel(
        id = shopItem.id,
        name = shopItem.name,
        count = shopItem.count,
        isEnabled = shopItem.isEnabled
    )

    fun mapDbModelToEntity(shopItemDBModel: ShopItemDBModel): ShopItem = ShopItem(
        id = shopItemDBModel.id,
        name = shopItemDBModel.name,
        count = shopItemDBModel.count,
        isEnabled = shopItemDBModel.isEnabled
    )

    fun mapListDbModelToListEntity(list: List<ShopItemDBModel>) = list.map {
        mapDbModelToEntity(it)
    }

}