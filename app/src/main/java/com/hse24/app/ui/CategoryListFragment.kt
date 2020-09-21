package com.hse24.app.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.hse24.app.adapter.CategoryAdapter
import com.hse24.app.db.entity.CategoryEntity
import com.hse24.app.adapter.model.CategoryItem
import com.hse24.app.viewmodel.CategoryListViewModel

import com.bignerdranch.expandablerecyclerview.Model.ParentObject
import com.hse24.app.R

class CategoryListFragment : Fragment() {

    companion object {
        val TAG = CategoryListFragment::class.simpleName.toString()
    }

    private var recyclerView: RecyclerView? = null
    private var categoryAdapter: CategoryAdapter? = null
    private var menuItemList: MutableList<ParentObject> = mutableListOf()
    private var mCategoryCallback: OnCategoryListener? = null

    private val onCategoryClickListener = View.OnClickListener { v ->
            val idCategory: Int = (v.getTag(R.id.tag_content) as CategoryItem).id
            Log.v("categorySelected", "" + idCategory)
            categoryAdapter!!.setSelection(idCategory)
            mCategoryCallback!!.onClick(idCategory)
        }

    interface OnCategoryListener {
        fun onClick(categoryId: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mCategoryCallback = if (context is OnCategoryListener) {
            context
        } else {
            throw RuntimeException(
                context.toString()
                        + " must implement OnCategoryListener"
            )
        }
    }

    override fun onDetach() {
        mCategoryCallback = null
        super.onDetach()
    }

    override fun onDestroyView() {
        categoryAdapter = null
        super.onDestroyView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root: View = inflater.inflate(R.layout.fragment_category_list, container, false)
        recyclerView = root.findViewById<View>(R.id.recycler_view) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(activity)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categoryListViewModel: CategoryListViewModel = ViewModelProvider(this).get(CategoryListViewModel::class.java)
        subscribeUi(categoryListViewModel.getCategories())
    }

    private fun subscribeUi(liveData: LiveData<List<CategoryEntity>>) {
        // Update the list when the data changes
        liveData.observe(viewLifecycleOwner, Observer<List<CategoryEntity>> { myCategories: List<CategoryEntity>? ->
                if (myCategories != null) {
                    menuItemList.clear()
                    for (i in myCategories.indices) {
                        val mainCategoryEntity = myCategories[i]
                        if (mainCategoryEntity.parentId == 0) {
                            val listSubCategory: MutableList<Any> = mutableListOf()

                            myCategories.indices.forEach { j ->
                                val subcategoryEntity: CategoryEntity = myCategories[j]
                                if (subcategoryEntity.parentId == mainCategoryEntity.categoryId) {
                                    listSubCategory.add(
                                        CategoryItem(subcategoryEntity.name, subcategoryEntity.categoryId)
                                    )
                                }
                            }

                            val categoryItem = CategoryItem(mainCategoryEntity.name, mainCategoryEntity.categoryId)
                            categoryItem.childObjectList = listSubCategory
                            menuItemList.add(categoryItem)
                        }
                    }
                    Log.v(TAG, "" + menuItemList.size)
                    categoryAdapter = CategoryAdapter(activity, menuItemList, onCategoryClickListener)
                    recyclerView!!.adapter = categoryAdapter
                }
            })
    }
}