package cn.lliiooll.ppbuff.utils

import com.github.kyuubiran.ezxhelper.utils.findField

object PPBuffClassLoader : ClassLoader() {

    override fun loadClass(name: String?, resolve: Boolean): Class<*>? {
        if (contextClassloader != null) {
            try {
                return contextClassloader!!.loadClass(name)
            } catch (_: ClassNotFoundException) {

            }
        }
        if (appClassloader != null) {
            try {
                return appClassloader!!.loadClass(name)
            } catch (_: ClassNotFoundException) {

            }
        }
        if (xposedClassloader != null) {
            try {
                return xposedClassloader!!.loadClass(name)
            } catch (_: ClassNotFoundException) {

            }
        }
        if (moduleClassloader != null) {
            try {
                return moduleClassloader!!.loadClass(name)
            } catch (_: ClassNotFoundException) {

            }
        }
        return super.loadClass(name, resolve)
    }

    fun loadClassOrNull(name: String?): Class<*>? {
        try {
            return loadClass(name)
        } catch (_: ClassNotFoundException) {
            return null
        }
    }

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
}

fun String.findClass(): Class<*> {
    return PPBuffClassLoader.loadClass(this)
}

fun String.findClassOrNull(): Class<*>? {
    return PPBuffClassLoader.loadClassOrNull(this)
}

