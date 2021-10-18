package org.shop.thewarehouse.data.repository

import org.shop.thewarehouse.api.FakeStoreService
import org.shop.thewarehouse.data.model.Product
import org.shop.thewarehouse.data.room.ProductDAO
import org.shop.thewarehouse.utils.NetworkConnectionError

class ProductRepository(val storeService: FakeStoreService,val productDAO: ProductDAO) {
    suspend fun getProducts(): List<Product>? {
        try {
            val result = storeService.getProducts()
            return result.body()
        } catch (cause: Throwable) {
            throw NetworkConnectionError("Hubo un error al llamar el servicio", cause)
        }
    }


}