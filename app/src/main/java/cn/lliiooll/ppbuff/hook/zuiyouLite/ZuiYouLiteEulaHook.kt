package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.app.Activity
import android.os.Bundle
import cn.lliiooll.ppbuff.PConfig
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
import cn.lliiooll.ppbuff.utils.toastLong
import cn.lliiooll.ppbuff.utils.toastShort
import com.github.kyuubiran.ezxhelper.utils.findAllConstructors
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.paramCount
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.PopNotification
import com.kongzue.dialogx.style.IOSStyle
import de.robv.android.xposed.XposedHelpers
import java.lang.RuntimeException
import kotlin.concurrent.thread
import kotlin.system.exitProcess

object ZuiYouLiteEulaHook : BaseHook(
    "测试Hook", "test", PHookType.DEBUG
) {
    override fun init(): Boolean {
        "cn.xiaochuankeji.zuiyouLite.ui.main.MainActivity"
            .findClass()
            .findMethod {
                this.name == "onCreate" && this.paramCount == 1 && this.parameterTypes[0] == Bundle::class.java
            }
            .hookAfter {
                val activity = it.thisObject as Activity
                sync {
                    MessageDialog
                        .build()
                        .setStyle(IOSStyle.style())
                        .setTitle("用户协议")
                        .setMessage(PPBuff.loadEula())
                        .setOkButton("接受") { _, _ ->
                            PConfig.set("first_inited", false)
                            PopNotification.build()
                                .setStyle(IOSStyle.style())
                                .setTitle("提示")
                                .setMessage("重启以生效")
                                .show(activity)
                                .noAutoDismiss()
                            false
                        }
                        .setCancelButton("拒绝") { _, _ ->
                            false
                        }
                        .show(activity)
                }
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