package com.example.projectcapstones.repository

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResultSkin(
    var nameSkin: String,
    var descSkin: String,
    var urlImgMedic: String,
    var nameMedic: String,
    var suggestMedic: String,
    var descMedic: String
) : Parcelable