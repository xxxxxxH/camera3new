package com.pipipi.camhd.utils

import android.app.Activity
import android.util.DisplayMetrics

object ScreenUtils {
    fun getScreenSize(activity: Activity): IntArray {
        val result = IntArray(2)
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        result[0] = height
        result[1] = width
        return result
    }
}