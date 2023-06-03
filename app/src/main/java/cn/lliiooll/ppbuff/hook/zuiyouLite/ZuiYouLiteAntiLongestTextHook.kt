package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.app.Activity
import android.os.Bundle
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.utils.UpdateUtils
import cn.lliiooll.ppbuff.utils.async
import cn.lliiooll.ppbuff.utils.catch
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.dump
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.sync
import cn.lliiooll.ppbuff.utils.toastShort
import com.github.kyuubiran.ezxhelper.utils.findAllConstructors
import com.github.kyuubiran.ezxhelper.utils.findAllFields
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.findField
import com.github.kyuubiran.ezxhelper.utils.findFieldObject
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.paramCount
import de.robv.android.xposed.XposedHelpers
import java.lang.RuntimeException
import kotlin.concurrent.thread

object ZuiYouLiteAntiLongestTextHook : BaseHook(
    "屏蔽评论超长刷屏文本", "anti_longest_text", PHookType.SIMPLE
) {
    override fun init(): Boolean {
        "cn.xiaochuankeji.zuiyouLite.ui.postlist.weight.PostContentView"
            .findClass()
            .findMethod {
                this.paramCount == 0 && this.returnType != Void::class.java
            }
            .hookAfter {
                val ins = it.thisObject
                var added = false
                ins.javaClass.findAllFields {
                    type == String::class.java
                }.forEach { field ->
                    val textObj = field.get(ins)
                    if (textObj != null) {
                        var text = textObj as String
                        if (text.contains("\n\n\n\n")) {
                            while (text.contains("\n\n")) {
                                text = text.replace("\n\n", "\n")
                            }
                            if (!added) {
                                text += "\n\n[已自动过滤多余换行]"
                                added = true
                            }
                            field.set(ins, text)
                        }

                    }
                }
                added = false
            }

        /*
        "com.izuiyou.network.NetCrypto"
            .findClass()
            .findMethod { true }
            .hookAfter {
                it.dump()
                //RuntimeException().catch()
            }
        //PPBuff.getApplication().packageManager.checkSignatures()

         */
        return true
    }
}