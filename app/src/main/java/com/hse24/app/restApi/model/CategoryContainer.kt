package com.hse24.app.restApi.model

import com.google.gson.annotations.SerializedName

data class CategoryContainer (

    @SerializedName("children")
    var children: List<MainCategory> = ArrayList()
)