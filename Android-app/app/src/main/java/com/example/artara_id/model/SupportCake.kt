package com.example.artara_id.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SupportCake(
    var name:String = "",
    var url :String = ""
):Parcelable
