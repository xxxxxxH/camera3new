package com.pipipi.camhd.item

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem
import com.pipipi.camhd.R
import com.pipipi.camhd.utils.ScreenUtils

class StickerItem(val context: Context, val item: Bitmap) : SimpleRecyclerItem() {
    override fun bind(holder: AdapterViewHolder) {
        val root = holder.itemView.findViewById<RelativeLayout>(R.id.root)
        root.let {
            it.layoutParams = it.layoutParams.apply {
                height = ScreenUtils.getScreenSize(context as Activity)[1] / 3
                width = ScreenUtils.getScreenSize(context)[1] / 3
            }
        }
        val img = holder.itemView.findViewById<ImageView>(R.id.itemSticker)
        Glide.with(context).load(item).into(img)
        holder.attachOnClickListener(R.id.root)
    }

    override fun getLayout(): Int {
        return R.layout.item_stickers
    }
}