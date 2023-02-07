package cn.lliiooll.ppbuff.tracker

import android.util.Log
import cn.lliiooll.ppbuff.BuildConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.utils.getModuleDebugInfo
import cn.lliiooll.ppbuff.utils.plusAssign
import com.microsoft.appcenter.crashes.Crashes
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.lang.StringBuilder


class PLog {
    companion object {
        fun i(msg: String) {
            if (PPBuff.isDebug())
                Log.i("PPBuff", "[INFO] >> $msg")
            else
                XposedBridge.log("PPBuff: [INFO] >> $msg")
        }

        fun d(msg: String) {
            if (PPBuff.isDebug())
                log("DEBUG", msg)
        }

        fun e(msg: String) {
            if (PPBuff.isDebug())
                Log.e("PPBuff", "[ERROR] >> $msg")
            else
                XposedBridge.log("PPBuff: [ERROR] >> $msg")
        }


        fun log(level: String, msg: String) {
            if (PPBuff.isDebug())
                Log.d("PPBuff", "[$level] >> $msg")
            else
                XposedBridge.log("PPBuff: [$level] >> $msg")
        }

        fun catch(throwable: Throwable) {
            if (!BuildConfig.DEBUG) {
                Crashes.trackError(throwable, hashMapOf<String, String>().apply {
                    putAll(getModuleDebugInfo())
                }, arrayListOf())
            }
            val sb = StringBuilder()
            sb += "发生了一个错误: ${throwable.message}"
            sb += "以下是堆栈: "
            for (s in throwable.stackTrace) {
                sb += "     $s"
            }
            e(sb.toString())
        }

        fun c(throwable: Throwable) {
            catch(throwable)

        }
    }
}