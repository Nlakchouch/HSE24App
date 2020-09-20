package com.hse24.app.restApi.model

import com.google.gson.annotations.SerializedName

data class MainCategory (
    @SerializedName("categoryId")
    var categoryId: Int,

    @SerializedName("displayName")
    var displayName: String,

    @SerializedName("children")
    val children: List<Category> = ArrayList()
    )
