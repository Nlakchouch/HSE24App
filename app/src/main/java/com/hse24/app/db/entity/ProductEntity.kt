package com.hse24.app.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

import com.hse24.app.model.Product

@Entity(tableName = "Products", indices = [Index(value = ["sku"], unique = true)])
data class ProductEntity (
    @PrimaryKey
    var sku: String,
    var categoryId: Int,
    var priceValidToTimestamp: String? = "",
    var nameShort: String? = "",
    var status: String? = "",
    var brandNameLong: String? = "",
    var averageStars: Int,
    var reviewers: Int,
    var brandNameShort: String? = "",
    var brandId: String? = "",
    var additionalInformation: String,
    var priceLabel: String? = "",
    var referencePriceLabel: String? = "",
    var price :Double,
    var referencePrice :Double,
    var currency: String? = "",
    var priceDiscount :Double,
    var priceValidTo: String? = "",
    var shippingCosts :Double,
    var percentDiscount: String? = "",
    var noShippingCosts: Boolean,
    var imageUri: String? = "",
    var notAllowedInCountry: Boolean,
    var picCount :Int,
    var title: String? = "",
    var longDescription: String? = "",
    var categoryCode: String? = "",
    var ingredients: String? = "",
    var legalText: String? = "",
    var stockColor: String? = "",
    var stockAmount :Int
){

   constructor(product: Product, categoryId: Int) : this(product.sku, categoryId, product.priceValidToTimestamp, product.nameShort,
       product.status, product.brandNameLong, product.averageStars, 0, "", product.brandId,"",
       product.priceLabel, product.referencePriceLabel, product.productPrice.price, product.productPrice.referencePrice, product.productPrice.currency,
       product.productPrice.priceDiscount, product.productPrice.priceValidTo, product.productPrice.shippingCosts, product.productPrice.percentDiscount,
       product.noShippingCosts, product.imageUris[0],product.notAllowedInCountry, 0, "", "","","",
       "","", 0){
        sku = product.sku
        this.categoryId = categoryId
        priceValidToTimestamp = product.priceValidToTimestamp
        nameShort = product.nameShort
        status = product.status
        brandNameLong = product.brandNameLong
        averageStars = product.averageStars
        brandId = product.brandId
        priceLabel = product.priceLabel
        referencePriceLabel = product.referencePriceLabel
        price = product.productPrice.price
        referencePrice = product.productPrice.referencePrice
        priceValidTo = product.productPrice.priceValidTo
        currency = product.productPrice.currency
        shippingCosts = product.productPrice.shippingCosts
        percentDiscount = product.productPrice.percentDiscount
        noShippingCosts = product.noShippingCosts
        imageUri = product.imageUris.get(0)
        notAllowedInCountry = product.notAllowedInCountry
    }
}
