package com.example.instagramclone.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.instagramclone.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password:String
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar!!.hide()
        //Firebase Auth initialize
        auth = Firebase.auth

        //Current user check
        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }

        //Button listener
        binding.ButtonSignUp.setOnClickListener {
            signUp(it)
        }
        binding.ButtonLogin.setOnClickListener {
            signIn(it)
        }

    }

    private fun signIn(view: View) {
        email = binding.emailText.text.toString()
        password = binding.passwordText.text.toString()

        if(email.isEmpty() || password.isEmpty()){
            sendErrorMessage(email,password)
        }
        else{
            auth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener {
                    val intent = Intent(this@MainActivity, FeedActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {exception->
                    Toast.makeText(this@MainActivity,exception.localizedMessage,Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun signUp(view: View) {
         email = binding.emailText.text.toString()
         password = binding.passwordText.text.toString()
        if(email.isEmpty() || password.isEmpty()){
            sendErrorMessage(email,password)
        }
        else{
            //Async olmalı
            auth.createUserWithEmailAndPassword(email,password).addOnSuccessListener {
                val intent = Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()

            }.addOnFailureListener {exception->
                Toast.makeText(this@MainActivity,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun sendErrorMessage(emailCheck:String,  passwordCheck:String){
        Toast.makeText(this@MainActivity,"Enter email and password !",Toast.LENGTH_LONG).show()
        if(emailCheck.isEmpty() || !(emailCheck.contains("@"))){
            binding.emailText.setError("Invalid email")
            if(!(emailCheck.contains("@"))){
                binding.emailLayout.helperText = "Please enter a invalid email . example@domain.com "
            }
        }
        if(passwordCheck.isEmpty() || passwordCheck.length <6){
            binding.passwordText.setError("Invalid Password")
            binding.passwordLayout.helperText = "Password must be 6 digits at least"

        }
    }
}