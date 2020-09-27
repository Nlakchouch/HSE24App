package com.hse24.app.ui

import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.hse24.app.Config
import com.hse24.app.R
import com.hse24.app.adapter.ImagesAdapter
import com.hse24.app.db.AppDatabase
import com.hse24.app.db.entity.CartEntity
import com.hse24.app.db.entity.ImageUriEntity
import com.hse24.app.db.entity.ProductEntity
import com.hse24.app.db.entity.VariationEntity
import com.hse24.app.restApi.model.ProductContainer
import com.hse24.app.restApi.ApiClient
import com.hse24.app.restApi.ApiInterface
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
    private var productRating: RatingBar? = null
    private var recyclerView: RecyclerView? = null
    private var productBrand: TextView? = null
    private var productOrder: TextView? = null
    private var productName: TextView? = null
    private var productPrice: TextView? = null
    private var productDesc: TextView? = null
    private var productOldPrice: TextView? = null
    private var productDimensions: TextView? = null
    private var productDiscount: TextView? = null
    private var descriptionLbl: TextView? = null
    private var dimensionLbl: TextView? = null
    private var productReviews: TextView? = null
    private var textCartItem: TextView? = null
    private var webView: WebView? = null

    private var imagesAdapter: ImagesAdapter? = null
    private var productEntity: ProductEntity? = null
    private var imageUriEntities: MutableList<ImageUriEntity> = mutableListOf()

    private var mDatabase: AppDatabase? = null
    private val pagerSnapHelper = PagerSnapHelper()
    private var productCall: Call<ProductContainer>? = null

    @Inject
    private lateinit var appExecutors: AppExecutors

    companion object {
        val TAG = ProductDetailsFragment::class.simpleName.toString()
        /** Creates product detail fragment for specific product SKU */
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
        productOrder    = root.findViewById(R.id.product_order)
        productBrand    = root.findViewById(R.id.product_brand)
        productName    = root.findViewById(R.id.product_name)
        productOldPrice = root.findViewById(R.id.product_old_price)
        productPrice    = root.findViewById(R.id.product_price)
        productRating   = root.findViewById(R.id.product_rating)
        productReviews  = root.findViewById(R.id.product_ratings)
        productDesc     = root.findViewById(R.id.product_description)
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

        // Create and set the adapter for the RecyclerView.
        imagesAdapter = ImagesAdapter(requireActivity(), imageUriEntities)
        recyclerView!!.adapter = imagesAdapter

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productDetailViewModel: ProductDetailViewModel = ViewModelProvider(this).get(ProductDetailViewModel::class.java)
        productDetailViewModel.setQuery(selectedSku!!)

        subscribeUi(productDetailViewModel.getProductDetails(),
                    productDetailViewModel.getCartByProduct())

        subscribeVariationsUi(productDetailViewModel.gatVariations(),
                              productDetailViewModel.getImageUris())

        Handler(Looper.getMainLooper()).postDelayed({
                setupBadge(productDetailViewModel.getCart())
        }, 300)


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
        }else{
            //Loading Product date for specific product SKU
            loadProductData()
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

    override fun onDestroy() {
        super.onDestroy()
        //Cancel Retrofit Requests if it's running
        if (productCall != null) {
            productCall!!.cancel()
        }
    }

    private fun subscribeUi(liveData: LiveData<ProductEntity>,
                            liveCartData: LiveData<CartEntity>) {
        // Update the product details when the data changes
        liveData.observe(viewLifecycleOwner, Observer<ProductEntity> { myProduct: ProductEntity? ->
                if (myProduct != null) {
                    productEntity = myProduct
                    detailsLayout!!.visibility = View.VISIBLE
                    productBrand!!.text = myProduct.brandNameLong
                    productName!!.text = myProduct.nameShort
                    productPrice!!.text = "%s %.2f".format(getString(R.string.euro), myProduct.price)
                    productOrder!!.text = "  %s  %s".format(getString(R.string.order), myProduct.sku)
                    productRating!!.rating = myProduct.averageStars.toFloat()

                    if (!TextUtils.isEmpty(myProduct.longDescription)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            productDesc!!.text = Html.fromHtml(myProduct.longDescription, Html.FROM_HTML_MODE_LEGACY)
                        } else {
                            productDesc!!.text = Html.fromHtml(myProduct.longDescription)
                        }
                        descriptionLbl!!.visibility = View.VISIBLE
                    } else {
                        descriptionLbl!!.visibility = View.INVISIBLE
                    }

                    if (!TextUtils.isEmpty(myProduct.additionalInformation)) {
                        //productDimensions.setText(Html.fromHtml(productDetails.getAdditionalInformation()));
                        webView!!.loadData(myProduct.additionalInformation, "text/html", "UTF-8")
                        dimensionLbl!!.visibility = View.VISIBLE
                    } else {
                        dimensionLbl!!.visibility = View.INVISIBLE
                    }

                    if (myProduct.reviewers > 1) {
                        productReviews!!.text = "%d %s".format(myProduct.reviewers, getString(R.string.ratings))
                        productReviews!!.visibility = View.VISIBLE
                    } else if (myProduct.reviewers == 1) {
                        productReviews!!.text = "%d %s".format(myProduct.reviewers, getString(R.string.rating))
                        productReviews!!.visibility = View.VISIBLE
                    }

                    if (!TextUtils.isEmpty(myProduct.percentDiscount)) {
                        productDiscount!!.text = "-%s%s".format(myProduct.percentDiscount, getString(R.string.symbol))
                        productDiscount!!.visibility = View.VISIBLE
                    } else {
                        productDiscount!!.visibility = View.INVISIBLE
                    }

                    if (myProduct.referencePrice > 0) {
                        productOldPrice!!.text = "%s %.2f".format(getString(R.string.euro), myProduct.referencePrice)
                        productOldPrice!!.visibility = View.VISIBLE
                        productOldPrice!!.paintFlags = productOldPrice!!.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    }
                }
            })

        // Update the text in Add To Cart Button when the data changes
        liveCartData.observe(viewLifecycleOwner, Observer<CartEntity> { cartEntity: CartEntity? ->
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

    private fun subscribeVariationsUi(liveVariationsData: LiveData<List<VariationEntity>>,
                                      liveImageData: LiveData<List<ImageUriEntity>>){
        // Update the product variations SlideShow when the data changes
        liveVariationsData.observe(viewLifecycleOwner, Observer<List<VariationEntity>>{ variations: List<VariationEntity> ->
            if (variations.isNotEmpty()) {
                imageUriEntities.clear()
                for (i in variations.indices) {
                    val imageEntity = ImageUriEntity(variations[i].sku, variations[i].imageUri)
                    imageUriEntities.add(imageEntity)
                }
                notifyImagesData()
            }else{
                // Update the product SlideShow when there are no variations and the data changes
                liveImageData.observe(viewLifecycleOwner, Observer<List<ImageUriEntity>> { productImages: List<ImageUriEntity?> ->
                    if (productImages.isNotEmpty()) {
                        imageUriEntities.clear()
                        for (i in productImages.indices) {
                            imageUriEntities.add(productImages[i]!!)
                        }
                        notifyImagesData()
                    }
                })
            }
        })
    }

    private fun setupBadge(liveSumCartData: LiveData<List<CartEntity>>) {
        // Update the number of items in Cart when the data changes
        liveSumCartData.observe(this.viewLifecycleOwner, Observer<List<CartEntity>> { sumCart: List<CartEntity> ->
            requireActivity().runOnUiThread {
                if (textCartItem != null) {
                    if (sumCart.isEmpty()) {
                        textCartItem!!.visibility = View.GONE
                    } else {
                        textCartItem!!.text = sumCart.size.toString()
                        textCartItem!!.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun notifyImagesData(){
        if (imageUriEntities.size > 1) {
            indicator!!.visibility = View.VISIBLE
            indicator!!.attachToRecyclerView(recyclerView!!, pagerSnapHelper)
        } else {
            indicator!!.visibility = View.GONE
        }
        imagesAdapter!!.notifyDataSetChanged()
    }

    private fun loadProductData() {
        progressBar!!.visibility = View.VISIBLE
        val apiService: ApiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        productCall = apiService.getProductDetails(selectedSku!!)

        productCall!!.enqueue(object : Callback<ProductContainer> {
            override fun onResponse(call: Call<ProductContainer>, response: Response<ProductContainer>) {
                progressBar!!.visibility = View.INVISIBLE

                if (response.isSuccessful) {
                    val productDetails: ProductContainer? = response.body()
                    val imageUriEntities: MutableList<ImageUriEntity> = mutableListOf()
                    val variationEntities: MutableList<VariationEntity> = mutableListOf()

                    for (i in productDetails!!.imageUris.indices) {
                        val imageEntity = ImageUriEntity(productDetails.sku, productDetails.imageUris[i])
                        imageUriEntities.add(imageEntity)
                    }

                    for (j in productDetails.variations.indices) {
                        if(productDetails.variations[j].status.equals(Config.SELLABLE_STATUS)){
                           val variationEntity = VariationEntity(productDetails.sku, productDetails.variations[j])
                           variationEntities.add(variationEntity)
                        }
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
                      mDatabase!!.variationDao().insertAll(variationEntities)
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
}