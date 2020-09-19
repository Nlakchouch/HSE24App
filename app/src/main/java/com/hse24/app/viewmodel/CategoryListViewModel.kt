package com.hse24.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

import com.hse24.app.HSE24App
import com.hse24.app.db.entity.CategoryEntity
import com.hse24.app.DataRepository

class CategoryListViewModel(application: Application) : AndroidViewModel(application) {

    private val mObservableCategories: LiveData<List<CategoryEntity>>

    /**
     * Expose the LiveData Products query so the UI can observe it.
     */
    fun getCategories(): LiveData<List<CategoryEntity>> {
        return mObservableCategories
    }

    init {
        val mRepository: DataRepository? = (application as HSE24App).getRepository()
        mObservableCategories = mRepository!!.getAllCategories()
    }
}
