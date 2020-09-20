package com.hse24.app.rest.model

import com.google.gson.annotations.SerializedName

data class Variation (
    @SerializedName("sku")
    var sku: String,

    @SerializedName("status")
    var status: String? = "",

    @SerializedName("picCount")
    var picCount: Int,

    @SerializedName("imageUris")
    var imageUris: List<String> = ArrayList(),

    @SerializedName("productPrice")
    var productPrice: ProductPrice,

    @SerializedName("stockColor")
    var stockColor: String,

    @SerializedName("stockAmount")
    var stockAmount: Int
)