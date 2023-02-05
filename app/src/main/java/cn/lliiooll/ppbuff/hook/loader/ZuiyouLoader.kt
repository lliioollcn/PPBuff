package cn.lliiooll.ppbuff.hook.loader

import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.hook.common.AppCenterHook
import cn.lliiooll.ppbuff.hook.common.XiaoChuanAntiADHook
import cn.lliiooll.ppbuff.hook.common.XiaoChuanAntiZyBuffHook
import cn.lliiooll.ppbuff.hook.common.XiaoChuanEvilInstrumentationHook
import cn.lliiooll.ppbuff.hook.zuiyou.ZuiYouTestHook
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.empty
import cn.lliiooll.ppbuff.utils.error
import cn.lliiooll.ppbuff.utils.sync
import io.luckypray.dexkit.DexKitBridge
import io.luckypray.dexkit.builder.BatchFindArgs

/**
 * 最右Hook加载器，提供启动界面加载动画
 */
object ZuiyouLoader : BaseLoader() {
    override fun load() {

        // 然后在加载界面处加载需要反混淆的hook
        val dexkit = DexKitBridge.create(PPBuff.getHostPath())
        hooks().forEach {
            if (it.isEnable()){
                if (it.needDeobf()) {
                    PConfig.cache(
                        if (it.needCustomDeobf()){
                            it.customDebof(dexkit)
                        }else{
                            dexkit?.batchFindClassesUsingStrings(BatchFindArgs.build {
                                queryMap(it.deobfMap())
                            })
                        }
                    )
                }
                it.init()
            }
        }
    }

    override fun hooks(): List<BaseHook> {
        return arrayListOf<BaseHook>().apply {
            add(ZuiYouTestHook)
            add(AppCenterHook)// AppCenter统计
            add(XiaoChuanAntiADHook)
            add(XiaoChuanEvilInstrumentationHook)
            add(XiaoChuanAntiZyBuffHook)
        }
    }

}