package com.pipipi.camhd

import android.app.Application
import android.os.Build
import android.webkit.WebView
import com.anythink.core.api.ATSDK
import com.pipipi.camhd.utils.loge
import com.tencent.mmkv.MMKV
import kotlin.system.measureTimeMillis

class Ktx private constructor(application: Application) {

    companion object {
        @Volatile
        private var INSTANCE: Ktx? = null

        fun initialize(application: Application) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Ktx(application).apply { INSTANCE = this }
            }


        fun getInstance() =
            INSTANCE ?: throw NullPointerException("Have you invoke initialize() before?")
    }

    val app = application


    fun initStartUp() {
        measureTimeMillis {
            MMKV.initialize(app)
            initOther()
        }.let {
            "application initTime -> ${it}".loge()
        }
    }

    private fun initOther() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val processName = Application.getProcessName()
            if (app.packageName != processName) {
                WebView.setDataDirectorySuffix(processName)
            }
        }

        ATSDK.setNetworkLogDebug(BuildConfig.DEBUG)
        ATSDK.integrationChecking(app)
        ATSDK.init(
            app,
            app.getString(R.string.top_on_app_id),
            app.getString(R.string.top_on_app_key)
        )
    }
}