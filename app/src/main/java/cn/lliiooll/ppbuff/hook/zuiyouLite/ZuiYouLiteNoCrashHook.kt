package cn.lliiooll.ppbuff.hook.zuiyouLite

import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.utils.catch
import cn.lliiooll.ppbuff.utils.debug
import com.github.kyuubiran.ezxhelper.utils.findAllConstructors
import com.github.kyuubiran.ezxhelper.utils.hookAfter

object ZuiYouLiteNoCrashHook : BaseHook(
    "捕捉报错", "crash_catch", PHookType.DEBUG
) {
    override fun init(): Boolean {
        Throwable::class.java
            .findAllConstructors {
                true
            }
            .hookAfter {
                "报错: ${it.thisObject.javaClass.name}".debug()
                val error = it.thisObject as Throwable
                error.catch()
            }
        return true
    }
}