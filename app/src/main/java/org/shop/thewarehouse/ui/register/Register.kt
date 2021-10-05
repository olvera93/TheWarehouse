package org.shop.thewarehouse.ui.register

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.shop.thewarehouse.R
import org.shop.thewarehouse.databinding.ActivityMainBinding
import org.shop.thewarehouse.databinding.ActivityRegisterBinding
import org.shop.thewarehouse.databinding.FragmentLoginEmailBinding
import org.shop.thewarehouse.utils.Utility

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

        // Guardado de datos
        val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.apply()


        binding.apply {

            btnGuardar.setOnClickListener {
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                if (email != null) {
                    db.collection("users").document(email).set(
                        hashMapOf("usuario" to binding.editTextUser.text.toString(),
                        "nombre" to binding.editTextName.text.toString(),
                        "apellido" to binding.editTextLastname.text.toString(),
                        "email" to email,
                        "password" to password)
                    )

                    createAccount(email, password)
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
            binding.btnGuardar.visibility = View.VISIBLE
            Utility.displaySnackBar(binding.root, exception.message.toString(), this, R.color.red)
        } else {
            Utility.displaySnackBar(binding.root, "Register was successful", this, R.color.green)
            binding.btnGuardar.visibility = View.VISIBLE
        }
    }

    /*
    // Borrado de datos
    val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
    prefs.clear()
    prefs.apply

     */



}