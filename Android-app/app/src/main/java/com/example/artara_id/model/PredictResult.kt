package com.example.artara_id.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PredictResult(
    var classPredict:String = "",
    var indexPredict:Int = 0,
    var scorePredict:Float = 0.0f
):Parcelable
