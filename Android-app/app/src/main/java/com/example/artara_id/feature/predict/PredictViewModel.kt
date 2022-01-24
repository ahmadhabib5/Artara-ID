package com.example.artara_id.feature.predict

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import com.example.artara_id.R
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.artara_id.ml.BestModel8
import com.example.artara_id.model.DetailCake
import com.example.artara_id.model.HistoryPredict
import com.example.artara_id.model.PredictResult
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.collections.ArrayList

class PredictViewModel : ViewModel() {
    private var predictList : MutableLiveData<PredictResult> = MutableLiveData()
    private var detailResult : MutableLiveData<DetailCake> = MutableLiveData()
    private var classList: ArrayList<String> = ArrayList()
    private var historyReference = FirebaseDatabase.getInstance().getReference("history")
    private val detailReference = FirebaseDatabase.getInstance().getReference("Detail")
    private var databaseReference = FirebaseStorage.getInstance().reference


    fun setPredict(context: Context, bitmap: Bitmap, onError: OnError){
        try {
            val resize = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
            val model = BestModel8.newInstance(context)
            val input = ByteBuffer.allocateDirect(224 * 224 * 3 * 4).order(ByteOrder.nativeOrder())
            for (y in 0 until 224) {
                for (x in 0 until 224) {
                    val px = resize.getPixel(x, y)

                    val r = Color.red(px)
                    val g = Color.green(px)
                    val b = Color.blue(px)

                    val rf = (r - 127) / 255f
                    val gf = (g - 127) / 255f
                    val bf = (b - 127) / 255f

                    input.putFloat(rf)
                    input.putFloat(gf)
                    input.putFloat(bf)
                }
            }

            val inputFeature = TensorBuffer.createFixedSize(
                intArrayOf(1, 224, 224, 3),
                DataType.FLOAT32
            )
            inputFeature.loadBuffer(input)
            classList.addAll(getClasses(context))
            val outputs = model.process(inputFeature)
            val outputFeature = outputs.outputFeature0AsTensorBuffer
            val resultPredict = getArgMax(outputFeature.floatArray, classList)
            predictList.postValue(resultPredict)
            model.close()
        }catch (e: Exception){
            e.printStackTrace()
            onError.onError(context.getString(R.string.wrongHappen))
        }
    }

    fun getPredict(): LiveData<PredictResult> {
        return predictList
    }

    private fun getArgMax(floatArray: FloatArray, list:ArrayList<String>):PredictResult{
        var index = 0
        var min = 0.00000000f
        for (i in floatArray.indices){
            if (floatArray[i] > min){
                index = i
                min = floatArray[i]
            }
        }

        return PredictResult(
            classPredict = list[index],
            indexPredict = index,
            scorePredict = min
        )
    }


    private fun getClasses(context: Context): List<String>{
        val filename = "label.txt"
        val inputString = context.applicationContext.assets.open(filename).bufferedReader().use {
            it.readText()
        }
        return inputString.split("\n")
    }

    fun savePrediction(currentUser:String, predictResult: PredictResult ,uri: Uri, onError: OnError,
                       onProses: OnProses, onSuccess: OnSuccess, context: Context){
        val uriFilename = Uri.parse(uri.toString())
        val filename = File("" + uriFilename)
        val id = getID(filename.name)
        val fileRef: StorageReference = databaseReference.child("images/${filename.name}")
        fileRef.putFile(uri).addOnSuccessListener {
            fileRef.downloadUrl.addOnSuccessListener {
                val historyPredict = HistoryPredict(
                    key = id,
                    imgUrl = it.toString(),
                    classPredict = predictResult.classPredict,
                    scorePredict = predictResult.scorePredict
                )
                historyReference.child(currentUser).child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        if (snapshot.value == null){
                            historyReference.child(currentUser).child(id).setValue(historyPredict).addOnSuccessListener {
                                onSuccess.onSuccess("Upload Successfully")
                                onProses.onProses(false)
                            }.addOnFailureListener { error->
                                onError.onError(error.message.toString())
                                onProses.onProses(false)
                            }
                            onProses.onProses(false)
                        }else {
                            onError.onError(context.getString(R.string.dataAlreadyExists))
                            onProses.onProses(false)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        onError.onError(error.message)
                        onProses.onProses(false)
                    }
                })
            }
        }.addOnProgressListener {
            onProses.onProses(true)
        }.addOnFailureListener {
            onError.onError("Upload failed, Error:  ${it.localizedMessage}")
            onProses.onProses(false)
        }
    }

    fun setDetail(name: String, onError: OnError){
        detailReference.child(name).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val map: MutableMap<String, String> = mutableMapOf()
                snapshot.children.forEach {
                    map[it.key.toString()] = it.value.toString()
                }
                detailResult.postValue(
                        DetailCake(
                                Sumber = map["Sumber"].toString(),
                                URL = map["URL"].toString(),
                                Resep = map["Resep"].toString()
                        )
                )
            }

            override fun onCancelled(error: DatabaseError) {
                onError.onError(error.message)
            }

        })
    }

    fun getDetail():LiveData<DetailCake>{
        return detailResult
    }

    interface OnError {
        fun onError(message: String)
    }
    interface OnProses {
        fun onProses(boolean: Boolean)
    }
    interface OnSuccess {
        fun onSuccess(message: String)
    }

    private fun getID(str : String):String{
        val re = "[^A-Za-z0-9 ]".toRegex()
        return re.replace(str, "")
    }
}