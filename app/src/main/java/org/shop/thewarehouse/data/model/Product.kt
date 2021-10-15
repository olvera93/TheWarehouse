package org.shop.thewarehouse.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "product_cart")
@Parcelize
data class Product(
    @PrimaryKey(autoGenerate = true) val localId: Int,
    val id: Int,
    val title: String,
    val price: Float,
    val description: String,
    val category: String,
    val image: String,
): Parcelable
