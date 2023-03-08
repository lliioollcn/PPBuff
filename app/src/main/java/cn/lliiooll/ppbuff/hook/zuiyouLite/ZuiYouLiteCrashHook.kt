package cn.lliiooll.ppbuff.hook.zuiyouLite

import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PViewType
import cn.lliiooll.ppbuff.hook.BaseHook

object ZuiYouLiteCrashHook : BaseHook(
    "手动抛错", "crash_custom", PHookType.DEBUG
) {


    override fun init(): Boolean {

        return true
    }

    override fun isEnable(): Boolean {
        return true
    }


    override fun router(): Boolean {
        return false
    }

    override fun click() {
        throw RuntimeException("手动抛错")

    }

    override fun needCustomClick(): Boolean {
        return true
    }

    override fun view(): PViewType {
        return PViewType.CUSTOM
    }
}