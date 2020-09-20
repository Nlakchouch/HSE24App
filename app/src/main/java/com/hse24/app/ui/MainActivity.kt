package com.hse24.app.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

import com.hse24.app.AppExecutors
import com.hse24.app.R
import com.hse24.app.db.AppDatabase
import com.hse24.app.db.entity.CategoryEntity
import com.hse24.app.rest.model.Category
import com.hse24.app.rest.model.CategoryContainer
import com.hse24.app.rest.model.MainCategory
import com.hse24.app.rest.ApiClient
import com.hse24.app.rest.ApiInterface
import com.hse24.app.utils.Hse24Utils

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), CategoryListFragment.OnCategoryListener {

    companion object {
    private val TAG = MainActivity::class.simpleName.toString()
    }

    private var selectedCategoryId: Int? = 0
    private var mTwoPanel = false

    private lateinit var progressBar: ProgressBar
    private lateinit var drawer: DrawerLayout

    private var categoryFragment: Fragment? = null
    private var catalogueFragment: Fragment? = null

    private lateinit var mDatabase: AppDatabase
    @Inject
    private lateinit var appExecutors: AppExecutors

    override fun onClick(categoryId: Int) {
        selectedCategoryId = categoryId
        changeCatalogue(selectedCategoryId!!)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
       //Forcing the
        if (Hse24Utils.isTablet(this)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        appExecutors = AppExecutors()
        mDatabase    = AppDatabase.getInstance(this)!!

        progressBar = findViewById(R.id.progressBar1)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        initActionBar()

        if (findViewById<View?>(R.id.drawer_layout) != null){
            mTwoPanel = false
            drawer      = findViewById(R.id.drawer_layout)

            val toggle = ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            drawer.addDrawerListener(toggle)
            toggle.syncState()
        }else{
            mTwoPanel = true
        }

        if (savedInstanceState != null) {
            //Restore the fragment's instance
            categoryFragment = supportFragmentManager.getFragment(
                savedInstanceState,
                CategoryListFragment.TAG
            )
            catalogueFragment = supportFragmentManager.getFragment(
                savedInstanceState,
                CatalogueFragment.TAG
            )
            selectedCategoryId = savedInstanceState.getInt("category")
            Log.v("SavedInstance", "" + selectedCategoryId)
        } else {
            categoryFragment = CategoryListFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.category_container, categoryFragment!!, CategoryListFragment.TAG)
                .commitAllowingStateLoss()
            supportFragmentManager.executePendingTransactions()
            loadCategoriesData()
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        //Save the fragment's instance
        supportFragmentManager.putFragment(
            savedInstanceState,
            CategoryListFragment.TAG,
            categoryFragment!!
        )
        supportFragmentManager.putFragment(
            savedInstanceState,
            CatalogueFragment.TAG,
            catalogueFragment!!
        )
        savedInstanceState.putInt("category", selectedCategoryId!!)
    }

    private fun initActionBar() {
        val actionBar = supportActionBar ?: return
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setCustomView(R.layout.logo_view)
    }

    private fun changeCatalogue(idCategory: Int){
        if(!mTwoPanel)
        drawer.closeDrawer(GravityCompat.START)

        //Creating and adding the CurrencyListFragment to CurrencyActivity
        catalogueFragment = CatalogueFragment.newInstance(idCategory,mTwoPanel)
        supportFragmentManager.beginTransaction()
            .replace(R.id.catalogue_container, catalogueFragment!!, CatalogueFragment.TAG)
            .commitAllowingStateLoss()
        supportFragmentManager.executePendingTransactions()
    }


    private fun loadCategoriesData() {

        val apiService: ApiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val call: Call<CategoryContainer> = apiService.getHSE24Categories()
        progressBar.visibility = View.VISIBLE

        call.enqueue(object : Callback<CategoryContainer> {
            override fun onResponse(
                call: Call<CategoryContainer>,
                response: Response<CategoryContainer>
            ) {
                Log.v(TAG, "" + response.code())

                if (response.isSuccessful) {
                    progressBar.visibility = View.GONE

                    val categories: List<MainCategory> = response.body()!!.children
                    val categoryEntities: MutableList<CategoryEntity> =
                        mutableListOf()

                    val rnd = Random()
                    val randomBound = categories.size - 1
                    if (selectedCategoryId == 0)
                        selectedCategoryId = categories[rnd.nextInt(randomBound)].categoryId


                    for (i in categories.indices) {
                        val mainCategory: MainCategory = categories[i]

                        for (j in mainCategory.children.indices) {

                            val category: Category = mainCategory.children[j]
                            val categoryEntity = CategoryEntity(category, mainCategory.categoryId)
                            categoryEntities.add(categoryEntity)
                        }
                        categoryEntities.add(
                            CategoryEntity(
                                mainCategory.categoryId,
                                mainCategory.displayName,
                                0
                            )
                        )
                    }

                    appExecutors.diskIO().execute {
                        mDatabase.categoryDao().insertAll(categoryEntities)
                    }

                    changeCatalogue(selectedCategoryId!!)
                    progressBar.visibility = View.INVISIBLE

                }
            }

            override fun onFailure(call: Call<CategoryContainer>, t: Throwable) {
                // Log error here since request failed
                Log.e(TAG, t.toString())
                progressBar.visibility = View.INVISIBLE
                if (!Hse24Utils.isNetworkConnected(this@MainActivity)) {
                    val snackbar = Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.network_error_msg),
                        Snackbar.LENGTH_SHORT
                    )
                    snackbar.show()
                } else {
                    val snackbar = Snackbar.make(
                        findViewById(android.R.id.content),
                        getString(R.string.service_error_msg),
                        Snackbar.LENGTH_SHORT
                    )
                    snackbar.show()
                }
            }
        })
    }
}