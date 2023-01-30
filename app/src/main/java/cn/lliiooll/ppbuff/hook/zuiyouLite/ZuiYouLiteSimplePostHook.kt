package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.app.Activity
import android.os.Bundle
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.hook.PHookType
import cn.lliiooll.ppbuff.hook.common.XiaoChuanAntiZyBuffHook
import cn.lliiooll.ppbuff.hook.isValid
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.toastShort
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.paramCount
import io.luckypray.dexkit.DexKitBridge
import io.luckypray.dexkit.descriptor.member.DexClassDescriptor
import java.lang.reflect.Modifier

object ZuiYouLiteSimplePostHook : BaseHook(
    "精简帖子列表", "simplePost", PHookType.SIMPLE

) {

    val DEOBFKEY_ALL_ADAPTER = "cn.xiaochuankeji.zuiyouLite.ui.adapter.Adapters"


    override fun init(): Boolean {

        PConfig.getCache(DEOBFKEY_ALL_ADAPTER).forEach {
            if (it.startsWith("cn.xiaochuankeji")){
                val clazz = it.findClass()
                for (m in clazz.declaredMethods){
                    if (m.name == "onCreateViewHolder"
                        && !Modifier.isAbstract(m.modifiers)
                        && m.paramCount == 2
                        && m.parameterTypes[1] == Int::class.java
                    ){
                        "Hook 在 $it 的方法".debug()
                        m.hookAfter {

                        }
                    }
                }
            }
        }

        return true
    }

    override fun needDeobf(): Boolean {
        return !PConfig.hasCache(DEOBFKEY_ALL_ADAPTER) || !PConfig.getCache(
            DEOBFKEY_ALL_ADAPTER
        )?.isValid()!!
    }

    override fun needCustomDeobf(): Boolean {
        return true
    }

    override fun customDebof(dexkit: DexKitBridge?): Map<String, List<DexClassDescriptor>> {

        return hashMapOf<String, List<DexClassDescriptor>>().apply {
            put(DEOBFKEY_ALL_ADAPTER,dexkit?.findSubClasses("androidx.recyclerview.widget.RecyclerView\$Adapter")!!)
        }
    }
}