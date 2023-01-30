package cn.lliiooll.ppbuff.utils

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.tracker.PLog
import com.github.kyuubiran.ezxhelper.utils.findField
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.paramCount
import de.robv.android.xposed.XposedHelpers
import java.io.File
import java.lang.reflect.Field


class Utils {
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
    Toast.makeText(ctx, this, Toast.LENGTH_SHORT).show()
}


fun String.toastLong(ctx: Context) {
    Toast.makeText(ctx, this, Toast.LENGTH_LONG).show()
}

fun Context.inflate(layout: Int): View {
    return LayoutInflater.from(this).inflate(layout, null)
}

fun sync(function: () -> Unit) {
    val handler = Handler(Looper.getMainLooper())
    handler.post(function)
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
    val clazz = "${PPBuff.getApplication().packageName}.R\$id".findClass()
    return XposedHelpers.getStaticIntField(clazz, this)
}

fun Context.jumpTo(clazz: Class<*>) {
    val intent = Intent(this, clazz)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    this.startActivity(intent)

}



