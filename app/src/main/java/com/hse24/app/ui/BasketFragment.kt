package com.hse24.app.ui

import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.hse24.app.R
import com.hse24.app.adapter.BasketAdapter
import com.hse24.app.db.entity.ProductEntity
import com.hse24.app.utils.GridSpacingItemDecoration
import com.hse24.app.utils.UiUtils
import com.hse24.app.viewmodel.CartViewModel

class BasketFragment : Fragment() {

    companion object {
        val TAG = BasketFragment::class.simpleName.toString()
    }

    private var recyclerView: RecyclerView? = null
    private var emptyBasket: TextView? = null
    private var basketAdapter: BasketAdapter? = null
    private var mLayoutManager: RecyclerView.LayoutManager? = null
    private val cartList: MutableList<ProductEntity>? = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root: View = inflater.inflate(R.layout.fragment_cart, container, false)
        emptyBasket  = root.findViewById(R.id.cart_empty)
        recyclerView = root.findViewById(R.id.cart_recycler)

        if (UiUtils.isTablet(requireActivity())) {
            //in Tablets the scrolling of Basket's RecyclerView is Vertical
            mLayoutManager = GridLayoutManager(activity, resources.getInteger(R.integer.span_count_cart))
            recyclerView!!.layoutManager = mLayoutManager
            recyclerView!!.addItemDecoration(GridSpacingItemDecoration(resources.getInteger(R.integer.span_count_cart), UiUtils.dpToPx(requireActivity(),10), true))
            recyclerView!!.itemAnimator = DefaultItemAnimator()
        }else{

          if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
              //in PORTRAIT Orientation the scrolling of Basket's RecyclerView is Vertical
              mLayoutManager = GridLayoutManager(activity, resources.getInteger(R.integer.span_count_cart))
              recyclerView!!.layoutManager = mLayoutManager
              recyclerView!!.addItemDecoration(GridSpacingItemDecoration(resources.getInteger(R.integer.span_count_cart),  UiUtils.dpToPx(requireActivity(),10), true))
              recyclerView!!.itemAnimator = DefaultItemAnimator()

           } else if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
              //in LANDSCAPE Orientation the scrolling of Basket's RecyclerView is Horizontal
             val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
             recyclerView!!.layoutManager = layoutManager
          }
        }

        // Create and set the adapter for the RecyclerView.
        basketAdapter = BasketAdapter(requireActivity(), cartList as ArrayList<ProductEntity>)
        recyclerView!!.adapter = basketAdapter

        // Change the Font of textViews (FiraFont)
        val typeface = Typeface.createFromAsset(requireActivity().assets, "fonts/FiraSans-Bold.ttf")
        emptyBasket!!.typeface = typeface

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mCartViewModel: CartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        subscribeUi(mCartViewModel.getCartProducts())
    }

    override fun onDestroyView() {
        basketAdapter = null
        super.onDestroyView()
    }

    private fun subscribeUi(liveData: LiveData<List<ProductEntity>>) {
        // Update the Basket list when the data changes
        liveData.observe(viewLifecycleOwner, Observer<List<ProductEntity>> { myProducts: List<ProductEntity>? ->
                if (myProducts != null && myProducts.isNotEmpty()) {
                    cartList!!.clear()
                    for (i in myProducts.indices) {
                        val productEntity: ProductEntity = myProducts[i]
                        cartList.add(productEntity)
                    }
                    recyclerView!!.visibility = View.VISIBLE
                    emptyBasket!!.visibility = View.GONE
                    basketAdapter!!.notifyDataSetChanged()
                } else {
                    cartList!!.clear()
                    emptyBasket!!.visibility = View.VISIBLE
                    basketAdapter!!.notifyDataSetChanged()
                }
            })
    }
}