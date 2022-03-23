package com.pipipi.camhd.ui.activity

import android.content.Intent
import android.view.View
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.pipipi.camhd.BusDestroyEvent
import com.pipipi.camhd.R
import com.pipipi.camhd.base.BaseActivity
import com.pipipi.camhd.utils.*
import kotlinx.android.synthetic.main.activity_splash.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class IndexActivity : BaseActivity(R.layout.activity_splash) {

    override fun onConvert() {
        registerEventBus()
        lifecycleScope.requestConfig {
            if (isLogin) {
                jumpToMain()
            } else {
                if (configEntity.needLogin()) {
                    if (configEntity.needDeepLink() && configEntity.faceBookId().isNotBlank()) {
                        fetchAppLink(configEntity.faceBookId()) {
                            "initFaceBook $it".loge()
                            it?.let {
                                runOnUiThread {
                                    activitySplashIvFb.isVisible = true
                                }
                            } ?: kotlin.run {
                                jumpToMain()
                            }
                        }
                    } else {
                        activitySplashIvFb.isVisible = true
                    }
                } else {
                    jumpToMain()
                }
            }
        }

        activitySplashIvFb.click {
            startActivity(Intent(this@IndexActivity, LoginActivity::class.java))
        }
    }

    private var isByShowStepTwo = false

    private fun jumpToMain() {
        if (showOpenAd(activitySplashRl, isForce = true)){
            isByShowStepTwo = true
        }else{
            startActivity(Intent(this@IndexActivity, MainActivity::class.java))
            finish()
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: BusDestroyEvent) {
        finish()
    }

    override fun onPause() {
        super.onPause()
        canShowAd = false
    }

    private var canShowAd = true

    override fun onSplashAdHidden() {
        super.onSplashAdHidden()
        if (isByShowStepTwo){
            isByShowStepTwo = false
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onInterstitialAdHidden() {
        super.onInterstitialAdHidden()
        if (isByShowStepTwo){
            isByShowStepTwo = false
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

}