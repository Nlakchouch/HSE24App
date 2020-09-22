package com.hse24.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations

import com.hse24.app.DataRepository
import com.hse24.app.HSE24App
import com.hse24.app.db.SumCart
import com.hse24.app.db.entity.CartEntity
import com.hse24.app.db.entity.ImageUriEntity
import com.hse24.app.db.entity.ProductEntity
import com.hse24.app.db.entity.VariationEntity

class ProductDetailViewModel(application: Application, private val mSavedStateHandler: SavedStateHandle) : AndroidViewModel(application) {

    private val mRepository: DataRepository = (application as HSE24App).getRepository()!!

    private val mObservableProduct: LiveData<ProductEntity>
    private val mObservableImageUris: LiveData<List<ImageUriEntity>>
    private val mObservableCartByProduct: LiveData<CartEntity>
    private val mObservableVariations: LiveData<List<VariationEntity>>
    private val mObservableCartSum: LiveData<SumCart>

    companion object {
        private const val QUERY_KEY = "QUERY"
    }

    init {

        mObservableProduct = Transformations.switchMap(mSavedStateHandler.getLiveData("QUERY", "")
        ) { sku: String? ->
            mRepository.getProduct(sku!!)
        }

        mObservableImageUris = Transformations.switchMap(mSavedStateHandler.getLiveData("QUERY", "")
        ) { sku: String? ->
            mRepository.getImageUris(sku!!)
        }

        mObservableCartByProduct = Transformations.switchMap(mSavedStateHandler.getLiveData("QUERY", "")
        ) { sku: String? ->
            mRepository.getCartByProduct(sku!!)
        }

        mObservableVariations = Transformations.switchMap(mSavedStateHandler.getLiveData("QUERY", "")
        ) { sku: String? ->
            mRepository.getProductVariation(sku!!)
        }

        mObservableCartSum = mRepository.getCartTotal()
    }

    fun setQuery(sku: String) {
        mSavedStateHandler.set(QUERY_KEY, sku)
    }

    /**
     * Expose the LiveData Product, its Variations and the total of items in the Cart so the UI can observe it.
     */
    fun getProductDetails(): LiveData<ProductEntity> {return  mObservableProduct }

    fun getImageUris() : LiveData<List<ImageUriEntity>> { return mObservableImageUris }

    fun getCartByProduct(): LiveData<CartEntity> { return mObservableCartByProduct}

    fun getCartTotal() : LiveData<SumCart> { return mObservableCartSum }

    fun gatVariations() : LiveData<List<VariationEntity>> { return mObservableVariations}
}
