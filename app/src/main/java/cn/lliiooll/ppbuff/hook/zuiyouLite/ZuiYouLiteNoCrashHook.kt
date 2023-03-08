package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.os.Handler
import android.os.Looper
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.utils.catch
import cn.lliiooll.ppbuff.utils.toastShort
import kotlin.system.exitProcess

object ZuiYouLiteNoCrashHook : BaseHook(
    "捕捉报错", "crash_catch", PHookType.DEBUG
) {
    override fun init(): Boolean {
        /*
        Throwable::class.java
            .findAllConstructors {
                true
            }
            .hookAfter {
                "报错: ${it.thisObject.javaClass.name}".debug()
                val error = it.thisObject as Throwable
                error.catch()
            }

         */
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            if (t.name.equals("main")) {
                "皮皮搞笑崩溃，即将退出".toastShort()
                e.catch()
                return@setDefaultUncaughtExceptionHandler
            }
            e.catch()
        }

        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                    break
                } catch (e: Exception) {
                    e.catch()
                } catch (th: Error) {
                    th.catch()
                    "皮皮搞笑崩溃，即将退出".toastShort()
                    throw th
                } catch (th: Throwable) {
                    th.catch()
                    "皮皮搞笑崩溃，即将退出".toastShort()
                    throw th
                }
            }
        }
        return true
    }
}