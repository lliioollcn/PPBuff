package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.content.pm.ActivityInfo
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.utils.findClass
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore

object ZuiYouLiteForcedVerticalHook : BaseHook(
    "强制竖屏", "forced_vertical", PHookType.COMMON
) {
    override fun init(): Boolean {

        "cn.xiaochuankeji.zuiyouLite.ui.base.BaseActivity"
            .findClass()
            .findMethod {
                this.name == "setRequestedOrientation"
            }
            .hookBefore {
                val flag = it.args[0] as Int
                if (flag == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                    it.args[0] = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                }
            }
        return true
    }
}