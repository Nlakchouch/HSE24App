package com.hse24.app.viewmodel

import android.app.Application
import androidx.arch.core.util.Function
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import com.hse24.app.DataRepository
import com.hse24.app.HSE24App
import com.hse24.app.db.entity.CategoryCountEntity
import com.hse24.app.db.entity.CategoryEntity
import com.hse24.app.db.entity.ProductEntity


class CatalogueViewModel(application: Application, private val mSavedStateHandler: SavedStateHandle) : AndroidViewModel(application) {

    private val mRepository: DataRepository = (application as HSE24App).getRepository()!!
    private var mObservableProducts: LiveData<List<ProductEntity>>
    private val mObservableCategory: LiveData<CategoryEntity>
    private val mObservableCategoryCount: LiveData<CategoryCountEntity>

    companion object {
        private const val QUERY_KEY = "QUERY"
    }

    init {

        mObservableProducts = Transformations.switchMap(mSavedStateHandler.getLiveData("QUERY", 0),
            Function<Int?, LiveData<List<ProductEntity>>> { categoryId: Int? ->
                mRepository.getCategoryProducts(categoryId!!)
            })

        mObservableCategory = Transformations.switchMap(mSavedStateHandler.getLiveData("QUERY", 0),
        Function<Int?, LiveData<CategoryEntity>> { categoryId: Int? ->
            mRepository.getCategory(categoryId!!)
        })

        mObservableCategoryCount = Transformations.switchMap(mSavedStateHandler.getLiveData("QUERY", 0),
            Function<Int?, LiveData<CategoryCountEntity>> { categoryId: Int? ->
                mRepository.getCategoryCount(categoryId!!)
            })

    }

    fun setQuery(categoryId: Int) {
        mSavedStateHandler.set(QUERY_KEY, categoryId)
    }

    /**
     * Expose the LiveData Products query so the UI can observe it.
     */
    fun getProducts(): LiveData<List<ProductEntity>> { return  mObservableProducts }

    fun getCategory(): LiveData<CategoryEntity> { return mObservableCategory }

    fun getCategoryCount(): LiveData<CategoryCountEntity> { return mObservableCategoryCount}
}
