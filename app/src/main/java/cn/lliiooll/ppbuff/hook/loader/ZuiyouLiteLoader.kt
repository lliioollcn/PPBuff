package cn.lliiooll.ppbuff.hook.loader

import android.app.Activity
import android.os.Handler
import android.widget.ProgressBar
import android.widget.TextView
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.R
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.hook.common.AppCenterHook
import cn.lliiooll.ppbuff.hook.common.XiaoChuanAntiADHook
import cn.lliiooll.ppbuff.hook.common.XiaoChuanAntiZyBuffHook
import cn.lliiooll.ppbuff.hook.common.XiaoChuanEvilInstrumentationHook
import cn.lliiooll.ppbuff.hook.needDeobfs
import cn.lliiooll.ppbuff.hook.notNeedDeobfs
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteAntiAntiDebugHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteAntiUpdateHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteAntiVoiceRoomHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteAutoFollowHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteAutoTaskHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteCrashHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteDebugHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteDetailCommentTimeHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteDetailLocationHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteForcedVerticalHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteNoCrashHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteNoWaterMarkHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteNovelVipHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteQuickStartHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteSettingHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteSimpleMeHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteSimplePostHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteTestHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteWebTokenHook
import cn.lliiooll.ppbuff.utils.*
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.findField
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import io.luckypray.dexkit.DexKitBridge
import io.luckypray.dexkit.builder.BatchFindArgs

/**
 * ????????????Hook??????????????????????????????????????????
 */
object ZuiyouLiteLoader : BaseLoader() {

    var inited = false
    override fun load() {
        if (inited) return

        val deobfs = hooks().size - hooks().notNeedDeobfs { hook ->
            try {
                // ????????? ??????/???????????? ????????????hook
                if (hook.isEnable() && !hook.init()) {
                    //if (!hook.init()) {
                    "Hook ${hook.name} ????????????!".error()
                }
                "Hook ${hook.name} ????????????: ${hook.isEnable()}".debug()
            } catch (e: Throwable) {
                "Hook ${hook.name} ????????????!".error()
                e.catch()
            }
        }
        // ????????????????????????????????????????????????hook

        "cn.xiaochuankeji.zuiyouLite.ui.splash.SplashActivity"
            .findClass()
            .findMethod { name == "onCreate" }
            .hookAfter {
                val activity = it.thisObject as Activity
                EzXHelperInit.addModuleAssetPath(activity)
                if (PConfig.isUpdateHost() || deobfs > 0) {
                    try {
                        val splash = activity.inflate(R.layout.pp_spalsh)
                        activity.setContentView(splash)
                        val text = splash.findViewById<TextView>(R.id.spalsh_text)
                        val bar = splash.findViewById<ProgressBar>(R.id.spalsh_bar)
                        bar.max = deobfs
                        "???????????????????????????".debug()
                        async {
                            val dexkit = DexKitBridge.create(PPBuff.getHostPath())
                            var deobf = 0
                            hooks().needDeobfs {
                                "????????? ${it.name} ?????????????????????...".debug()
                                val result = if (it.needCustomDeobf()) {
                                    it.customDebof(dexkit)
                                } else {
                                    dexkit?.batchFindClassesUsingStrings(BatchFindArgs.build {
                                        sync {
                                            bar.progress = deobf
                                            text.text = "????????? ${it.name} ?????????????????????"
                                        }
                                        queryMap(it.deobfMap())
                                    })
                                }
                                "????????? ${it.name} ???????????????...".debug()
                                PConfig.cache(result)
                                deobf++
                                sync {
                                    bar.progress = deobf
                                }
                                try {
                                    "????????????hook: ${it.name}".debug()
                                    if (it.isEnable() && !it.init()) {
                                        //if (!it.init()) {
                                        "hook????????????: ${it.name}".debug()
                                    }
                                } catch (e: Throwable) {
                                    "Hook ${it.name} ????????????!".error()
                                    e.catch()
                                }
                            }
                            sync {
                                bar.max = 1
                                bar.progress = 1
                                text.text = "????????????~"
                                PConfig.init(true)
                                inited = true
                                if (PConfig.isUpdateHost()) PConfig.updateHost()
                                activity.javaClass
                                    .findField {
                                        this.type == Handler::class.java
                                    }
                                    .invokeMethod(activity, "sendEmptyMessageDelayed", 29, 2000L)

                            }
                            dexkit?.close()
                        }
                    } catch (e: Throwable) {
                        e.catch()
                    }
                } else {
                    PConfig.init(true)
                    if (!PConfig.boolean("is_first_launch_pp", true)) {
                        activity.javaClass
                            .findField {
                                this.type == Handler::class.java
                            }
                            .invokeMethod(activity, "sendEmptyMessage", 29)
                    } else {
                        "?????????????????????????????????".debug()
                        PConfig.set("is_first_launch_pp", false)
                    }
                }

            }

    }

    override fun hooks(): List<BaseHook> {
        return arrayListOf<BaseHook>().apply {
            add(ZuiYouLiteNoCrashHook)// ????????????
            add(ZuiYouLiteDebugHook)// ??????Hook
            add(ZuiYouLiteTestHook)// ??????Hook
            add(AppCenterHook)// AppCenter??????
            add(ZuiYouLiteQuickStartHook)// ????????????
            add(XiaoChuanAntiADHook)// ?????????
            add(XiaoChuanAntiZyBuffHook)// ???ZyBuff
            add(ZuiYouLiteNovelVipHook)// ????????????vip
            add(XiaoChuanEvilInstrumentationHook)// ???EvilInstrumentatio
            add(ZuiYouLiteAntiVoiceRoomHook)// ????????????
            add(ZuiYouLiteSimpleMeHook)// ??????"??????"
            add(ZuiYouLiteSimplePostHook)// ??????????????????
            add(ZuiYouLiteSettingHook)// ????????????
            add(ZuiYouLiteDetailLocationHook)// IP????????????
            add(ZuiYouLiteDetailCommentTimeHook)// ??????????????????
            add(ZuiYouLiteNoWaterMarkHook)// ?????????
            add(ZuiYouLiteForcedVerticalHook)// ????????????????????????
            add(ZuiYouLiteAutoTaskHook)// ????????????
            add(ZuiYouLiteWebTokenHook)// ??????Token
            add(ZuiYouLiteAntiAntiDebugHook)// ????????????
            add(ZuiYouLiteCrashHook)// ????????????
            add(ZuiYouLiteAutoFollowHook)// ????????????
            add(ZuiYouLiteAntiUpdateHook)// ????????????

            //add(ZuiYouLiteWebTaskHook)// ??????????????????
        }
    }

}