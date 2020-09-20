package com.hse24.app.rest.model

import com.google.gson.annotations.SerializedName

data class CategoryContainer (

    @SerializedName("children")
    var children: List<MainCategory> = ArrayList()
)