package cn.lliiooll.ppbuff.hook.zuiyouLite

import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.hook.isValid
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore

object ZuiYouLiteNoTrackerHook : BaseHook(
    "复制链接防止追踪", "clipboard_no_tracker", PHookType.SIMPLE
) {

    val DEOBFKEY_CLIPBOARD_MANAGER = "cn.xiaochuankeji.zuiyouLite.utils.ClipBoardManager"
    override fun init(): Boolean {
        PConfig.getCache(DEOBFKEY_CLIPBOARD_MANAGER)
            .forEach {
                "<<<<< 过滤类: $it".debug()
                it
                    .findClass()
                    .findMethod { parameterCount == 1 && parameterTypes[0] == String::class.java }
                    .hookBefore { param ->
                        var url = param.args[0] as String
                        "尝试写入粘贴板: $url".debug()
                        url = url.substring(0, url.indexOf("?"))
                        "处理完毕后: $url".debug()
                        param.args[0] = url
                    }
            }
        return true
    }

    override fun deobfMap(): Map<String, List<String>> {
        return hashMapOf<String, List<String>>().apply {
            put(DEOBFKEY_CLIPBOARD_MANAGER, arrayListOf<String>().apply {
                add("clipboard")
                add("text_label")
                add("activity")
            })
        }
    }

    override fun needDeobf(): Boolean {
        return !PConfig.hasCache(DEOBFKEY_CLIPBOARD_MANAGER) || !PConfig.getCache(
            DEOBFKEY_CLIPBOARD_MANAGER
        ).isValid()
    }
}