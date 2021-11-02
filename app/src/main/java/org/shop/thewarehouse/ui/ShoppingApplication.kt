package org.shop.thewarehouse.ui

import android.app.Application
import org.shop.thewarehouse.api.Api.storeService
import org.shop.thewarehouse.data.repository.ProductRepository
import org.shop.thewarehouse.data.room.ProductDB

class ShoppingApplication: Application() {
    val repository by lazy{productDao}

    val productDao by lazy { ProductDB.getDatabase(this).productDao()}
    val productRepository by lazy{ ProductRepository(storeService, productDao) }
}
