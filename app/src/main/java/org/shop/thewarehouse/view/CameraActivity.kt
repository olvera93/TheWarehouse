package org.shop.thewarehouse.view


import Register
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.shop.thewarehouse.R
import org.shop.thewarehouse.databinding.ActivityCameraBinding

import org.shop.thewarehouse.ui.settings.SettingsFragment
import java.io.File
import android.content.SharedPreferences




const val PHOTO = "org.shop.thewarehouse.PHOTO"
const val PATH = "org.shop.thewarehouse.PATH"

class CameraActivity : AppCompatActivity() {

    companion object {
        const val PERMISSION_ID = 35
        val FILE_NAME = "${System.currentTimeMillis()}"
        private const val TAG = "CameraActivity"
    }


    private lateinit var binding: ActivityCameraBinding
    lateinit var photoFile: File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)


        startCamera()
    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun startCamera() {

        binding.apply {
            btnNext.visibility = View.INVISIBLE
            btnTakePhoto.setOnClickListener {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                photoFile = getPhotoFile(FILE_NAME)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile)

                val fileProvider = FileProvider.getUriForFile(
                    this@CameraActivity,
                    "org.shop.thewarehouse",
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)


                @RequiresApi(Build.VERSION_CODES.R)
                if (takePictureIntent.resolveActivity(packageManager) != null) {
                    startActivityForResult(takePictureIntent, PERMISSION_ID)
                } else {
                    Toast.makeText(applicationContext, "Unable", Toast.LENGTH_SHORT).show()
                }

                btnTakePhoto.visibility = View.INVISIBLE
                btnNext.visibility = View.VISIBLE

            }

            btnNext.setOnClickListener {
                val bundle = Bundle()
                bundle.putString(PHOTO, photoFile.name)
                bundle.putString(PATH, photoFile.absolutePath)

                val intent = Intent(this@CameraActivity, Register::class.java).apply {
                    putExtras(bundle)
                }
                startActivity(intent)
                overridePendingTransition(
                    R.transition.translate_left_side,
                    R.transition.translate_left_out
                )
                finish()
            }

        }

    }


    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg", storageDirectory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == PERMISSION_ID && resultCode == Activity.RESULT_OK) {
            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
            binding.shapeableImageView.setImageBitmap(takenImage)
            Log.d("PATH", photoFile.absolutePath)
        } else {
            if (resultCode == Activity.RESULT_CANCELED) {
                try {
                    Log.e(TAG, "Not photo: " + photoFile.exists())
                } catch (e: Exception) {
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
                binding.btnNext.visibility = View.INVISIBLE
                binding.btnTakePhoto.visibility = View.VISIBLE
            }
            super.onActivityResult(requestCode, resultCode, data)

        }
    }


}