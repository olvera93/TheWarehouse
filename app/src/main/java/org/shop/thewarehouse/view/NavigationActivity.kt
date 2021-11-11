package org.shop.thewarehouse.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import es.dmoral.toasty.Toasty
import org.shop.thewarehouse.R
import org.shop.thewarehouse.data.repository.ProductRepository
import org.shop.thewarehouse.databinding.ActivityMainNavigationBinding
import org.shop.thewarehouse.ui.ShoppingApplication
import org.shop.thewarehouse.ui.home.HomeViewModel
import org.shop.thewarehouse.ui.home.HomeViewModelFactory
import shortbread.Shortcut

class NavigationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainNavigationBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        val application by lazy { this.applicationContext as ShoppingApplication }
        val repository : ProductRepository by lazy { application.productRepository }
        var HVMFactory = HomeViewModelFactory(repository)
        var homeViewModel = ViewModelProvider(this,HVMFactory)[HomeViewModel::class.java]

        binding = ActivityMainNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        homeViewModel.let{
            it.getCart().observe(this) { cartItems ->
                Log.d("cartitems: ",cartItems.size.toString())
            }
        }
        homeViewModel.message.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                if(it=="true"){
                    Toasty.success(this, R.string.productAdded, Toast.LENGTH_SHORT, true).show()
                }else{
                    Toasty.warning(this, R.string.productNotAdded, Toast.LENGTH_SHORT, true).show()
                }
            }
        })
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE) ?: return
        val isLogin = sharedPref.getString("Email", "1")

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main_navigation)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_cart ,R.id.navigation_notifications, R.id.navigation_settings
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (isLogin == "1") {
            val email = intent.getStringExtra("email")
            if (email != null) {
                setText(email)
                with(sharedPref.edit()) {
                    putString("Email", email)
                    apply()
                }
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            setText(isLogin)
        }


    }

    @Shortcut(id = "Cuenta", icon = R.drawable.ic_profile, shortLabelResName = R.string.account_shortbread.toString())
    fun account() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main_navigation)
        navController.navigate(R.id.fragment_data_profile)
    }

    @Shortcut(id = "Share App", icon = R.drawable.ic_share, shortLabelResName = R.string.shareapp_shortbread.toString())
    fun shareApp() {
        val share = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "https://github.com/olvera93/TheWarehouse.git")
            type = "text/plain"
            putExtra(Intent.EXTRA_TITLE, "The Warehouse")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }, null)
        startActivity(share)
    }

    private fun setText(email: String?) {
        db = FirebaseFirestore.getInstance()
        if (email != null) {
            db.collection("users").document(email).get()
                .addOnSuccessListener { tasks ->
                    tasks.get("email").toString()
                }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }
}