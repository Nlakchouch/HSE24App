package com.hse24.app.ui

import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.widget.*

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

import com.hse24.app.AppExecutors
import com.hse24.app.R
import com.hse24.app.adapter.ImagesAdapter
import com.hse24.app.db.AppDatabase
import com.hse24.app.db.SumCart
import com.hse24.app.db.entity.CartEntity
import com.hse24.app.db.entity.ImageUriEntity
import com.hse24.app.db.entity.ProductEntity
import com.hse24.app.rest.model.ProductContainer
import com.hse24.app.rest.model.ProductPrice
import com.hse24.app.rest.ApiClient
import com.hse24.app.rest.ApiInterface
import com.hse24.app.utils.AppUtils
import com.hse24.app.utils.UiUtils
import com.hse24.app.viewmodel.ProductDetailViewModel

import me.relex.circleindicator.CircleIndicator2

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import javax.inject.Inject

class ProductDetailsFragment : Fragment() {

    private var selectedSku: String? = null
    private var cartButton: Button? = null
    private var indicator: CircleIndicator2? = null
    private var detailsLayout: LinearLayout? = null
    private var progressBar: ProgressBar? = null
    private var ratingBar: RatingBar? = null
    private var recyclerView: RecyclerView? = null
    private var brandProduct: TextView? = null
    private var orderProduct: TextView? = null
    private var nameProduct: TextView? = null
    private var priceProduct: TextView? = null
    private var descProduct: TextView? = null
    private var productOldPrice: TextView? = null
    private var productDimensions: TextView? = null
    private var productDiscount: TextView? = null
    private var descriptionLbl: TextView? = null
    private var dimensionLbl: TextView? = null
    private var ratingsProduct: TextView? = null
    private var textCartItem: TextView? = null
    private var webView: WebView? = null
    private var mAdapter: ImagesAdapter? = null
    private var productEntity: ProductEntity? = null

    private var mDatabase: AppDatabase? = null
    private val pagerSnapHelper = PagerSnapHelper()
    private val handler = Handler()

    @Inject
    private lateinit var appExecutors: AppExecutors

    companion object {
        val TAG = ProductDetailsFragment::class.simpleName.toString()
        fun newInstance(sku: String?) = ProductDetailsFragment().apply {
            val frag = ProductDetailsFragment()
            val args = Bundle()
            args.putString("param", sku)
            frag.arguments = args
            return frag
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            selectedSku = requireArguments().getString("param")
        }
        setHasOptionsMenu(true)
        appExecutors = AppExecutors()
        mDatabase = AppDatabase.getInstance(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root: View = inflater.inflate(R.layout.fragment_product_detail, container, false)

        progressBar     = root.findViewById(R.id.progressBar3)
        productDiscount = root.findViewById(R.id.product_discount)
        brandProduct    = root.findViewById(R.id.product_brand)
        nameProduct     = root.findViewById(R.id.product_name)
        productOldPrice = root.findViewById(R.id.product_old_price)
        priceProduct    = root.findViewById(R.id.product_price)
        orderProduct    = root.findViewById(R.id.product_order)
        ratingBar       = root.findViewById(R.id.product_rating)
        ratingsProduct  = root.findViewById(R.id.product_ratings)
        descProduct     = root.findViewById(R.id.product_description)
        webView         = root.findViewById(R.id.webView)

        productDimensions = root.findViewById(R.id.product_dimensions)
        detailsLayout     = root.findViewById(R.id.details_layout)
        descriptionLbl    = root.findViewById(R.id.description_label)
        dimensionLbl      = root.findViewById(R.id.dimension_label)
        recyclerView      = root.findViewById(R.id.recycler_view)
        indicator         = root.findViewById(R.id.indicator)
        cartButton        = root.findViewById(R.id.cart_button)

        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView!!.layoutManager = layoutManager
        pagerSnapHelper.attachToRecyclerView(recyclerView)

        applyFont()
        loadProductData()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productDetailViewModel: ProductDetailViewModel = ViewModelProvider(this).get(ProductDetailViewModel::class.java)
        productDetailViewModel.setQuery(selectedSku!!)
        subscribeUi(productDetailViewModel.getProductDetails(),
                    productDetailViewModel.getImageUris(),
                    productDetailViewModel.getCartByProduct())

        val runnable = Runnable { setupBadge(productDetailViewModel.getCartTotal()) }
        handler.postAtTime(runnable, System.currentTimeMillis() + 300)
        handler.postDelayed(runnable, 300)

        cartButton!!.setOnClickListener {

            if (productEntity != null && productEntity!!.stockAmount > 0) {
                appExecutors.diskIO().execute {
                    val cartEntity = CartEntity(productEntity!!.sku, 1)
                    mDatabase!!.cartDao().insertAll(cartEntity)
                }
                UiUtils.showSnackBar(requireActivity(), getString(R.string.adding_message))
            }else{
                UiUtils.showSnackBar(requireActivity(), getString(R.string.sold_out))
            }
        }
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putString("Sku", selectedSku)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (savedInstanceState != null) {
            selectedSku = savedInstanceState.getString("Sku")
            Log.v("SavedInstance", selectedSku!!)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // TODO Auto-generated method stub
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
        val menuItem = menu.findItem(R.id.action_cart)
        val actionView = menuItem.actionView
        textCartItem = actionView.findViewById<View>(R.id.cart_badge) as TextView
        actionView.setOnClickListener { startActivity(Intent(activity, BasketActivity::class.java)) }
    }

    private fun subscribeUi(liveData: LiveData<ProductEntity>,
                            liveImageData: LiveData<List<ImageUriEntity>>,
                            liveCartData: LiveData<CartEntity>) {

        liveData.observe(viewLifecycleOwner, Observer<ProductEntity> { myProduct: ProductEntity? ->

                if (myProduct != null) {
                    productEntity = myProduct
                    detailsLayout!!.visibility = View.VISIBLE
                    brandProduct!!.text = myProduct.brandNameLong
                    nameProduct!!.text = myProduct.nameShort
                    priceProduct!!.text = "%s %.2f".format(getString(R.string.euro), myProduct.price)
                    orderProduct!!.text = "  %s  %s".format(getString(R.string.order), myProduct.sku)
                    ratingBar!!.rating = myProduct.averageStars.toFloat()

                    if (myProduct.longDescription != null) {
                        descProduct!!.text = Html.fromHtml(myProduct.longDescription)
                        descriptionLbl!!.visibility = View.VISIBLE
                    } else {
                        descriptionLbl!!.visibility = View.INVISIBLE
                    }

                    if (myProduct.additionalInformation != null) {
                        //productDimensions.setText(Html.fromHtml(productDetails.getAdditionalInformation()));
                        webView!!.loadData(myProduct.additionalInformation, "text/html", "UTF-8")
                        dimensionLbl!!.visibility = View.VISIBLE
                    } else {
                        dimensionLbl!!.visibility = View.INVISIBLE
                    }

                    if (myProduct.reviewers > 1) {
                        ratingsProduct!!.text = "%d %s".format(myProduct.reviewers, getString(R.string.ratings))
                        ratingsProduct!!.visibility = View.VISIBLE
                    } else if (myProduct.reviewers === 1) {
                        ratingsProduct!!.text = "%d %s".format(myProduct.reviewers, getString(R.string.rating))
                        ratingsProduct!!.visibility = View.VISIBLE
                    }

                    if (!TextUtils.isEmpty(myProduct.percentDiscount)) {
                        productDiscount!!.text = "-%s%s".format(myProduct.percentDiscount, getString(R.string.symbol)) + "%"
                        productDiscount!!.visibility = View.VISIBLE
                    } else {
                        productDiscount!!.visibility = View.INVISIBLE
                    }

                    if (myProduct.referencePrice != null && myProduct.referencePrice > 0) {
                        productOldPrice!!.text = "%s %.2f".format(getString(R.string.euro), myProduct.referencePrice)
                        productOldPrice!!.visibility = View.VISIBLE
                        productOldPrice!!.paintFlags = productOldPrice!!.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    }
                }
            })

        liveImageData.observe(
            viewLifecycleOwner,
            Observer<List<ImageUriEntity?>> { productImages: List<ImageUriEntity?>? ->
                if (productImages != null && productImages.isNotEmpty()) {
                    mAdapter = ImagesAdapter(requireActivity(), productImages as List<ImageUriEntity>)
                    recyclerView!!.adapter = mAdapter
                    if (productImages.size > 1) {
                        indicator!!.visibility = View.VISIBLE
                        indicator!!.attachToRecyclerView(recyclerView!!, pagerSnapHelper)
                    } else {
                        indicator!!.visibility = View.GONE
                    }
                }
            })

        liveCartData.observe(
            viewLifecycleOwner,
            Observer<CartEntity> { cartEntity: CartEntity? ->
                if (cartEntity != null) {
                    cartButton!!.text = getString(R.string.already_added)
                    cartButton!!.isEnabled = false
                    Log.v(TAG, cartEntity.sku)
                }else{
                    cartButton!!.text = getString(R.string.add_cart)
                    cartButton!!.isEnabled = true
                }
            })
    }

    private fun setupBadge(liveSumCartData: LiveData<SumCart>) {
        liveSumCartData.observe(this.viewLifecycleOwner, Observer<SumCart> { sumCart: SumCart ->
            Log.v("cartDetails", "" + sumCart.total)
            requireActivity().runOnUiThread {
                if (textCartItem != null) {
                    if (sumCart.total === 0) {
                        textCartItem!!.visibility = View.GONE
                    } else {
                        textCartItem!!.text = java.lang.String.valueOf(sumCart.total)
                        textCartItem!!.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun loadProductData() {
        progressBar!!.visibility = View.VISIBLE
        val apiService: ApiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val call: Call<ProductContainer> = apiService.getProductDetails(selectedSku!!)

        call.enqueue(object : Callback<ProductContainer> {
            override fun onResponse(call: Call<ProductContainer>, response: Response<ProductContainer>) {
                progressBar!!.visibility = View.INVISIBLE

                if (response.isSuccessful) {
                    val productDetails: ProductContainer? = response.body()
                    val productPrice: ProductPrice = productDetails!!.productPrice
                    val imageUriEntities: MutableList<ImageUriEntity> = ArrayList()

                  for (j in productDetails.imageUris.indices) {
                    val imageEntity = ImageUriEntity(productDetails.sku, productDetails.imageUris[j])
                    imageUriEntities.add(imageEntity)
                  }

                  appExecutors.diskIO().execute {
                    mDatabase!!.productDao().updateProduct(
                        productDetails.picCount,
                        productDetails.title,
                        productDetails.longDescription,
                        productDetails.reviewers,
                        productDetails.brandNameShort,
                        productDetails.additionalInformation,
                        productDetails.stockAmount,
                        selectedSku
                    )
                    mDatabase!!.imageDao().insertAll(imageUriEntities)
                  }
                }else{
                    UiUtils.showSnackBar(requireActivity(), getString(R.string.service_error_msg))
                }
            }

            override fun onFailure(call: Call<ProductContainer?>, t: Throwable) {
                Log.e(TAG, t.toString())

                progressBar!!.visibility = View.INVISIBLE
                if (!AppUtils.isNetworkConnected(requireActivity())) {
                    UiUtils.showSnackBar(requireActivity(), getString(R.string.network_error_msg))
                } else {
                    UiUtils.showSnackBar(requireActivity(), getString(R.string.service_error_msg))
                }
            }
        })
    }

    private fun applyFont() {
        val typeface = Typeface.createFromAsset(requireActivity().assets, "fonts/FiraSans-Regular.ttf")
        productDiscount!!.typeface = typeface
        brandProduct!!.typeface = typeface
        nameProduct!!.typeface = typeface
        priceProduct!!.typeface = typeface
        descProduct!!.typeface = typeface
        orderProduct!!.typeface = typeface
        productOldPrice!!.typeface = typeface
        descriptionLbl!!.typeface = typeface
        dimensionLbl!!.typeface = typeface
        productDimensions!!.typeface = typeface
        ratingsProduct!!.typeface = typeface
        cartButton!!.typeface = typeface
    }
}