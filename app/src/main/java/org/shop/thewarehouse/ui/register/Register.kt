package org.shop.thewarehouse.ui.register


import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import org.shop.thewarehouse.R
import org.shop.thewarehouse.databinding.ActivityRegisterBinding
import org.shop.thewarehouse.utils.Utility
import org.shop.thewarehouse.view.NavigationActivity
import org.shop.thewarehouse.view.PHOTO
import java.util.*

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    //Obeto que obtiene la localización
    private lateinit var mFusedLocationClient: FusedLocationProviderClient


    companion object {
        const val CHANNEL_SHOP = "TheWareHouse"
        var notificationId = 0
        const val PERMISSION_ID_LOCATION = 33


    }

    private val callback = object : LocationCallback() {
        override fun onLocationAvailability(p0: LocationAvailability?) {
            super.onLocationAvailability(p0)
        }

        override fun onLocationResult(result: LocationResult?) {
            val lastLocation = result?.lastLocation
            Log.d("TAG", "onLocationResult: ${lastLocation?.longitude.toString()}")

            val email = binding.textEmail.text.toString()
            val password = binding.textPassword.text.toString()
            // Setup
            val bundle = intent.extras
            val photo = bundle?.getString(PHOTO)

            val lat = lastLocation?.latitude!!.toDouble()
            val lng = lastLocation?.longitude!!.toDouble()
            val geocoder =
                Geocoder(applicationContext, Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(lat, lng, 1)
            val address: String =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            val postalCode: String = addresses[0].postalCode
            val state: String = addresses[0].adminArea


            if (binding.textPassword.length() >= 6) {
                if (email != null) {
                    db.collection("users").document(email).set(
                        hashMapOf(
                            "usuario" to binding.textUserName.text.toString(),
                            "nombre" to binding.textUserFullName.text.toString(),
                            "apellido" to binding.textUserLastName.text.toString(),
                            "email" to email,
                            "password" to password,
                            "idPhoto" to photo,
                            "direccion" to address,
                            "codigoPostal" to postalCode,
                            "estado" to state
                        )
                    )
                    createAccount(email, password)
                }
            } else {
                Utility.displaySnackBar(
                    binding.root,
                    getString(R.string.password_error),
                    applicationContext,
                    R.color.red
                )

            }
            super.onLocationResult(result)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        db = FirebaseFirestore.getInstance()


//agregando un nuevo cliete de localización
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        onGPS()

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
                        fetchLocation()
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
                    intent.putExtra("email", email)
                    startActivity(intent)
                    overridePendingTransition(
                        R.transition.translate_left_side,
                        R.transition.translate_left_out
                    )
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
            Utility.displaySnackBar(
                binding.root,
                getString(R.string.registe_successful),
                this,
                R.color.green
            )
            binding.btnRegister.visibility = View.VISIBLE
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setNotificationChannel() {
        val name = getString(R.string.channel_shop)
        val descriptionText = getString(R.string.shop_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_SHOP, name, importance).apply {
            description = descriptionText
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(channel)
    }

    private fun expandableNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_SHOP)
            .setSmallIcon(R.drawable.ic_shopping_cart)
            .setColor(ContextCompat.getColor(this, R.color.red))
            .setContentTitle(getString(R.string.simple_title))
            .setContentText(getString(R.string.large_text))
            .setLargeIcon(getDrawable(R.mipmap.thewarehouse)?.toBitmap())
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(getString(R.string.large_text))
            )
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(this).run {
            notify(++notificationId, notification)
        }
    }

    fun onGPS() {

        Log.d("TAG", "onGPS: ${isLocationEnabled()}")

        if (!isLocationEnabled()) {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        } else {
            fetchLocation()
        }


    }

    private fun fetchLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSION_ID_LOCATION
                )
                return
            } else {
                requestLocation()
            }


        }
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        Log.d("TAG", "requestLocation: ")
        val requestLocation = LocationRequest()
        requestLocation.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        requestLocation.interval = 0
        requestLocation.fastestInterval = 0
        requestLocation.numUpdates = 1
        mFusedLocationClient.requestLocationUpdates(
            requestLocation, callback, Looper.myLooper()
        )

    }


    private fun isLocationEnabled(): Boolean {
        val locationManager =
            applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    /*private fun checkPermissions(): Boolean {
        if (checkGranted(Manifest.permission.ACCESS_COARSE_LOCATION) &&
            checkGranted(Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            return true
        }
        return false
    }



    //Pedir los permisos requeridos para que funcione la localización
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID_LOCATION
        )
    }

    private fun checkGranted(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

     */

}