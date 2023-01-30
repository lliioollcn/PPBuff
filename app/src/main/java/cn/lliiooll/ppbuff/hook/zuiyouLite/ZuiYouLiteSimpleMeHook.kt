package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.app.Activity
import android.os.Bundle
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.hook.PHookType
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.toastShort
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.paramCount

object ZuiYouLiteSimpleMeHook : BaseHook(
    "精简\"我的界面\"", "simpleMe", PHookType.SIMPLE

) {
    override fun init(): Boolean {


        return true
    }
}