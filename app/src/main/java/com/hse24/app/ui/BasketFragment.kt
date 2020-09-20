package com.hse24.app.ui

import android.content.res.Configuration
import android.graphics.Rect
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
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

import com.hse24.app.R
import com.hse24.app.adapter.BasketAdapter
import com.hse24.app.db.entity.ProductEntity
import com.hse24.app.utils.Hse24Utils
import com.hse24.app.viewmodel.CartViewModel

class BasketFragment : Fragment() {

    companion object {
        val TAG = BasketFragment::class.simpleName.toString()
    }

    private var recyclerView: RecyclerView? = null
    private var emptyTxt: TextView? = null
    private var adapter: BasketAdapter? = null
    private val cartList: MutableList<ProductEntity>? = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root: View = inflater.inflate(R.layout.fragment_cart, container, false)
        emptyTxt = root.findViewById(R.id.cart_empty)
        recyclerView = root.findViewById(R.id.cart_recycler)

        if (Hse24Utils.isTablet(requireActivity())) {
            val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, resources.getInteger(R.integer.span_count_cart))
            recyclerView!!.layoutManager = mLayoutManager
            recyclerView!!.addItemDecoration(GridSpacingItemDecoration(resources.getInteger(R.integer.span_count_cart),  Hse24Utils.dpToPx(requireActivity(),10), true))
            recyclerView!!.itemAnimator = DefaultItemAnimator()
        }else{
          if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
              val mLayoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, resources.getInteger(R.integer.span_count_cart))
              recyclerView!!.layoutManager = mLayoutManager
              recyclerView!!.addItemDecoration(GridSpacingItemDecoration(resources.getInteger(R.integer.span_count_cart),  Hse24Utils.dpToPx(requireActivity(),10), true))
              recyclerView!!.itemAnimator = DefaultItemAnimator()
           } else if (requireActivity().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
             val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
             recyclerView!!.layoutManager = layoutManager
          }
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mCartViewModel: CartViewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        subscribeUi(mCartViewModel.getCartProducts())
    }

    private fun subscribeUi(liveData: LiveData<List<ProductEntity>>) {
        liveData.observe(
            viewLifecycleOwner,
            Observer<List<ProductEntity>> { myProducts: List<ProductEntity>? ->
                if (myProducts != null && myProducts.isNotEmpty()) {
                    cartList!!.clear()
                    for (i in myProducts.indices) {
                        val productEntity: ProductEntity = myProducts[i]
                        cartList.add(productEntity)
                    }
                    recyclerView!!.visibility = View.VISIBLE
                    emptyTxt!!.visibility = View.GONE
                    adapter = BasketAdapter(requireActivity(), cartList as ArrayList<ProductEntity>)
                    recyclerView!!.adapter = adapter
                } else {
                    cartList!!.clear()
                    emptyTxt!!.visibility = View.VISIBLE
                    adapter = BasketAdapter(requireActivity(), cartList as ArrayList<ProductEntity>)
                    adapter!!.notifyDataSetChanged()
                }
            })
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int,
        private val includeEdge: Boolean
    ) :
        ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount
            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount
                outRect.right = (column + 1) * spacing / spanCount
                if (position < spanCount) {
                    outRect.top = spacing
                }
                outRect.bottom = spacing
            } else {
                outRect.left = column * spacing / spanCount
                outRect.right = spacing - (column + 1) * spacing / spanCount
                if (position >= spanCount) {
                    outRect.top = spacing
                }
            }
        }
    }
}