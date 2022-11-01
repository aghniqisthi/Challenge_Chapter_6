package com.example.challengechapter6.view

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.example.challengechapter6.R
import com.example.challengechapter6.databinding.ActivityLoginBinding
import com.example.challengechapter6.model.ResponseDataUserItem
import com.example.challengechapter6.model.ViewModelUser
import com.example.challengechapter6.network.RetrofitClientUser
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    val Req_Code:Int=123
    val firebaseAuth= FirebaseAuth.getInstance()

    lateinit var binding: ActivityLoginBinding
    lateinit var viewModelUser : ViewModelUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLoginGoogle.setOnClickListener {
            FirebaseApp.initializeApp(this)

            // Configure Google Sign In
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

            // getting the value of gso inside the GoogleSigninClient
            mGoogleSignInClient= GoogleSignIn.getClient(this,gso)
            signInGoogle()
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

    // signInGoogle() function
    private  fun signInGoogle(){
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent,Req_Code)
    }

    // onActivityResult() function : this is where we provide the task and data for the Google Account
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==Req_Code){
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
//            firebaseAuthWithGoogle(account!!)
        }
    }

    // handleResult() function -  this is where we update the UI after Google signin takes place
    private fun handleResult(completedTask: Task<GoogleSignInAccount>){
        try {
            val account: GoogleSignInAccount? =completedTask.getResult(ApiException::class.java)
            if (account != null) {
                UpdateUI(account)
            }
        } catch (e: ApiException){
            Toast.makeText(this,e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // UpdateUI() function - this is where we specify what UI updation are needed after google signin has taken place.
    private fun UpdateUI(account: GoogleSignInAccount){
        val credential= GoogleAuthProvider.getCredential(account.idToken,null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {task->
            if(task.isSuccessful) {
                viewModelUser.liveDataUser.observe(this, {
                    viewModelUser.editData(it.lastIndex.plus(1), account.email!!, account.displayName!!, account.displayName!!, "", 0)
                })
                toast("login")
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish()
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