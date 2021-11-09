package org.shop.thewarehouse.data.repository

import androidx.lifecycle.MutableLiveData
import org.shop.thewarehouse.data.model.CartItem

import androidx.lifecycle.LiveData
import org.shop.thewarehouse.data.model.Product

class CartRepository {
    private var mutableCart = MutableLiveData<List<CartItem>>()
    private var mutableTotal = MutableLiveData<Double>()
    fun getCart(): LiveData<List<CartItem>> {
        if(mutableCart.value == null){
            initCart()
        }
        return mutableCart
    }
    fun initCart() {
        mutableCart.value = ArrayList()
        calculateCartTotal()
    }
    fun addItemToCart(product: Product): Boolean {
        if(mutableCart.value == null){
            initCart()
        }
        var cartItemList: MutableList<CartItem> = ArrayList(mutableCart.value)
        for(cartProduct in cartItemList){
            if(cartProduct.product.id == product.id){
                if(cartProduct.quantity == 5){
                        return false
                }
                val index = cartItemList.indexOf(cartProduct)
                cartProduct.quantity += 1
                cartItemList[index] = cartProduct
                mutableCart.value = cartItemList
                calculateCartTotal()
                return true
            }
        }
        var cartItem = CartItem(product,1)
        cartItemList.add(cartItem)
        mutableCart.value = cartItemList
        calculateCartTotal()
        return true
    }
    fun removeItemFromCart(cartItem: CartItem){
        if(mutableCart == null){
            return
        }
        var cartItemList: MutableList<CartItem> = ArrayList(mutableCart.value)
        cartItemList.remove(cartItem)
        mutableCart.value = cartItemList
        calculateCartTotal()
    }
    fun changeQuantity(cartItem: CartItem,quantity:Int){
        if(mutableCart==null){
            return
        }
        var cartItemList: MutableList<CartItem> = ArrayList(mutableCart.value)
        var updatedCartItem = CartItem(cartItem.product,quantity)
        cartItemList[cartItemList.indexOf(cartItem)] = updatedCartItem
        mutableCart.value = cartItemList
        calculateCartTotal()
    }
    fun getTotalprice(): LiveData<Double>{
        if(mutableTotal == null){
            mutableTotal.value = 0.0
        }
        return mutableTotal
    }
    fun calculateCartTotal(){
        if(mutableCart.value ==null){
            return
        }
        var total : Double = 0.0
        var cartItemList: MutableList<CartItem> = ArrayList(mutableCart.value)
        for(cartProduct in cartItemList){
            total+=cartProduct.product.price * cartProduct.quantity
        }
        mutableTotal.value = total

    }
}