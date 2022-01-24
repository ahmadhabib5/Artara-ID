package com.example.artara_id.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Resep(
    var nomor:String = "",
    var bahan:String = ""
):Parcelable
