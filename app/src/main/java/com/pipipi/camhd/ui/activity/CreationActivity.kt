package com.pipipi.camhd.ui.activity

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import com.pipipi.camhd.R
import com.pipipi.camhd.base.BaseActivity
import com.pipipi.camhd.item.CreationItem
import com.tencent.mmkv.MMKV
import kotlinx.android.synthetic.main.activity_creation.*

class CreationActivity : BaseActivity(R.layout.activity_creation) {
    override fun onConvert() {
        initData()
    }

    private fun initData() {
        val keySet = MMKV.defaultMMKV()!!.decodeStringSet("keys") as HashSet?
        val data = ArrayList<String>()
        if (keySet != null) {
            for (item in keySet) {
                MMKV.defaultMMKV()!!.decodeString(item)?.let {
                    data.add(it)
                }
            }
        }
        if (data.size > 0) {
            val adapter = RecyclerAdapter()
            data.forEach {
                adapter.add(CreationItem(this, it))
            }
            recycler.layoutManager = GridLayoutManager(this, 3)
            recycler.adapter = adapter
            adapter.onClickListener = View.OnClickListener {
                val holder: AdapterViewHolder = AdapterUtils.getHolder(it)
                val url = data[holder.bindingAdapterPosition]
                val i = Intent(this, PreviewActivity::class.java)
                i.putExtra("url", url)
                startActivity(i)
            }
        }
    }
}