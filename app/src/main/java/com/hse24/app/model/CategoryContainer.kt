package com.hse24.app.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class CategoryContainer (

    @SerializedName("children")
    var children: List<MainCategory> = ArrayList()
)