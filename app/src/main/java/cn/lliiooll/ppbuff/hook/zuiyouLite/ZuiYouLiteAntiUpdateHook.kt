package cn.lliiooll.ppbuff.hook.zuiyouLite

import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.hook.isValid
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import com.github.kyuubiran.ezxhelper.utils.hookReplace
import com.github.kyuubiran.ezxhelper.utils.paramCount

object ZuiYouLiteAntiUpdateHook : BaseHook(
    "屏蔽更新", "anti_update_check", PHookType.SIMPLE
) {

    val DEOBFKEY_UPDATE_UTILS = "cn.xiaochuankeji.zuiyouLite.update.UpdateUtils"
    override fun init(): Boolean {
        PConfig.getCache(DEOBFKEY_UPDATE_UTILS)
            .forEach { cm ->
                val clazz = cm.findClass()
                for (m in clazz.declaredMethods) {
                    if (m.returnType == Boolean::class.java && m.paramCount == 0) {
                        m.hookReplace {
                            "没更新，一定没更新".debug()
                            return@hookReplace false
                        }
                    }
                }

            }
        return true
    }

    override fun deobfMap(): Map<String, List<String>> {
        return hashMapOf<String, List<String>>().apply {
            put(DEOBFKEY_UPDATE_UTILS, arrayListOf<String>().apply {
                add("version_type")
                add("notification_type")
                add("k_base_version")
            })
        }
    }

    override fun needDeobf(): Boolean {
        return !PConfig.hasCache(DEOBFKEY_UPDATE_UTILS) || !PConfig.getCache(
            DEOBFKEY_UPDATE_UTILS
        )?.isValid()!!
    }

    override fun isEnable(): Boolean {
        return PConfig.boolean(label, false)
    }
}