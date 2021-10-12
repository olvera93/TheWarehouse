package org.shop.thewarehouse.ui.register

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.shop.thewarehouse.R
import org.shop.thewarehouse.databinding.ActivityRegisterBinding
import org.shop.thewarehouse.ui.loginEmail.LoginEmail
import org.shop.thewarehouse.utils.Utility
import org.shop.thewarehouse.view.NavigationActivity
import org.shop.thewarehouse.view.PHOTO

class Register: AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth= FirebaseAuth.getInstance()

        db= FirebaseFirestore.getInstance()
        // Setup
        val bundle = intent.extras
        val email = bundle?.getString("email")

        val photo = bundle?.getString(PHOTO)




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
                            createAccount(email, password)

                        }



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
                    val intent = Intent(applicationContext, NavigationActivity::class.java)
                    intent.putExtra("email",email)
                    startActivity(intent)
                    overridePendingTransition(R.transition.translate_left_side, R.transition.translate_left_out)
                    finish()

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

}