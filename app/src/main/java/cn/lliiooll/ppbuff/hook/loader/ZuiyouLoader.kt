package cn.lliiooll.ppbuff.hook.loader

import android.app.Activity
import android.os.Handler
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.hook.common.AppCenterHook
import cn.lliiooll.ppbuff.hook.common.XiaoChuanAntiADHook
import cn.lliiooll.ppbuff.hook.common.XiaoChuanAntiZyBuffHook
import cn.lliiooll.ppbuff.hook.common.XiaoChuanEvilInstrumentationHook
import cn.lliiooll.ppbuff.hook.needDeobfs
import cn.lliiooll.ppbuff.hook.zuiyou.ZuiYouQuickStartHook
import cn.lliiooll.ppbuff.hook.zuiyou.ZuiYouSettingHook
import cn.lliiooll.ppbuff.hook.zuiyou.ZuiYouSimpleMeHook
import cn.lliiooll.ppbuff.hook.zuiyou.ZuiYouTestHook
import cn.lliiooll.ppbuff.utils.async
import cn.lliiooll.ppbuff.utils.catch
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.error
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.invokeMethod
import com.github.kyuubiran.ezxhelper.utils.findField
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import io.luckypray.dexkit.DexKitBridge
import io.luckypray.dexkit.builder.BatchFindArgs

/**
 * 最右Hook加载器，提供启动界面加载动画
 */
object ZuiyouLoader : BaseLoader() {
    var inited = false
    override fun load() {
        if (inited) return
        val deobfs = hooks().needDeobfs {}
        if (PConfig.isUpdateHost() || deobfs > 0) {
            try {
                "开始清理反混淆信息...".debug()
                PConfig.clearCache()
                "开始进行反混淆操作...".debug()
                val dexkit = DexKitBridge.create(PPBuff.getHostPath())
                var deobf = 0
                hooks().needDeobfs {
                    "开始为 ${it.name} 进行反混淆操作...".debug()
                    val result = if (it.needCustomDeobf()) {
                        it.customDebof(dexkit)
                    } else {
                        dexkit?.batchFindClassesUsingStrings(BatchFindArgs.build {
                            queryMap(it.deobfMap())
                        })
                    }
                    "开始为 ${it.name} 缓存反混淆...".debug()
                    PConfig.cache(result)
                    deobf++
                }
                PConfig.init(true)
                inited = true
                if (PConfig.isUpdateHost()) PConfig.updateHost()
                dexkit?.close()
            } catch (e: Throwable) {
                e.catch()
            }
        }

        XiaoChuanAntiADHook.init()
        ZuiYouQuickStartHook.init()
        XiaoChuanAntiZyBuffHook.init()
        XiaoChuanEvilInstrumentationHook.init()
        ZuiYouSettingHook.init()
        ZuiYouTestHook.init()
        if (PConfig.boolean("is_first_launch_pp", true)) {
            "第一次启动".debug()
            PConfig.set("is_first_launch_pp", false)
        } else {
            "cn.xiaochuankeji.tieba.ui.base.SplashActivity"
                .findClass()
                .findMethod { name == "onCreate" }
                .hookAfter {
                    val activity = it.thisObject as Activity
                    activity.javaClass
                        .findField {
                            this.type.superclass == Handler::class.java
                        }
                        .invokeMethod(activity, "sendEmptyMessage", 27)
                    "跳过启动".debug()
                }
        }
        async {
            hooks().forEach { h ->
                try {
                    "开始加载hook: ${h.name}".debug()
                    if (
                        h.isEnable() &&
                        !h.init()
                    ) {
                        "hook加载失败: ${h.name}".debug()
                    }
                } catch (e: Throwable) {
                    "Hook ${h.name} 加载失败!".error()
                    e.catch()
                }
            }
        }
    }

    override fun hooks(): List<BaseHook> {
        return arrayListOf<BaseHook>().apply {
            add(AppCenterHook)// AppCenter统计
            add(XiaoChuanAntiZyBuffHook)//
            add(XiaoChuanEvilInstrumentationHook)//
            //add(ZuiYouSimpleMeHook)//
        }
    }

}