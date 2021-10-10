package org.shop.thewarehouse.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.fragment_login_email.*
import org.shop.thewarehouse.R
import org.shop.thewarehouse.databinding.ActivityCameraBinding
import org.shop.thewarehouse.ui.register.Register
import java.io.File
import java.util.concurrent.Executors

const val PHOTO = "org.shop.thewarehouse"
class CameraActivity : AppCompatActivity() {

    companion object{
        val PERMISSION_ID = 34
        val FILE_NAME = "${System.currentTimeMillis()}"
    }

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var binding: ActivityCameraBinding
    lateinit var photoFile: File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startCamera()
    }



    private fun startCamera() {

        binding.apply {
            btnNext.visibility = View.INVISIBLE
            btnTakePhoto.setOnClickListener {
                val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                photoFile = getPhotoFile(FILE_NAME)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFile)

                val fileProvider = FileProvider.getUriForFile(applicationContext, "org.shop.thewarehouse", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

                if (takePictureIntent.resolveActivity(packageManager) != null){
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
                val intent = Intent(applicationContext, Register::class.java).apply {
                    putExtras(bundle)
                }
                startActivity(intent)
                overridePendingTransition(R.transition.translate_left_side, R.transition.translate_left_out)

            }

        }

    }


    private fun getPhotoFile(fileName: String): File {
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, ".jpg",storageDirectory)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == PERMISSION_ID && resultCode == Activity.RESULT_OK){
            val takenImage = BitmapFactory.decodeFile(photoFile.absolutePath)
                binding.shapeableImageView.setImageBitmap(takenImage)
            Toast.makeText(applicationContext, photoFile.absolutePath, Toast.LENGTH_LONG).show()
            Log.d("PATH", photoFile.absolutePath)
        } else {
            super.onActivityResult(requestCode, resultCode, data)

        }
    }



}