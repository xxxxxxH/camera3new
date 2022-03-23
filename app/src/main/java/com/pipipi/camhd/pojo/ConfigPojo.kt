package com.pipipi.camhd.pojo

import kotlin.random.Random

data class ConfigPojo(
    //应用名称
    val app_name: String? = "",
    //深度链接开关，为1表示开启，为0表示关闭
    val d: Int,
    //以逗号分隔，前2个值表示5次调用展示插屏，只实际展示2次；第3个值表示插屏展示的时间间隔；第4、第5个值是VPN专用的，相机不需要处理。
    val ext1: String? = "",
    val ext2: String? = "",
    //插屏代替开屏开关，为1表示开启，为0表示关闭
    val i: Int,
    //FacebookID，前端需根据此ID进行FB追踪和FB深度链接的处理
    val id: String? = "",
    //info敏感信息，解密后如下
    val info: String? = "",
    //登录开关，为1表示开启，为0表示关闭
    val l: Int,
    //登录面板关闭时的插屏展示率，默认为50，表示50%的概率
    val lr: String? = "",
) : Ipojo {
    fun needLogin() = l == 1

    fun needDeepLink() = d == 1

    fun faceBookId() = id ?: ""

    fun httpUrl() = ext2 ?: ""

    fun isOpenAdReplacedByInsertAd() = i == 1

    fun isCanShowInsertAd() = needLogin()

    fun isCanShowByPercent() = Random.nextInt(1, 101) <= lr?.toIntOrNull() ?: 0

    fun insertAdOffset() = ext1?.split(",")?.getOrNull(2)?.toIntOrNull() ?: -1
}
