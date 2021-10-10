package org.shop.thewarehouse.ui.loginEmail

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import org.shop.thewarehouse.databinding.FragmentLoginEmailBinding
import org.shop.thewarehouse.utils.Utility.hideKeyboard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.shop.thewarehouse.R
import org.shop.thewarehouse.ui.MainFragment
import org.shop.thewarehouse.utils.Utility

class LoginEmail: AppCompatActivity() {

    private lateinit var binding: FragmentLoginEmailBinding

    private lateinit var auth: FirebaseAuth


    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        handleClick()
    }

    private fun handleClick() {

        binding.apply {

            btnLogin.setOnClickListener {
                it.hideKeyboard()

                btnLogin.visibility = View.GONE

                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                signIn(email, password)

                navigateToMain()

            }

            editTextEmail.doAfterTextChanged {
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                btnLogin.isEnabled = email.isNotEmpty() && password.isNotEmpty()
                //btnRegister.isEnabled = email.isNotEmpty() && password.isNotEmpty()

            }

            editTextPassword.doAfterTextChanged {
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                btnLogin.isEnabled = email.isNotEmpty() && password.isNotEmpty()
                //btnRegister.isEnabled = email.isNotEmpty() && password.isNotEmpty()
            }



        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful){
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user, null)
                } else {
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    task.exception?.let { updateUI(null, it)}
                }
            }
    }

    private fun updateUI(user: FirebaseUser?, exception: Exception?) {
        if (exception != null) {
            binding.btnLogin.visibility = View.VISIBLE
            Utility.displaySnackBar(binding.root, exception.message.toString(), this, R.color.red)
        } else {
            Utility.displaySnackBar(binding.root, "Login was successful", this, R.color.green)
            binding.btnLogin.visibility = View.VISIBLE
        }
    }

    private fun navigateToMain() {


    }



}