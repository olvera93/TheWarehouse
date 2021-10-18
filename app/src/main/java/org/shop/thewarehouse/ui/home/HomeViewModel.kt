package org.shop.thewarehouse.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.shop.thewarehouse.data.model.Product
import org.shop.thewarehouse.data.repository.ProductRepository
import org.shop.thewarehouse.utils.NetworkFailedError

class HomeViewModel(val repository: ProductRepository): ViewModel() {

    private var _products = MutableLiveData<List<Product>>()
    val products get() = _products

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

}