package com.hse24.app.rest.model

import com.google.gson.annotations.SerializedName

data class Filter (

    @SerializedName("selectedItemCount")
    var selectedItemCount : Int,

    @SerializedName("filterGroups")
    var filterGroups: List<FilterGroup> = ArrayList() )