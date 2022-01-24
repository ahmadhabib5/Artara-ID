package com.example.artara_id.feature.predict

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.artara_id.R
import com.example.artara_id.adapter.ResepAdapter
import com.example.artara_id.databinding.ActivityPredictBinding
import com.example.artara_id.model.PredictResult
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.android.material.appbar.AppBarLayout


class PredictActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPredictBinding
    private lateinit var startForProfileImageResult: ActivityResultLauncher<Intent>
    private var myImageBitmap: Bitmap? = null
    private var myImageUri: Uri? = null
    private lateinit var predictViewModel: PredictViewModel
    private var resPredict: PredictResult? = null
    private var loading: AlertDialog? = null
    private var currentUser = ""
    private var url = ""
    private lateinit var resepAdapter:ResepAdapter
    private lateinit var  auth: FirebaseAuth
    private var res = ""
    private val onError = object: PredictViewModel.OnError {
        override fun onError(message: String) {
            Toast.makeText(this@PredictActivity, "Error: $message", Toast.LENGTH_LONG).show()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_predict)
        binding = ActivityPredictBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loading = showDialogLoading()
        resepAdapter = ResepAdapter()
        binding.apply {
            rvResep.layoutManager = LinearLayoutManager(
                this@PredictActivity,
                LinearLayoutManager.VERTICAL,
                false)
            rvResep.adapter = resepAdapter
        }

        if(ivIsNull()){
            binding.apply {
                btnTrashUpload.setImageResource(R.drawable.camera_selector)
            }
        }


        auth = Firebase.auth
        currentUser = auth.currentUser!!.displayName.toString()



        predictViewModel = ViewModelProvider(this@PredictActivity, ViewModelProvider.NewInstanceFactory())[PredictViewModel::class.java]
        startForProfileImageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result: ActivityResult->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    myImageUri = data?.data!!
                    binding.IVFoto.setImageURI(myImageUri)
                    myImageBitmap = binding.IVFoto.drawable.toBitmap()
                    binding.btnTrashUpload.setImageResource(R.drawable.trash_selector)
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                    binding.btnTrashUpload.setImageResource(R.drawable.camera_selector)
                }
                else -> {
                    binding.btnTrashUpload.setImageResource(R.drawable.camera_selector)
                }
            }
        }

        binding.btnOpen.setOnClickListener {
            if (binding.TVResult.text.toString().equals("", ignoreCase = true)){
                Toast.makeText(this@PredictActivity, "Error, ${getString(R.string.yetMakePredict)}", Toast.LENGTH_SHORT).show()
            }else {
                try {
                    gotoUrl(url)
                    Toast.makeText(this@PredictActivity, getString(R.string.openYoutube), Toast.LENGTH_SHORT).show()
                }catch (e: Exception){
                    Toast.makeText(this@PredictActivity, "Error : ${getString(R.string.notFound)}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnTrashUpload.setOnClickListener {
            if (ivIsNull()){
                takePhoto()
            }else {
                binding.apply {
                    predictViewModel.getPredict().removeObservers(this@PredictActivity)
                    predictViewModel.getDetail().removeObservers(this@PredictActivity)
                    viewModelStore.clear()
                    LRResep.visibility = View.GONE
                    val params = collapsing.layoutParams as AppBarLayout.LayoutParams
                    params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
                    binding.collapsing.layoutParams = params
                    IVFoto.setImageResource(0)
                    TVResult.text = ""
                    binding.btnTrashUpload.setImageResource(R.drawable.camera_selector)
                    myImageBitmap = null
                    resPredict = null
                    myImageUri = null
                }
            }
        }

        binding.btnPredict.setOnClickListener {
            if (myImageBitmap == null){
                Toast.makeText(this@PredictActivity, "Error : ${getString(R.string.selectImage)}", Toast.LENGTH_SHORT).show()
            } else {
                onLoading(true)
                predictViewModel.setPredict(this@PredictActivity, myImageBitmap!!, onError)
                predictViewModel.getPredict().observe(this@PredictActivity, { predict ->
                    if (predict != null) {
                        resPredict = PredictResult(
                                classPredict = predict.classPredict.replace("\r", ""),
                                indexPredict = predict.indexPredict,
                                scorePredict = predict.scorePredict
                        )
                        onSetDetail(resPredict!!)
                        onLoading(false)
                    }
                })
            }
        }

        binding.btnSave.setOnClickListener {
            when {
                binding.TVResult.text.toString().equals("", ignoreCase = true) -> {
                    Toast.makeText(this@PredictActivity, "Error, ${getString(R.string.yetMakePredict)}", Toast.LENGTH_SHORT).show()
                }
                res.equals("Others", ignoreCase = true) -> {
                    Toast.makeText(this@PredictActivity, "Can't be saved", Toast.LENGTH_SHORT).show()
                }
                else -> {

                    onLoading(true)
                    val onSuccess = object: PredictViewModel.OnSuccess {
                        override fun onSuccess(message: String) {
                            Toast.makeText(this@PredictActivity, "Success: $message", Toast.LENGTH_LONG).show()
                        }
                    }
                    val onProcess = object: PredictViewModel.OnProses {
                        override fun onProses(boolean: Boolean) {
                            onLoading(boolean)
                        }
                    }
                    predictViewModel.savePrediction(currentUser, resPredict!!, myImageUri!!,
                        onError, onProcess, onSuccess, this@PredictActivity)
                }
            }
        }
    }

    private fun takePhoto(){
        ImagePicker.Companion.with(this)
            .crop()
            .cropSquare()
            .createIntent {
                startForProfileImageResult.launch(it)
            }
    }

    private fun ivIsNull():Boolean{
        return binding.IVFoto.drawable == null
    }

    private fun onSetDetail(resultPredict: PredictResult){
        val score: String

        if (resultPredict.classPredict.equals("Others", ignoreCase = true)){
            res = "Others"
            score = "\nI'm sorry, our system can't identify this cake"
            binding.TVResult.gravity = Gravity.CENTER
        }else {
            if (resultPredict.scorePredict <= 0.50){
                res = "Others"
                score = "\nI'm sorry, our system can't identify this cake"
                binding.TVResult.gravity = Gravity.CENTER
            }else {
                res = resPredict!!.classPredict.replace("_", " ")
                score = "\nScore  : ${resultPredict.scorePredict}"
                binding.TVResult.gravity = Gravity.START
            }
        }
        val text = "Result : $res $score"
        binding.TVResult.text = text

        predictViewModel.setDetail(resultPredict.classPredict, onError)
        predictViewModel.getDetail().observe(this@PredictActivity, {
            if (it != null) {
                if (res.equals("Others", ignoreCase=true)){
                    binding.LRResep.visibility = View.GONE
                    url = ""
                }else {
                    url = it.URL
                    val params = binding.collapsing.layoutParams as AppBarLayout.LayoutParams
                    params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL

                    binding.collapsing.layoutParams = params
                    binding.LRResep.visibility = View.VISIBLE
                    resepAdapter.change(it.Resep.split(","))

                }
            }else {
                binding.LRResep.visibility = View.GONE
                url = ""
            }
        })

    }

    private fun gotoUrl(url:String){
        val urlUri = Uri.parse(url)
        val i = Intent(Intent.ACTION_VIEW, urlUri)
        startActivity(i)
    }

    private fun showDialogLoading(): AlertDialog {
        val view = LayoutInflater.from(this).inflate(R.layout.item_layout, null, false)
        return AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()
    }
    private fun onLoading(boolean: Boolean){
        if (boolean) {
            loading?.show()
        } else {
            loading?.dismiss()
        }
    }
}