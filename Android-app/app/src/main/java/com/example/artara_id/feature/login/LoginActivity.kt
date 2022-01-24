package com.example.artara_id.feature.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.widget.doOnTextChanged
import com.example.artara_id.R
import com.example.artara_id.databinding.ActivityLoginBinding
import com.example.artara_id.feature.home.HomeActivity
import com.example.artara_id.feature.register.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var loading: AlertDialog?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loading = showDialogLoading()
        binding.btnContinue.setOnClickListener {
            when {
                binding.ETEmail.text.toString().equals("", ignoreCase = false) -> {
                    binding.ETEmail.error = getString(R.string.doNotEmpty)
                }
                binding.ETPassword.text.toString().equals("", ignoreCase = false) -> {
                    binding.PasswordContainer.helperText = getString(R.string.doNotEmpty)
                }
                else -> {
                    login(binding.ETEmail.text.toString(), binding.ETPassword.text.toString())
                }
            }
        }

        binding.TVIfDontHaveAccount.setOnClickListener {
            val gotoRegister = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(gotoRegister)
        }
        binding.ETEmail.setOnFocusChangeListener { _, b->
            if (b){
                binding.ETEmail.setTextColor(getColor(R.color.brown_primary))
            }else {
                binding.ETEmail.setTextColor(getColor(R.color.white_primary))
            }
        }

        binding.ETPassword.setOnFocusChangeListener { _, b ->
            if (b) {
                binding.PasswordContainer.counterTextColor = getColorStateList(R.color.brown_primary)
                binding.ETPassword.setTextColor(getColor(R.color.brown_primary))
                binding.ETPassword.doOnTextChanged{ _: CharSequence?, _: Int, _: Int, _: Int ->
                    binding.PasswordContainer.helperText = null
                }
            }else {
                binding.PasswordContainer.counterTextColor = getColorStateList(R.color.white_primary)
                binding.ETPassword.setTextColor(getColor(R.color.white_primary))
            }
        }
    }

    private fun login(email: String, pass: String) {
        onPrecess(true)
        val firebaseAuth: FirebaseAuth = Firebase.auth
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful){
                onPrecess(false)
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                val gotoHome = Intent(this@LoginActivity, HomeActivity::class.java)
                gotoHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(gotoHome)
                finish()
            }else {
                onPrecess(false)
                Toast.makeText(this@LoginActivity, "errors : ${it.exception!!.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun onPrecess(boolean: Boolean) {
        if (boolean){
            loading?.show()
        }else {
            loading?.dismiss()
        }
    }

    private fun showDialogLoading(): AlertDialog {
        val view = LayoutInflater.from(this).inflate(R.layout.item_layout, null, false)
        return AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create()
    }
}