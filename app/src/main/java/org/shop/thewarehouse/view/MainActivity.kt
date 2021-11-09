package org.shop.thewarehouse.view

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.shop.thewarehouse.R
import org.shop.thewarehouse.databinding.ActivityMainBinding
import org.shop.thewarehouse.ui.loginEmail.LoginEmail

import org.shop.thewarehouse.utils.DownloadController
import org.shop.thewarehouse.utils.Utility
import org.shop.thewarehouse.utils.Utility.buildAlertDialog
import org.shop.thewarehouse.utils.Utility.checkSelfPermissionCompat
import org.shop.thewarehouse.utils.Utility.requestPermissionsCompat
import org.shop.thewarehouse.utils.Utility.shouldShowRequestPermissionRationaleCompat
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Variables
    private lateinit var auth: FirebaseAuth
    private lateinit var downloadController: DownloadController

    private lateinit var googleSignInClient: GoogleSignInClient

    companion object{
        const val PERMISSION_ID = 34
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
        const val PERMISSION_REQUEST_STORAGE = 0
        const val urlCode = "https://github.com/olvera93/TheWarehouse/raw/master/versionCode.txt"
        const val urlApp = "https://github.com/olvera93/TheWarehouse/blob/develop/app/release/app-release.apk?raw=true"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        downloadController = DownloadController(this, urlApp)
        checkUpdate()
        // Dentro de onCreate
        FirebaseApp.initializeApp(this)
        auth = Firebase.auth


        with(binding.animationView) {
            setMinAndMaxFrame(30, 60)
        }

        handleClick()

    }



    private fun handleClick() {

        binding.apply {

            btnRegisterUser.setOnClickListener {
                if (checkCameraPermission()){
                    openCamera()
                    overridePendingTransition(R.transition.translate_left_side, R.transition.translate_left_out)
                    finish()
                } else {
                    requestPermission()
                }
            }

            btnRegisterLogin.setOnClickListener {
                val intent = Intent(applicationContext, LoginEmail::class.java)
                startActivity(intent)
                overridePendingTransition(R.transition.translate_left_side, R.transition.translate_left_out)
            }

            btnGoogle.setOnClickListener {
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

                googleSignInClient = GoogleSignIn.getClient(applicationContext, gso)
                googleSignInClient.signOut()
                val signInIntent = googleSignInClient.signInIntent
                startActivityForResult(signInIntent, RC_SIGN_IN)

            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle: " + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Utility.displaySnackBar(binding.root, "Google sign in failed", this, R.color.red)
            }
        }

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user, null)
                    val intent = Intent(applicationContext, NavigationActivity::class.java)
                    intent.putExtra("email",idToken)
                    startActivity(intent)
                    overridePendingTransition(R.transition.translate_left_side, R.transition.translate_left_out)
                    finish()

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null, task.exception)
                }
            }
    }

    private fun updateUI(user: FirebaseUser?, exception: Exception?) {
        binding.btnGoogle.visibility = View.VISIBLE
        if (exception != null) {
            binding.btnGoogle.visibility = View.VISIBLE
            Utility.displaySnackBar(binding.root, exception.message.toString(), this, R.color.red)
        } else {
            Utility.displaySnackBar(binding.root, "Login was successful: ", this, R.color.green)
            binding.btnGoogle.visibility = View.VISIBLE
        }
    }



    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            }
        }
        if (requestCode == Register.PERMISSION_REQUEST_STORAGE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadController.enqueueDownload()
            } else {
                Toast.makeText(this, R.string.storage_permission_denied, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun openCamera(){
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            PERMISSION_ID
        )

    }

    private fun checkCameraPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun checkStoragePermission() {
        if (checkSelfPermissionCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        ) {
            downloadController.enqueueDownload()
        } else {
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission() {
        if (shouldShowRequestPermissionRationaleCompat(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, R.string.storage_permission_denied, Toast.LENGTH_LONG).show()
            requestPermissionsCompat(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_STORAGE
            )
        } else {
            requestPermissionsCompat(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_STORAGE
            )
        }
    }
    private fun checkUpdate(){
        Thread {
            val remoteVersionCode = readUrlFile(urlCode)
            if (remoteVersionCode !== null) {
                val localVersionCode: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageManager.getPackageInfo(packageName, 0).longVersionCode.toInt()
                } else {
                    packageManager.getPackageInfo(packageName, 0).versionCode
                }

                Log.e("TAG", "onCreate: $remoteVersionCode, $localVersionCode")
                runOnUiThread {
                    if (remoteVersionCode.toInt() > localVersionCode) {
                        val alertDialog: AlertDialog = buildAlertDialog(
                            this,
                            R.string.new_version,
                            R.string.new_version_msg
                        )
                        alertDialog.setButton(
                            DialogInterface.BUTTON_POSITIVE, getString(R.string.btn_update)
                        ) { dialog: DialogInterface, _: Int ->
                            dialog.dismiss()
                            checkStoragePermission()
                        }
                        alertDialog.setButton(
                            DialogInterface.BUTTON_NEGATIVE, getString(R.string.btn_cancel)
                        ) { dialog: DialogInterface, _: Int ->
                            dialog.dismiss()
                        }
                        alertDialog.show()
                    }
                }
            } else {
                Log.e("TAG", "onCreate: error checking version")
            }
        }.start()
    }

    private fun readUrlFile(url: String): String? {
        var data: String? = null
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            urlConnection = URL(url).openConnection() as HttpURLConnection?
            urlConnection?.connect()
            iStream = urlConnection?.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuilder()
            var line: String?
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            br.close()
        } catch (e: Exception) {
            Log.w("", "Exception while downloading url: $e")
        } finally {
            iStream?.close()
            urlConnection?.disconnect()
        }
        return data
    }



}