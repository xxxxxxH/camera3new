package com.pipipi.camhd.view.topon

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import com.anythink.banner.api.ATBannerExListener
import com.anythink.banner.api.ATBannerView
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.pipipi.camhd.R
import com.pipipi.camhd.utils.app
import com.pipipi.camhd.utils.loge

class TopOnBannerAdView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        val bannerView by lazy {
            ATBannerView(app)
        }
    }

    init {
        addBanner()
    }

    private fun addBanner() {
        (bannerView.parent as? ViewGroup)?.removeView(bannerView)
        addView(
            bannerView,
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        bannerView.apply {
            setPlacementId(app.getString(R.string.top_on_banner_ad_id))
            setBannerAdListener(object : ATBannerExListener {
                override fun onBannerLoaded() {
                    "xxxxxxHTopOnBannerAdView onBannerLoaded".loge()
                }

                override fun onBannerFailed(p0: AdError?) {
                    "xxxxxxHTopOnBannerAdView onBannerFailed $p0".loge()
                }

                override fun onBannerClicked(p0: ATAdInfo?) {
                    "xxxxxxHTopOnBannerAdView onBannerClicked $p0".loge()
                }

                override fun onBannerShow(p0: ATAdInfo?) {
                    "xxxxxxHTopOnBannerAdView onBannerShow $p0".loge()
                }

                override fun onBannerClose(p0: ATAdInfo?) {
                    "xxxxxxHTopOnBannerAdView onBannerClose $p0".loge()
                }

                override fun onBannerAutoRefreshed(p0: ATAdInfo?) {
                    "xxxxxxHTopOnBannerAdView onBannerAutoRefreshed $p0".loge()
                }

                override fun onBannerAutoRefreshFail(p0: AdError?) {
                    "xxxxxxHTopOnBannerAdView onBannerAutoRefreshFail $p0".loge()
                }

                override fun onDeeplinkCallback(p0: Boolean, p1: ATAdInfo?, p2: Boolean) {
                    "xxxxxxHTopOnBannerAdView onDeeplinkCallback $p0".loge()
                }
            })
        }
        bannerView.loadAd()
    }
}