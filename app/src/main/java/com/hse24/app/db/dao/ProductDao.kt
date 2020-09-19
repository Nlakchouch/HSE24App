package com.hse24.app.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.hse24.app.db.entity.ProductEntity

@Dao
interface ProductDao {
    @Query("SELECT * FROM Products where categoryId = :categoryId")
    fun loadCategoryProducts(categoryId: Int): LiveData<List<ProductEntity>>

    @Query("SELECT * FROM Products where sku = :sku")
    fun loadProduct(sku: String?): LiveData<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(products: List<ProductEntity>)

    @Query("UPDATE Products SET picCount=:picCount, title=:title, longDescription=:longDescription, reviewers=:reviewers, brandNameShort=:brandNameShort, additionalInformation=:additionalInformation, stockAmount=:stockAmount WHERE sku =:sku")
    fun updateProduct(
        picCount: Int,
        title: String?,
        longDescription: String?,
        reviewers: Int,
        brandNameShort: String?,
        additionalInformation: String?,
        stockAmount: Int,
        sku: String?
    )
}