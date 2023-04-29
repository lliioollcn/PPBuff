package cn.lliiooll.ppbuff.utils

import com.github.kyuubiran.ezxhelper.utils.findField

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
        throw ClassNotFoundException(name)
        // 不在父类搜索
        //return super.loadClass(name, resolve)
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
        val clazz = PPBuffClassLoader::class.java
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

