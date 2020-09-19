package com.hse24.app.model

import com.google.gson.annotations.SerializedName

data class Paging (
    @SerializedName("pageSize")
    var pageSize : Int,

    @SerializedName("page")
    var page : Int,

    @SerializedName("numPages")
    var numPages : Int
)