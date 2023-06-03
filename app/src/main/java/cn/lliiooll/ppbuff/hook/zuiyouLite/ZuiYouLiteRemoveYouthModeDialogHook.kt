package cn.lliiooll.ppbuff.hook.zuiyouLite

import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.hook.isValid
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.paramCount
import java.lang.reflect.Modifier

object ZuiYouLiteRemoveYouthModeDialogHook : BaseHook(
    "去除青少年模式弹窗", "remove_youth_mode", PHookType.SIMPLE
) {
    val DEOBF_YOUTH_MODE_DIALOG = "cn.xiaochuankeji.zuiyouLite.ui.dialog.YouthModeDialog"

    var inited = false
    override fun init(): Boolean {
        if (inited) return true
        PConfig.getCache(DEOBF_YOUTH_MODE_DIALOG).forEach {
            val clazz = it.findClass()
            clazz.findMethod {
                !Modifier.isStatic(modifiers) && paramCount == 0 && returnType != Void::class.java
            }.hookAfter {
                "尝试展示青少年模式弹窗".debug()
                it.result = null
            }
        }
        inited = true
        return true
    }

    override fun deobfMap(): Map<String, List<String>> {
        return hashMapOf<String, List<String>>().apply {
            put(DEOBF_YOUTH_MODE_DIALOG, arrayListOf<String>().apply {
                add("为呵护青少年健康成长，皮皮搞笑推出青少年模式，该模式仅提供青少年精选内容。")
            })
        }
    }

    override fun needDeobf(): Boolean {
        return !PConfig.hasCache(DEOBF_YOUTH_MODE_DIALOG) || !PConfig.getCache(
            DEOBF_YOUTH_MODE_DIALOG
        )?.isValid()!!
    }

}