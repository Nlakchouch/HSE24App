package com.hse24.app

/**
 * Created by LAKCHOUCH NAOUFAL on 01/09/20.
 */
object Config {
    //General URLs used by the app to load Exchange Rate Data
    public  const val HSE24_BASE_URL = "https://www.hse24.de/ext-api/app/1/"
    const val HSE24_CATEGORY_URL = HSE24_BASE_URL + "category/tree"
    const val HSE24_CATALOG_URL  = HSE24_BASE_URL + "c/**/*-"
    const val HSE24_PRODUCT_URL  = HSE24_BASE_URL + "product/"

    const val HSE24_IMAGE_BASE_URL  = "https://pic.hse24-dach.net/media/de/products/"

    //The necessary parameters to construct the Exchange Rates History URL
    const val HSE24_CATALOG_PARAM = "/%3FfilterAttribute_motive%3D"
    const val HSE24_PAGING_PARAM = "/%3FpageSize%3D24" ;
    const val HSE24_IMAGE_PARAM = "pics480.jpg" ;


    //The SharedPreferences Keys
    const val PREFERENCE_NAME = "hse24_preference"
    const val PREFERENCE_CURRENCY = "pr_currency"
    const val PREFERENCE_POSITION = "pr_position"
}