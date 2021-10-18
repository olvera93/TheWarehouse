package org.shop.thewarehouse.ui.register

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
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


    companion object {
        const val CHANNEL_SHOP = "TheWareHouse"
        var notificationId = 0
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth= FirebaseAuth.getInstance()

        db= FirebaseFirestore.getInstance()
        // Setup
        val bundle = intent.extras

        val photo = bundle?.getString(PHOTO)

        // Para android Oreo en adelante, es obligatorio registrar el canal de noticacioón
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setNotificationChannel()
        }



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
                        if (textPassword.length() >= 6) {
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
                        } else {
                            Utility.displaySnackBar(binding.root, getString(R.string.password_error), applicationContext, R.color.red)

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
                    expandableNotification()
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
            Utility.displaySnackBar(binding.root, getString(R.string.registe_successful), this, R.color.green)
            binding.btnRegister.visibility = View.VISIBLE
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setNotificationChannel(){
        val name = getString(R.string.channel_shop)
        val descriptionText = getString(R.string.shop_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_SHOP, name, importance).apply {
            description = descriptionText
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }

    private fun expandableNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_SHOP)
            .setSmallIcon(R.drawable.ic_shopping_cart)
            .setColor(ContextCompat.getColor(this, R.color.red))
            .setContentTitle(getString(R.string.simple_title))
            .setContentText(getString(R.string.large_text))
            .setLargeIcon(getDrawable(R.mipmap.thewarehouse)?.toBitmap())
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(getString(R.string.large_text)))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this).run {
            notify(++notificationId, notification)
        }
    }

}