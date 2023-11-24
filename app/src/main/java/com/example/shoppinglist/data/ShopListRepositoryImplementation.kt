package com.example.shoppinglist.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.shoppinglist.domain.ShopItem
import com.example.shoppinglist.domain.ShopListRepository
import javax.inject.Inject

class ShopListRepositoryImplementation @Inject constructor(
    private val shopListDao: ShopListDao,
    private val mapper: ShopListMapper,
) : ShopListRepository {

    override suspend fun addShopItem(shopItem: ShopItem) {
        shopListDao.addShopItem(mapper.mapEntityToDbModel(shopItem))
    }

    override suspend fun deleteShopItem(shopItem: ShopItem) {
        shopListDao.deleteShopItem(shopItem.id)
    }

    override suspend fun editShopItem(shopItem: ShopItem) {
        shopListDao.addShopItem(mapper.mapEntityToDbModel(shopItem))
    }

    override suspend fun getShopItem(id: Int): ShopItem =
        mapper.mapDbModelToEntity(shopListDao.getShopItem(id))

    override fun getShopList(): LiveData<List<ShopItem>> = shopListDao.getShopList().map {
        mapper.mapListDbModelToListEntity(it)
    }
}
