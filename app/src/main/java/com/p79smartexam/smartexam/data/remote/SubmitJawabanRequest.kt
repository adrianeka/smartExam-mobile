package com.p79smartexam.smartexam.data.remote

import com.google.gson.annotations.SerializedName

data class SubmitJawabanRequest(

	@field:SerializedName("answers")
	val answers: List<AnswersItem?>? = null
)

data class AnswersItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("answer")
	val answer: String? = null
)
