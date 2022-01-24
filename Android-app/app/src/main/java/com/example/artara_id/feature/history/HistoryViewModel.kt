package com.example.artara_id.feature.history

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.artara_id.R
import com.example.artara_id.model.DetailCake
import com.example.artara_id.model.HistoryPlusDetail
import com.example.artara_id.model.HistoryPredict
import com.google.firebase.database.*
import java.lang.Exception

class HistoryViewModel(): ViewModel() {

    private val listHistoryPlusDetail: MutableLiveData<ArrayList<HistoryPlusDetail>> = MutableLiveData()
    private val databaseInstance = FirebaseDatabase.getInstance()

    private fun setHistory(currentUser:String, onError: OnError, onSuccess: OnSuccessHist, context: Context){
        val historyRef = databaseInstance.getReference("history")
        val listHist : ArrayList<HistoryPredict> = ArrayList()

        historyRef.child(currentUser).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.e(TAG, "onDataChange: snap : $snapshot")
                if (snapshot.value == null){
                    onError.onError(context.getString(R.string.noAvailable))
                }else {
                    snapshot.children.forEach {
                        Log.e(TAG, "onDataChange: ${it.key}")
                        listHist.add(it.getValue(HistoryPredict::class.java)!!)
                    }
                    onSuccess.onSuccess(listHist)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onError.onError(error.message)
            }
        })
    }

    private fun setDetail(hist:HistoryPredict, onError: OnError, onSuccess: OnSuccessDet){
        val detailReference = databaseInstance.getReference("Detail")
        detailReference.child(hist.classPredict).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    snapshot.getValue(DetailCake::class.java)?.let { onSuccess.onSuccess(it, hist) }
                }catch (e: Exception){
                    onError.onError(e.message.toString())
                }
            }
            override fun onCancelled(error: DatabaseError) {
                onError.onError(error.message)
            }

        })

    }

    interface OnSuccessHist{
        fun onSuccess(list:ArrayList<HistoryPredict>)
    }
    interface OnSuccessDet{
        fun onSuccess(det: DetailCake, hist: HistoryPredict)
    }
    interface OnError {
        fun onError(message:String)
    }

    fun setHistoryPlusDetail(currentUser:String, context: Context, onError: OnError){
        var listHist: ArrayList<HistoryPredict>
        val lisHistPlusDet : ArrayList<HistoryPlusDetail> = ArrayList()

        val onSuccessHist = object : OnSuccessHist{
            override fun onSuccess(list: ArrayList<HistoryPredict>) {
                listHist = list
                val onSuccessDet = object : OnSuccessDet {
                    override fun onSuccess(det: DetailCake, hist: HistoryPredict) {
                        lisHistPlusDet.add(HistoryPlusDetail(
                                key= hist.key,
                                imgUrl = hist.imgUrl,
                                scorePredict = hist.scorePredict,
                                classPredict = hist.classPredict,
                                URL = det.URL,
                                Resep = det.Resep,
                                Sumber = det.Sumber
                        ))
                        listHistoryPlusDetail.postValue(lisHistPlusDet)
                    }
                }
                for (hist in listHist){
                    setDetail(hist, onError, onSuccessDet)
                }
            }
        }
        setHistory(currentUser, onError, onSuccessHist, context)
    }

    fun getHistoryPlusDetail(): LiveData<ArrayList<HistoryPlusDetail>>{
        return listHistoryPlusDetail
    }

    interface OnProcess {
        fun onProcess(boolean: Boolean)
    }

}