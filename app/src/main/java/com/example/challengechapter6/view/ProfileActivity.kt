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
    lateinit var pass:String
    var id by Delegates.notNull<Int>()
    private var imageMultiPart: MultipartBody.Part? = null
    private var imageUri: Uri? = Uri.EMPTY
    private var imageFile: File? = null
    private val viewModel: BlurViewModel by viewModels { BlurViewModelFactory(application) }

    companion object {
        const val REQUEST_CODE_IMAGE = 100 // Intent request constant for Picking an Image
        const val REQUEST_CODE_PERMISSIONS = 101 // Permission request constant for External storage access
        const val KEY_PERMISSIONS_REQUEST_COUNT = "KEY_PERMISSIONS_REQUEST_COUNT" // Bundle Constant to save the count of permission requests retried
        const val MAX_NUMBER_REQUEST_PERMISSIONS = 2 // Constant to limit the number of permission request retries
    }

    // permissions required by the app to access external storage to select an image
    private val permissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private val galleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result ->
            startActivity(Intent(this, ProfileActivity::class.java).apply {
                putExtra(KEY_IMAGE_URI, result.toString())
            })
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val viewModelUser = ViewModelProvider(this).get(ViewModelUser::class.java)

        viewModelUser.dataUser.observe(this, Observer {
            binding.editUsernameProf.setText(it.username)
            binding.editNamaProf.setText(it.name)
            binding.editAgeProf.setText(it.age.toString())
            binding.editAddressProf.setText(it.address)
            id = it.id
            pass = it.password
        })

        viewModel.outputWorkInfos.observe(this, Observer { workInfos ->
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
                    workInfo.outputData.getString(KEY_IMAGE_URI)
                        .takeIf { !it.isNullOrEmpty() }?.let { outputUriStr ->
                            // When we have the final Image URI
                            // Save the final Image URI string in the ViewModel
                            viewModel.setOutputUri(outputUriStr)
                        }
                }
                else {
                    // In other cases, show and hide the appropriate views for the same
                    showWorkInProgress()
                }
            }
        })

        viewModel.progressWorkInfos.observe(this, Observer { workInfos ->
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

        binding.imageViewProf.setOnClickListener {
            // Make sure the app has correct permissions to run
            requestPermissionsIfNecessary()

            // When activity is reloaded after configuration change
            savedInstanceState?.let {
                // Restore the permission request count
                permissionRequestCount = it.getInt(KEY_PERMISSIONS_REQUEST_COUNT, 0)
            }
            intent.type = "image/*"
            galleryResult.launch("image/*")

            // Image URI should be stored in the ViewModel; put it there then display
            val imageUriExtra = intent.getStringExtra(KEY_IMAGE_URI)
            viewModel.setImageUri(imageUriExtra)

            viewModel.imageUri.let { imageUri ->
                viewModel.applyBlur(3)
                Glide.with(this).load(imageUriExtra).into(binding.imageViewProf)
                Toast.makeText(this, "imageuriexttra: $imageUriExtra", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnUpdate.setOnClickListener {
            val name = binding.editNamaProf.text.toString()
            val username = binding.editUsernameProf.text.toString()
            val passwd = pass
            val address = binding.editAddressProf.text.toString()
            val age = binding.editAgeProf.text.toString()

            viewModelUser.putLDUser.observe(this,{
                if (it != null){
                    Toast.makeText(this, "Edit Data Success!", Toast.LENGTH_SHORT).show()
                }
            })
            viewModelUser.callEditUser(id,name,username,passwd,age,address, imageMultiPart!!)

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

    private fun requestPermissionsIfNecessary() {
        // Check if all required permissions are granted
        if (!checkAllPermissions()) {
            // When all required permissions are not granted yet

            if (permissionRequestCount < MAX_NUMBER_REQUEST_PERMISSIONS) {
                // When the number of permission request retried is less than the max limit set
                permissionRequestCount += 1 // Increment the number of permission requests done
                // Request the required permissions for external storage access
                ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS)
            } else {
                // Disable the "Select Image" button when access is denied by the user
                binding.imageViewProf.isEnabled = false
            }
        }
    }

    private fun checkAllPermissions(): Boolean {
        // Boolean state to indicate all permissions are granted
        var hasPermissions = true
        // Verify all permissions are granted
        for (permission in permissions) {
            hasPermissions = hasPermissions and isPermissionGranted(permission)
        }
        // Return the state of all permissions granted
        return hasPermissions
    }

    private fun isPermissionGranted(permission: String) = ContextCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            // For External Storage access permission request
            REQUEST_CODE_PERMISSIONS -> requestPermissionsIfNecessary() // no-op if permissions are granted already.
            // For other requests, delegate to super
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save the permission request count on rotation
        outState.putInt(KEY_PERMISSIONS_REQUEST_COUNT, permissionRequestCount)
    }
}