package com.pipipi.camhd.item

import android.app.Activity
import android.content.Context
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem
import com.pipipi.camhd.R
import com.pipipi.camhd.utils.ScreenUtils

class CreationItem(val context: Context, val item: String) : SimpleRecyclerItem() {
    override fun bind(holder: AdapterViewHolder) {
        val root = holder.itemView.findViewById<RelativeLayout>(R.id.itemRoot)
        val iv = holder.itemView.findViewById<ImageView>(R.id.item)
        Glide.with(context).load(item).into(iv)
        iv.layoutParams.apply {
            width = ScreenUtils.getScreenSize(context as Activity)[1] / 3
            height = ScreenUtils.getScreenSize(context)[1] / 3
        }
        root.layoutParams.apply {
            width = ScreenUtils.getScreenSize(context as Activity)[1] / 3
            height = ScreenUtils.getScreenSize(context)[1] / 3
        }
        holder.attachOnClickListener(R.id.itemRoot)
    }

    override fun getLayout() = R.layout.layout_pic_item
}