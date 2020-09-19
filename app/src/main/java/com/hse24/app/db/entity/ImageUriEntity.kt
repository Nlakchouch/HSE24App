package com.hse24.app.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "ImageUris", indices = [Index(value = ["imageUri"], unique = true)])
data class ImageUriEntity (
    @PrimaryKey(autoGenerate = true) var id: Int ,
    var productSku: String? = "",
    var imageUri: String? = ""
) {
    constructor(productSku: String, imageUri: String) : this(0, productSku, imageUri) {
        this.productSku = productSku
        this.imageUri = imageUri
    }
}