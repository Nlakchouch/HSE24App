package com.hse24.app.db.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.Index
import androidx.room.PrimaryKey

import com.hse24.app.model.Category

@Entity(tableName = "Categories", indices = [Index(value = ["categoryId"], unique = true)])
data class CategoryEntity (
    @PrimaryKey(autoGenerate = true) var id: Int,
    var categoryId: Int,
    var name: String,
    var parentId: Int)
{

    @Ignore
    constructor(categoryId : Int, name : String, parentId: Int) : this(0, categoryId, name, parentId ){
        this.categoryId = categoryId
        this.name = name
        this.parentId = parentId
    }

    constructor(category: Category, parentId: Int) : this(0, category.categoryId,category.displayName, parentId ){
        this.categoryId = category.categoryId
        this.name = category.displayName
        this.parentId = parentId
    }
}
