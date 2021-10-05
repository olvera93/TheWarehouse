package org.shop.thewarehouse.ui.loginEmail

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.shop.thewarehouse.R
import org.shop.thewarehouse.databinding.FragmentLoginEmailBinding
import org.shop.thewarehouse.utils.Utility.hideKeyboard

class LoginFragment: Fragment() {


    private lateinit var _binding: FragmentLoginEmailBinding
    private val binding get() = _binding

    companion object {
        private const val TAG = "EmailPassword"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginEmailBinding.inflate(layoutInflater, container, false)

        handleClick()

        return binding.root
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

            /*
            btnRegister.setOnClickListener {
                it.hideKeyboard()

                btnLogin.visibility = View.GONE

                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                createAccount(email, password)

            }

             */

            editTextEmail.doAfterTextChanged {
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                btnLogin.isEnabled = email.isNotEmpty() && password.isNotEmpty()
              //  btnRegister.isEnabled = email.isNotEmpty() && password.isNotEmpty()

            }

            editTextPassword.doAfterTextChanged {
                val email = editTextEmail.text.toString()
                val password = editTextPassword.text.toString()

                btnLogin.isEnabled = email.isNotEmpty() && password.isNotEmpty()
                //btnRegister.isEnabled = email.isNotEmpty() && password.isNotEmpty()
            }



        }
    }

    private fun createAccount(email: String, password: String) {
        updateUI()
    }

    private fun signIn(email: String, password: String) {
        updateUI()
    }

    private fun updateUI(){
        Handler(Looper.getMainLooper()).postDelayed({
            binding.btnLogin.visibility = View.VISIBLE
        }, 2000)
    }

    private fun navigateToMain() {
        findNavController().navigate(R.id.main_action)
    }

}