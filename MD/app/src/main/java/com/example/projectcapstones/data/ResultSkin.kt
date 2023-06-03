package com.example.projectcapstones.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultSkin(
    var nameSkin: String,
    var descSkin: String,
    var urlImgMedic: String,
    var nameMedic: String,
    var suggestMedic: String,
    var descMedic: String,
    val imageUrl: String? = null,
    val timestamp: Long? = null
) : Parcelable