package com.pipipi.camhd.ui.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.os.Message
import androidx.recyclerview.widget.GridLayoutManager
import com.pacific.adapter.RecyclerAdapter
import com.pipipi.camhd.R
import com.pipipi.camhd.base.BaseActivity
import com.pipipi.camhd.item.AgeItem
import com.pipipi.camhd.utils.AssetsManager
import com.pipipi.camhd.utils.MessageEvent
import com.pipipi.camhd.utils.createBitmapFromView
import com.sdsmdg.tastytoast.TastyToast
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_age.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.concurrent.thread

class AgeActivity : BaseActivity(R.layout.activity_age) {
    var dialog: AlertDialog? = null
    private val handler: Handler = @SuppressLint("HandlerLeak")
    object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                1 -> {
                    val data: ArrayList<Bitmap> = msg.obj as ArrayList<Bitmap>
                    val adapter = RecyclerAdapter()
                    data.forEach {
                        adapter.add(AgeItem(this@AgeActivity, it))
                    }
                    recycler.layoutManager = GridLayoutManager(this@AgeActivity, 4)
                    recycler.adapter = adapter
                }
            }
        }
    }

    override fun onConvert() {
        EventBus.getDefault().register(this)
        val url = intent.getStringExtra("url")
        iv.displayImage(url)
        initData()
        cancel.setOnClickListener { finish() }
        save.setOnClickListener {
            dialog = SpotsDialog.Builder().setContext(this).build()
            dialog!!.show()
            thread {
                createBitmapFromView(main)
            }
        }
    }

    private fun initData() {
        thread {
            val data = AssetsManager.get().getAges(this)
            val msg = Message()
            msg.what = 1
            msg.obj = data
            handler.sendMessage(msg)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
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
}