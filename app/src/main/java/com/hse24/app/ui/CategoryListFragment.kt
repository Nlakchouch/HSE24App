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

    // Create the listener for the selected category
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

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        if(categoryAdapter != null)
           categoryAdapter!!.onSaveInstanceState(savedInstanceState);
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(categoryAdapter != null)
           categoryAdapter!!.onRestoreInstanceState(savedInstanceState);
    }

    override fun onDetach() {
        mCategoryCallback = null
        super.onDetach()
    }

    override fun onDestroyView() {
        categoryAdapter = null
        super.onDestroyView()
    }

    private fun subscribeUi(liveData: LiveData<List<CategoryEntity>>) {
        Log.v(TAG,"Creating Categories List")
        // Update the category list when the data changes
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
                    // Create and set the adapter for the RecyclerView.
                     categoryAdapter = CategoryAdapter(activity, menuItemList, onCategoryClickListener)
                     recyclerView!!.adapter = categoryAdapter
                }
            })
    }
}