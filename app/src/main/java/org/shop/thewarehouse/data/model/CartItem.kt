package org.shop.thewarehouse.data.model

import androidx.recyclerview.widget.DiffUtil

class CartItem {
    var product: Product
    var quantity : Int = 0
    constructor(product: Product, quantity: Int) {
        this.product = product
        this.quantity = quantity
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CartItem

        if (product != other.product) return false
        if (quantity != other.quantity) return false

        return true
    }
    companion object itemCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.quantity == newItem.quantity
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }


}