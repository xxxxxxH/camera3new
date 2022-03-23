package com.pipipi.camhd.ui.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Handler
import android.os.Message
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.lcw.library.stickerview.Sticker
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import com.pipipi.camhd.R
import com.pipipi.camhd.base.BaseActivity
import com.pipipi.camhd.item.StickerItem
import com.pipipi.camhd.utils.AssetsManager
import com.pipipi.camhd.utils.MessageEvent
import com.pipipi.camhd.utils.createBitmapFromView
import com.sdsmdg.tastytoast.TastyToast
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_sticker.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.concurrent.thread

class StickerActivity : BaseActivity(R.layout.activity_sticker) {

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
                        adapter.add(StickerItem(this@StickerActivity, it))
                    }
                    recycler.layoutManager = GridLayoutManager(this@StickerActivity, 3)
                    recycler.adapter = adapter
                    adapter.onClickListener = View.OnClickListener {
                        val holder: AdapterViewHolder = AdapterUtils.getHolder(it)
                        val bitmap = data[holder.bindingAdapterPosition]
                        val s = Sticker(this@StickerActivity, bitmap)
                        stickers.addSticker(s)
                    }
                }
            }
        }
    }

    override fun onConvert() {
        EventBus.getDefault().register(this)
        val url = intent.getStringExtra("url")
        iv.displayImage(url)
        initData()
    }

    private fun initData() {
        thread {
            val data = AssetsManager.get().getStickers(this)
            val msg = Message()
            msg.what = 1
            msg.obj = data
            handler.sendMessage(msg)
        }
    }

    fun optionClick(view: View) {
        when (view.contentDescription) {
            "cancel" -> {
                finish()
            }
            "save" -> {
                dialog = SpotsDialog.Builder().setContext(this).build()
                dialog!!.show()
                thread {
                    createBitmapFromView(main)
                }
            }
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