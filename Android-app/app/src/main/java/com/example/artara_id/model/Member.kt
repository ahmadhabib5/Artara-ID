package com.example.artara_id.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Member(
    var url: String = "",
    var name: String = "",
    var university: String = "",
    var email: String = ""
) : Parcelable
