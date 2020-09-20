package com.hse24.app.restApi.model

import com.google.gson.annotations.SerializedName

class FilterGroup (
    @SerializedName("name")
    var name: String,

    @SerializedName("fieldName")
    var fieldName: String,

    @SerializedName("filterName")
    var filterName: String,

    @SerializedName("displayName")
    var displayName: String,

    @SerializedName("displayType")
    var displayType: String,

    @SerializedName("filterItems")
    val filterItems: List<FilterItem> = ArrayList()

)
