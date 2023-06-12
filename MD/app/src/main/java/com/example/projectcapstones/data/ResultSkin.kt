package com.example.projectcapstones.data

import android.os.Parcel
import android.os.Parcelable

data class ResultSkin(
    val result: String?,
    val accuracy: String?,
    val deskripsi: String?,
    val imgObat: String?,
    val namaObat: String?,
    val pemakaianObat: String?,
    val detailObat: String?,
    val imageUrl: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(result)
        parcel.writeString(accuracy)
        parcel.writeString(deskripsi)
        parcel.writeString(imgObat)
        parcel.writeString(namaObat)
        parcel.writeString(pemakaianObat)
        parcel.writeString(detailObat)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ResultSkin> {
        override fun createFromParcel(parcel: Parcel): ResultSkin {
            return ResultSkin(parcel)
        }

        override fun newArray(size: Int): Array<ResultSkin?> {
            return arrayOfNulls(size)
        }
    }
}