package cn.lliiooll.ppbuff.hook.common

import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.hookReplace

object XiaoChuanAntiADHook : BaseHook(
    "去广告", "anti_ad", PHookType.COMMON
) {
    override fun init(): Boolean {

        "cn.xiaochuankeji.hermes.core.Hermes"
            .findClass()
            .findAllMethods(true) {
                name.contains("create") && name.contains("AD") && !name.contains("RewardAD")
            }
            .hookReplace {
                "阻止广告加载，类型: ${it.method.name}".debug()
            }


        return true
    }
}