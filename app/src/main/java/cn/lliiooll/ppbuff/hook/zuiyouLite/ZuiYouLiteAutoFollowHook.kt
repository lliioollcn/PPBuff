package cn.lliiooll.ppbuff.hook.zuiyouLite

import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PViewType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.hook.isValid
import cn.lliiooll.ppbuff.utils.callMethod
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.toastShort
import com.github.kyuubiran.ezxhelper.utils.findAllConstructors
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.hookReplace
import com.github.kyuubiran.ezxhelper.utils.paramCount
import de.robv.android.xposed.XposedHelpers

object ZuiYouLiteAutoFollowHook : BaseHook(
    "自动关注", "auto_follow", PHookType.HIDE
) {
    private const val DEOBF_API_FOLLOWSERVICE =
        "cn.xiaochuankeji.zuiyouLite.api.follow.FollowServiceImpl"
    var inited = false
    override fun init(): Boolean {
        val success = PConfig.boolean(ZuiYouLiteAutoFollowHook.label, false)
        "是否关注: $success".debug()
        if (success) {
            return true
        }
        if (inited) return true
        PPBuff.isFollow = false
        "cn.xiaochuankeji.zuiyouLite.ui.follow.holder.FollowedRecommendAuthorItemHolder\$b"
            .findClass()
            .findMethod {
                name == "call"
            }
            .hookBefore {
                if (!PPBuff.isFollow){
                    PPBuff.isFollow = true
                    "收到回调".debug()
                    it.result = null
                }
            }
        click()
        inited = true
        return true
    }

    override fun isEnable(): Boolean {

        return true
    }

    override fun deobfMap(): Map<String, List<String>> {
        return hashMapOf<String, List<String>>().apply {
            put(DEOBF_API_FOLLOWSERVICE, arrayListOf<String>().apply {
                add("uid")
                add("from")
                add("action_type")
                add("target_mid")
                add("target_mid_list")
            })
        }
    }

    override fun needDeobf(): Boolean {
        return !PConfig.hasCache(DEOBF_API_FOLLOWSERVICE) || !PConfig.getCache(
            DEOBF_API_FOLLOWSERVICE
        )?.isValid()!!
    }

    override fun router(): Boolean {
        return false
    }

    override fun click() {
        "关注中，请稍后".debug()
        val type = PConfig.getCache(DEOBF_API_FOLLOWSERVICE).toList()[0]
        val ins = type.findClass().newInstance()
        for (m in ins.javaClass.declaredMethods) {
            if (m.paramCount > 1 && m.parameterTypes[0] == Long::class.java && m.parameterTypes[1] == String::class.java) {
                val subscribe = m.invoke(ins, 80104341L, null)
                "cn.xiaochuankeji.zuiyouLite.ui.follow.holder.FollowedRecommendAuthorItemHolder\$b"
                    .findClass()
                    .findAllConstructors {
                        true
                    }
                    .forEach { c ->
                        "寻找构造方法: ${c.paramCount} ${c.parameterTypes[0].name} ${c.parameterTypes[1].name}".debug()
                        if (c.paramCount == 3) {
                            val i1 = c.newInstance(null, null, null)
                            PPBuff.isFollow = false
                            XposedHelpers.callMethod(
                                subscribe, "subscribe",
                                i1
                            )
                        }
                    }
                //m.invoke(ins,7592965460762L,null)
                "关注完毕".debug()
                break
            }
        }

    }

    override fun needCustomClick(): Boolean {
        return false
    }

    override fun view(): PViewType {
        return PViewType.NONE
    }
}