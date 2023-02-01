package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.app.Activity
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.R
import cn.lliiooll.ppbuff.activity.ConfigActivity
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PRecordType
import cn.lliiooll.ppbuff.data.types.PViewType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.hook.isValid
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.downloadVideo
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.toastShort
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.paramCount
import de.robv.android.xposed.XposedHelpers

object ZuiYouLiteNoWaterMarkHook : BaseHook(
    "去水印", "no_water_mark", PHookType.COMMON
) {

    val DEOBFKEY_COMMENT_HOLDER = "cn.xiaochuankeji.zuiyouLite.common.CommentVideo"
    override fun init(): Boolean {

        "cn.xiaochuankeji.zuiyouLite.ui.postlist.holder.PostOperator"
            .findClass()
            .findMethod {
                this.paramCount == 5 && this.parameterTypes[0] == Activity::class.java && this.parameterTypes[1] == String::class.java
            }
            .hookAfter {
                val imageData = it.args[2]
                // 下载
                imageData?.downloadVideo()
            }
        PConfig.getCache(DEOBFKEY_COMMENT_HOLDER)
            .forEach {
                val clazz = it.findClass()
                for (m in clazz.declaredMethods) {
                    if (m.paramCount == 1 && m.parameterTypes[0].name.contains("CommentBean")) {
                        m.hookAfter {
                            val commentData = it.args[0]
                            // 下载
                            val serverImageBean =
                                XposedHelpers.getObjectField(commentData, "serverImages")
                            if (serverImageBean != null) {
                                val serverImageData = (serverImageBean as List<*>)[0]
                                serverImageData?.downloadVideo()
                            }
                        }
                        break
                    }
                }
            }
        return true
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
                var openMenu by remember {
                    mutableStateOf(false)
                }

                Row(modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp)) {// 开关
                    var hookEnable by remember {
                        mutableStateOf(PConfig.boolean("video_no_water_mark_plus", true))
                    }
                    var lastSwitch by remember {
                        mutableStateOf(0L)
                    }
                    var quickSwitch by remember {
                        mutableStateOf(0)
                    }
                    Text(
                        text = "增强版去水印",
                        fontSize = TextUnit(15f, TextUnitType.Sp),
                        modifier = Modifier
                            .weight(1f, true)
                            .align(Alignment.CenterVertically)
                    )
                    Switch(checked = hookEnable, onCheckedChange = {
                        PConfig.set("video_no_water_mark_plus", it)
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
                        openMenu = !openMenu
                    }
                    .padding(0.dp, 0.dp, 0.dp, 5.dp)) {
                    Text(
                        text = "选择储存方式",
                        fontSize = TextUnit(15f, TextUnitType.Sp),
                        modifier = Modifier
                            .weight(1f, true)
                            .align(Alignment.CenterVertically)
                    )
                    Text(
                        text = PRecordType.getValue(
                            PConfig.string(
                                "video_record",
                                PRecordType.STORE.getLabel()
                            )
                        ).getShow(),
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        fontSize = TextUnit(12f, TextUnitType.Sp),
                    )
                    DropdownMenu(expanded = openMenu, onDismissRequest = { openMenu = false }) {
                        PRecordType.values().forEach {
                            DropdownMenuItem(text = {
                                Text(text = it.getShow())
                            }, onClick = {
                                openMenu = false
                                if (it == PRecordType.SAF) {
                                    "请选择一个保存路径".toastShort(ctx)
                                    if (ConfigActivity.requestVideoRecord != null) {
                                        ConfigActivity.requestVideoRecord!!.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE))
                                    }
                                    "启动完毕".debug()
                                } else {
                                    PConfig.set("video_record", it.getLabel())
                                    "保存成功".toastShort(ctx)
                                }
                            })
                        }
                    }
                    Image(
                        painter = painterResource(if (isSystemInDarkTheme()) R.drawable.ic_arrow_right_dark else R.drawable.ic_arrow_right_light),
                        contentDescription = "icon_more",
                        modifier = Modifier
                            .size(25.dp)
                            .align(Alignment.CenterVertically)
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
            put(DEOBFKEY_COMMENT_HOLDER, arrayListOf<String>().apply {
                add("event_media_play_observer");
                add("event_on_play_review_comment");
                add("post");
                add("review");
                add("+%d");
                add("http://alfile.ippzone.com/img/mp4/id/");
                add("videocomment");
            })
        }
    }

    override fun needDeobf(): Boolean {
        return !PConfig.hasCache(DEOBFKEY_COMMENT_HOLDER) || !PConfig.getCache(
            DEOBFKEY_COMMENT_HOLDER
        )?.isValid()!!
    }

}