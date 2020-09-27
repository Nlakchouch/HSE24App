package com.hse24.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

import com.hse24.app.db.AppDatabase
import com.hse24.app.db.entity.*

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository handling the work with Categories, products, Variations, and Basket items.
 */
@Singleton
class DataRepository @Inject constructor(private val database: AppDatabase) {

    private val mObservableCategories: MediatorLiveData<List<CategoryEntity>>

    companion object {
        private var sInstance: DataRepository? = null
        fun getInstance(database: AppDatabase): DataRepository? {
            if (sInstance == null) {
                synchronized(DataRepository::class.java) {
                    if (sInstance == null) {
                        sInstance = DataRepository(database)
                    }
                }
            }
            return sInstance
        }
    }

    init {
        database
        mObservableCategories = MediatorLiveData()
        mObservableCategories.addSource(database.categoryDao().loadAllCategories())
            { categoryEntities ->
            if (database.getDatabaseCreated().value != null) {
                mObservableCategories.postValue(categoryEntities)
            }
        }
    }

    /**
     * Get the list of Categories, products, Variations, and Basket items from the database and get notified when the data changes.
     */
    fun getAllCategories(): LiveData<List<CategoryEntity>>{
        return mObservableCategories
    }

    fun getCategory(categoryId: Int): LiveData<CategoryEntity> {
        return database.categoryDao().loadCategory(categoryId)
    }

    fun loadSubCategories(parentId: Int): LiveData<List<CategoryEntity>> {
        return database.categoryDao().loadSubCategories(parentId)
    }

    fun getCategoryProducts(categoryId: Int): LiveData<List<ProductEntity>> {
        return database.productDao().loadCategoryProducts(categoryId)
    }

    fun getProduct(sku: String?): LiveData<ProductEntity> {
        return database.productDao().loadProduct(sku)
    }

    fun getImageUris(sku: String?): LiveData<List<ImageUriEntity>> {
        return database.imageDao().loadImages(sku)
    }

    fun getCartByProduct(sku: String?): LiveData<CartEntity> {
        return database.cartDao().loadCartByProduct(sku)
    }

    fun getCart() : LiveData<List<CartEntity>> {
        return database.cartDao().loadCart()
    }

    fun getCartProducts() : LiveData<List<ProductEntity>> {
        return database.cartDao().loadCartProducts()
    }

    fun getCategoryCount(categoryId: Int) : LiveData<CategoryCountEntity> {
        return database.categoryCountDao().loadCategoryCount(categoryId)
    }

    fun getProductVariation(productSku: String) : LiveData<List<VariationEntity>> {
        return database.variationDao().loadProductVariations(productSku)
    }

}