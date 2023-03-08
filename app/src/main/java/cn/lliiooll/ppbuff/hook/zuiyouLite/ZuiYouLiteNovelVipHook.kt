package cn.lliiooll.ppbuff.hook.zuiyouLite

import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.utils.dump
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.toastShort
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.hookAfter

object ZuiYouLiteNovelVipHook : BaseHook(
    "破解小说卡", "pp_novel_vip", PHookType.COMMON
) {
    override fun init(): Boolean {
        /*
        "j.g.v.a.a"
            .findClass()
            .findAllMethods {
                name != "r" && name != "q" && name != "u" && name != "g"
            }
            .hookAfter {
                it.dump()
            }

         */
        return true
    }

    override fun click() {
        "等一等，还没做呢".toastShort()
    }

    override fun needCustomClick(): Boolean {
        return true
    }
}