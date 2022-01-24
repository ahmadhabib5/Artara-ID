package com.example.artara_id.feature.home

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.artara_id.R
import com.example.artara_id.databinding.ActivityHomeBinding
import com.example.artara_id.feature.about.AboutActivity
import com.example.artara_id.feature.history.HistoryActivity
import com.example.artara_id.feature.predict.PredictActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var  auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        val currentUser = auth.currentUser
        binding.TVUser.text = currentUser?.displayName
        binding.menuAbout.setOnClickListener {
            val gotoAbout = Intent(this, AboutActivity::class.java)
            startActivity(gotoAbout)
        }
        binding.menuPredict.setOnClickListener {
            val gotoPredict = Intent(this, PredictActivity::class.java)
            gotoPredict.putExtra(DISPLAY_NAME, binding.TVUser.text.toString())
            startActivity(gotoPredict)
        }

        binding.menuHistory.setOnClickListener {
            val gotoHistory = Intent(this@HomeActivity, HistoryActivity::class.java)
            gotoHistory.putExtra(DISPLAY_NAME, binding.TVUser.text.toString())
            startActivity(gotoHistory)
        }
        binding.btnLogout.setOnClickListener {
            showPopup()
        }
        binding.btnAccount.setOnClickListener {
            Toast.makeText(this@HomeActivity, "Welcome ${binding.TVUser.text}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showPopup(){
        val builder = MaterialAlertDialogBuilder(this@HomeActivity)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure ?")
        builder.setPositiveButton("Yes") { _, _ ->
            logout()
        }
        builder.setNegativeButton("Cancel", null)

        val dialog = builder.create()
        dialog.show()

        val buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        val buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonPositive.setTextColor(ContextCompat.getColor(this@HomeActivity, R.color.brown_primary))
        buttonNegative.setTextColor(ContextCompat.getColor(this@HomeActivity, R.color.brown_primary))
    }

    private fun logout(){
        Firebase.auth.signOut()
        Toast.makeText(this, "Log Out Success", Toast.LENGTH_LONG).show()
        val i = Intent(this, StartActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
        finish()
    }

    companion object {
        const val DISPLAY_NAME = "display_name"
    }
}