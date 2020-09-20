package com.hse24.app.rest.model

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(
    "sku",
    "status",
    "picCount",
    "imageUris",
    "title",
    "nameShort",
    "longDescription",
    "usps",
    "averageStars",
    "reviewers",
    "productPrice",
    "variations",
    "brandId",
    "reviewsForbidden",
    "brandNameShort",
    "brandNameLong",
    "additionalInformation",
    "categoryCode",
    "ingredients",
    "legalText",
    "stockColor",
    "stockAmount"
)
data class ProductContainer (
    @get:JsonProperty("sku")
    @set:JsonProperty("sku")
    @JsonProperty("sku")
    var sku: String,

    @get:JsonProperty("status")
    @set:JsonProperty("status")
    @JsonProperty("status")
    var status: String? = null,

    @get:JsonProperty("picCount")
    @set:JsonProperty("picCount")
    @JsonProperty("picCount")
    var picCount: Int,

    @get:JsonProperty("imageUris")
    @set:JsonProperty("imageUris")
    @JsonProperty("imageUris")
    var imageUris: List<String> = ArrayList(),

    @get:JsonProperty("title")
    @set:JsonProperty("title")
    @JsonProperty("title")
    var title: String,

    @get:JsonProperty("nameShort")
    @set:JsonProperty("nameShort")
    @JsonProperty("nameShort")
    var nameShort: String,

    @get:JsonProperty("longDescription")
    @set:JsonProperty("longDescription")
    @JsonProperty("longDescription")
    var longDescription: String,

    @get:JsonProperty("usps")
    @set:JsonProperty("usps")
    @JsonProperty("usps")
    var usps: List<String> = ArrayList(),

    @get:JsonProperty("averageStars")
    @set:JsonProperty("averageStars")
    @JsonProperty("averageStars")
    var averageStars: Int,

    @get:JsonProperty("reviewers")
    @set:JsonProperty("reviewers")
    @JsonProperty("reviewers")
    var reviewers: Int,

    @get:JsonProperty("productPrice")
    @set:JsonProperty("productPrice")
    @JsonProperty("productPrice")
    var productPrice: ProductPrice,

    @get:JsonProperty("variations")
    @set:JsonProperty("variations")
    @JsonProperty("variations")
    var variations: List<Variation> = ArrayList(),

    @get:JsonProperty("brandId")
    @set:JsonProperty("brandId")
    @JsonProperty("brandId")
    var brandId: String,

    @get:JsonProperty("reviewsForbidden")
    @set:JsonProperty("reviewsForbidden")
    @JsonProperty("reviewsForbidden")
    var reviewsForbidden: Boolean,

    @get:JsonProperty("brandNameShort")
    @set:JsonProperty("brandNameShort")
    @JsonProperty("brandNameShort")
    var brandNameShort: String,

    @get:JsonProperty("brandNameLong")
    @set:JsonProperty("brandNameLong")
    @JsonProperty("brandNameLong")
    var brandNameLong: String,

    @get:JsonProperty("additionalInformation")
    @set:JsonProperty("additionalInformation")
    @JsonProperty("additionalInformation")
    var additionalInformation: String,

    @get:JsonProperty("categoryCode")
    @set:JsonProperty("categoryCode")
    @JsonProperty("categoryCode")
    var categoryCode: String,

    @get:JsonProperty("ingredients")
    @set:JsonProperty("ingredients")
    @JsonProperty("ingredients")
    var ingredients: String,

    @get:JsonProperty("legalText")
    @set:JsonProperty("legalText")
    @JsonProperty("legalText")
    var legalText: String,

    @get:JsonProperty("stockColor")
    @set:JsonProperty("stockColor")
    @JsonProperty("stockColor")
    var stockColor: String,

    @get:JsonProperty("stockAmount")
    @set:JsonProperty("stockAmount")
    @JsonProperty("stockAmount")
    var stockAmount: Int

    )