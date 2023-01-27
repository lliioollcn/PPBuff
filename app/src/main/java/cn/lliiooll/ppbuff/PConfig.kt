package cn.lliiooll.ppbuff

import cn.lliiooll.ppbuff.utils.debug
import com.tencent.mmkv.MMKV
import io.luckypray.dexkit.descriptor.member.DexClassDescriptor

class PConfig {
    companion object {

        private var mmkv: MMKV? = null

        init {
            mmkv = MMKV.defaultMMKV()
        }


        fun boolean(label: String, def: Boolean): Boolean {
            if (mmkv == null) return def
            return mmkv?.decodeBool(label, def)!!
        }

        fun isHideConfig(): Boolean {
            return boolean("hide_icon", false)
        }

        fun boolean(label: String): Boolean {
            return boolean(label, true)
        }

        fun number(label: String, def: Int): Int {
            if (mmkv == null) return def
            return mmkv?.decodeInt(label, def)!!
        }

        fun number(label: String, def: Long): Long {
            if (mmkv == null) return def
            return mmkv?.decodeLong(label, def)!!
        }

        fun number(label: String): Int {
            return number(label, 0)
        }

        fun set(label: String, enable: Boolean) {
            if (mmkv == null) return
            mmkv?.encode(label, enable)!!
        }

        fun set(label: String, number: Int) {
            if (mmkv == null) return
            mmkv?.encode(label, number)!!
        }

        fun isUpdateHost(): Boolean {
            val app = PPBuff.getApplication()
            if (app != null) {
                val last = number(
                    app.packageName
                )
                return last == 0 || last != PPBuff.getHostVersionCode()
            }
            return false

        }

        fun isInited(): Boolean {
            return boolean("ppbuff_inited", false)
        }

        fun init(init: Boolean) {

            set("ppbuff_inited", init)
        }

        fun cache(result: Map<String, List<DexClassDescriptor>>?) {
            result?.forEach { (k, v) ->
                "缓存反混淆信息: $k@$v".debug()
                mmkv?.encode("debof_$k", hashSetOf<String?>().apply {
                    v.forEach {
                        val name = PPBuff.doReplace(it.name)
                        add(name)
                        "   找到的类: $name".debug()
                    }
                })
            }
        }

        fun updateHost() {
            val app = PPBuff.getApplication()
            if (app != null) {
                set(
                    app.packageName,
                    PPBuff.getHostVersionCode()
                )
            }
        }

        fun hasCache(k: String): Boolean {
            if (mmkv == null) return false
            return mmkv?.decodeStringSet("debof_$k", null) != null
        }

        fun getCache(k: String): MutableSet<String>? {
            if (mmkv == null) return hashSetOf()
            return mmkv?.decodeStringSet("debof_$k", hashSetOf())

        }

        fun setHideConfig() {
            set("hide_icon", !isHideConfig())
        }
    }
}