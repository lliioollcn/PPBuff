package cn.lliiooll.ppbuff.hook.zuiyouLite

import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.hook.BaseHook

object ZuiYouLiteAutoSignHook : BaseHook(
    "自动签到", "auto_sign", PHookType.PLAY
) {


    override fun init(): Boolean {

        return ZuiYouLiteWebTokenHook.init()
    }

}