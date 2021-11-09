package org.shop.thewarehouse.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shop.thewarehouse.data.model.CartItem
import org.shop.thewarehouse.data.model.Product
import org.shop.thewarehouse.data.repository.CartRepository
import org.shop.thewarehouse.data.repository.ProductRepository
import org.shop.thewarehouse.ui.detail.Event
import org.shop.thewarehouse.utils.NetworkFailedError

class HomeViewModel(val repository: ProductRepository): ViewModel() {
    private var _products = MutableLiveData<List<Product>>()
    val products get() = _products
    var cartRepo: CartRepository = CartRepository()
    private val statusMessage = MutableLiveData<Event<String>>()

    val message : LiveData<Event<String>>
        get() = statusMessage

    private var mutableProduct = MutableLiveData<Product>()
    fun getProducts(){
        viewModelScope.launch{
            try{
                withContext(Dispatchers.IO){
                    val response = repository.getProducts()
                    _products.postValue(response!!)
                }
            } catch(error: NetworkFailedError){
                //TO-DO
            } finally{
                //
            }
        }
    }
    fun setProduct(product: Product){
        mutableProduct.value = product
    }

    fun getProduct():LiveData<Product>{
        return mutableProduct
    }
    fun getCart(): LiveData<List<CartItem>> {
        return cartRepo.getCart()
    }
    fun addItemToCart(product: Product): Boolean {
        val wasAdded = cartRepo.addItemToCart(product)
        statusMessage.value = Event("$wasAdded")
        return wasAdded
    }
    fun removeItemFromCart(cartItem: CartItem){
        cartRepo.removeItemFromCart(cartItem)
    }
    fun changeQuantity(cartItem: CartItem,quantity:Int){
        cartRepo.changeQuantity(cartItem,quantity)
    }
    fun getTotalPrice():LiveData<Double>{
        return cartRepo.getTotalprice()
    }
}