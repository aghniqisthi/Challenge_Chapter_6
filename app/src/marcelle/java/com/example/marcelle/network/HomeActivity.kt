package com.example.marcelle.network

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.challengechapter6.R
import com.example.challengechapter6.view.CartActivity
import com.example.challengechapter6.view.ProfileActivity
import com.example.challengechapter6.viewmodel.ViewModelUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.marcelle.activity_home.*

class HomeActivity : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var viewModelUser : ViewModelUser
    lateinit var viewModelProduct: ViewModelProduct

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        viewModelUser = ViewModelProvider(this).get(ViewModelUser::class.java)
        viewModelProduct = ViewModelProvider(this).get(ViewModelProduct::class.java)
        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        showData()

        btnProfile.setOnClickListener {
            val pindah = Intent(this, ProfileActivity::class.java)
            startActivity(pindah)
        }

        btnFavorit.setOnClickListener {
            val pindah = Intent(this, CartActivity::class.java)
            startActivity(pindah)
        }
    }

    fun showData(){
        viewModelUser.dataUser.observe(this, Observer {
            txtWelcomeUser.text = it.username
        })
        viewModelProduct.getliveDataProduct().observe(this, Observer {
            if (it != null) {
                rvProductMarcelle.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                rvProductMarcelle.adapter = ProductAdapter(it)
            } else {
                Toast.makeText(this, "There is no data to show", Toast.LENGTH_SHORT).show()
            }
        })
        viewModelProduct.callApiProduct()
    }
}