package cn.lliiooll.ppbuff.hook.zuiyouLite

import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.hook.BaseHook

object ZuiYouLiteDetailLocationHook : BaseHook(
    "IP信息精准到市(部分没有)", "ip_detail_location", PHookType.PLAY
) {


    override fun init(): Boolean {
        return true
    }

    override fun isEnable(): Boolean {
        return PConfig.boolean(label, false)
    }

}