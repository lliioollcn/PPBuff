package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.navigation.NavHostController
import cn.hutool.core.util.NumberUtil
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.R
import cn.lliiooll.ppbuff.activity.ConfigActivity
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PRecordType
import cn.lliiooll.ppbuff.data.types.PViewType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.toastLong
import cn.lliiooll.ppbuff.utils.toastShort
import cn.lliiooll.ppbuff.view.FloatingViewTouch
import cn.lliiooll.ppbuff.view.PDialogVoice
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter

object ZuiYouLiteVoiceSendHook : BaseHook(
    "语音发送", "voice_send", PHookType.PLAY
) {
    private var imageView: ImageView? = null
    private var dialog: PDialogVoice? = null

    override fun init(): Boolean {
        "cn.xiaochuankeji.zuiyouLite.ui.input.ActivityInputReview"
            .findClass()
            .findMethod {
                this.name == "onCreate"
            }
            .hookAfter {
                val activity = it.thisObject as Activity
                if (!Settings.canDrawOverlays(activity)) {
                    "请开启皮皮搞笑的悬浮窗权限来使用此功能".toastShort()
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    intent.data =
                        Uri.parse("package:" + PPBuff.getApplication().packageName)
                    activity.startActivityForResult(
                        intent,
                        0x3c
                    )
                } else {
                    initOverlay(activity)
                }
            }
        "cn.xiaochuankeji.zuiyouLite.ui.input.ActivityInputReview"
            .findClass()
            .findMethod {
                this.name == "onActivityResult"
            }
            .hookAfter {
                val req = it.args[0] as Int
                val activity = it.thisObject as Activity
                if (req == 0x3c) {
                    if (!Settings.canDrawOverlays(activity)) {
                        "请开启皮皮搞笑的悬浮窗权限来使用此功能".toastShort()
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        intent.data =
                            Uri.parse("package:" + PPBuff.getApplication().packageName)
                        activity.startActivityForResult(
                            intent,
                            0x3c
                        )
                    } else {
                        initOverlay(activity)
                    }
                }
            }
        "cn.xiaochuankeji.zuiyouLite.ui.input.ActivityInputReview"
            .findClass()
            .findMethod {
                this.name == "onDestroy"
            }
            .hookAfter {
                val activity = it.thisObject as Activity
                if (imageView != null) {
                    activity.windowManager
                        .removeView(imageView)
                    imageView = null
                }

                if (dialog != null) {
                    if (dialog!!.isShowing) {
                        dialog!!.dismiss()
                    }

                    dialog = null
                }

            }
        return true
    }

    private fun initOverlay(activity: Activity) {
        if (imageView == null) {
            imageView = ImageView(activity)
            imageView!!.setBackgroundResource(R.drawable.ic_voice)
            imageView!!.setOnClickListener { v: View? ->
                val path: String = PConfig.string("voicePath", "")
                if (path.isBlank()) {
                    "请到设置中选择语音路径后继续".toastLong(activity)
                    "启动完毕".debug()
                } else {
                    val uri = Uri.parse(path)
                    if (!activity.isDestroyed && !activity.isFinishing) {
                        if (dialog == null) {
                            dialog =
                                PDialogVoice(activity)
                        }
                        dialog!!.uri(uri).show()
                    }
                }
            }
            val lp = WindowManager.LayoutParams(120, 120, 0, 0, PixelFormat.TRANSPARENT)
            lp.flags = (WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            lp.format = PixelFormat.RGBA_8888
            lp.x = activity.windowManager.defaultDisplay.width / 2 - 20
            lp.y = 0
            imageView!!.setOnTouchListener(
                FloatingViewTouch(lp, activity.windowManager)
            )
            activity.windowManager.addView(
                imageView,
                lp
            )
        }
    }

    override fun isEnable(): Boolean {
        return return PConfig.boolean(label, false)
    }

    override fun router(): Boolean {
        return true
    }

    override fun view(): PViewType {
        return PViewType.CUSTOM
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun compose(navController: NavHostController) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp, 5.dp, 10.dp, 0.dp)
        ) {
            Column {
                val ctx = LocalContext.current as Activity
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
                            if (!Settings.canDrawOverlays(ctx)) {
                                "请开启皮皮搞笑的悬浮窗权限来使用此功能".toastShort()
                                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                                intent.data =
                                    Uri.parse("package:" + PPBuff.getApplication().packageName)
                                ctx.startActivityForResult(
                                    intent,
                                    0x3c
                                )
                            }
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

                Row(modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp)) {// 自动转换
                    var hookEnable by remember {
                        mutableStateOf(PConfig.boolean("voiceAutoCovert", true))
                    }
                    var lastSwitch by remember {
                        mutableStateOf(0L)
                    }
                    var quickSwitch by remember {
                        mutableStateOf(0)
                    }
                    Text(
                        text = "自动转换语音格式",
                        fontSize = TextUnit(15f, TextUnitType.Sp),
                        modifier = Modifier
                            .weight(1f, true)
                            .align(Alignment.CenterVertically)
                    )
                    Switch(checked = hookEnable, onCheckedChange = {
                        PConfig.set("voiceAutoCovert", it)
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
                        if (ConfigActivity.readVoiceDir != null) {
                            ConfigActivity.readVoiceDir!!.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE))
                        }
                        "启动完毕".debug()
                    }
                    .padding(0.dp, 5.dp, 0.dp, 5.dp)) {
                    Text(
                        text = "选择语音路径",
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
                Text(
                    text = "语音发送自定义秒数,单位: 秒",
                    fontSize = TextUnit(12f, TextUnitType.Sp),
                )
                Row(modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 0.dp)) {// 自定义时间
                    var timeFormat by remember {
                        mutableStateOf(PConfig.numberEx("voiceTime", 5201314L))
                    }
                    TextField(modifier = Modifier
                        .weight(1f, true)
                        .padding(0.dp, 0.dp, 10.dp, 0.dp)
                        .align(Alignment.CenterVertically),
                        value = "$timeFormat",
                        onValueChange = {
                            if (NumberUtil.isNumber(it)) {
                                timeFormat = it.toLong()
                            }
                        })

                    Button(onClick = {
                        PConfig.set("voiceTime", timeFormat)
                        "保存成功".toastShort(ctx)
                    }) {
                        Text(text = "保存")
                    }
                }
            }

        }
    }
}