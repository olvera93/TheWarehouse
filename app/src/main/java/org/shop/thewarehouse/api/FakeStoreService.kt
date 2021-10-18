package org.shop.thewarehouse.api

import org.shop.thewarehouse.data.model.Product
import retrofit2.Response
import retrofit2.http.GET

interface FakeStoreService {
    @GET("products")
    suspend fun getProducts(): Response<List<Product>>
}