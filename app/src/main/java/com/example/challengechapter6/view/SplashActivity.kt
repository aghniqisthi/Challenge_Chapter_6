package com.example.challengechapter6.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.challengechapter6.R
import com.example.challengechapter6.databinding.ActivitySplashBinding
import com.example.challengechapter6.model.ViewModelUser
import dagger.hilt.android.AndroidEntryPoint

class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding
    lateinit var viewModelUser : ViewModelUser
//    lateinit var userPref: UserPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this).asGif().load(R.drawable.gifsplash).into(binding.ivSplash)

//        val crashButton = Button(this)
//        crashButton.text = "Test Crash"
//        crashButton.setOnClickListener {
//            throw RuntimeException("Test Crash") // Force a crash
//        }
//
//        addContentView(crashButton, ViewGroup.LayoutParams(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT))

        //splash : seleksi data login tersimpan atau ngga.
//        userPref = UserPref(this)
        viewModelUser = ViewModelProvider(this).get(ViewModelUser::class.java)

        viewModelUser.dataUser.observe(this, Observer {
            if(it.username != "" && it.username != null){
                Handler().postDelayed({
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 5000)
            }
            else {
                Handler().postDelayed({
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }, 5000)
            }
        })
    }
}