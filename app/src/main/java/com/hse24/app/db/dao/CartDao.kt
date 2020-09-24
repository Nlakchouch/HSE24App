package com.hse24.app.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

import com.hse24.app.db.SumCart
import com.hse24.app.db.entity.CartEntity
import com.hse24.app.db.entity.ProductEntity

@Dao
interface CartDao {

    @Query("SELECT * FROM Cart")
    fun loadCart(): LiveData<List<CartEntity>>

    @Query("SELECT * FROM Cart where sku = :sku")
    fun loadCartByProduct(sku: String?): LiveData<CartEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(cartEntity: CartEntity)

    @Query("SELECT SUM(IFNULL(quantity,0)) as total FROM Cart")
    fun loadCartTotal(): LiveData<SumCart>

    @Query("SELECT * FROM Products inner join Cart on Products.sku = Cart.sku")
    fun loadCartProducts(): LiveData<List<ProductEntity>>

    @Query("DELETE FROM Cart where sku = :sku")
    fun deleteCartProduct(sku: String?)

}