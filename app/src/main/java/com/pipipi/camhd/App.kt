package com.pipipi.camhd

import android.app.Activity
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.anythink.splashad.api.ATSplashAd
import com.anythink.splashad.api.ATSplashAdListener
import com.applovin.mediation.ads.MaxAdView
import com.applovin.mediation.ads.MaxInterstitialAd
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkSettings


class App : MultiDexApplication() {

    companion object {
        var instance: App? = null
    }

    private val LOVINSDK by lazy {
        AppLovinSdk.getInstance(
            this.getString(R.string.lovin_app_key).reversed(),
            AppLovinSdkSettings(this),
            this
        )
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Ktx.initialize(this)
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        Ktx.getInstance().initStartUp()

    }

    fun lovinInterstitialAd(ac: Activity): MaxInterstitialAd {
        return MaxInterstitialAd(getString(R.string.lovin_insert_ad_id), LOVINSDK, ac)
    }

    fun lovinBanner(): MaxAdView {
        return MaxAdView(this.getString(R.string.lovin_banner_ad_id), LOVINSDK, this)
    }

    fun openAd(listener: ATSplashAdListener?): ATSplashAd {
        return ATSplashAd(this, this.getString(R.string.top_on_open_ad_id), listener)
    }
}