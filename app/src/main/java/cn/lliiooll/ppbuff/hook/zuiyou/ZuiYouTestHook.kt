package cn.lliiooll.ppbuff.hook.zuiyou

import android.app.Activity
import android.os.Bundle
import cn.hutool.core.date.DateUtil
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteEulaHook
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.toastShort
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.paramCount
import java.util.Date

object ZuiYouTestHook : BaseHook(
    "测试Hook", "test", PHookType.DEBUG
) {
    override fun init(): Boolean {

        "cn.xiaochuankeji.tieba.ui.home.page.PageMainActivity"
            .findClass()
            .findMethod {
                this.name == "onCreate" && this.paramCount == 1 && this.parameterTypes[0] == Bundle::class.java
            }
            .hookAfter {
                val activity = it.thisObject as Activity
                "Buff加载成功~".toastShort(activity)
                if (DateUtil.betweenDay(
                        Date(),
                        Date(PConfig.numberEx("last_pop", 0L)),
                        true
                    ) >= 7 || PConfig.isUpdateHost()
                ) {
                    ZuiYouLiteEulaHook.showPop(activity)
                    PConfig.set("last_pop", System.currentTimeMillis())

                }
            }

        return true
    }
}