package com.hse24.app.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide

import com.hse24.app.Config
import com.hse24.app.R
import com.hse24.app.db.entity.ProductEntity
import com.hse24.app.ui.ProductDetailsActivity

class CatalogueAdapter(private val mContext: Context, private val productList: List<ProductEntity>) : RecyclerView.Adapter<CatalogueAdapter.MyViewHolder>() {

    inner class MyViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {

        val brand: TextView = mView.findViewById<View>(R.id.brand) as TextView
        val name: TextView = mView.findViewById<View>(R.id.name) as TextView
        val price: TextView = mView.findViewById<View>(R.id.price) as TextView
        val oldPrice: TextView = mView.findViewById<View>(R.id.old_price) as TextView
        val discount: TextView = mView.findViewById<View>(R.id.discount) as TextView
        val thumbnail: ImageView = mView.findViewById<View>(R.id.thumbnail) as ImageView
        val ratingBar: RatingBar = mView.findViewById<View>(R.id.rating) as RatingBar

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.product_card, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val catalogue: ProductEntity = productList[position]
        val typeface = Typeface.createFromAsset(mContext.assets, "fonts/FiraSans-Regular.ttf")

        holder.mView.tag  = position
        holder.brand.text = catalogue.brandNameLong
        holder.name.text  = catalogue.nameShort
        holder.price.text = "%s %.2f".format(mContext.getString(R.string.euro), catalogue.price)

        if (catalogue.referencePrice > 0) {
            holder.oldPrice.text ="%s %.2f".format(mContext.getString(R.string.euro),catalogue.referencePrice)
            holder.oldPrice.paintFlags = holder.oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.oldPrice.text = ""
        }

        if (!TextUtils.isEmpty(catalogue.percentDiscount)) {
            holder.discount.visibility = View.VISIBLE
            holder.discount.text = "-%s%s".format(catalogue.percentDiscount, mContext.getString(R.string.symbol))
        } else {
            holder.discount.visibility = View.INVISIBLE
        }

        if (catalogue.averageStars == 0) {
            holder.ratingBar.visibility = View.INVISIBLE
        } else {
            holder.ratingBar.visibility = View.VISIBLE
            holder.ratingBar.rating = catalogue.averageStars.toFloat()
        }

        holder.brand.typeface    = typeface
        holder.name.typeface     = typeface
        holder.price.typeface    = typeface
        holder.oldPrice.typeface = typeface
        holder.discount.typeface = typeface

        // loading album cover using Glide library
        val imgUrl = "%s%s%s".format(Config.HSE24_IMAGE_BASE_URL, catalogue.imageUri, Config.HSE24_IMAGE_PARAM)
        Glide.with(mContext).load(imgUrl).into(holder.thumbnail)

        holder.mView.setOnClickListener {
            val intent = Intent(mContext, ProductDetailsActivity::class.java)
            intent.putExtra("sku", productList[position].sku)
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

}