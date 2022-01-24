package com.example.artara_id.feature.history

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.artara_id.R
import com.example.artara_id.adapter.HistoryAdapter
import com.example.artara_id.databinding.ActivityHistoryBinding
import com.example.artara_id.databinding.HistoryItemBinding
import com.example.artara_id.feature.ingredients.IngredientsActivity
import com.example.artara_id.model.DetailCake
import com.example.artara_id.model.HistoryPlusDetail
import com.example.artara_id.model.HistoryPredict
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding : ActivityHistoryBinding
    private var historyAdapter:HistoryAdapter? = null
    private var loading: AlertDialog? = null
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var  auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loading = showDialogLoading()
        onLoading(true)
        auth = Firebase.auth
        val currentUser = auth.currentUser!!.displayName

        val onClick = object:HistoryAdapter.OnClickItem{
            override fun onClickItem(historyPlusDetail: HistoryPlusDetail) {
                val gotoIngredients = Intent(this@HistoryActivity, IngredientsActivity::class.java)
                gotoIngredients.putExtra(EXTRA_DETAIL, historyPlusDetail)
                startActivity(gotoIngredients)
            }
        }

        val onError = object: HistoryViewModel.OnError {
            override fun onError(message: String) {
                Toast.makeText(this@HistoryActivity, message, Toast.LENGTH_SHORT).show()
                onLoading(false)
                binding.include.root.visibility = View.VISIBLE
            }

        }

        historyViewModel = ViewModelProvider(this@HistoryActivity, ViewModelProvider.NewInstanceFactory())[HistoryViewModel::class.java]

        val onDelete = object: HistoryAdapter.OnDelete{
            override fun onDelete(historyPredict: HistoryPredict, position : Int) {
                historyViewModel.getHistoryPlusDetail().removeObservers(this@HistoryActivity)
                deleteHistory(historyPredict.key, currentUser!!)
            }
        }
        historyAdapter = HistoryAdapter(this@HistoryActivity, onClick, onDelete)
        binding.rvHistory.layoutManager = LinearLayoutManager(this@HistoryActivity, LinearLayoutManager.VERTICAL, false)
        binding.rvHistory.adapter = historyAdapter

        historyViewModel.setHistoryPlusDetail(currentUser!!, this@HistoryActivity, onError)
        historyViewModel.getHistoryPlusDetail().observe(this@HistoryActivity, {
            historyAdapter!!.change(it)
            onLoading(false)
        })
    }

    private fun onLoading(boolean: Boolean){
        if (boolean) {
            loading?.show()
        } else {
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

    private fun deleteHistory(id: String, currentUser: String) {
        val historyRef = FirebaseDatabase.getInstance().getReference("history")
        historyRef.child(currentUser).child(id).removeValue().addOnSuccessListener {
            Toast.makeText(this@HistoryActivity, getString(R.string.successfullyDeleted), Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this@HistoryActivity, "Error : ${it.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        val EXTRA_DETAIL = "extra_detail"
    }

}