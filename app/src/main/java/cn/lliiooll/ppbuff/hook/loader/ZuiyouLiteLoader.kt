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
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteAntiVoiceRoomHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteAutoTaskHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteDebugHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteDetailCommentTimeHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteDetailLocationHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteForcedVerticalHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteNoCrashHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteNoWaterMarkHook
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
 * 皮皮搞笑Hook加载器，提供启动界面加载动画
 */
object ZuiyouLiteLoader : BaseLoader() {

    var inited = false
    override fun load() {
        if (inited) return

        val deobfs = hooks().size - hooks().notNeedDeobfs { hook ->
            try {
                // 先加载 不用/已经缓存 反混淆的hook
                if (hook.isEnable() && !hook.init()) {
                    //if (!hook.init()) {
                    "Hook ${hook.name} 加载失败!".error()
                }
                "Hook ${hook.name} 是否启用: ${hook.isEnable()}".debug()
            }catch (e:Throwable){
                e.catch()
            }
        }
        // 然后在加载界面处加载需要反混淆的hook

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
                        "开始进行反混淆操作".debug()
                        async {
                            val dexkit = DexKitBridge.create(PPBuff.getHostPath())
                            var deobf = 0
                            hooks().needDeobfs {
                                "开始为 ${it.name} 进行反混淆操作...".debug()
                                val result = if (it.needCustomDeobf()) {
                                    it.customDebof(dexkit)
                                } else {
                                    dexkit?.batchFindClassesUsingStrings(BatchFindArgs.build {
                                        sync {
                                            bar.progress = deobf
                                            text.text = "正在为 ${it.name} 寻找被混淆的类"
                                        }
                                        queryMap(it.deobfMap())
                                    })
                                }
                                "开始为 ${it.name} 缓存反混淆...".debug()
                                PConfig.cache(result)
                                deobf++
                                sync {
                                    bar.progress = deobf
                                }
                                try {
                                    "开始加载hook: ${it.name}".debug()
                                    if (it.isEnable() && !it.init()) {
                                        //if (!it.init()) {
                                        "hook加载失败: ${it.name}".debug()
                                    }
                                }catch (e:Throwable){
                                    e.catch()
                                }
                            }
                            sync {
                                bar.max = 1
                                bar.progress = 1
                                text.text = "加载成功~"
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
                    activity.javaClass
                        .findField {
                            this.type == Handler::class.java
                        }
                        .invokeMethod(activity, "sendEmptyMessage", 29)
                }

            }

    }

    override fun hooks(): List<BaseHook> {
        return arrayListOf<BaseHook>().apply {
            add(ZuiYouLiteNoCrashHook)// 错误拦截
            add(ZuiYouLiteTestHook)// 测试Hook
            add(ZuiYouLiteDebugHook)// 调试Hook
            add(AppCenterHook)// AppCenter统计
            add(ZuiYouLiteQuickStartHook)// 快速启动
            add(XiaoChuanAntiADHook)// 去广告
            add(XiaoChuanAntiZyBuffHook)// 去ZyBuff
            add(XiaoChuanEvilInstrumentationHook)// 去EvilInstrumentatio
            add(ZuiYouLiteAntiVoiceRoomHook)// 去语音房
            add(ZuiYouLiteSimpleMeHook)// 精简"我的"
            add(ZuiYouLiteSimplePostHook)// 精简帖子列表
            add(ZuiYouLiteSettingHook)// 设置界面
            add(ZuiYouLiteDetailLocationHook)// IP精准到市
            add(ZuiYouLiteDetailCommentTimeHook)// 评论详细时间
            add(ZuiYouLiteNoWaterMarkHook)// 去水印
            add(ZuiYouLiteForcedVerticalHook)// 视频全屏强制竖屏
            add(ZuiYouLiteAutoTaskHook)// 自动签到
            add(ZuiYouLiteWebTokenHook)// 获取Token
            add(ZuiYouLiteAntiAntiDebugHook)// 反反调试
            //add(ZuiYouLiteWebTaskHook)// 云端自动任务
        }
    }

}