package org.shop.thewarehouse.ui.home

import android.view.View
import org.shop.thewarehouse.data.model.Product

interface ProductAdapterListener {
    fun onProductClicked(view: View, product: Product)
}