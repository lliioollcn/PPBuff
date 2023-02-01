package cn.lliiooll.ppbuff.hook.common

import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.hook.isValid
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.hookReplace
import com.github.kyuubiran.ezxhelper.utils.paramCount

object XiaoChuanAntiZyBuffHook : BaseHook(
    "去除ZyBuff", "anti_zybuff", PHookType.DEBUG
) {

    val DEOBFKEY_ZUBUFF = "cn.xiaochuankeji.zuiyouLite.control.crashcatch.ZyBuff"
    override fun init(): Boolean {

        PConfig.getCache(DEOBFKEY_ZUBUFF)?.forEach {
            "过滤方法: $it".debug()

            it
                .findClass()
                .findAllMethods {
                    this.paramCount == 0 && this.returnType == Void.TYPE
                }
                .hookReplace {
                    "阻止ZyBuff加载，类型: ${it.method.name}".debug()
                }
        }
        return true
    }

    override fun deobfMap(): Map<String, List<String>> {
        return hashMapOf<String, List<String>>().apply {
            put(DEOBFKEY_ZUBUFF, arrayListOf<String>().apply {
                add("ZyBuff")
                add("start buff")
                add("start bless finalizer")
            })
        }
    }

    override fun needDeobf(): Boolean {
        return !PConfig.hasCache(DEOBFKEY_ZUBUFF) || !PConfig.getCache(DEOBFKEY_ZUBUFF)?.isValid()!!
    }


}