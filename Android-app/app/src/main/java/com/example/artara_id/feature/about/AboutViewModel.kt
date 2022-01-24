package com.example.artara_id.feature.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.artara_id.model.Member
import com.example.artara_id.model.SupportCake
import com.google.firebase.database.*

class AboutViewModel: ViewModel() {
    private var listMember: MutableLiveData<ArrayList<Member>> = MutableLiveData()
    private var listSupportCake: MutableLiveData<ArrayList<SupportCake>> = MutableLiveData()

    fun setMember(onProcess:OnProses, onError: OnError){
        onProcess.onProses(true)
        val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference("member")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allMember: ArrayList<Member> = ArrayList()
                for (memberSnap in snapshot.children){
                    val member = memberSnap.getValue(Member::class.java)
                    if (member != null){
                        allMember.add(member)
                    }
                }
                listMember.postValue(allMember)
                onProcess.onProses(false)
            }

            override fun onCancelled(error: DatabaseError) {
                onError.onError("Error ${error.message}")
                onProcess.onProses(false)
            }

        })
    }

    fun getMember(): LiveData<ArrayList<Member>> {
        return listMember
    }

    fun setSupportCake(onProcess:OnProses, onError: OnError){
        onProcess.onProses(true)
        val ref: DatabaseReference = FirebaseDatabase.getInstance().getReference("supportCake")
        ref.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allSupportCake: ArrayList<SupportCake> = ArrayList()
                for (supportSnap in snapshot.children){
                    val support = supportSnap.getValue(SupportCake::class.java)
                    if (support != null){
                        allSupportCake.add(support)
                    }
                }
                listSupportCake.postValue(allSupportCake)
                onProcess.onProses(false)
            }

            override fun onCancelled(error: DatabaseError) {
                onError.onError("Error ${error.message}")
                onProcess.onProses(false)
            }

        })
    }

    fun getSupportCake():LiveData<ArrayList<SupportCake>>{
        return listSupportCake
    }

    interface OnError {
        fun onError(message: String)
    }

    interface OnProses{
        fun onProses(boolean: Boolean)
    }
}