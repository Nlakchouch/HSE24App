package com.hse24.app.rest

import com.hse24.app.rest.model.CategoryContainer
import com.hse24.app.rest.model.CatalogueContainer
import com.hse24.app.rest.model.ProductContainer

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ApiInterface {

    @Headers("Accept: application/json", "appDevice: ANDROID_PHONE", "locale: de_DE")
    @GET("category/tree")
    fun getHSE24Categories(): Call<CategoryContainer>

    @Headers("Accept: application/json", "appDevice: ANDROID_PHONE", "locale: de_DE")
    @GET("c/**/*-{categoryId}")
    fun getCatalogue(@Path("categoryId") categoryId: Int): Call<CatalogueContainer>

    @Headers("Accept: application/json", "appDevice: ANDROID_PHONE", "locale: de_DE")
    @GET("c/**/*-{categoryId}/%3Fpage%3D{page}")
    fun getCataloguePaging(
        @Path("categoryId") categoryId: Int,
        @Path("page") page: Int
    ): Call<CatalogueContainer>

    @Headers("Accept: application/json", "appDevice: ANDROID_PHONE", "locale: de_DE")
    @GET("product/{productId}")
    fun getProductDetails(@Path("productId") sku: String): Call<ProductContainer>
}
