package com.example.projectcapstones.response

import com.google.gson.annotations.SerializedName

data class SkinneaResponse(
    @field:SerializedName("result")
    val result: String? = null,
    @field:SerializedName("img_obat")
    val imgObat: String? = null,
    @field:SerializedName("detail_obat")
    val detailObat: String? = null,
    @field:SerializedName("accuracy")
    val accuracy: String? = null,
    @field:SerializedName("deskripsi")
    val deskripsi: String? = null,
    @field:SerializedName("nama_obat")
    val namaObat: String? = null,
    @field:SerializedName("pemakaian_obat")
    val pemakaianObat: String? = null
)
