package com.pipipi.camhd.ui.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.view.View
import android.widget.TextView
import com.pipipi.camhd.R
import com.pipipi.camhd.base.BaseActivity
import com.pipipi.camhd.utils.MessageEvent
import com.pipipi.camhd.utils.createBitmapFromView
import com.sdsmdg.tastytoast.TastyToast
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_slimming2.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.concurrent.thread


class SlimmingActivity : BaseActivity(R.layout.activity_slimming2) {
    var dialog: AlertDialog? = null
    override fun onConvert() {
        val url = intent.getStringExtra("url") as String
        skt.displayImage(url)
        EventBus.getDefault().register(this)
        cancel.setOnClickListener { finish() }
        save.setOnClickListener {
            dialog = SpotsDialog.Builder().setContext(this).build()
            dialog!!.show()
            thread {
                createBitmapFromView(main)
            }
        }
    }

    fun click(view: View) {
        when ((view as TextView).text.toString()) {
            "slimming" -> {
                setTvDrawable(slimming, R.drawable.slimming_1)
                setTvDrawable(waist, R.drawable.waist)
                setTvDrawable(leg, R.drawable.legs)
                setTvDrawable(length, R.drawable.legs_length)
                setTvDrawable(breast, R.drawable.breast)
                setTvDrawable(shoulder, R.drawable.shoulder)
            }
            "waist" -> {
                setTvDrawable(slimming, R.drawable.slimming)
                setTvDrawable(waist, R.drawable.waist_2)
                setTvDrawable(leg, R.drawable.legs)
                setTvDrawable(length, R.drawable.legs_length)
                setTvDrawable(breast, R.drawable.breast)
                setTvDrawable(shoulder, R.drawable.shoulder)
            }
            "legs" -> {
                setTvDrawable(slimming, R.drawable.slimming)
                setTvDrawable(waist, R.drawable.waist)
                setTvDrawable(leg, R.drawable.legs_3)
                setTvDrawable(length, R.drawable.legs_length)
                setTvDrawable(breast, R.drawable.breast)
                setTvDrawable(shoulder, R.drawable.shoulder)
            }
            "legs length" -> {
                setTvDrawable(slimming, R.drawable.slimming)
                setTvDrawable(waist, R.drawable.waist)
                setTvDrawable(leg, R.drawable.legs)
                setTvDrawable(length, R.drawable.legs_length_4)
                setTvDrawable(breast, R.drawable.breast)
                setTvDrawable(shoulder, R.drawable.shoulder)
            }
            "breast" -> {
                setTvDrawable(slimming, R.drawable.slimming)
                setTvDrawable(waist, R.drawable.waist)
                setTvDrawable(leg, R.drawable.legs)
                setTvDrawable(length, R.drawable.legs_length)
                setTvDrawable(breast, R.drawable.breast_5)
                setTvDrawable(shoulder, R.drawable.shoulder)
            }
            "shoulder" -> {
                setTvDrawable(slimming, R.drawable.slimming)
                setTvDrawable(waist, R.drawable.waist)
                setTvDrawable(leg, R.drawable.legs)
                setTvDrawable(length, R.drawable.legs_length)
                setTvDrawable(breast, R.drawable.breast)
                setTvDrawable(shoulder, R.drawable.shoulder_6)
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setTvDrawable(tv: TextView, id: Int) {
        val d = resources.getDrawable(id)
        d.setBounds(0, 0, d.minimumWidth, d.minimumHeight)
        tv.setCompoundDrawables(null, d, null, null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(e: MessageEvent) {
        val msg = e.getMessage()
        when (msg[0]) {
            "saveSuccess" -> {
                dialog!!.dismiss()
                TastyToast.makeText(this, "save success", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS)
                startActivity(Intent(this, CreationActivity::class.java))
                finish()
            }
            "saveError" -> {
                dialog!!.dismiss()
                TastyToast.makeText(this, "save failed", TastyToast.LENGTH_SHORT, TastyToast.ERROR)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}