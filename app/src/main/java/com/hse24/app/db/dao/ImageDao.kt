package com.hse24.app.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.hse24.app.db.entity.ImageUriEntity

@Dao
interface ImageDao {
    @Query("SELECT * FROM ImageUris where productSku = :productSku")
    fun loadImages(productSku: String?): LiveData<List<ImageUriEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(images: List<ImageUriEntity>)
}