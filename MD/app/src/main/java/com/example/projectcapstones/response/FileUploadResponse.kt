package com.example.projectcapstones.response

import com.google.gson.annotations.SerializedName

data class FileUploadResponse(
	@field:SerializedName("detail")
	val detail: List<DetailItem?>? = null,
	//coba tambah parsing buat dapetin result
	@SerializedName("responseBody")
	val responseBody: String? = null
)

data class DetailItem(
	@field:SerializedName("msg")
	val msg: String? = null,
	@field:SerializedName("loc")
	val loc: List<String?>? = null,
	@field:SerializedName("type")
	val type: String? = null
)