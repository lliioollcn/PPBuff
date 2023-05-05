package cn.lliiooll.ppbuff

import cn.lliiooll.ppbuff.utils.debug
import com.tencent.mmkv.MMKV
import io.luckypray.dexkit.descriptor.member.DexClassDescriptor

class PConfig {
    companion object {

        private var mmkv: MMKV? = null

        @JvmStatic
        fun init() {
            mmkv = MMKV.defaultMMKV()
        }


        fun boolean(label: String, def: Boolean): Boolean {
            if (mmkv == null) return def
            return mmkv?.decodeBool(label, def)!!
        }

        @JvmStatic
        fun bool(label: String, def: Boolean): Boolean {
            return boolean(label, def)
        }

        @JvmStatic
        fun bool(label: String): Boolean {
            return boolean(label)
        }

        fun isHideConfig(): Boolean {
            return boolean("hide_icon", false)
        }

        fun boolean(label: String): Boolean {
            return boolean(label, true)
        }

        @JvmStatic
        fun number(label: String, def: Int): Int {
            if (mmkv == null) return def
            return mmkv?.decodeInt(label, def)!!
        }

        @JvmStatic
        fun numberEx(label: String, def: Long): Long {
            if (mmkv == null) return def
            return mmkv?.decodeLong(label, def)!!
        }

        @JvmStatic
        fun numberEx(label: String): Long {
            return numberEx(label, 0L)
        }

        @JvmStatic
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

        fun set(label: String, number: Long) {
            if (mmkv == null) return
            mmkv?.encode(label, number)!!
        }

        fun set(label: String, str: String) {
            if (mmkv == null) return
            mmkv?.encode(label, str)!!
        }

        fun isUpdateHost(): Boolean {
            val app = PPBuff.getApplication()
            if (app != null) {
                val last = number(
                    app.packageName
                )
                "最新版本号: $last".debug()
                "当前版本号: ${PPBuff.getHostVersionCode()}".debug()
                return last == 0 || last != PPBuff.getHostVersionCode()
            }
            "应用实例为null".debug()
            return false

        }

        fun isInited(): Boolean {
            return boolean("ppbuff_inited", false)
        }

        fun init(init: Boolean) {
            "设置初始化状态为: $init".debug()
            set("ppbuff_inited", init)
        }

        fun cache(result: Map<String, List<DexClassDescriptor>>?) {
            if (result?.isEmpty()!!) {
                "没有找到任何类".debug()
            }
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

        fun getCache(k: String): MutableSet<String> {
            if (mmkv == null) return hashSetOf()
            return mmkv?.decodeStringSet("debof_$k", hashSetOf())!!
        }

        fun setHideConfig() {
            set("hide_icon", !isHideConfig())
        }

        fun isHidePost(i: Int): Boolean {
            return mmkv?.decodeStringSet("hide_post", hashSetOf())?.contains("$i")!!
        }

        fun addHidePost(i: Int) {
            mmkv?.encode("hide_post", mmkv?.decodeStringSet("hide_post", hashSetOf())?.apply {
                add("$i")
            })
        }

        fun delHidePost(i: Int) {
            mmkv?.encode("hide_post", mmkv?.decodeStringSet("hide_post", hashSetOf())?.apply {
                remove("$i")
            })
        }


        fun isHideMine(i: String): Boolean {
            return mmkv?.decodeStringSet("hide_mine", hashSetOf())?.contains(i)!!
        }

        fun addHideMine(i: String) {
            mmkv?.encode("hide_mine", mmkv?.decodeStringSet("hide_mine", hashSetOf())?.apply {
                add(i)
            })
        }

        fun delHideMine(i: String) {
            mmkv?.encode("hide_mine", mmkv?.decodeStringSet("hide_mine", hashSetOf())?.apply {
                remove(i)
            })
        }

        fun string(str: String): String {
            return string(str, "")
        }

        fun string(str: String, def: String): String {
            return mmkv?.decodeString(str, def)!!
        }
    }
}