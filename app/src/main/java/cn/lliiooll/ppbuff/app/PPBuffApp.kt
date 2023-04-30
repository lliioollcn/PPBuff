package cn.lliiooll.ppbuff.app

import android.app.Application
import android.os.Build
import cn.lliiooll.ppbuff.BuildConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.utils.Natives
import cn.lliiooll.ppbuff.utils.PJavaUtils
import cn.lliiooll.ppbuff.utils.getModuleDebugInfo
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.tencent.mmkv.MMKV

class PPBuffApp : Application() {

    override fun onCreate() {
        super.onCreate()
        PPBuff.init(this)
        Natives.init()
        if (!AppCenter.isConfigured()) {
            AppCenter.start(
                this,
                "6da9e4c2-e2e4-48f2-82a1-d5b97bf4a713",
                Analytics::class.java,
                Crashes::class.java
            )
        }

        Analytics.trackEvent("onModuleStart", hashMapOf<String?, String?>().apply {
            putAll(getModuleDebugInfo())
        })

    }
}