package cn.lliiooll.ppbuff.hook.zuiyouLite

import cn.lliiooll.ppbuff.BuildConfig
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.hook.BaseHook
import de.robv.android.xposed.XposedHelpers

object ZuiYouLiteDebugHook : BaseHook(
    "调试模式", "ppbuff_debug", PHookType.DEBUG
) {
    override fun init(): Boolean {
        XposedHelpers.setStaticBooleanField(BuildConfig::class.java, "DEBUG", isEnable())
        return true
    }

    override fun isEnable(): Boolean {
        return PConfig.boolean(label, PPBuff.isDebug())
    }

    override fun setEnable(enable: Boolean) {
        XposedHelpers.setStaticBooleanField(BuildConfig::class.java, "DEBUG", enable)
        super.setEnable(enable)
    }
}