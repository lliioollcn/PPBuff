package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.navigation.NavHostController
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.R
import cn.lliiooll.ppbuff.activity.ConfigActivity
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PViewType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.hook.isValid
import cn.lliiooll.ppbuff.tracker.PLog.Companion.d
import cn.lliiooll.ppbuff.utils.IOUtils
import cn.lliiooll.ppbuff.utils.PDownload
import cn.lliiooll.ppbuff.utils.async
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.downloadVideo
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.sync
import cn.lliiooll.ppbuff.utils.toastShort
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.hookReplace
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.style.IOSStyle
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Method


object ZuiYouLiteAudioDownloadHook : BaseHook(
    "语音下载", "audio_download", PHookType.PLAY
) {

    private const val OBF_AIO1 = "cn.xiaochuankeji.zuiyouLite.post.review.AIO1"
    private const val OBF_AIO2 = "cn.xiaochuankeji.zuiyouLite.post.review.AIO2"
    private const val OBF_AIO3 = "cn.xiaochuankeji.zuiyouLite.post.review.AIO3"
    override fun init(): Boolean {
        "cn.xiaochuankeji.zuiyouLite.ui.postlist.holder.PostOperator"
            .findClass()
            .findAllMethods {
                this.parameterTypes.size == 5 && this.parameterTypes[0] == Activity::class.java && this.parameterTypes[1] == String::class.java
            }
            .hookReplace {
                val imageData = it.args[2]
                // 下载
                imageData?.downloadVideo()
            }
        val findMs: MutableList<Method> = ArrayList<Method>()

        PConfig.getCache(OBF_AIO3)
            .forEach {
                "在类 $it 中寻找方法".debug()
                val clazz = it.findClass()
                for (m in clazz.declaredMethods) {
                    if (m.parameterCount == 1 && m.parameterTypes[0] === View::class.java && m.returnType === Boolean::class.javaPrimitiveType
                    ) {
                        d("过滤方法: " + m.name)
                        findMs.add(m)
                    }
                }
                val m = findMs[findMs.size - 1]
                m.hookReplace { param ->
                    d(">>>>>>>>>>")
                    d("调用方法: " + m.name)
                    d("来自: $it")
                    val view = param.args[0] as View
                    EzXHelperInit.addModuleAssetPath(view.context)
                    EzXHelperInit.addModuleAssetPath(view.context)
                    for (f in clazz.declaredFields) {
                        if (f.type.name.contains("CommentBean")) {
                            val commentBean = XposedHelpers.getObjectField(param.thisObject, f.name)
                            if (commentBean != null) {
                                d("评论bean不为null")
                                val audio = XposedHelpers.getObjectField(commentBean, "audio")
                                if (audio != null) {
                                    val url = XposedHelpers.getObjectField(audio, "url") as String
                                    d("语音url: $url")
                                    MessageDialog.build()
                                        .setTitle("选择")
                                        .setMessage("请选择你的操作")
                                        .setStyle(IOSStyle.style())
                                        .setOkButton("下载语音") { _, _ ->
                                            doDownloadVoice(url)
                                            false
                                        }
                                        .setCancelButton("原操作") { _, _ ->
                                            src1(view, param.thisObject)
                                            false
                                        }
                                        .show()
                                } else {
                                    src1(view, param.thisObject)
                                }
                            } else {
                                d("评论bean为null!!!")
                                src1(view, param.thisObject)
                            }
                        }
                    }
                    d(">>>>>>>>>>")
                    return@hookReplace true
                }
            }


        return true
    }

    private fun doDownloadVoice(url: String) {
        if (PConfig.string("voiceSavePath", "").isEmpty()) {
            sync {
                "请选择一个读取路径".toastShort()
                if (ConfigActivity.saveAudioRecord != null) {
                    ConfigActivity.saveAudioRecord!!.launch(
                        Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
                    )
                }
                "启动完毕".debug()
            }
        } else {
            async {
                val file = PDownload.downloadTemp(url)
                val path = PConfig.string("voiceSavePath", "")
                if (path.isBlank()) {
                    "未设置SAF路径，语音保存失败!".toastShort()
                } else {
                    val uri = Uri.parse(path)
                    val saveDir = DocumentFile.fromTreeUri(
                        PPBuff.getApplication(), uri
                    )
                    val saveFile = saveDir?.createFile(
                        "audio/x-mpeg",
                        file.name
                    )
                    async {
                        IOUtils.copy(
                            PPBuff.getApplication(),
                            file,
                            saveFile?.uri
                        )
                        sync {
                            "语音下载成功".toastShort()
                        }
                    }
                }
            }
        }
    }

    private fun src1(view: View, thisObject: Any) {
        PConfig.getCache(OBF_AIO3).forEach { aio3 ->
            val clazz3: Class<*> = aio3.findClass()
            for (f in clazz3.declaredFields) {
                //if (f.getType().getSimpleName().length() == 2) {
                for (m in f.type.declaredMethods) {
                    if (m.returnType == Boolean::class.javaPrimitiveType && m.parameterTypes.size == 2 && m.parameterTypes[0] == View::class.java && m.parameterTypes[1] == Int::class.javaPrimitiveType) {
                        d("找到AIO4: " + f.type.name)
                        val aio4Obj = XposedHelpers.getObjectField(thisObject, f.name)
                        if (aio4Obj != null) {
                            XposedHelpers.callMethod(aio4Obj, m.name, view, -1)
                            break
                        }
                    }
                }
                // }
            }
        }

    }

    private fun src(view: View, commentBean: Any, i3: Int, thisObject: Any): Boolean {
        var i2 = i3
        PConfig.getCache(OBF_AIO1).forEach {
            val aio1Clazz: Class<*> = it.findClass()
            if (aio1Clazz.declaredMethods.size > 1) {
                aio1Clazz.declaredMethods.forEach { method ->
                    if (method.parameterCount == 1 && method.parameterTypes[0] == Context::class.java && method.returnType == Activity::class.java) {
                        PConfig.getCache(OBF_AIO2).forEach { aio2 ->
                            val aio2Clazz: Class<*> = aio2.findClass()
                            for (m2 in aio1Clazz.declaredMethods) {
                                if (java.lang.reflect.Modifier.isStatic(m2.modifiers) && m2.parameterTypes.size == 1 && m2.parameterTypes[0] == Context::class.java && m2.returnType == Activity::class.java) {
                                    d("找到放方法AIO1: " + m2.name)
                                    val actObj =
                                        XposedHelpers.callStaticMethod(
                                            aio1Clazz,
                                            m2.name,
                                            view.context
                                        ) ?: return false
                                    val activity = actObj as Activity
                                    if (j_g_v_h0_w_t0_b_h(commentBean) && i2 == -1) {
                                        i2 = 0
                                    }
                                    for (m3 in aio2Clazz.declaredMethods) {
                                        if (m3.parameterTypes.size == 0 && java.lang.reflect.Modifier.isStatic(
                                                m3.modifiers
                                            ) && m3.returnType != Void.TYPE
                                        ) {
                                            d("找到放方法AIO2: " + m3.name)
                                            val aio3Obj =
                                                XposedHelpers.callStaticMethod(aio2Clazz, m3.name)
                                            for (m4 in aio3Obj.javaClass.declaredMethods) {
                                                if (m4.parameterTypes.size == 4 && !java.lang.reflect.Modifier.isStatic(
                                                        m4.modifiers
                                                    ) && m4.parameterTypes[0] == Activity::class.java && (m4.parameterTypes[1] == Any::class.java) and (m4.parameterTypes[3] == Int::class.javaPrimitiveType)
                                                ) {
                                                    d("找到放方法AIO3: " + m4.name)
                                                    XposedHelpers.callMethod(
                                                        aio3Obj,
                                                        m4.name,
                                                        activity,
                                                        thisObject,
                                                        commentBean,
                                                        i2
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true
    }

    fun j_g_v_h0_w_t0_b_h(ins: Any?): Boolean {
        return if (ins == null) {
            false
        } else {
            val serverImageBeanObj =
                XposedHelpers.getObjectField(ins, "serverImages") ?: return false
            val serverImageBean = serverImageBeanObj as List<*>
            if (serverImageBean.isEmpty()) {
                false
            } else {
                if (serverImageBean[0] == null) {
                    false
                } else {
                    serverImageBean.size == 1
                }
            }
        }
    }


    @Composable
    override fun compose(navController: NavHostController) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp, 5.dp, 10.dp, 0.dp)
        ) {
            Column {
                val ctx = LocalContext.current
                Row(modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 5.dp)) {// 开关
                    var hookEnable by remember {
                        mutableStateOf(isEnable())
                    }
                    var lastSwitch by remember {
                        mutableStateOf(0L)
                    }
                    var quickSwitch by remember {
                        mutableStateOf(0)
                    }
                    Text(
                        text = name,
                        fontSize = TextUnit(15f, TextUnitType.Sp),
                        modifier = Modifier
                            .weight(1f, true)
                            .align(Alignment.CenterVertically)
                    )
                    Switch(checked = hookEnable, onCheckedChange = {
                        setEnable(it)
                        hookEnable = it
                        if (System.currentTimeMillis() - lastSwitch > 5000L) {
                            "重启应用生效".toastShort(ctx)
                            quickSwitch = 0
                        } else {
                            quickSwitch++
                        }
                        if (quickSwitch > 100) {
                            "点点点，都nm快点坏了还点".toastShort(ctx)
                        }
                        lastSwitch = System.currentTimeMillis()
                    })
                }

                Row(modifier = Modifier
                    .clickable {
                        "请选择一个读取路径".toastShort(ctx)
                        if (ConfigActivity.saveAudioRecord != null) {
                            ConfigActivity.saveAudioRecord!!.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE))
                        }
                        "启动完毕".debug()
                    }
                    .padding(0.dp, 5.dp, 0.dp, 5.dp)) {
                    Text(
                        text = "选择语音保存路径",
                        fontSize = TextUnit(15f, TextUnitType.Sp),
                        modifier = Modifier
                            .weight(1f, true)
                            .align(Alignment.CenterVertically)
                    )
                    Image(
                        painter = painterResource(if (isSystemInDarkTheme()) R.drawable.ic_arrow_right_dark else R.drawable.ic_arrow_right_light),
                        contentDescription = "icon_more",
                        modifier = Modifier
                            .size(25.dp)
                    )
                }
            }

        }
    }

    override fun router(): Boolean {
        return true
    }

    override fun view(): PViewType {
        return PViewType.CUSTOM
    }

    override fun deobfMap(): Map<String, List<String>> {
        return hashMapOf<String, List<String>>().apply {
            put(OBF_AIO1, arrayListOf<String>().apply {
                add("^%d:%02d:%02d$")
                add("^%02d:%02d$")
                add("00:00")
            })
            put(OBF_AIO2, arrayListOf<String>().apply {
                add("巡查举报")
                add("举报成功，感谢你对家园的贡献!")
            })
            put(OBF_AIO3, arrayListOf<String>().apply {
                add("event_on_play_review_comment")
                add("videocomment")
            })
        }
    }

    override fun needDeobf(): Boolean {
        return !PConfig.hasCache(OBF_AIO1) || !PConfig.getCache(
            OBF_AIO1
        )?.isValid()!!
                ||
                !PConfig.hasCache(OBF_AIO2) || !PConfig.getCache(
            OBF_AIO2
        )?.isValid()!!
                ||
                !PConfig.hasCache(OBF_AIO3) || !PConfig.getCache(
            OBF_AIO3
        )?.isValid()!!
    }

}