package com.p79smartexam.smartexam.api

import com.google.gson.annotations.SerializedName

data class SubmitJawabanResponse(

	@field:SerializedName("success")
	val success: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)
