package com.example.artara_id.feature.register

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.example.artara_id.R
import com.example.artara_id.databinding.ActivityRegisterBinding
import com.example.artara_id.feature.home.HomeActivity
import com.example.artara_id.feature.login.LoginActivity
import com.example.artara_id.model.User
import com.example.artara_id.model.UserNoPass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding:ActivityRegisterBinding
    private var loading: AlertDialog?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loading = showDialogLoading()
        binding.btnContinue.setOnClickListener {
            when {
                binding.ETEmail.text.toString().equals("", ignoreCase = false) -> {
                    binding.ETEmail.error = getString(R.string.requirement)
                }
                binding.ETUsername.text.toString().equals("", ignoreCase = false) -> {
                    binding.ETUsername.error = getString(R.string.requirement)
                }
                binding.ETPassword.text.toString().equals("", ignoreCase = false) -> {
                    binding.passwordContainer.helperText = getString(R.string.requirement)
                }
                binding.ETRePassword.text.toString().equals("", ignoreCase = false) -> {
                    binding.rePasswordContainer.helperText = getString(R.string.requirement)
                }
                binding.ETPassword.text.toString().length < 6 -> {
                    binding.passwordContainer.helperText = getString(R.string.password6)
                }
                !(binding.ETRePassword.text.toString().equals(binding.ETPassword.text.toString(), ignoreCase = false)) -> {
                    binding.rePasswordContainer.helperText = getString(R.string.passwordNotMatch)
                }
                else -> {
                    val user = User(
                            binding.ETEmail.text.toString(),
                            binding.ETUsername.text.toString(),
                            binding.ETPassword.text.toString()
                    )
                    isCheckRegister(user)
                }
            }
        }
        binding.TVIfHaveAccount.setOnClickListener {
            val gotoRegister = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(gotoRegister)
        }


        binding.ETEmail.setOnFocusChangeListener { _, b->
            if (b){
                binding.ETEmail.setTextColor(getColor(R.color.brown_primary))
                binding.ETEmail.doOnTextChanged { _, _, _, _ ->
                    binding.emailContainer.helperText = null
                }
            }else {
                binding.ETEmail.setTextColor(getColor(R.color.white_primary))
            }
        }

        binding.ETUsername.setOnFocusChangeListener { _, b->
            if (b){
                binding.ETUsername.setTextColor(getColor(R.color.brown_primary))
            }else {
                binding.ETUsername.setTextColor(getColor(R.color.white_primary))
            }
        }

        binding.ETRePassword.setOnFocusChangeListener { _, b->
            if (b){
                binding.rePasswordContainer.counterTextColor = getColorStateList(R.color.brown_primary)
                binding.ETRePassword.setTextColor(getColor(R.color.brown_primary))
                binding.ETRePassword.doOnTextChanged{ _, _, _, _ ->
                    binding.rePasswordContainer.helperText = null
                }
            }else {
                binding.ETRePassword.setTextColor(getColor(R.color.white_primary))
                binding.rePasswordContainer.counterTextColor = getColorStateList(R.color.white_primary)
                binding.rePasswordContainer.helperText = null
            }
        }

        binding.ETPassword.setOnFocusChangeListener { _, b->
            if (b) {
                binding.passwordContainer.counterTextColor = getColorStateList(R.color.brown_primary)
                binding.ETPassword.setTextColor(getColor(R.color.brown_primary))
                binding.ETPassword.doOnTextChanged{ _: CharSequence?, _: Int, _: Int, _: Int ->
                    binding.passwordContainer.helperText = null
                }
            }else {
                binding.passwordContainer.counterTextColor = getColorStateList(R.color.white_primary)
                binding.ETPassword.setTextColor(getColor(R.color.white_primary))
                binding.passwordContainer.helperText = null
            }
        }

    }

    private fun register(user: User) {
        onPrecess(true)
        val firebaseAuth : FirebaseAuth = Firebase.auth
        val ref : DatabaseReference = FirebaseDatabase.getInstance().getReference("user")
        val refWithoutPass : DatabaseReference = FirebaseDatabase.getInstance().getReference("userNoPass")

        firebaseAuth.createUserWithEmailAndPassword(user.email, user.password).addOnCompleteListener {
            if(it.isSuccessful){
                ref.push().setValue(user).addOnCompleteListener {
                    Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                    val currentUser =  Firebase.auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = user.username
                    }
                    currentUser!!.updateProfile(profileUpdates).addOnSuccessListener {
                        refWithoutPass.child(getID(user.email)).setValue(UserNoPass(
                                email = user.email,
                                username = user.username
                        )).addOnSuccessListener {
                            onPrecess(false)
                            val gotoHome = Intent(this@RegisterActivity, HomeActivity::class.java)
                            gotoHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(gotoHome)
                            finish()
                        }
                    }
                }.addOnFailureListener{ error->
                    onPrecess(false)
                    Toast.makeText(this@RegisterActivity, "errors : ${error.message}", Toast.LENGTH_LONG).show()
                    Log.e(TAG, "register: ${error.message}")
                }
            } else {
                onPrecess(false)
                val exception = it.exception
                if (exception != null){
                    Toast.makeText(this@RegisterActivity, "error : ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
                    if (exception.localizedMessage.equals("The email address is already in use by another account.", ignoreCase = true))
                        binding.ETEmail.error = exception.localizedMessage
                }else {
                    Toast.makeText(this@RegisterActivity, "error : Unidentified error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isCheckRegister(user: User){
        onPrecess(true)
        val refWithoutPass : DatabaseReference = FirebaseDatabase.getInstance().getReference("userNoPass")
        refWithoutPass.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var exists = false
                for (dataSnap in snapshot.children) {
                    val userNoPass = dataSnap.getValue(UserNoPass::class.java)
                    if (user.username == userNoPass!!.username) {
                        exists = true
                        break
                    }
                }
                if (exists) {
                    onPrecess(false)
                    binding.ETUsername.error = getString(R.string.usernameHasUsed)
                } else {
                    register(user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RegisterActivity, "errors : ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun onPrecess(boolean: Boolean) {
        if (boolean){
            loading?.show()
        }else {
            loading?.dismiss()
        }
    }

    private fun getID(str: String):String{
        val re = "[^A-Za-z0-9 ]".toRegex()
        return re.replace(str, "")
    }

    private fun showDialogLoading(): AlertDialog {
        val view = LayoutInflater.from(this).inflate(R.layout.item_layout, null, false)
        return AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create()
    }

}