package com.p79smartexam.smartexam.api

import com.google.gson.annotations.SerializedName

data class SoalResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)

data class DataItem(

	@field:SerializedName("question")
	val question: String? = null,

	@field:SerializedName("options")
	val options: Options? = null,

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("type")
	val type: String? = null
)

data class Options(

	@field:SerializedName("A")
	val a: String? = null,

	@field:SerializedName("B")
	val b: String? = null,

	@field:SerializedName("C")
	val c: String? = null,

	@field:SerializedName("D")
	val d: String? = null
)
