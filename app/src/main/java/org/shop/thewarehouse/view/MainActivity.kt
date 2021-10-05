package org.shop.thewarehouse.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.FirebaseApp
import kotlinx.android.synthetic.main.activity_main.*
import org.shop.thewarehouse.databinding.ActivityMainBinding
import org.shop.thewarehouse.ui.loginEmail.LoginEmail
import org.shop.thewarehouse.ui.loginEmail.LoginFragment
import org.shop.thewarehouse.ui.register.Register

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this)
        handleClick()
    }


    private fun handleClick() {

        binding.apply {
            btnRegisterUser.setOnClickListener {
                val intent = Intent(applicationContext, Register::class.java)
                startActivity(intent)
            }

            btnRegisterLogin.setOnClickListener {
                val intent = Intent(applicationContext, LoginEmail::class.java)
                startActivity(intent)
            }

        }


    }
}