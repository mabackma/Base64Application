package com.example.base64application.datatypes

import com.google.gson.annotations.SerializedName

data class MyPicture(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("value") var value: String? = null

)

{
    fun showTitle(): String {
        return "${title}"
    }

    override fun toString(): String {
        return "${description}"
    }
}