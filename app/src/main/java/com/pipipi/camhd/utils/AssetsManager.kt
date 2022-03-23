package com.pipipi.camhd.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class AssetsManager {
    companion object {
        private var i: AssetsManager? = null
            get() {
                field ?: run {
                    field = AssetsManager()
                }
                return field
            }

        @Synchronized
        fun get(): AssetsManager {
            return i!!
        }
    }

    fun getStickers(context: Context): ArrayList<Bitmap> {
        val result = ArrayList<Bitmap>()
        val imgs = context.resources.assets.list("sticker")
        imgs?.let {
            for (item in it) {
                val b = BitmapFactory.decodeStream(context.resources.assets.open("sticker/$item"))
                result.add(b)
            }
        }
        return result
    }

    fun getCartoons(context: Context): ArrayList<Bitmap> {
        val result = ArrayList<Bitmap>()
        val imgs = context.resources.assets.list("cartoon")
        imgs?.let {
            for (item in it) {
                val b = BitmapFactory.decodeStream(context.resources.assets.open("cartoon/$item"))
                result.add(b)
            }
        }
        return result
    }

    fun getAges(context: Context): ArrayList<Bitmap> {
        val result = ArrayList<Bitmap>()
        val imgs = context.resources.assets.list("ages")
        imgs?.let {
            for (item in it) {
                val b = BitmapFactory.decodeStream(context.resources.assets.open("ages/$item"))
                result.add(b)
            }
        }
        return result
    }
}