package com.pipipi.camhd.view.topon

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import com.anythink.core.api.AdError
import com.anythink.nativead.api.ATNative
import com.anythink.nativead.api.ATNativeAdView
import com.anythink.nativead.api.ATNativeNetworkListener
import com.pipipi.camhd.R
import com.pipipi.camhd.utils.app
import com.pipipi.camhd.utils.loge

class TopOnNativeAdView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr) {

    private fun onAdLoad() {
        atNative?.nativeAd?.let {
            try {
                it.renderAdView(atNativeAdView, render)
            } catch (e: Exception) {
                "TopOnNativeAdView onAdLoad $e".loge()
            }
            it.prepare(atNativeAdView, render.clickView, null)
        }
    }

    private var atNative: ATNative? = null

    private val render by lazy {
        TopOnRender(context)
    }

    private val atNativeAdView by lazy {
        ATNativeAdView(context)
    }

    init {
        addView(
            atNativeAdView,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        atNative = ATNative(
            context,
            app.getString(R.string.top_on_native_ad_id),
            object : ATNativeNetworkListener {
                override fun onNativeAdLoaded() {
                    "TopOnNativeAdView onNativeAdLoaded".loge()
                    onAdLoad()
                }

                override fun onNativeAdLoadFail(p0: AdError?) {
                    "TopOnNativeAdView onNativeAdLoadFail $p0".loge()
                }
            }).apply {
            makeAdRequest()
        }

    }
}