package com.example.artara_id.feature.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.artara_id.R
import com.example.artara_id.feature.home.HomeActivity
import com.example.artara_id.feature.home.StartActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SplashActivity : AppCompatActivity() {
    private lateinit var  auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        val handler = Handler(Looper.getMainLooper())


        if (currentUser == null){
            handler.postDelayed({
                val gotoStart = Intent(this@SplashActivity, StartActivity::class.java)
                startActivity(gotoStart)
                finish()
            }, 2000)
        }else {
            handler.postDelayed({
                val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000)
        }
    }
}