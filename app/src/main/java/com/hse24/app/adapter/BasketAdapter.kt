package com.hse24.app.adapter

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hse24.app.AppExecutors

import com.hse24.app.Config
import com.hse24.app.R
import com.hse24.app.db.AppDatabase
import com.hse24.app.db.entity.ProductEntity

import javax.inject.Inject

class BasketAdapter(private val mContext: Context, private val basketList: List<ProductEntity>) : RecyclerView.Adapter<BasketAdapter.MyViewHolder>() {

    private lateinit var mDatabase: AppDatabase

    @Inject
    private lateinit var appExecutors: AppExecutors

    inner class MyViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val brand: TextView       = mView.findViewById<View>(R.id.cart_brand) as TextView
        val name: TextView        = mView.findViewById<View>(R.id.cart_name) as TextView
        val price: TextView       = mView.findViewById<View>(R.id.cart_price) as TextView
        val oldPrice: TextView    = mView.findViewById<View>(R.id.cart_old_price) as TextView
        val thumbnail: ImageView  = mView.findViewById<View>(R.id.cart_thumbnail) as ImageView
        val deleteImg: ImageView  = mView.findViewById<View>(R.id.cart_delete) as ImageView
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context).inflate(R.layout.basket_card, parent, false)
        appExecutors = AppExecutors()
        mDatabase    = AppDatabase.getInstance(mContext)!!
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val catalogue: ProductEntity = basketList[position]

        holder.mView.tag  = position
        holder.brand.text = catalogue.brandNameLong
        holder.name.text  = catalogue.nameShort
        holder.price.text = "%s %.2f".format(mContext.getString(R.string.euro), catalogue.price)

        if (catalogue.referencePrice > 0) {
            holder.oldPrice.text = "%s %.2f".format(mContext.getString(R.string.euro), catalogue.referencePrice)
            holder.oldPrice.paintFlags = holder.oldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.oldPrice.text = ""
        }

        // loading album cover using Glide library
        val imgUrl = "%s%s%s".format(Config.HSE24_IMAGE_BASE_URL, catalogue.imageUri, Config.HSE24_IMAGE_PARAM)
        Glide.with(mContext).load(imgUrl).into(holder.thumbnail)

        holder.deleteImg.setOnClickListener {
            val builder = AlertDialog.Builder(mContext)
            builder.setTitle(mContext.getString(R.string.dialog_title))
            builder.setMessage(mContext.getString(R.string.dialog_message))

            builder.setPositiveButton(android.R.string.ok) { dialog, _ ->
                appExecutors.diskIO().execute {
                    mDatabase.cartDao().deleteCartProduct(basketList[position].sku)
                }
                dialog.dismiss()
            }

            builder.setNegativeButton(android.R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }

            builder.show()
        }
    }

    override fun getItemCount(): Int {
        return basketList.size
    }

}