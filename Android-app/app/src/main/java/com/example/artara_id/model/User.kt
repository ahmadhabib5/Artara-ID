package com.example.artara_id.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var email      :String = "",
    var username   :String = "",
    var password   :String = "",
):Parcelable
