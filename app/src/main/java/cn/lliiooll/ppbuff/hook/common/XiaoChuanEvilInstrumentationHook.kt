package cn.lliiooll.ppbuff.hook.common

import android.view.View
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.hookReplace

object XiaoChuanEvilInstrumentationHook : BaseHook(
    "去除EvilInstrumentation", "anti_evilinstrumentation"
) {
    override fun init(): Boolean {

        "cn.xiaochuankeji.hermes.workaround.EvilInstrumentation"
            .findClass()
            .findAllMethods(true) {
                this.name.contains("attach")
            }
            .hookReplace {
                "阻止EvilInstrumentation加载，类型: ${it.method.name}".debug()
            }


        return true
    }

    override fun view(): View? {
        return null
    }

    override fun isEnable(): Boolean {
        return true
    }


}