package org.shop.thewarehouse.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import org.shop.thewarehouse.R

object Utility {

    // Display SnackBar
    fun displaySnackBar(view: View, s: String, context: Context, @ColorRes colorRes: Int) {
        Snackbar.make(view, s, Snackbar.LENGTH_LONG)
            .withColor(ContextCompat.getColor(context, colorRes))
            .setTextColor(ContextCompat.getColor(context, R.color.white))
            .show()

    }

    fun View.hideKeyboard() {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(windowToken, 0)
    }


    private fun Snackbar.withColor(@ColorInt colorInt: Int): Snackbar{
        this.view.setBackgroundColor(colorInt)
        return this
    }

    fun AppCompatActivity.checkSelfPermissionCompat(permission: String) =
        ActivityCompat.checkSelfPermission(this, permission)

    fun AppCompatActivity.shouldShowRequestPermissionRationaleCompat(permission: String) =
        ActivityCompat.shouldShowRequestPermissionRationale(this, permission)

    fun AppCompatActivity.requestPermissionsCompat(
        permissionsArray: Array<String>,
        requestCode: Int
    ) {
        ActivityCompat.requestPermissions(this, permissionsArray, requestCode)
    }

    fun buildAlertDialog(context: Context, resTitle: Int, resMessage: Int): AlertDialog {
        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setTitle(context.getString(resTitle))
        alertDialog.setMessage(context.getString(resMessage))
        alertDialog.setCancelable(false)
        return alertDialog
    }


}