package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.app.Activity
import android.os.Bundle
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.toastShort
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.paramCount

object ZuiYouLiteAntiVoiceRoomHook : BaseHook(
    "去除语音房", "anti_voice"
) {
    override fun init(): Boolean {

        "com.youyisia.voices.sdk.api.HYVoiceRoomSdk"
            .findClass()
            .findMethod {
                this.name == "isInited"
            }
            .hookAfter {
                it.result = true
                "阻止语音房的加载...".debug()
            }
        return true
    }
}