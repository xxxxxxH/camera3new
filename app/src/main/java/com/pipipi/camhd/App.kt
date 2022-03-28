package com.pipipi.camhd

import android.content.Context
import androidx.multidex.MultiDexApplication
import com.anythink.splashad.api.ATSplashAd
import com.anythink.splashad.api.ATSplashAdListener


class App : MultiDexApplication() {

    companion object {
        var instance: App? = null
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

    fun openAd(listener: ATSplashAdListener?): ATSplashAd {
        return ATSplashAd(this, this.getString(R.string.top_on_open_ad_id), listener)
    }
}