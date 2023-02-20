package cn.lliiooll.ppbuff.hook.zuiyouLite

import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter

object ZuiYouLiteAntiAntiDebugHook : BaseHook(
    "反反调试", "anti_anti_debug", PHookType.DEBUG
) {
    override fun init(): Boolean {
        "android.os.Debug"
            .findClass()
            .findMethod {
                name == "isDebuggerConnected"
            }
            .hookAfter {
                "绕过反调试检测: ${it.result}".debug()
                it.result = false
            }
        return true
    }

    override fun isEnable(): Boolean {
        return PConfig.boolean(label, false)
    }
}