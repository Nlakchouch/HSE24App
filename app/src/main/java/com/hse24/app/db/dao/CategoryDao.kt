package com.hse24.app.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.hse24.app.db.entity.CategoryEntity

@Dao
interface CategoryDao {
    @Query("SELECT * FROM Categories")
    fun loadAllCategories(): LiveData<List<CategoryEntity>>

    @Query("SELECT * FROM Categories where parentId = 0")
    fun loadMainCategories(): LiveData<List<CategoryEntity>>

    @Query("SELECT * FROM Categories where parentId = :parentId")
    fun loadSubCategories(parentId: Int): LiveData<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(categories: List<CategoryEntity>)

    @Query("select * from Categories where categoryId = :categoryId")
    fun loadCategory(categoryId: Int): LiveData<CategoryEntity>

    @Query("select * from Categories where categoryId = :categoryId")
    fun loadCategorySync(categoryId: Int): CategoryEntity
}
