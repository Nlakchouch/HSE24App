package com.hse24.app.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "ProductCount", indices = [Index(value = ["categoryId"], unique = true)])
data class CategoryCountEntity(
    @PrimaryKey(autoGenerate = true) var id: Int,
    var categoryId: Int,
    var resultCount: Int
) {
    constructor(categoryId: Int, resultCount: Int): this(0, categoryId, resultCount){
        this.categoryId = categoryId
        this.resultCount = resultCount
    }
}