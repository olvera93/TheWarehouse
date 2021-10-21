package org.shop.thewarehouse.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import org.shop.thewarehouse.R
import org.shop.thewarehouse.databinding.ActivityMainNavigationBinding

class NavigationActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMainNavigationBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)

        super.onCreate(savedInstanceState)

        binding = ActivityMainNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref=this?.getPreferences(Context.MODE_PRIVATE)?:return
        val isLogin=sharedPref.getString("Email","1")

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main_navigation)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_notifications, R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if(isLogin=="1") {
            var email=intent.getStringExtra("email")
            if(email!=null) {
                setText(email)
                with(sharedPref.edit()) {
                    putString("Email",email)
                    apply()
                }
            } else{
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
        else {
            setText(isLogin)
        }


    }

    private fun setText(email:String?) {
        db= FirebaseFirestore.getInstance()
        if (email != null) {
            db.collection("users").document(email).get()
                .addOnSuccessListener {
                        tasks->
                    tasks.get("email").toString()

                }
        }

    }
}