package com.hse24.app.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.hse24.app.restApi.model.Variation

@Entity(tableName = "Variations", indices = [Index(value = ["sku"], unique = true)])
data class VariationEntity (
    @PrimaryKey
    var sku: String,
    var productSku: String? = "",
    var status: String? = "",
    var picCount :Int,
    var imageUri: String? = "",
    var price :Double,
    var referencePrice :Double,
    var currency: String? = "",
    var priceDiscount :Double,
    var priceValidTo: String? = "",
    var shippingCosts :Double,
    var percentDiscount: String? = "",
    var stockColor: String? = "",
    var stockAmount :Int
){

    constructor(productSku: String, variation: Variation) : this(variation.sku, productSku, variation.status, variation.picCount,
        variation.imageUris[0], variation.productPrice.price,
        variation.productPrice.referencePrice, variation.productPrice.currency,
        variation.productPrice.priceDiscount, variation.productPrice.priceValidTo,
        variation.productPrice.shippingCosts, variation.productPrice.percentDiscount,
        variation.stockColor, variation.stockAmount){
        sku = variation.sku
        this.productSku = productSku
        status = variation.status
        picCount = variation.picCount
        imageUri = variation.imageUris[0]
        price = variation.productPrice.price
        referencePrice = variation.productPrice.referencePrice
        currency = variation.productPrice.currency
        priceDiscount = variation.productPrice.priceDiscount
        priceValidTo = variation.productPrice.priceValidTo
        shippingCosts = variation.productPrice.shippingCosts
        percentDiscount = variation.productPrice.percentDiscount
        stockColor = variation.stockColor
        stockAmount = variation.stockAmount
    }
}