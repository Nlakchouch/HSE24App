package com.hse24.app.rest.model

import com.google.gson.annotations.SerializedName

data class FilterItem (
    @SerializedName("id")
    var id : Int,

    @SerializedName("name")
    var name: String,

    @SerializedName("displayName")
    var displayName: String,

    @SerializedName("rgbCode")
    var rgbCode: String,

    @SerializedName("filterName")
    var filterName: String,

    @SerializedName("filterValue")
    var filterValue: String,

    @SerializedName("resultCount")
    var resultCount : Int
)
