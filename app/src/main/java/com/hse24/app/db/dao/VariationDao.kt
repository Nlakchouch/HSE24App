package com.hse24.app.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hse24.app.db.entity.VariationEntity

@Dao
interface VariationDao {

    @Query("SELECT * FROM Variations where productSku = :productSku")
    fun loadProductVariations(productSku: String?): LiveData<List<VariationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(products: List<VariationEntity>)
}