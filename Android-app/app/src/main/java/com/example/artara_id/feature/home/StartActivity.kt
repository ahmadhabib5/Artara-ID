package com.example.artara_id.feature.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.artara_id.R
import com.example.artara_id.databinding.ActivityStartBinding
import com.example.artara_id.feature.login.LoginActivity

class StartActivity : AppCompatActivity() {
    private lateinit var binding : ActivityStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            val gotoLogin = Intent(this, LoginActivity::class.java)
            startActivity(gotoLogin)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }
    }
}