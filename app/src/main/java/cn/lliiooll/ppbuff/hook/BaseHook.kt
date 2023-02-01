package cn.lliiooll.ppbuff.hook

import android.app.Activity
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PViewType
import cn.lliiooll.ppbuff.utils.findClassOrNull
import io.luckypray.dexkit.DexKitBridge
import io.luckypray.dexkit.descriptor.member.DexClassDescriptor

/**
 * 基础Hook类
 */
abstract class BaseHook(
    var name: String,
    var label: String,
    var type: PHookType,
) {

    abstract fun init(): Boolean

    open fun deobfMap(): Map<String, List<String>> {
        return hashMapOf()
    }

    open fun view(): PViewType {
        return PViewType.SWITCH
    }

    open fun isEnable(): Boolean {
        return PConfig.boolean(label, true)
    }

    open fun needDeobf(): Boolean {
        return false
    }

    open fun needCustomDeobf(): Boolean {
        return false
    }

    open fun customDebof(dexkit: DexKitBridge?): Map<String, List<DexClassDescriptor>> {
        return hashMapOf()
    }

    open fun setEnable(enable: Boolean) {
        PConfig.set(label, enable)
    }


    @Composable
    open fun compose(navController: NavHostController) {
        return Spacer(
            modifier = Modifier
                .width(0.dp)
                .height(0.dp)
        )
    }

    open fun router(): Boolean {
        return false
    }

}

fun List<BaseHook>.notNeedDeobfs(function: (BaseHook) -> Unit): List<BaseHook> {
    this.forEach {
        if (!it.needDeobf()) {
            function.invoke(it)
        }
    }
    return this
}

fun List<BaseHook>.needDeobfs(function: (BaseHook) -> Unit) {
    this.forEach {
        if (it.needDeobf()) {
            function.invoke(it)
        }
    }
}

fun MutableSet<String>.isValid(): Boolean {
    if (this.isEmpty()) return false
    var b = true
    for (c in this) {
        if (c.findClassOrNull() == null) {
            b = false
            break
        }
    }
    return b
}