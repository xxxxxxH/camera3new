package com.pipipi.camhd.item

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem
import com.pipipi.camhd.R
import com.pipipi.camhd.utils.ScreenUtils
import me.panpf.sketch.SketchImageView

class AgeItem(val context: Context, val item: Bitmap) : SimpleRecyclerItem() {
    override fun bind(holder: AdapterViewHolder) {
        val root = holder.itemView.findViewById<RelativeLayout>(R.id.root)
        val iv = holder.itemView.findViewById<SketchImageView>(R.id.itemCartoon)
        Glide.with(context).load(item).into(iv)
        iv.layoutParams.apply {
            this!!.width = ScreenUtils.getScreenSize(context as Activity)[1] / 4
            height = ScreenUtils.getScreenSize(context)[1] / 3
        }
        root.layoutParams.apply {
            width = ScreenUtils.getScreenSize(context as Activity)[1] / 4
        }
    }

    override fun getLayout() = R.layout.item_cartoon
}