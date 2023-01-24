package cn.lliiooll.ppbuff.hook.loader

import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.hook.common.XiaoChuanAntiADHook
import cn.lliiooll.ppbuff.hook.zuiyou.ZuiYouTestHook
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.empty
import cn.lliiooll.ppbuff.utils.error

/**
 * 最右Hook加载器，提供启动界面加载动画
 */
object ZuiyouLoader : BaseLoader() {
    override fun load() {
        hooks().forEach { hook ->
            hook.deobfMap().empty {
                // 先加载不用反混淆的hook
                "Hook ${hook.name} 是否启用: ${hook.isEnable()}".debug()
                if (hook.isEnable() && !hook.init()) {
                    "Hook ${hook.name} 加载失败!".error()
                }
            }
        }
        // 然后在加载界面处加载需要反混淆的hook
    }

    override fun hooks(): List<BaseHook> {
        return arrayListOf<BaseHook>().apply {
            add(ZuiYouTestHook)
            add(XiaoChuanAntiADHook)
        }
    }

}