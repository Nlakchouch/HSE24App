package com.hse24.app.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class CatalogueContainer (
    @SerializedName("resultCount")
    val resultCount : Int,

    @SerializedName("productResults")
    val productResults: List<Product> = ArrayList(),

    @SerializedName("filter")
    val filter : Filter,

    @SerializedName("paging")
    val paging : Paging
)
