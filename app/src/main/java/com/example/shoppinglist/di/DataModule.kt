package com.example.shoppinglist.di

import android.app.Application
import com.example.shoppinglist.data.ApplicationDB
import com.example.shoppinglist.data.ShopListDao
import com.example.shoppinglist.data.ShopListRepositoryImplementation
import com.example.shoppinglist.domain.ShopListRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @Binds
    @ApplicationScope
    fun bindShopListRepository(implementation: ShopListRepositoryImplementation): ShopListRepository

    companion object {
        @Provides
        @ApplicationScope
        fun provideShopListDao(
            application: Application
        ): ShopListDao {
            return ApplicationDB.getInstance(application).shopListDao()
        }
    }
}