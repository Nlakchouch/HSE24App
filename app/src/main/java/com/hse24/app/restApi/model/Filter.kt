package com.hse24.app.restApi.model

import com.google.gson.annotations.SerializedName

data class Filter (

    @SerializedName("selectedItemCount")
    var selectedItemCount : Int,

    @SerializedName("filterGroups")
    var filterGroups: List<FilterGroup> = ArrayList() )