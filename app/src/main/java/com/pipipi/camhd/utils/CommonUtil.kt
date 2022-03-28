package com.pipipi.camhd.utils

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.pipipi.camhd.BuildConfig
import com.pipipi.camhd.Constant
import com.pipipi.camhd.Ktx
import com.pipipi.camhd.pojo.ConfigPojo
import com.pipipi.camhd.pojo.UpdatePojo
import com.tencent.mmkv.MMKV
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.io.FileOutputStream


fun View.click(block: (View) -> Unit) {
    setOnClickListener {
        block(it)
    }
}

fun createBitmapFromView(view: View) {
    view.clearFocus()
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    if (bitmap != null) {
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        canvas.setBitmap(null)
    }
    val path = File(Environment.getExternalStorageDirectory().path + File.separator)
    val fileName = System.currentTimeMillis().toString()
    val imgFile = File(path, "$fileName.png")
    if (!imgFile.exists())
        imgFile.createNewFile()
    var fos: FileOutputStream? = null
    try {
        if (bitmap == null) return
        fos = FileOutputStream(imgFile)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()
        scanNotice(view.context, imgFile)
        EventBus.getDefault().post(MessageEvent("saveSuccess"))
        saveKeys("keys", fileName)
        MMKV.defaultMMKV()!!.encode(fileName, imgFile.absolutePath)
    } catch (e: Exception) {
        e.printStackTrace()
        EventBus.getDefault().post(MessageEvent("saveError"))
    } finally {
        fos?.flush()
        fos!!.close()
    }
}

fun saveKeys(key: String, keyValues: String) {
    var keys = MMKV.defaultMMKV()!!.decodeStringSet(key)
    if (keys == null) {
        keys = HashSet()
    }
    keys.add(keyValues)
    MMKV.defaultMMKV()!!.encode(key, keys)
}

fun scanNotice(context: Context, file: File) {
    MediaScannerConnection.scanFile(
        context,
        arrayOf(file.absolutePath),
        null,
        object : MediaScannerConnection.MediaScannerConnectionClient {
            override fun onMediaScannerConnected() {}
            override fun onScanCompleted(path: String, uri: Uri) {}
        })
}

fun isInBackground(): Boolean {
    val activityManager = app.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val appProcesses = activityManager
        .runningAppProcesses
    for (appProcess in appProcesses) {
        if (appProcess.processName == app.packageName) {
            return appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        }
    }
    return false
}

fun AppCompatActivity.requestPermission(block: () -> Unit = {}) {
    XXPermissions.with(this)
        .permission(
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        )
        .request(object : OnPermissionCallback {
            override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
                if (all) {
                    EventBus.getDefault().post(MessageEvent("onGranted"))
                } else {
                    EventBus.getDefault().post(MessageEvent("not all"))
                }
            }

            override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
                super.onDenied(permissions, never)
                EventBus.getDefault().post(MessageEvent("onDenied"))
            }
        })
}
fun dp2px(context: Context, dp: Float): Int {
    val density = context.resources.displayMetrics.density
    return (dp * density + 0.5f).toInt()
}
var adLastTime
    get() = mmkv.getLong(Constant.KEY_AD_LAST_TIME, 0)
    set(value) {
        mmkv.putLong(Constant.KEY_AD_LAST_TIME, value)
    }
var account
    get() = mmkv.getString(Constant.KEY_ACCOUNT, "") ?: ""
    set(value) {
        mmkv.putString(Constant.KEY_ACCOUNT, value)
    }

private var config
    get() = mmkv.getString(Constant.KEY_CONFIG, "") ?: ""
    set(value) {
        mmkv.putString(Constant.KEY_CONFIG, value)
    }

var adShownIndex
    get() = mmkv.getInt(Constant.KEY_AD_SHOWN_INDEX, 0)
    set(value) {
        mmkv.putInt(Constant.KEY_AD_SHOWN_INDEX, value)
    }
private var adShown
    get() = mmkv.getString(Constant.KEY_AD_SHOWN, "") ?: ""
    set(value) {
        mmkv.putString(Constant.KEY_AD_SHOWN, value)
    }
var configEntity
    get() = (config.ifBlank {
        "{}"
    }).let {
        gson.fromJson(it, ConfigPojo::class.java)
    }
    set(value) {
        config = gson.toJson(value)
    }
var adShownList
    get() = (adShown.ifBlank {
        "{}"
    }).let {
        gson.fromJson<List<Boolean>>(it, object : TypeToken<List<Boolean>>() {}.type)
    }
    set(value) {
        adShown = gson.toJson(value)
    }


private var update
    get() = mmkv.getString(Constant.KEY_UPDATE, "") ?: ""
    set(value) {
        mmkv.putString(Constant.KEY_UPDATE, value)
    }

var updateEntity
    get() = (update.ifBlank {
        "{}"
    }).let {
        gson.fromJson(it, UpdatePojo::class.java)
    }
    set(value) {
        update = gson.toJson(value)
    }

var password
    get() = mmkv.getString(Constant.KEY_PASSWORD, "") ?: ""
    set(value) {
        mmkv.putString(Constant.KEY_PASSWORD, value)
    }

var isLogin
    get() = mmkv.getBoolean(Constant.KEY_IS_LOGIN, false)
    set(value) {
        mmkv.putBoolean(Constant.KEY_IS_LOGIN, value)
    }

val gson by lazy {
    Gson()
}

val mmkv by lazy {
    MMKV.defaultMMKV()
}

val app by lazy {
    Ktx.getInstance().app
}


fun <T> T.loge(tag: String = "defaultTag") {
    if (BuildConfig.DEBUG) {
        var content = toString()
        val segmentSize = 3 * 1024
        val length = content.length.toLong()
        if (length <= segmentSize) {
            Log.e(tag, content)
        } else {
            while (content.length > segmentSize) {
                val logContent = content.substring(0, segmentSize)
                content = content.replace(logContent, "")
                Log.e(tag, logContent)
            }
            Log.e(tag, content)
        }
    }
}
