package com.hse24.app.ui

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ProgressBar

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment

import com.hse24.app.AppExecutors
import com.hse24.app.R
import com.hse24.app.db.AppDatabase
import com.hse24.app.db.entity.CategoryEntity
import com.hse24.app.restApi.model.Category
import com.hse24.app.restApi.model.CategoryContainer
import com.hse24.app.restApi.model.MainCategory
import com.hse24.app.restApi.ApiClient
import com.hse24.app.restApi.ApiInterface
import com.hse24.app.utils.AppUtils
import com.hse24.app.utils.UiUtils

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

    private var categoriesCall: Call<CategoryContainer>? = null

    private lateinit var mDatabase: AppDatabase
    @Inject
    private lateinit var appExecutors: AppExecutors

    //Adding the call for Categories selection
    override fun onClick(categoryId: Int) {
        selectedCategoryId = categoryId
        changeCatalogue(selectedCategoryId!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
       //Forcing the LANDSCAPE Orientation in case of a Tablet
        if (UiUtils.isTablet(this)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }

        appExecutors = AppExecutors()
        mDatabase    = AppDatabase.getInstance(this)!!

        progressBar = findViewById(R.id.progressBar1)

        //Setting ActionBar and its customization
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        Handler(Looper.getMainLooper()).postDelayed({
            initActionBar()
        }, 300)

        //Adding Support for Tablets
        if (findViewById<View?>(R.id.drawer_layout) != null){
            //Using Navigation Drawer for SmartPhone
            mTwoPanel = false
            drawer  = findViewById(R.id.drawer_layout)
            val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
            drawer.addDrawerListener(toggle)
            toggle.syncState()
        }else{
            //Using Master/Details flow for Tablet, Run the App on a Tablet to experience the difference
            mTwoPanel = true
        }

        if (savedInstanceState != null) {
            //Restore the fragments's instances if this is not the first creation
            categoryFragment = supportFragmentManager.getFragment(savedInstanceState, CategoryListFragment.TAG)
            catalogueFragment = supportFragmentManager.getFragment(savedInstanceState, CatalogueFragment.TAG)
            selectedCategoryId = savedInstanceState.getInt("category")
            Log.v("SavedInstance", "" + selectedCategoryId)

        } else {
            //Add categories list fragment if this is first creation
            categoryFragment = CategoryListFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.category_container, categoryFragment!!, CategoryListFragment.TAG)
                .commitAllowingStateLoss()
            supportFragmentManager.executePendingTransactions()

            //Loading Categories
            loadCategoriesData()
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        //Save the fragments's instance
        if(categoryFragment != null)
           supportFragmentManager.putFragment(savedInstanceState, CategoryListFragment.TAG, categoryFragment!!)

        if(catalogueFragment != null)
        supportFragmentManager.putFragment(savedInstanceState, CatalogueFragment.TAG, catalogueFragment!!)

        savedInstanceState.putInt("category", selectedCategoryId!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        //Cancel Retrofit Requests if it's running
        if (categoriesCall != null) {
            categoriesCall!!.cancel()
        }
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

        //Creating and adding the CatalogueFragment to MainActivity
        catalogueFragment = CatalogueFragment.newInstance(idCategory,mTwoPanel)
        supportFragmentManager.beginTransaction()
            .replace(R.id.catalogue_container, catalogueFragment!!, CatalogueFragment.TAG)
            .commitAllowingStateLoss()
        supportFragmentManager.executePendingTransactions()
    }


    private fun loadCategoriesData() {

        val apiService: ApiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        categoriesCall = apiService.getHSE24Categories()
        progressBar.visibility = View.VISIBLE

        categoriesCall!!.enqueue(object : Callback<CategoryContainer> {
            override fun onResponse(call: Call<CategoryContainer>, response: Response<CategoryContainer>) {
                Log.v(TAG, "" + response.code())
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val categories: List<MainCategory> = response.body()!!.children
                    val categoryEntities: MutableList<CategoryEntity> = mutableListOf()

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
                        categoryEntities.add(CategoryEntity(mainCategory.categoryId, mainCategory.displayName, 0))
                    }
                    appExecutors.diskIO().execute {
                        mDatabase.categoryDao().insertAll(categoryEntities)
                    }
                    changeCatalogue(selectedCategoryId!!)
                }else {
                    UiUtils.showSnackBar(this@MainActivity, getString(R.string.service_error_msg))
                }
            }

            override fun onFailure(call: Call<CategoryContainer>, t: Throwable) {
                // Log error here since request failed
                Log.e(TAG, t.toString())
                progressBar.visibility = View.INVISIBLE
                if (!AppUtils.isNetworkConnected(this@MainActivity)) {
                    UiUtils.showSnackBar(this@MainActivity, getString(R.string.network_error_msg))
                } else {
                    UiUtils.showSnackBar(this@MainActivity, getString(R.string.service_error_msg))
                }
            }
        })
    }
}