package org.shop.thewarehouse.ui.register

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.shop.thewarehouse.R
import org.shop.thewarehouse.databinding.ActivityRegisterBinding
import org.shop.thewarehouse.ui.MainFragment
import org.shop.thewarehouse.utils.Utility
import org.shop.thewarehouse.view.CameraActivity
import org.shop.thewarehouse.view.PHOTO

class Register: AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth


    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        // Setup
        val bundle = intent.extras
        val email = bundle?.getString("email")

        val photo = bundle?.getString(PHOTO)

        // Guardado de datos
        val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.apply()


        binding.apply {

            btnRegister.setOnClickListener {


                when {
                    textUserName.text.isNullOrEmpty() -> {
                        textUserName.error = getString(R.string.empty_field)
                    }
                    textUserFullName.text.isNullOrEmpty() -> {
                        textUserFullName.error = getString(R.string.empty_field)
                    }
                    textUserLastName.text.isNullOrEmpty() -> {
                        textUserLastName.error = getString(R.string.empty_field)
                    }
                    textEmail.text.isNullOrEmpty() -> {
                        textEmail.error = getString(R.string.empty_field)
                    }
                    textPassword.text.isNullOrEmpty() -> {
                        textPassword.error = getString(R.string.empty_field)
                    }
                    else -> {
                        val email = textEmail.text.toString()
                        val password = textPassword.text.toString()
                        if (email != null) {
                            db.collection("users").document(email).set(
                                hashMapOf(
                                    "usuario" to textUserName.text.toString(),
                                    "nombre" to textUserFullName.text.toString(),
                                    "apellido" to textUserLastName.text.toString(),
                                    "email" to email,
                                    "password" to password,
                                    "idPhoto" to photo
                                )
                            )
                        }
                        createAccount(email, password)

                    }
                }




            }
        }


    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user, null)
                } else {
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    updateUI(null, task.exception)
                }
            }
    }


    private fun updateUI(user: FirebaseUser?, exception: Exception?) {
        if (exception != null) {
            binding.btnRegister.visibility = View.VISIBLE
            Utility.displaySnackBar(binding.root, exception.message.toString(), this, R.color.red)
        } else {
            Utility.displaySnackBar(binding.root, "Register was successful", this, R.color.green)
            binding.btnRegister.visibility = View.VISIBLE
        }
    }



    /*
    // Borrado de datos
    val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
    prefs.clear()
    prefs.apply

     */

}