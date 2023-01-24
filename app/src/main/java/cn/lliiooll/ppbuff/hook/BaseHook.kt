package cn.lliiooll.ppbuff.hook

import android.view.View
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.findClassOrNull

/**
 * 基础Hook类
 */
abstract class BaseHook(
    var name: String,
    var label: String,
) {

    abstract fun init(): Boolean

    open fun deobfMap(): Map<String, List<String>> {
        return hashMapOf()
    }

    open fun view(): View? {
        return null
    }

    open fun isEnable(): Boolean {
        return PConfig.boolean(label, true)
    }

    open fun needDeobf(): Boolean {
        return false
    }

    open fun setEnable(enable: Boolean) {
        PConfig.set(label, enable)
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