package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.os.Handler
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.utils.debug
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.paramCount

object ZuiYouLiteQuickStartHook : BaseHook(
    "快速启动", "quickStart", PHookType.DEBUG
) {
    var first = true
    var inited = false
    override fun init(): Boolean {
        if (inited) return true
        if (PConfig.boolean("is_first_launch_pp", true)) {
            "第一次启动，不自动跳转".debug()
            return true
        }

        Handler::class.java.findMethod(true) {
            this.name == "sendEmptyMessageDelayed" && this.paramCount == 2 && this.parameterTypes[0] == Int::class.java && this.parameterTypes[1] == Long::class.java
        }
            .hookBefore {
                if (it.args[0] == 29) {
                    it.args[1] = 1L
                }
            }
        inited = true
        return true
    }
}