package cn.lliiooll.ppbuff.hook.loader

import android.app.Activity
import android.os.Handler
import android.widget.ProgressBar
import android.widget.TextView
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.R
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.hook.common.XiaoChuanAntiADHook
import cn.lliiooll.ppbuff.hook.needDeobfs
import cn.lliiooll.ppbuff.hook.notNeedDeobfs
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteAntiVoiceRoomHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteQuickStartHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteTestHook
import cn.lliiooll.ppbuff.utils.*
import com.github.kyuubiran.ezxhelper.utils.findField
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.invokeMethod
import io.luckypray.dexkit.DexKitBridge
import io.luckypray.dexkit.builder.BatchFindArgs
import io.luckypray.dexkit.enums.MatchType
import org.w3c.dom.Text
import kotlin.concurrent.thread

/**
 * 皮皮搞笑Hook加载器，提供启动界面加载动画
 */
object ZuiyouLiteLoader : BaseLoader() {
    override fun load() {
        val deobfs = hooks().size - hooks().notNeedDeobfs { hook ->
            // 先加载 不用/已经缓存 反混淆的hook
            if (hook.isEnable() && !hook.init()) {
                "Hook ${hook.name} 加载失败!".error()
            }
            "Hook ${hook.name} 是否启用: ${hook.isEnable()}".debug()
        }.size
        // 然后在加载界面处加载需要反混淆的hook
        if (PConfig.isUpdateHost() || deobfs > 0) {
            "cn.xiaochuankeji.zuiyouLite.ui.splash.SplashActivity"
                .findClass()
                .findMethod { name == "onCreate" }
                .hookAfter {
                    val activity = it.thisObject as Activity
                    val splash = activity.inflate(R.layout.pp_spalsh)
                    activity.setContentView(splash)
                    val text = splash.findViewById<TextView>(R.id.spalsh_text)
                    val bar = splash.findViewById<ProgressBar>(R.id.spalsh_bar)
                    bar.max = deobfs
                    "开始进行反混淆操作".debug()
                    thread {
                        val dexkit = DexKitBridge.create(PPBuff.getHostPath())
                        var deobf = 0
                        hooks().needDeobfs {
                            "开始为 ${it.name} 进行反混淆操作...".debug()
                            val result = dexkit?.batchFindClassesUsingStrings(BatchFindArgs.build {
                                sync {
                                    bar.progress = deobf
                                    text.text = "正在为 ${it.name} 寻找被混淆的类"
                                }
                                queryMap(it.deobfMap())
                                matchType(MatchType.FULL)
                            })
                            "开始为 ${it.name} 缓存反混淆...".debug()
                            PConfig.cache(result)
                            deobf++
                            sync {
                                bar.progress = deobf
                            }
                        }
                        sync {
                            bar.max = 1
                            bar.progress = 1
                            text.text = "加载成功~"
                            PConfig.init(true)
                            if (PConfig.isUpdateHost()) PConfig.updateHost()
                            activity.javaClass
                                .findField {
                                    this.type == Handler::class.java
                                }
                                .invokeMethod(activity, "sendEmptyMessage", 29)

                        }
                        dexkit?.close()
                    }

                }
        }


    }

    override fun hooks(): List<BaseHook> {
        return arrayListOf<BaseHook>().apply {
            add(ZuiYouLiteTestHook)// 测试Hook
            add(ZuiYouLiteQuickStartHook)// 快速启动
            add(XiaoChuanAntiADHook)// 去广告
            add(ZuiYouLiteAntiVoiceRoomHook)// 去语音房
        }
    }

}