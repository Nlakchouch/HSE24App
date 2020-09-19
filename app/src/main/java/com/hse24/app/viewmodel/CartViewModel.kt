package com.hse24.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

import com.hse24.app.HSE24App
import com.hse24.app.db.SumCart
import com.hse24.app.db.entity.CartEntity
import com.hse24.app.db.entity.ProductEntity
import com.hse24.app.DataRepository

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private var mRepository: DataRepository = (application as HSE24App).getRepository()!!
    private val mObservableCart: LiveData<List<CartEntity>>
    private val mObservableCartSum: LiveData<SumCart>
    private val mObservableCartProducts: LiveData<List<ProductEntity>>

    init {
        mObservableCart = mRepository.getCart()
        mObservableCartSum = mRepository.getCartTotal()
        mObservableCartProducts = mRepository.getCartProducts()
    }

    fun getCart(): LiveData<List<CartEntity>>{ return mObservableCart }

    fun getCartTotal(): LiveData<SumCart>{ return mObservableCartSum }

    fun getCartProducts(): LiveData<List<ProductEntity>>{ return mObservableCartProducts }
}