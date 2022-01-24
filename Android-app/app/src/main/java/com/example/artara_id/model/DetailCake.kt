package com.example.artara_id.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailCake(
    var Resep  :String  = "",
    var Sumber :String = "",
    var URL    :String    = ""
):Parcelable
