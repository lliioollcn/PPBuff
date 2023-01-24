package cn.lliiooll.ppbuff.hook

import android.view.View
import cn.lliiooll.ppbuff.PConfig

/**
 * 基础Hook类
 */
abstract class BaseHook(
    var name: String,
    var label: String,
) {

    abstract fun init(): Boolean

    fun deobfMap(): Map<String, List<String>> {
        return hashMapOf()
    }

    fun view(): View? {
        return null
    }

    fun isEnable(): Boolean {
        return PConfig.boolean(label, true)
    }

    fun needDeobf(): Boolean {
        return false
    }

    fun setEnable(enable: Boolean) {
        PConfig.set(label, enable)
    }
}

fun List<BaseHook>.notNeedDeobfs(function: (BaseHook) -> Unit):List<BaseHook> {
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