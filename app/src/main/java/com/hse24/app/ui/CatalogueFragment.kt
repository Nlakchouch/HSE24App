package com.hse24.app.ui

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.TextView

import androidx.annotation.NonNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.hse24.app.AppExecutors
import com.hse24.app.Config
import com.hse24.app.R
import com.hse24.app.adapter.CatalogueAdapter
import com.hse24.app.adapter.FilterAdapter
import com.hse24.app.db.AppDatabase
import com.hse24.app.db.SumCart
import com.hse24.app.db.entity.CategoryCountEntity
import com.hse24.app.db.entity.CategoryEntity
import com.hse24.app.db.entity.ImageUriEntity
import com.hse24.app.db.entity.ProductEntity
import com.hse24.app.rest.ApiClient
import com.hse24.app.rest.ApiInterface
import com.hse24.app.rest.model.CatalogueContainer
import com.hse24.app.rest.model.Filter
import com.hse24.app.rest.model.Paging
import com.hse24.app.rest.model.Product
import com.hse24.app.utils.AppUtils
import com.hse24.app.utils.GridSpacingItemDecoration
import com.hse24.app.utils.UiUtils
import com.hse24.app.viewmodel.CartViewModel
import com.hse24.app.viewmodel.CatalogueViewModel

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class CatalogueFragment : Fragment() {

    private var progressBar: ProgressBar? = null
    private var recyclerView: RecyclerView? = null
    private var filtersRecycler: RecyclerView? = null
    private var emptyTxt: TextView? = null
    private var categoryTxt: TextView? = null
    private var textCartItemCount: TextView? = null

    private var selectedCategory: Int = 0
    private var twoPanel: Boolean = false
    private var productList: MutableList<ProductEntity>? = mutableListOf()

    private var adapter: CatalogueAdapter? = null
    private var filterAdapter: FilterAdapter? = null
    private var mDatabase: AppDatabase? = null

    private var paging: Paging? = null
    private var filter: Filter? = null

    @Inject
    private lateinit var appExecutors: AppExecutors

    companion object {
        val TAG = CatalogueFragment::class.simpleName.toString()
        fun newInstance(idCategory: Int, mTwoPanel: Boolean) = CatalogueFragment().apply {
            val frag = CatalogueFragment()
            val args = Bundle()
            args.putInt("param", idCategory)
            args.putBoolean("panel", mTwoPanel)
            frag.arguments = args
            return frag
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            selectedCategory = requireArguments().getInt("param")
            twoPanel  = requireArguments().getBoolean("panel")
        }
        setHasOptionsMenu(true)
        appExecutors = AppExecutors()
        mDatabase = AppDatabase.getInstance(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root: View  = inflater.inflate(R.layout.fragment_catalogue, container, false)
        emptyTxt        = root.findViewById(R.id.text_empty)
        categoryTxt     = root.findViewById(R.id.text_category)
        recyclerView    = root.findViewById(R.id.catalogue_recycler)
        filtersRecycler = root.findViewById(R.id.filter_recycler)
        progressBar     = root.findViewById(R.id.progressBar2)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        filtersRecycler!!.layoutManager = layoutManager

        if(twoPanel){
            val mLayoutManager = GridLayoutManager(activity, resources.getInteger(
                    R.integer.span_count
                ))
            recyclerView!!.layoutManager = mLayoutManager
            recyclerView!!.addItemDecoration(
                GridSpacingItemDecoration(resources.getInteger(R.integer.span_count), UiUtils.dpToPx(requireActivity(), 10), true)
            )
            recyclerView!!.itemAnimator = DefaultItemAnimator()
        }else{

          if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
              val mLayoutManager = GridLayoutManager(activity, resources.getInteger(
                      R.integer.span_count
                  ))
              recyclerView!!.layoutManager = mLayoutManager
              recyclerView!!.addItemDecoration(
                  GridSpacingItemDecoration(resources.getInteger(R.integer.span_count), UiUtils.dpToPx(requireActivity(), 10), true)
              )
              recyclerView!!.itemAnimator = DefaultItemAnimator()

          } else if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
              categoryTxt!!.visibility = View.GONE
              filtersRecycler!!.visibility = View.GONE
              val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
              recyclerView!!.layoutManager = layoutManager
          }
        }

        adapter = CatalogueAdapter(requireActivity(), productList as ArrayList<ProductEntity>)
        recyclerView!!.adapter = adapter

        recyclerView!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(@NonNull recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) { //1 for down
                    if (paging != null && paging!!.numPages > 1 && paging!!.numPages != paging!!.page) {
                        loadCatalogueData(paging!!.page + 1)
                    }
                }
            }
        })

        val typeface = Typeface.createFromAsset(requireActivity().assets, "fonts/FiraSans-Bold.ttf")
        categoryTxt!!.typeface = typeface

        if (paging == null)
            loadCatalogueData(1)

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productListViewModel: CatalogueViewModel = ViewModelProvider(this).get(CatalogueViewModel::class.java)
        productListViewModel.setQuery(selectedCategory)
        subscribeUi(
            productListViewModel.getProducts(),
            productListViewModel.getCategory(),
            productListViewModel.getCategoryCount()
        )

        val mCartViewModel: CartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        Handler(Looper.getMainLooper()).postDelayed({
            setupBadge(mCartViewModel.getCartTotal())
        }, 300)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putInt("category", selectedCategory)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            selectedCategory = savedInstanceState.getInt("category")
            Log.v("SavedInstance", "" + selectedCategory)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // TODO Auto-generated method stub
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        val menuItem = menu.findItem(R.id.action_cart)
        val actionView = menuItem.actionView
        textCartItemCount = actionView.findViewById<View>(R.id.cart_badge) as TextView
        actionView.setOnClickListener { startActivity(Intent(activity, BasketActivity::class.java)) }
    }

    private fun subscribeUi(
        liveData: LiveData<List<ProductEntity>>,
        liveCategoryData: LiveData<CategoryEntity>,
        liveCountData: LiveData<CategoryCountEntity>
    ) {

        liveData.observe(viewLifecycleOwner, Observer<List<ProductEntity>> { myProducts: List<ProductEntity>? ->
                if (myProducts != null && myProducts.isNotEmpty()) {
                    productList!!.clear()
                    myProducts.indices.forEach { i -> productList!!.add(myProducts[i]) }
                    recyclerView!!.visibility = View.VISIBLE
                    emptyTxt!!.visibility = View.GONE
                    adapter!!.notifyDataSetChanged()
                } else {
                    recyclerView!!.visibility = View.INVISIBLE
                    adapter!!.notifyDataSetChanged()
                }
            })

        liveCategoryData.observe(viewLifecycleOwner, Observer<CategoryEntity> { myCategory: CategoryEntity? ->
                if (myCategory != null) {
                    categoryTxt!!.text = myCategory.name
                }
            })

        liveCountData.observe(viewLifecycleOwner, Observer<CategoryCountEntity> { myCountCategory: CategoryCountEntity? ->
                if (myCountCategory != null) {
                    if (myCountCategory.resultCount == 0) {
                        emptyTxt!!.text = getString(R.string.empty_catalogue)
                        emptyTxt!!.visibility = View.VISIBLE
                    } else if (myCountCategory.resultCount > 0) {
                        emptyTxt!!.text = ""
                        emptyTxt!!.visibility = View.GONE
                    }
                }
            })
    }

    private fun setupBadge(liveData: LiveData<SumCart>) {
        liveData.observe(this.viewLifecycleOwner, Observer<SumCart> { sumCart: SumCart ->
            if (textCartItemCount != null) {
                if (sumCart.total === 0) {
                    textCartItemCount!!.visibility = View.GONE
                } else {
                    textCartItemCount!!.text = sumCart.total.toString()
                    textCartItemCount!!.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun loadCatalogueData(pageNum: Int) {

        progressBar!!.visibility = View.VISIBLE
        val apiService: ApiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
       // val call: Call<CatalogueContainer> = apiService.getCatalogue(selectedCategory)
        val call: Call<CatalogueContainer> = apiService.getCataloguePaging(selectedCategory,pageNum)

        call.enqueue(object : Callback<CatalogueContainer> {
            override fun onResponse(call: Call<CatalogueContainer>, response: Response<CatalogueContainer>) {
                progressBar!!.visibility = View.INVISIBLE

                if (response.isSuccessful) {
                     //Delete and clean BD during the first loading of products
                    if(pageNum ==1){
                        appExecutors.diskIO().execute {
                            mDatabase!!.productDao().deleteCategoryProducts(selectedCategory)
                        }
                    }

                    val productContainer: CatalogueContainer? = response.body()
                    val products: List<Product> = productContainer!!.productResults
                    val productEntities: MutableList<ProductEntity> = mutableListOf()
                    val imageUriEntities: MutableList<ImageUriEntity> = mutableListOf()

                    paging = productContainer.paging
                    filter = productContainer.filter

                    val productCountEntity = CategoryCountEntity(selectedCategory, productContainer.resultCount)

                    for (i in products.indices) {
                        val product: Product = products[i]
                        if (product!!.status == Config.SELLABLE_STATUS) {
                            val productEntity = ProductEntity(product, selectedCategory)
                            productEntities.add(productEntity)

                            for (j in product.imageUris.indices) {
                                val imageEntity = ImageUriEntity(product.sku, product.imageUris[j])
                                imageUriEntities.add(imageEntity)
                            }
                        }
                    }

                    appExecutors.diskIO().execute {
                        mDatabase!!.productDao().insertAll(productEntities)
                        mDatabase!!.imageDao().insertAll(imageUriEntities)
                        mDatabase!!.categoryCountDao().insertRow(productCountEntity)
                    }

                    if (paging != null && paging!!.numPages > 1) {
                        Log.e(TAG, "Paging " + paging!!.numPages)
                    }

                    if (filter != null && filter!!.filterGroups.isNotEmpty()) {
                        Log.v(TAG, "Filters " + filter!!.filterGroups.size)
                        filtersRecycler!!.visibility = View.GONE
                        filterAdapter = FilterAdapter(requireActivity(), filter!!.filterGroups)
                        filtersRecycler!!.adapter = filterAdapter
                    } else {
                        filtersRecycler!!.visibility = View.GONE
                    }

                } else {
                    UiUtils.showSnackBar(requireActivity(), getString(R.string.service_error_msg))
                }
            }

            override fun onFailure(call: Call<CatalogueContainer>, t: Throwable) {
                progressBar!!.visibility = View.INVISIBLE
                Log.e(TAG, t.toString())
                if (!AppUtils.isNetworkConnected(requireActivity())) {
                    UiUtils.showSnackBar(requireActivity(), getString(R.string.network_error_msg))
                } else {
                    UiUtils.showSnackBar(requireActivity(), getString(R.string.service_error_msg))
                }
            }
        })
    }
}