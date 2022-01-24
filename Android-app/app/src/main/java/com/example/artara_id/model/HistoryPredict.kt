package com.example.artara_id.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryPredict(
    val key:String = "",
    val imgUrl:String = "",
    var classPredict:String = "",
    var scorePredict:Float = 0.0f,
): Parcelable
