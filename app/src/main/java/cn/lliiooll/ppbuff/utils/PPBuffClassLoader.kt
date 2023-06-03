package cn.lliiooll.ppbuff.utils

import android.content.res.Resources
import cn.hutool.core.io.resource.Resource
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.hook.zuiyouLite.hideHolder
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.findField
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.paramCount

object PPBuffClassLoader : ClassLoader() {


    override fun loadClass(name: String?, resolve: Boolean): Class<*>? {

        if (contextClassloader != null && contextClassloader !is PPBuffClassLoader) {
            try {
                //"Context: 尝试加载类: $name".debug()
                return contextClassloader!!.loadClass(name)
            } catch (_: ClassNotFoundException) {

            }
        }
        if (name != null && (
                    name.contains("androidx") ||
                            name.contains("com.tencent.mmkv") ||
                            name.contains("org.jetbrains") ||
                            name.contains("javax")
                    )
        ) {
            "不允许加载的类: $name".debug()
            throw ClassNotFoundException(name)
        }
        if (appClassloader != null && contextClassloader !is PPBuffClassLoader) {
            try {
                //"App: 尝试加载类: $name".debug()
                return appClassloader!!.loadClass(name)
            } catch (_: ClassNotFoundException) {

            }
        }
        if (xposedClassloader != null && contextClassloader !is PPBuffClassLoader) {
            try {
                //"Xposed: 尝试加载类: $name".debug()
                return xposedClassloader!!.loadClass(name)
            } catch (_: ClassNotFoundException) {

            }
        }
        if (moduleClassloader != null && contextClassloader !is PPBuffClassLoader) {
            try {
                //"Module: 尝试加载类: $name".debug()
                return moduleClassloader!!.loadClass(name)
            } catch (_: ClassNotFoundException) {

            }
        }


        // 不在父类搜索
        return PPBuffClassLoader::class.java.classLoader?.loadClass(name)
    }

    fun loadClassOrNull(name: String?): Class<*>? {
        try {
            //"尝试加载类: $name".debug()
            return loadClass(name)
        } catch (_: ClassNotFoundException) {
            // "类不存在: $name".debug()
            return null
        }
    }

    private var mClassLoader: PPBuffClassLoader? = null
    private var sObfuscatedPackageName: String? = null
    private var xposedClassloader: ClassLoader? = null
    private var appClassloader: ClassLoader? = null
    private var contextClassloader: ClassLoader? = null
    private var moduleClassloader: ClassLoader? = null

    fun withXposed(classLoader: ClassLoader?): PPBuffClassLoader {
        xposedClassloader = classLoader
        return this
    }

    fun withApplition(classLoader: ClassLoader?): PPBuffClassLoader {
        appClassloader = classLoader
        return this
    }

    fun withContext(classLoader: ClassLoader?): PPBuffClassLoader {
        contextClassloader = classLoader
        return this
    }

    fun inject() {
        mClassLoader = this
        inject(PPBuffClassLoader::class.java)
        Class::class.java.findMethod {
            name == "forName" &&
                    paramCount == 3 &&
                    parameterTypes[0] == String::class.java &&
                    parameterTypes[1] == Boolean::class.java &&
                    parameterTypes[2] == ClassLoader::class.java
        }
            .hookBefore {

                val name = it.args[0] as String
                if (name.startsWith("com.kongzue.dialogx")) {
                    "尝试替换classloader".debug()
                    it.args[2] = mClassLoader
                }

            }
        Resources::class.java.findMethod {
            name == "loadXmlResourceParser"
        }
            .hookBefore {
                EzXHelperInit.addModuleAssetPath(it.thisObject as Resources)
            }
    }

    private fun inject(clazz: Class<*>) {
        val parent = clazz.findField(true) {
            name == "parent"
        }.get(this) as ClassLoader
        moduleClassloader = parent
        clazz.findField(true) {
            name == "parent"
        }.set(this, PPBuffClassLoader)
    }

    @JvmStatic
    fun getXposedBridgeClassName(): String? {
        return if (sObfuscatedPackageName == null) {
            "de.robv.android.xposed.XposedBridge"
        } else {
            val sb: StringBuilder =
                StringBuilder(sObfuscatedPackageName)
            sb.append(".XposedBridge")
            sb.toString()
        }
    }

    @JvmStatic
    fun setObfuscatedXposedApiPackage(packageName: String) {
        sObfuscatedPackageName = packageName
    }
}

fun String.findClass(): Class<*> {
    return PPBuffClassLoader.loadClass(this)
}

fun String.findClassOrNull(): Class<*>? {
    return PPBuffClassLoader.loadClassOrNull(this)
}

