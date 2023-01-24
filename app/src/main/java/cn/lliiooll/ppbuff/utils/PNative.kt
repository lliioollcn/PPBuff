package cn.lliiooll.ppbuff.utils

import android.app.Application
import android.os.Build
import cn.lliiooll.ppbuff.PPBuff
import com.tencent.mmkv.MMKV

object PNative {
    val libList = arrayListOf<String>().apply {
        add("dexkit")
        add("mmkv")
    }

    fun init(app: Application) {
        try {
            if (PPBuff.isInHostApp()) {
                val mmkvDir = app.getExternalFilesDir("buffMMKV")?.checkDir()
                MMKV.initialize(app, mmkvDir?.absolutePath) {
                    libList.forEach {
                        "尝试加载$it".debug()
                        System.load("${PPBuff.getModulePath()}!/lib/${PPBuff.getAbiForLibrary()}/lib${it}.so")
                    }
                }
            } else {
                libList.forEach {
                    "尝试加载$it".debug()
                    System.loadLibrary(it)
                }
            }
        } catch (e: Throwable) {
            e.catch()
        }

    }
}