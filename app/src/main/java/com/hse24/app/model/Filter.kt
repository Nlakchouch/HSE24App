package com.hse24.app.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class Filter (

    @SerializedName("selectedItemCount")
    var selectedItemCount : Int,

    @SerializedName("filterGroups")
    var filterGroups: List<FilterGroup> = ArrayList() )