package cn.lliiooll.ppbuff.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import androidx.documentfile.provider.DocumentFile
import cn.lliiooll.ppbuff.BuildConfig
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.data.types.PRecordType
import cn.lliiooll.ppbuff.tracker.PLog
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.findAllFields
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.paramCount
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.style.IOSStyle
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.io.File
import java.lang.StringBuilder
import java.lang.reflect.Executable
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.Arrays
import java.util.concurrent.Executors


class Utils {
    companion object {
        @JvmStatic
        fun syncStatic(runnable: Runnable) {
            sync {
                runnable.run()
            }
        }

        @JvmStatic
        fun asyncStatic(runnable: Runnable) {
            async {
                runnable.run()
            }

        }

        @JvmStatic
        fun loadClass(s: String): Class<*> {
            return s.findClass()
        }

        @JvmStatic
        fun toastShort(s: String) {
            s.toastShort()
        }

        @JvmStatic
        fun toastLong(s: String) {
            s.toastLong()
        }
    }
}

fun String.info() {
    PLog.i(this)
}

fun String.error() {
    PLog.e(this)
}

fun String.debug() {
    PLog.d(this)
}

fun String.findFieldIn(clazz: Class<*>): Field? {
    for (f in clazz.declaredFields) {
        if (f.name == this) {
            return f
        }
    }
    return null;
}

fun Field.value(ins: Any): Any? {
    this.isAccessible = true
    return this.get(ins)
}

fun Throwable.catch() {
    PLog.catch(this)
}

fun File.checkDir(): File {
    if (this.isFile) {
        this.delete()
    }
    if (!this.exists()) {
        this.mkdirs()
    }

    return this
}

fun Map<String, List<String>>.deobf(function: (String, List<String>) -> Unit) {
    if (!this.isEmpty()) {
        this.forEach(function)
    }
}

fun <V : Any> List<V>.empty(function: () -> Unit) {
    if (this.isEmpty()) {
        function.invoke()
    }
}

fun String.toastShort(ctx: Context) {
    sync {
        EzXHelperInit.addModuleAssetPath(ctx)
        PopTip.show(this)
            .setRadius(50f)
            .setStyle(IOSStyle.style())
            .showShort()
        //Toast.makeText(ctx, this, Toast.LENGTH_SHORT).show()
    }
}

fun String.toastShort() {
    toastShort(PPBuff.getApplication())
}

fun String.toastLong() {
    toastLong(PPBuff.getApplication())
}

fun requireMinVersion(version: Int): Boolean {
    return PPBuff.getHostVersionCode() >= version
}

fun String.toastLong(ctx: Context) {
    sync {
        EzXHelperInit.addModuleAssetPath(ctx)
        PopTip.show(this)
            .setRadius(50f)
            .setStyle(IOSStyle.style())
            .showLong()
        //Toast.makeText(ctx, this, Toast.LENGTH_LONG).show()
    }
}

fun Context.inflate(layout: Int): View {
    return LayoutInflater.from(this).inflate(layout, null)
}

fun sync(function: () -> Unit) {
    val handler = Handler(Looper.getMainLooper())
    handler.post(function)
}

val pool = Executors.newCachedThreadPool()

fun async(function: () -> Unit) {
    pool.execute(function)
}

fun List<Class<*>>.allEquals(any: List<*>): Boolean {
    var b = true
    for (i in 0..any.size) {
        if (this[i] != any[i]?.javaClass) {
            b = false
            break
        }
    }
    return b
}

fun Field.invokeMethod(ins: Any, name: String, vararg a: Any?) {
    this.isAccessible = true
    val obj = this.get(ins)
    val clazz = this.type
    clazz.findMethod(true) {
        this.name == name &&
                this.paramCount == a.size
    }.apply {
        "尝试调用方法: ${this.name}".debug()
    }.invoke(obj, *a)
}

fun Int.toDp(ctx: Context): Float {
    return this.toFloat() / (ctx.getResources()
        .getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}

fun String.findId(): Int {
    return findId("id")
}

fun String.findId(type: String): Int {
    val clazz = "${PPBuff.getApplication().packageName}.R\$$type".findClass()
    return XposedHelpers.getStaticIntField(clazz, this)
}

fun Context.jumpTo(clazz: Class<*>) {
    val intent = Intent(this, clazz)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    this.startActivity(intent)

}

fun Any.downloadVideo() {
    if (this.javaClass.name.contains("ServerImageBean")) {
        "无水印视频开始下载".toastShort()
        val videoBean = XposedHelpers.getObjectField(this, "videoBean")
        if (videoBean != null) {
            var url = ""
            if (PConfig.boolean("video_no_water_mark_plus", true)) {
                val urls = arrayListOf<String>()
                val h265Sources = XposedHelpers.getObjectField(videoBean, "h265Sources")
                if (h265Sources != null) {
                    val h265SourcesList = h265Sources as List<*>
                    h265SourcesList.forEach {
                        val videoUrls = XposedHelpers.getObjectField(it, "urls")
                        if (videoUrls != null) {
                            val videoUrlsData = videoUrls as List<*>
                            videoUrlsData.forEach { u ->
                                urls.add(XposedHelpers.getObjectField(u, "url") as String)
                            }
                        }
                    }
                }

                val sources = XposedHelpers.getObjectField(videoBean, "sources")
                if (sources != null) {
                    val sourcesList = sources as List<*>
                    sourcesList.forEach {
                        val videoUrls = XposedHelpers.getObjectField(it, "urls")
                        if (videoUrls != null) {
                            val videoUrlsData = videoUrls as List<*>
                            videoUrlsData.forEach { u ->
                                urls.add(XposedHelpers.getObjectField(u, "url") as String)
                            }
                        }
                    }
                }
                //val u = urls[0].replace("http://", "").replace("https://", "")
                url = urls[0]
                /*
                url = "http://127.0.0.1:2017/$u"
                if (!PJavaUtils.isConnected(url)) {
                    url = "http://127.0.0.1:2018/$u"
                }
                if (!PJavaUtils.isConnected(url)) {
                    url = u
                }

                */
                "下载url: $url".debug()
            } else {
                url = XposedHelpers.getObjectField(videoBean, "urlsrc") as String
                "下载url: $url".debug()
            }
            async {
                "开始下载文件: $url".debug()
                val file = PDownload.downloadTemp(url)
                "文件下载完毕: $url".debug()
                val recordType = PRecordType.getValue(
                    PConfig.string(
                        "video_record",
                        PRecordType.STORE.getLabel()
                    )
                )
                sync {
                    "开始保存到相册: $url".debug()
                    file.saveVideo(recordType)
                }
            }
        }
    }
}

fun File.saveVideo(type: PRecordType) {
    if (type == PRecordType.SAF) {
        val path = PConfig.string("video_record_uri", "")
        if (path.isBlank()) {
            "未设置SAF路径，视频保存失败!".toastShort()
        } else {
            val uri = Uri.parse(path)
            val saveDir = DocumentFile.fromTreeUri(PPBuff.getApplication(), uri)
            val saveFile = saveDir?.createFile("video/mp4", this.name)
            async {
                IOUtils.copy(PPBuff.getApplication(), this, saveFile?.uri)
                sync {
                    "无水印视频下载成功".toastShort()
                }
            }
        }
    } else if (type == PRecordType.STORE) {
        val values = ContentValues()
        values.put(MediaStore.Video.Media.DISPLAY_NAME, this.name);
        values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
        val uri = PPBuff.getApplication().contentResolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            values
        )
        async {
            IOUtils.copy(PPBuff.getApplication(), this, uri)
            sync {
                "无水印视频下载成功".toastShort()
            }
        }
    } else if (type == PRecordType.TRADITIONAL) {
        val dir = File(Environment.getExternalStorageDirectory(), "PPBuff/Video")
        val file = File(dir, this.name)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        if (!file.exists()) {
            file.createNewFile()
        }
        async {
            IOUtils.copy(this, file)
            sync {
                "无水印视频下载成功".toastShort()
            }
        }
    }
}


fun getModuleDebugInfo(): MutableMap<String, String> {
    return hashMapOf<String, String>().apply {
        put("module_version_name", BuildConfig.VERSION_NAME)
        put("module_version_code", "${BuildConfig.VERSION_CODE}")
        put("module_version_package", BuildConfig.APPLICATION_ID)
        put("module_build_type", BuildConfig.BUILD_TYPE)
        put("module_build_debug", "${BuildConfig.DEBUG}")
        put(
            "module_build_time",
            PJavaUtils.commentDetailTime(
                "yyyy年MM月dd日HH:mm:ss",
                BuildConfig.BUILD_TIMESTAMP / 1000
            )
        )
        put("sys_brand", Build.BRAND)
        put("sys_display", Build.DISPLAY)
        put("sys_product", Build.PRODUCT)
        put("sys_device", Build.DEVICE)
        put("sys_ver", Build.VERSION.SDK)
        put("sys_ver_int", "${Build.VERSION.SDK_INT}")
        put("sys_ver_release", Build.VERSION.RELEASE)
    }
}

fun String.openUrl(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(this)
    context.startActivity(intent)
}

operator fun StringBuilder.plusAssign(str: String) {
    this.append(str).append("\n")
}

fun XC_MethodHook.MethodHookParam.dump() {
    val args = this.args
    val m = this.method as Executable
    val sb = StringBuilder()
    sb += "ZuiyouLiteNetDump=>"
    sb += "============================================================"
    sb += "方法 ${m.name} 被调用"
    if (!Modifier.isStatic(m.modifiers)) {
        sb += "   方法来自类: ${this.thisObject?.javaClass?.name}"
        sb += "~~~~~~~~~~~~~~~~~~~~"
        this.thisObject.javaClass.findAllFields {
            !Modifier.isStatic(this.modifiers)
        }.forEach {
            val value = XposedHelpers.getObjectField(this.thisObject, it.name)
            if (value == null) {
                sb += "     变量(${it.type.name}): null"
            } else {
                sb += "     变量(${it.type.name}): $value"
            }
        }
    } else {
        sb += "   方法是静态的，无法获得当前实例"
    }
    sb += "~~~~~~~~~~~~~~~~~~~~"
    if (args != null) {
        sb += "   方法参数如下(${args.size}):"
        for ((i, a) in args.withIndex()) {
            sb += if (a == null) {
                "[$i](null)       null"
            } else {
                if (a is ByteArray) {
                    "[$i](${a.javaClass.name})       $a(${Arrays.toString(a)})\n[$i](${a.javaClass.name})       $a(${
                        String(
                            a
                        )
                    })"


                } else {
                    "[$i](${a.javaClass.name})       $a"
                }

            }
        }
    } else {
        sb += "   方法参数为null"
    }
    if (m is Method) {
        sb += "~~~~~~~~~~~~~~~~~~~~"
        sb += if (m.returnType != Void.TYPE) {
            if (this.result == null) {
                "   方法返回值(${m.returnType.name}): null"
            } else {
                "   方法返回值(${m.returnType.name}): ${this.result}"
            }
        } else {
            "   方法没有返回值"
        }
        sb += "~~~~~~~~~~~~~~~~~~~~"
    }

    sb += "   方法参数类型如下(${m.parameterCount}):"
    for ((i, t) in m.parameterTypes.withIndex()) {
        sb += if (t == null) {
            "[$i]       null"
        } else {
            "[$i]       ${t.name}"
        }
    }
    sb += "============================================================"
    sb.toString().error()
}


fun postOperator(): Any? {
    val clazz = "cn.xiaochuankeji.zuiyouLite.ui.postlist.holder.PostOperator".findClass()
    for (m in clazz.declaredMethods) {
        if (m.returnType == clazz) {
            return XposedHelpers.callStaticMethod(clazz, m.name)
        }
    }
    return null
}


fun Any.callMethod(name: String, vararg a: Any): Any? {
    return XposedHelpers.callMethod(this, name, a)
}

fun Any.callMethod(vararg a: Any?): Any? {
    for (m in this.javaClass.declaredMethods) {
        if (m.paramCount == a.size) {
            var c = true
            for (i in 0..m.paramCount) {
                if (a[i]?.javaClass != m.parameterTypes[i]) {
                    c = false
                    break
                }
            }
            if (c) {
                return XposedHelpers.callMethod(this, m.name, a)
            }
        }
    }
    return null
}