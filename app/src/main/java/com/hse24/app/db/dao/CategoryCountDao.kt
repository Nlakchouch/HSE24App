package com.hse24.app.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.hse24.app.db.entity.CategoryCountEntity

@Dao
interface CategoryCountDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRow(productCount: CategoryCountEntity)

    @Query("select * from ProductCount where categoryId = :categoryId")
    fun loadCategoryCount(categoryId: Int): LiveData<CategoryCountEntity>
}