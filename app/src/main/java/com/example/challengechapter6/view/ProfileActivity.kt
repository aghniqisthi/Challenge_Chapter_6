package com.example.challengechapter6.view

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.WorkInfo
import com.bumptech.glide.Glide
import com.example.challengechapter6.BlurViewModel
import com.example.challengechapter6.BlurViewModelFactory
import com.example.challengechapter6.R
import com.example.challengechapter6.databinding.ActivityProfileBinding
import com.example.challengechapter6.model.ViewModelUser
import com.example.challengechapter6.workers.KEY_IMAGE_URI
import com.example.challengechapter6.workers.KEY_PROGRESS
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import kotlin.properties.Delegates

class ProfileActivity : AppCompatActivity() {

    lateinit var binding : ActivityProfileBinding
    private var permissionRequestCount: Int = 0
    lateinit var viewModelUser : ViewModelUser
    var id by Delegates.notNull<Int>()
    lateinit var password : String
    private var imageUri:Uri? = null
    private var imageMultiPart: MultipartBody.Part? = null
    private val viewModelBlur : BlurViewModel by viewModels()

    companion object {
        const val REQUEST_CODE_IMAGE = 100 // Intent request constant for Picking an Image
        const val REQUEST_CODE_PERMISSIONS = 101 // Permission request constant for External storage access
        const val KEY_PERMISSIONS_REQUEST_COUNT = "KEY_PERMISSIONS_REQUEST_COUNT" // Bundle Constant to save the count of permission requests retried
        const val MAX_NUMBER_REQUEST_PERMISSIONS = 2 // Constant to limit the number of permission request retries
    }

    private fun checkingPermissions() {
        if (isGranted(this, Manifest.permission.CAMERA,
                arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 97,)
            ) {
            chooseImageDialog()
        } else chooseImageDialog()
    }

    private fun isGranted(activity: Activity, permission: String, permissions: Array<String>, request: Int): Boolean {
        val permissionCheck = ActivityCompat.checkSelfPermission(activity, permission)
        return if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                showPermissionDeniedDialog()
            }
            else ActivityCompat.requestPermissions(activity, permissions, request)
            false
        }
        else true
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("Permission is denied, Please allow permissions from App Settings.")
            .setPositiveButton("App Settings") {
                    _, _ -> val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .show()
    }

    private fun chooseImageDialog() {
        AlertDialog.Builder(this).setMessage("Pilih Gambar")
            .setPositiveButton("Gallery") { _, _ -> openGallery() }
            .setNegativeButton("Camera") { _, _ -> openCamera() }
            .show()
    }

    private fun openGallery() {
        intent.type = "image/*"
        galleryResult.launch("image/*")
    }

    //    camera
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraResult.launch(cameraIntent)
    }

    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                handleCameraImage(result.data)
            }
        }

    private val galleryResult = registerForActivityResult(ActivityResultContracts.GetContent()) {
            result -> viewModelBlur.setImageUri(result.toString())
        binding.imageViewProf.setImageURI(result)
    }


    private fun handleCameraImage(intent: Intent?) {
        val bitmap = intent?.extras?.get("data") as Bitmap
        binding.imageViewProf.setImageBitmap(bitmap)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* //tes firebase
        val crashButton = Button(this)
        crashButton.text = "Test Crash"
        crashButton.setOnClickListener {
            throw RuntimeException("Test Crash") // Force a crash
        }

        addContentView(crashButton, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)) */

        val viewModelUser = ViewModelProvider(this).get(ViewModelUser::class.java)

        viewModelUser.dataUser.observe(this, Observer {
            binding.editUsernameProf.setText(it.username)
            binding.editNamaProf.setText(it.name)
            binding.editAgeProf.setText(it.age.toString())
            binding.editAddressProf.setText(it.address)
            password = it.password
            id = it.id
        })

        viewModelBlur.outputWorkInfos.observe(this, Observer { workInfos ->
            if (!workInfos.isNullOrEmpty()) {
                // When WorkInfo Objects are generated
                // Pick the first WorkInfo object. There will be only one WorkInfo object
                // since the corresponding WorkRequest that was tagged is part of a unique work chain
                val workInfo = workInfos[0]

                // Check the work status
                if (workInfo.state.isFinished) {
                    // When the work is finished (i.e., SUCCEEDED / FAILED / CANCELLED),
                    // show and hide the appropriate views for the same
                    showWorkFinished()

                    // Read the final output Image URI string from the WorkInfo's Output Data
                    workInfo.outputData.let{ outputUriStr ->
                            // When we have the final Image URI
                            // Save the final Image URI string in the ViewModel
                            viewModelBlur.setOutputUri(outputUriStr.toString())
                    }
                }
                else {
                    // In other cases, show and hide the appropriate views for the same
                    showWorkInProgress()
                }
            }
        })

        viewModelBlur.progressWorkInfos.observe(this, Observer { workInfos ->
            if (!workInfos.isNullOrEmpty()) {
                // When WorkInfo Objects are generated

                // Apply for all WorkInfo Objects
                workInfos.forEach { workInfo ->
                    if (workInfo.state == WorkInfo.State.RUNNING) {
                        // When the Work is in progress,
                        // obtain the Progress Data and update the Progress to ProgressBar
                        binding.progressbar.progress = workInfo.progress.getInt(KEY_PROGRESS, 0)
                    }
                }
            }
        })

        //nampilin gambar sebelum diblur
        binding.imageViewProf.setOnClickListener {
            checkingPermissions()
        }

        //ngeblur + nampilin gambar setelah diblur
        binding.btnBlur.setOnClickListener {
            // Load the Image picked
            viewModelBlur.imageUri?.let { imageUri ->
                Glide.with(this).load(imageUri).into(binding.imageViewProf)
            }
            viewModelBlur.applyBlur(3)

            val chooseIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // Start the activity for picking an Image
            startActivityForResult(chooseIntent, REQUEST_CODE_IMAGE)
        }

        binding.btnUpdate.setOnClickListener {
            val name = binding.editNamaProf.text.toString()
            val username = binding.editUsernameProf.text.toString()
            val address = binding.editAddressProf.text.toString()
            val age = binding.editAgeProf.text.toString()

            viewModelUser.putLDUser.observe(this,{
                if (it != null){
                    Toast.makeText(this, "Edit Data Success!", Toast.LENGTH_SHORT).show()
                }
            })
            viewModelUser.callEditUser(id,name,username,age.toInt(),address, imageUri)
            //add ke datastore
            viewModelUser.editData(id, name, username, password, address, age.toInt())

            val pindah = Intent(this, HomeActivity::class.java)
            startActivity(pindah)
        }

        binding.btnLogout.setOnClickListener {
            viewModelUser.clearData()
            var pinda = Intent(this, LoginActivity::class.java)
            startActivity(pinda)
        }
    }

    private fun showWorkInProgress() {
        binding.progressbar.visibility = View.VISIBLE
    }

    private fun showWorkFinished() {
        binding.progressbar.visibility = View.GONE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the permission request count on rotation
        outState.putInt(KEY_PERMISSIONS_REQUEST_COUNT, permissionRequestCount)
    }
}