package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.app.Activity
import android.content.pm.ActivityInfo
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore

object ZuiYouLiteForcedVerticalHook : BaseHook(
    "强制竖屏", "forced_vertical", PHookType.COMMON
) {
    override fun init(): Boolean {

        Activity::class.java
            .findMethod {
                this.name == "setRequestedOrientation"
            }
            .hookBefore {
                val flag = it.args[0] as Int
                if (flag == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    it.args[0] = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
                "修改为竖屏".debug()
            }
        return true
    }
}