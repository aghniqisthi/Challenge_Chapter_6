package com.example.challengechapter6.view

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.challengechapter6.BlurViewModel
import com.example.challengechapter6.BlurViewModelFactory
import com.example.challengechapter6.R
import com.example.challengechapter6.databinding.ActivityProfileBinding
import com.example.challengechapter6.model.ViewModelUser
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import kotlin.properties.Delegates

class ProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileBinding
    lateinit var viewModelUser : ViewModelUser
    lateinit var pass:String
    var id by Delegates.notNull<Int>()
    private var imageMultiPart: MultipartBody.Part? = null
    private var imageUri: Uri? = Uri.EMPTY
    private var imageFile: File? = null
    private val viewModel: BlurViewModel by viewModels { BlurViewModelFactory(application) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelUser = ViewModelProvider(this).get(ViewModelUser::class.java)

        viewModelUser.dataUser.observe(this, Observer {
            binding.editUsernameProf.setText(it.username)
            binding.editNamaProf.setText(it.name)
            binding.editAgeProf.setText(it.age.toString())
            binding.editAddressProf.setText(it.address)
            id = it.id
            pass = it.password
        })

        binding.imageViewProf.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.btnUpdate.setOnClickListener {
            val name = binding.editNamaProf.text.toString().toRequestBody("multipart/form-data".toMediaType())
            val username = binding.editUsernameProf.text.toString().toRequestBody("multipart/form-data".toMediaType())
            val passwd = pass.toRequestBody("multipart/form-data".toMediaType())
            val address = binding.editAddressProf.text.toString().toRequestBody("multipart/form-data".toMediaType())
            val age = binding.editAgeProf.text.toString().toRequestBody("multipart/form-data".toMediaType())

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

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val contentResolver: ContentResolver = this!!.contentResolver
            val type = contentResolver.getType(it)
            imageUri = it

            //set nama file image
            val fileNameimg = "${System.currentTimeMillis()}.png"

            //set image to imageview
            binding.imageViewProf.setImageURI(it)

            //toast buat munculin nama filenya
            Toast.makeText(this, "$imageUri", Toast.LENGTH_SHORT).show()

            val tempFile = File.createTempFile("and1-", fileNameimg, null)
            imageFile = tempFile

            val inputstream = contentResolver.openInputStream(uri)
            tempFile.outputStream().use    { result ->
                inputstream?.copyTo(result)
            }
            val requestBody: RequestBody = tempFile.asRequestBody(type?.toMediaType())
            imageMultiPart = MultipartBody.Part.createFormData("image", tempFile.name, requestBody)
        }
    }
}