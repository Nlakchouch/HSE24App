package com.hse24.app.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.bumptech.glide.Glide
import com.hse24.app.Config
import com.hse24.app.R
import com.hse24.app.db.entity.ProductEntity
import com.hse24.app.ui.ProductDetailsActivity
import java.util.*


class FooterAdapter(private val mContext: Context, productList: List<ProductEntity>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val productList: List<ProductEntity> = productList

    companion object {
    public const val TYPE_ITEM: Int = 1
    public const val TYPE_FOOTER: Int = 2
    }

    inner class FooterViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val loadButton: Button = mView.findViewById<View>(R.id.paging_button) as Button
        init {
            mView.setOnClickListener {
                // Do whatever you want on clicking the item
            }
        }
    }

     inner class NormalViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {

        val brand: TextView = mView.findViewById<View>(R.id.brand) as TextView
        val name: TextView = mView.findViewById<View>(R.id.name) as TextView
        val price: TextView = mView.findViewById<View>(R.id.price) as TextView
        val oldPrice: TextView = mView.findViewById<View>(R.id.old_price) as TextView
        val discount: TextView = mView.findViewById<View>(R.id.discount) as TextView
        val thumbnail: ImageView = mView.findViewById<View>(R.id.thumbnail) as ImageView
        val ratingBar: RatingBar = mView.findViewById<View>(R.id.rating) as RatingBar
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View

        if (viewType === TYPE_FOOTER) {
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_footer, parent, false)
            return FooterViewHolder(itemView)
        }else {
            itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_card, parent, false)
            return NormalViewHolder(itemView)
        }

        //itemView = LayoutInflater.from(parent.context).inflate(R.layout.product_card, parent, false)
        //return NormalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val typeface = Typeface.createFromAsset(mContext.assets, "fonts/FiraSans-Regular.ttf")

        try {
            if (holder is NormalViewHolder) {

                val catalogue: ProductEntity = productList[position]
                holder.mView.tag  = position
                holder.brand.text = catalogue.brandNameLong
                holder.name.text  = catalogue.nameShort
                holder.price.text = String.format(Locale.ENGLISH, "€ %.2f", catalogue.price)

                if (catalogue.referencePrice != null && catalogue.referencePrice > 0) {
                    holder.oldPrice.text = String.format(Locale.ENGLISH, "€ %.2f", catalogue.referencePrice)
                    holder.oldPrice.paintFlags = holder.oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    holder.oldPrice.text = ""
                }

                if (!TextUtils.isEmpty(catalogue.percentDiscount)) {
                    holder.discount.visibility = View.VISIBLE
                    holder.discount.text = String.format(Locale.ENGLISH, "-%s", catalogue.percentDiscount).toString() + "%"
                } else {
                    holder.discount.visibility = View.INVISIBLE
                }

                if (catalogue.averageStars === 0) {
                    holder.ratingBar.visibility = View.INVISIBLE
                } else {
                    holder.ratingBar.visibility = View.VISIBLE
                    holder.ratingBar.rating = catalogue!!.averageStars.toFloat()
                }

                holder.brand.typeface    = typeface
                holder.name.typeface     = typeface
                holder.price.typeface    = typeface
                holder.oldPrice.typeface = typeface
                holder.discount.typeface = typeface

                // loading album cover using Glide library
                val imgUrl = String.format(Locale.ENGLISH, "%s%s%s", Config.HSE24_IMAGE_BASE_URL,
                    catalogue.imageUri, Config.HSE24_IMAGE_PARAM
                )
                Glide.with(mContext).load(imgUrl).into(holder.thumbnail)

                holder.mView.setOnClickListener {
                    val intent = Intent(mContext, ProductDetailsActivity::class.java)
                    intent.putExtra("sku", productList[position].sku)
                    mContext.startActivity(intent)
                }

            } else if (holder is FooterViewHolder) {
                val vh = holder
                Log.v("Footer", "Here iam")
                holder.mView.tag  = position
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        if (productList == null && productList.isEmpty()) {
            return 0;
        }

        return productList.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == productList.size + 1) {
            TYPE_FOOTER
        }else{
            TYPE_ITEM
        }
    }

}