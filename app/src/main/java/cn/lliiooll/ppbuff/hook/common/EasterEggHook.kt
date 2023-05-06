package cn.lliiooll.ppbuff.hook.common

import android.view.View
import cn.hutool.core.date.DateUtil
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PViewType
import cn.lliiooll.ppbuff.hook.BaseHook
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import java.util.Date


object EasterEggHook : BaseHook(
    "easteregg", "easteregg", PHookType.HIDE
) {
    override fun init(): Boolean {
        val time = PConfig.numberEx("4_1", System.currentTimeMillis())
        if (System.currentTimeMillis() - time < 1000 * 60 * 60 * 24) {
            return true
        }
        val date = Date()
        if (DateUtil.month(date) == 4 && DateUtil.dayOfMonth(date) == 1) {
            View::class.java
                .findMethod {
                    name == "onTouchEvent"
                }
                .hookAfter {
                    val view = it.thisObject as View
                    view.visibility = View.GONE
                }
            PConfig.set("4_1", System.currentTimeMillis())
        }
        return true
    }

    override fun isEnable(): Boolean {
        return true
    }

    override fun view(): PViewType {
        return PViewType.NONE
    }
}