package com.hse24.app.restApi.model

import com.google.gson.annotations.SerializedName

data class Product (
    @SerializedName("sku")
    var sku: String,

    @SerializedName("priceValidToTimestamp")
    var priceValidToTimestamp: String,

    @SerializedName("nameShort")
    var nameShort: String,

    @SerializedName("status")
    var status: String,

    @SerializedName("brandNameLong")
    var brandNameLong: String,

    @SerializedName("averageStars")
    var averageStars: Int,

    @SerializedName("brandId")
    var brandId: String,

    @SerializedName("priceLabel")
    var priceLabel: String,

    @SerializedName("referencePriceLabel")
    var referencePriceLabel: String,

    @SerializedName("noShippingCosts")
    var noShippingCosts: Boolean,

    @SerializedName("imageUris")
    var imageUris: List<String> = ArrayList(),

    @SerializedName("notAllowedInCountry")
    var notAllowedInCountry: Boolean,

     @SerializedName("productPrice")
     var productPrice: ProductPrice
)
