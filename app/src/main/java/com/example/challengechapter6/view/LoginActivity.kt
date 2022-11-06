package com.example.challengechapter6.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.challengechapter6.R
import com.example.challengechapter6.databinding.ActivityLoginBinding
import com.example.challengechapter6.model.ResponseDataUserItem
import com.example.challengechapter6.network.RetrofitClientUser
import com.example.challengechapter6.viewmodel.ViewModelUser
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var firebaseAuth : FirebaseAuth
    lateinit var binding: ActivityLoginBinding
    lateinit var viewModelUser : ViewModelUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.btnLoginGoogle.setOnClickListener {

            //referensi google auth:
            // https://medium.com/swlh/google-login-and-logout-in-android-with-firebase-kotlin-implementation-73cf6a5a989e
            // https://console.firebase.google.com/u/0/project/challenge6-8599a/authentication/providers?hl=id

                loginWithGoogleAcc()

        }

        binding.btnLogin.setOnClickListener {
            //data inputan user
            var inputUsername = binding.editUsernameLog.text.toString()
            var inputPass = binding.editPasswordLog.text.toString()

            if(inputUsername != null && inputPass !=null){
                viewModelUser = ViewModelProvider(this).get(ViewModelUser::class.java)
                requestLogin(inputUsername, inputPass)
            }
            else if(inputUsername == null && inputPass == null) toast("Empty Username or Password!")
        }

        binding.txtRegister.setOnClickListener{
            val pindah = Intent(this, RegisterActivity::class.java)
            startActivity(pindah)
        }

        /* binding.txtLangEn.setOnClickListener{
            setLocale("en")
        }
        binding.txtLangIdn.setOnClickListener{
            setLocale("id")
        }
        binding.txtLangKor.setOnClickListener{
            setLocale("ko")
        } */
    }

    private fun loginWithGoogleAcc() {
        val intent = mGoogleSignInClient.signInIntent
        resultLauncher.launch(intent)
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            val task : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            if (account != null){
                updateUI(account)
            }
        }catch(e : ApiException){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful){
                viewModelUser.liveDataUser.observe(this, {
                    viewModelUser.editData(account.idToken!!.toInt(), account.givenName.toString(), account.displayName.toString(), account.familyName.toString(), "", 0)
                })
                Toast.makeText(this, "Login Berhasil!!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, "Login Gagal!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun toast(mess:String){
        Toast.makeText(this, mess, Toast.LENGTH_LONG).show()
    }

    fun requestLogin(username:String, password:String){
        RetrofitClientUser.instance.getAllUser().enqueue(object : Callback<List<ResponseDataUserItem>> {
            override fun onResponse(call: Call<List<ResponseDataUserItem>>, response: Response<List<ResponseDataUserItem>>) {
                var data = false
                if(response.isSuccessful){
                    if(response.body() != null){
                        val respon = response.body()
                        for (i in 0 until respon!!.size){
                            if(respon[i].username.equals(username) && respon[i].password.equals(password)){
                                data = true

                                //add ke datastore
                                viewModelUser.editData(respon[i].id.toInt(), respon[i].name, username, password, respon[i].address, respon[i].age)

                                toast("Login Success!")
                                var pinda = Intent(this@LoginActivity, HomeActivity::class.java)
                                startActivity(pinda)
                            }
                        }
                        if(data == false) toast("Wrong Username or Password!")
                    }
                    else toast("Empty Response!")
                }
                else toast("Failed Load Data!")
            }

            override fun onFailure(call: Call<List<ResponseDataUserItem>>, t: Throwable) {

            }
        })
    }
}