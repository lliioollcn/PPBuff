package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.app.Activity
import android.os.Bundle
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.utils.UpdateUtils
import cn.lliiooll.ppbuff.utils.catch
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.dump
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.sync
import cn.lliiooll.ppbuff.utils.toastShort
import com.github.kyuubiran.ezxhelper.utils.findAllConstructors
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.paramCount
import de.robv.android.xposed.XposedHelpers
import java.lang.RuntimeException
import kotlin.concurrent.thread

object ZuiYouLiteTestHook : BaseHook(
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
                "Buff加载成功~".toastShort(activity)
                thread {
                    if (UpdateUtils.hasUpdate()) {
                        sync {
                            "发现模块新版本，请及时更新".toastShort()
                        }
                    }
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