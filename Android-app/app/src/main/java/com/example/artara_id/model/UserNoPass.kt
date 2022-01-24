package com.example.artara_id.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserNoPass(
    var email      :String = "",
    var username   :String = "",
):Parcelable