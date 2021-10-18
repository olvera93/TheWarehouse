package org.shop.thewarehouse.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import org.shop.thewarehouse.data.model.Product

@Database(entities = arrayOf(Product::class), version = 1, exportSchema = false)
abstract class ProductDB: RoomDatabase(){

    abstract fun productDao(): ProductDAO

    companion object {
        private const val DB_NAME = "warehouse_database"

        @Volatile
        private var INSTANCE: ProductDB? = null

        fun getDatabase(context: Context): ProductDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProductDB::class.java,
                    DB_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

}