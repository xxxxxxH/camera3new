package com.pipipi.camhd.base

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.anythink.core.api.ATAdInfo
import com.anythink.core.api.AdError
import com.anythink.interstitial.api.ATInterstitial
import com.anythink.interstitial.api.ATInterstitialExListener
import com.anythink.splashad.api.ATSplashAd
import com.anythink.splashad.api.ATSplashAdListener
import com.anythink.splashad.api.IATSplashEyeAd
import com.applovin.mediation.ads.MaxInterstitialAd
import com.pipipi.camhd.App
import com.pipipi.camhd.R
import com.pipipi.camhd.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

abstract class BaseActivity(layoutId: Int) : AppCompatActivity(layoutId) {

    private var isBackground = false
    private var lovinInterstitialAd: MaxInterstitialAd? = null
    private var openAd: ATSplashAd? = null
    private var topOnInterstitialAd: ATInterstitial? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openAd = App.instance!!.openAd(openAdListener())
        openAd.loge("xxxxxxHopenAd")
        openAd!!.loadAd()
        createTopOnInterstitialAd()
        onConvert()
    }


    abstract fun onConvert()

    open fun onSplashAdHidden() {}

    open fun onInterstitialAdHidden() {}

    fun registerEventBus() {
        object : BaseLifeCycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                super.onCreate(owner)
                EventBus.getDefault().register(this@BaseActivity)
            }

            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                EventBus.getDefault().unregister(this@BaseActivity)
            }
        }.bindWithLifecycle(this)
    }

    fun showOpenAd(viewGroup: ViewGroup, tag: String = "", isForce: Boolean = false): Boolean {
        if (configEntity.isOpenAdReplacedByInsertAd()) {
            return showInsertAd(tag = tag, isForce = isForce)
        } else {
            return showOpenAdImpl(viewGroup, tag = tag)
        }
    }

    fun showInsertAd(showByPercent: Boolean = false, isForce: Boolean = false, tag: String = ""): Boolean {
        if (isForce) {
            return showInsertAdImpl(tag)
        } else {
            if (configEntity.isCanShowInsertAd()) {
                if ((showByPercent && configEntity.isCanShowByPercent()) || (!showByPercent)) {
                    if (System.currentTimeMillis() - adLastTime > configEntity.insertAdOffset() * 1000) {
                        var showInsertAd = false
                        if (adShownList.getOrNull(adShownIndex) == true) {
                            showInsertAd = showInsertAdImpl(tag)
                        }
                        adShownIndex++
                        if (adShownIndex >= adShownList.size) {
                            adShownIndex = 0
                        }
                        return showInsertAd
                    }
                }
            }
            return false
        }
    }

    private fun showInsertAdImpl(tag: String = ""): Boolean {
        lovinInterstitialAd?.let {
            if (it.isReady) {
                it.showAd(tag)
                return true
            }
        }
        return false
    }

    fun showOpenAdImpl(viewGroup: ViewGroup, tag: String = ""): Boolean {
        openAd?.let {
            if (it.isAdReady) {
                it.show(this, viewGroup)
                return true
            }
        }
        return false
    }

    private fun createTopOnInterstitialAd(offset: Long = 0L) {
        lifecycleScope.launch(Dispatchers.IO) {
            if (offset > 0) {
                delay(offset)
            }
            withContext(Dispatchers.Main) {
                topOnInterstitialAd = topOnInterstitialAdCreator()
            }
        }
    }

    private fun topOnInterstitialAdCreator() =
        ATInterstitial(this, app.getString(R.string.top_on_insert_ad_id)).apply {
            setAdListener(object : ATInterstitialExListener {
                override fun onInterstitialAdLoaded() {
                    "xxxxxxHtopOnInterstitialAd onInterstitialAdLoaded".loge()
                }

                override fun onInterstitialAdLoadFail(p0: AdError?) {
                    "xxxxxxHtopOnInterstitialAd onInterstitialAdLoadFail $p0".loge()
                    createTopOnInterstitialAd(3000)
                }

                override fun onInterstitialAdClicked(p0: ATAdInfo?) {
                    "xxxxxxHtopOnInterstitialAd onInterstitialAdClicked $p0".loge()
                }

                override fun onInterstitialAdShow(p0: ATAdInfo?) {
                    "xxxxxxHtopOnInterstitialAd onInterstitialAdShow $p0".loge()
                }

                override fun onInterstitialAdClose(p0: ATAdInfo?) {
                    "topOnInterstitialAd onInterstitialAdClose $p0".loge()
                    adLastTime = System.currentTimeMillis()
                    createTopOnInterstitialAd()
                    onInterstitialAdHidden()
                }

                override fun onInterstitialAdVideoStart(p0: ATAdInfo?) {
                    "xxxxxxHtopOnInterstitialAd onInterstitialAdVideoStart $p0".loge()
                }

                override fun onInterstitialAdVideoEnd(p0: ATAdInfo?) {
                    "xxxxxxHtopOnInterstitialAd onInterstitialAdVideoEnd $p0".loge()
                }

                override fun onInterstitialAdVideoError(p0: AdError?) {
                    "xxxxxxHtopOnInterstitialAd onInterstitialAdVideoError $p0".loge()
                    createTopOnInterstitialAd(3000)
                }

                override fun onDeeplinkCallback(p0: ATAdInfo?, p1: Boolean) {
                    "xxxxxxHtopOnInterstitialAd onDeeplinkCallback $p0".loge()
                }
            })
            load()
        }

    override fun onStop() {
        super.onStop()
        isBackground = isInBackground()
    }

    override fun onResume() {
        super.onResume()
        if (isBackground) {
            isBackground = false
            val content = findViewById<ViewGroup>(android.R.id.content)
            (content.getTag(R.id.open_ad_view_id) as? FrameLayout)?.let {
                showOpenAd(it)
            } ?: kotlin.run {
                FrameLayout(this).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    content.addView(this)
                    content.setTag(R.id.open_ad_view_id, this)
                    showOpenAd(this)
                }
            }
        }
    }

    inner class openAdListener : ATSplashAdListener {
        override fun onAdLoaded() {
            "onAdLoaded".loge("xxxxxxHopenAdonAdLoaded")
        }

        override fun onNoAdError(p0: AdError?) {
            "$p0".loge("xxxxxxHopenAdonNoAdError")
            lifecycleScope.launch(Dispatchers.IO) {
                delay(3000)
                openAd?.onDestory()
                openAd = App.instance!!.openAd(this@openAdListener)
                openAd?.loadAd()
            }
        }

        override fun onAdShow(p0: ATAdInfo?) {
            "onAdShow".loge("xxxxxxHopenAdonAdShow")
        }

        override fun onAdClick(p0: ATAdInfo?) {
            "onAdClick".loge("xxxxxxHopenAdonAdClick")
        }

        override fun onAdDismiss(p0: ATAdInfo?, p1: IATSplashEyeAd?) {
            "onAdDismiss".loge("xxxxxxHopenAdonAdDismiss")
            onSplashAdHidden()
            lifecycleScope.launch(Dispatchers.IO) {
                delay(3000)
                openAd?.onDestory()
                openAd = App.instance!!.openAd(this@openAdListener)
                openAd?.loadAd()
            }
        }
    }
}