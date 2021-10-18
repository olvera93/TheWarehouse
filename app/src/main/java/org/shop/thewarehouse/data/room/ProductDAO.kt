package org.shop.thewarehouse.data.room

import androidx.room.*
import org.shop.thewarehouse.data.model.Product

@Dao
interface ProductDAO {
    @Insert
    fun insertProduct(product: Product)

    @Update
    fun updateProduct(product: Product)

    @Delete
    fun removeProduct(product: Product)

    @Query("DELETE FROM products WHERE id=:id")
    fun removeProductById(id: Int)

    @Delete
    fun removeProducts(vararg product: Product)

    @Query("SELECT * FROM products")
    fun getProducts(): List<Product>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: Int): Product
}