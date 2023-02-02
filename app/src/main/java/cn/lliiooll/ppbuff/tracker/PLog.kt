package cn.lliiooll.ppbuff.tracker

import android.os.Build
import android.util.Log
import cn.lliiooll.ppbuff.BuildConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.utils.getModuleDebugInfo
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog
import kotlin.concurrent.thread


class PLog {
    companion object {
        fun i(msg: String) {
            Log.i("PPBuff", "[INFO] >> $msg")
        }

        fun d(msg: String) {
            if (PPBuff.isDebug()) log("DEBUG", msg)
        }

        fun e(msg: String) {
            Log.e("PPBuff", "[ERROR] >> $msg")
        }


        fun log(level: String, msg: String) {
            Log.d("PPBuff", "[$level] >> $msg")
        }

        fun catch(throwable: Throwable) {
            if (!BuildConfig.DEBUG) {
                Crashes.trackError(throwable, hashMapOf<String, String>().apply {
                    putAll(getModuleDebugInfo())
                }, arrayListOf())

            }
            e("发生了一个错误: ${throwable.message}")
            e("以下是堆栈: ")
            for (s in throwable.stackTrace) {
                e("     $s")
            }


        }

        fun c(throwable: Throwable) {
            catch(throwable)

        }
    }
}