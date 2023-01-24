package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.os.Handler
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.utils.debug
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.paramCount

object ZuiYouLiteQuickStartHook : BaseHook(
    "快速启动", "quickStart"
) {
    override fun init(): Boolean {

        Handler::class.java.findMethod(true) {
            this.name == "sendEmptyMessageDelayed" && this.paramCount == 2 && this.parameterTypes[0] == Int::class.java && this.parameterTypes[1] == Long::class.java
        }
            .hookBefore {
                if (it.args[0] == 29) {
                    if (PConfig.isUpdateHost() || !PConfig.isInited()) {
                        // 等待反混淆完毕后再跳转
                        "应用更新，阻塞直到反混淆加载完毕.".debug()
                        //it.args[0] = 666
                    }
                    it.args[1] = 1L
                }
            }


        return true
    }
}