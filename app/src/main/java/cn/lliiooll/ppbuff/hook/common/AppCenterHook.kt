package cn.lliiooll.ppbuff.hook.common

import android.os.Build
import cn.lliiooll.ppbuff.BuildConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.PPBuff.getApplication
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.utils.PJavaUtils
import cn.lliiooll.ppbuff.utils.getModuleDebugInfo
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes


object AppCenterHook : BaseHook(
    "AppCenter数据统计", "appcenter", PHookType.DEBUG
) {
    override fun init(): Boolean {
        if (!AppCenter.isConfigured()) {
            AppCenter.start(
                getApplication(),
                "6da9e4c2-e2e4-48f2-82a1-d5b97bf4a713",
                Analytics::class.java,
                Crashes::class.java
            )
        }
        Analytics.trackEvent("onHookLoad", hashMapOf<String?, String?>().apply {
            put("host_app_package", getApplication().packageName)
            put("host_app_version_code", "${PPBuff.getHostVersionCode()}")
            put("host_app_version_name", PPBuff.getHostVersionName())
            putAll(getModuleDebugInfo())
        })
        return true
    }
}