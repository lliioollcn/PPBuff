package cn.lliiooll.ppbuff.hook.zuiyouLite

import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.data.callback.TokenGetCallBack
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.hook.isValid
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import com.github.kyuubiran.ezxhelper.utils.ArgTypes
import com.github.kyuubiran.ezxhelper.utils.Args
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.findConstructor
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.newInstance
import com.github.kyuubiran.ezxhelper.utils.paramCount
import de.robv.android.xposed.XposedHelpers

object ZuiYouLiteWebTokenHook : BaseHook(
    "获取Token", "web_token", PHookType.HIDE
) {
    val DEOBF_API_GETDEVICEINFO = "cn.xiaochuan.jsbridge.JSGetDeviceInfo"
    var callBack: TokenGetCallBack? = null
    var TOKEN = ""
    var inited = false
    override fun init(): Boolean {
        if (inited) return true
        val ins = "cn.xiaochuan.jsbridge.XCWebView"
            .findClass()
            .superclass
        val ins1 = "${ins.name}\$a"
        "!!! 实例类11111: $ins1".debug()
        ins1.findClass()
            .findAllMethods {
                name == "onCallBack"
            }
            .hookBefore {
                val data = it.args[0] as String
                if (data.contains("userstatus")
                    && data.contains("isbind")
                    && data.contains("nickname")
                    && data.contains("token")
                ) {
                    TOKEN = data
                    if (callBack != null) {
                        callBack?.onCallBack(TOKEN)
                    }
                }

            }
        inited = true
        return true
    }

    override fun isEnable(): Boolean {
        return true
    }

    fun getWebToken(callBack: (String) -> Unit) {
        this.callBack = object : TokenGetCallBack {
            override fun onCallBack(token: String) {
                callBack.invoke(token)
            }
        }
        val type = PConfig.getCache(DEOBF_API_GETDEVICEINFO).toList()[0]
        val ins = type.split("\$")[0]
        val insq = "cn.xiaochuan.jsbridge.XCWebView"
            .findClass()
            .superclass
        val ins1 = "${insq.name}\$a"
        "!!! 实例类: $ins".debug()
        "获取信息类: $type".debug()
        val clazz = type.findClass()
        val obj = clazz.newInstance(
            args = Args(arrayOf(null)),
            argTypes = ArgTypes(arrayOf(ins.findClass()))
        )
        val insC = ins1.findClass()
            .findConstructor {
                true
            }
        val m = clazz.findMethod {
            paramCount == 2 && parameterTypes[0] == String::class.java
        }
        XposedHelpers.callMethod(obj, m.name, "", insC.newInstance(null))
    }

    override fun deobfMap(): Map<String, List<String>> {
        return hashMapOf<String, List<String>>().apply {
            put(DEOBF_API_GETDEVICEINFO, arrayListOf<String>().apply {
                add("userstatus")
                add("isbind")
                add("nickname")
            })
        }
    }

    override fun needDeobf(): Boolean {
        return !PConfig.hasCache(DEOBF_API_GETDEVICEINFO) || !PConfig.getCache(
            DEOBF_API_GETDEVICEINFO
        )?.isValid()!!
    }
}