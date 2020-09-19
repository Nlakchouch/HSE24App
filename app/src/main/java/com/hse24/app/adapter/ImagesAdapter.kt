package com.hse24.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

import com.hse24.app.Config
import com.hse24.app.R
import com.hse24.app.db.entity.ImageUriEntity

import java.lang.String
import java.util.*

class ImagesAdapter(private val mContext: Context, imageUris: List<ImageUriEntity>) : RecyclerView.Adapter<ImagesAdapter.MyViewHolder>() {

    private val imageUris: List<ImageUriEntity> = imageUris

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val imgUrl = String.format(
            Locale.ENGLISH, "%s%s%s", Config.HSE24_IMAGE_BASE_URL,
            imageUris[position].imageUri,
            Config.HSE24_IMAGE_PARAM
        )

        // loading album cover using Glide library
        Glide.with(mContext).load(imgUrl).into(holder.productImage)
    }

    override fun getItemCount(): Int {
        return imageUris.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById<View>(R.id.product_img) as ImageView
    }

}