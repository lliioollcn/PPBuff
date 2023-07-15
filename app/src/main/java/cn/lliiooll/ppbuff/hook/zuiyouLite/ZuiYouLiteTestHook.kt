package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.PPBuff.HostInfo.ZuiyouLite
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.utils.UpdateUtils
import cn.lliiooll.ppbuff.utils.async
import cn.lliiooll.ppbuff.utils.catch
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.dump
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.requireMinVersion
import cn.lliiooll.ppbuff.utils.sync
import cn.lliiooll.ppbuff.utils.toastShort
import com.github.kyuubiran.ezxhelper.utils.findAllConstructors
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.paramCount
import de.robv.android.xposed.XposedHelpers
import java.lang.RuntimeException
import java.util.Arrays
import kotlin.concurrent.thread

object ZuiYouLiteTestHook : BaseHook(
    "测试Hook", "test", PHookType.DEBUG
) {
    override fun init(): Boolean {
        if (requireMinVersion(ZuiyouLite.PP_2_67_10)) {
            "cn.xiaochuankeji.zuiyouLite.ui.main.MainTest"
        } else {
            "cn.xiaochuankeji.zuiyouLite.ui.main.MainActivity"
        }
            .findClass()
            .findMethod {
                this.name == "onCreate" && this.paramCount == 1 && this.parameterTypes[0] == Bundle::class.java
            }
            .hookAfter {
                val activity = it.thisObject as Activity
                "Buff加载成功~".toastShort(activity)
            }
/*
        "com.izuiyou.network.NetCrypto"
            .findClass()
            .findAllMethods { name == "sign" || name == "encodeAES" }
            .hookAfter {
                //val data = it.args[0] as ByteArray
                //"DID: ${Arrays.toString(data)}".debug()
                //RuntimeException().catch()
                it.dump()
            }


 */
        /*
                "com.google.android.exoplayer2.MediaItem"
                    .findClass()
                    .findAllConstructors { true }
                    .hookAfter {
                        it.args.forEach { arg ->
                            if (arg is String) {
                                if (arg.length >= 25)
                                    "Url: ${arg.subSequence(0, 25)}".toastShort()
                            }
                        }
                        //it.dump()
                        //RuntimeException().catch()
                    }

         */
        //PPBuff.getApplication().packageManager.checkSignatures()


        return true
    }
}