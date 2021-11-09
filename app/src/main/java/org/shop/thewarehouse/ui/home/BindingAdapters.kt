package org.shop.thewarehouse.ui.home

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import coil.load
import org.shop.thewarehouse.R
import android.widget.Spinner




@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl.let {
        val imgUri = imgUrl?.toUri()?.buildUpon()?.scheme("https")?.build()
        imgView.load(imgUri) {
            placeholder(R.drawable.loading_animation)
            error(R.drawable.ic_broken_image)
        }
    }
}

@BindingAdapter("android:setVal")
fun getSelectedSpinnerValue(spinner: Spinner, quantity: Int) {
    spinner.setSelection(quantity - 1, true)
}