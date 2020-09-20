package com.hse24.app.rest.model

import com.google.gson.annotations.SerializedName

data class ProductPrice (
    @SerializedName("price")
    var price : Double,

    @SerializedName("referencePrice")
    var referencePrice : Double,

    @SerializedName("currency")
    var currency: String,

    @SerializedName("priceDiscount")
    var priceDiscount: Double,

    @SerializedName("priceValidTo")
    var priceValidTo: String,

    @SerializedName("shippingCosts")
    var shippingCosts : Double,

    @SerializedName("percentDiscount")
    var percentDiscount: String
    )