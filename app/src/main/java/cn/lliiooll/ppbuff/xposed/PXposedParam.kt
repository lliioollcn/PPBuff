package cn.lliiooll.ppbuff.xposed

import android.content.pm.ApplicationInfo

data class PXposedParam(
    val packageName: String,
    val processName: String,
    val classLoader: ClassLoader,
    val appInfo: ApplicationInfo,
)
