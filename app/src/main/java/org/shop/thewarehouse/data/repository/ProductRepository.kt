package org.shop.thewarehouse.data.repository

import android.util.Log
import org.shop.thewarehouse.api.FakeStoreService
import org.shop.thewarehouse.data.model.Product
import org.shop.thewarehouse.data.room.ProductDAO
import org.shop.thewarehouse.utils.NetworkConnectionError

class ProductRepository(val storeService: FakeStoreService, val productDAO: ProductDAO) {
    suspend fun getProducts(): List<Product>? {
        Log.d("TAG", "MOSTRAR PANTALLaA")

        val result = storeService.getProducts()
        return result.body()

    }


}