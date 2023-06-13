package com.example.projectcapstones.data

import android.os.Parcel
import android.os.Parcelable

data class ResultSkin(
    val imageUrl: String? = null,
    val result: String? = null,
    val accuracy: String? = null,
    val deskripsi: String? = null,
    val imgObat: String? = null,
    val namaObat: String? = null,
    val pemakaianObat: String? = null,
    val detailObat: String? = null,
    val timestamp: Long? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(imageUrl)
        parcel.writeString(result)
        parcel.writeString(accuracy)
        parcel.writeString(deskripsi)
        parcel.writeString(imgObat)
        parcel.writeString(namaObat)
        parcel.writeString(pemakaianObat)
        parcel.writeString(detailObat)
        timestamp?.let { parcel.writeLong(it) }
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