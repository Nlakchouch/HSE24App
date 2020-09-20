package com.hse24.app.rest.model

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("categoryId")
    var categoryId: Int,

    @SerializedName("displayName")
    var displayName: String
)
