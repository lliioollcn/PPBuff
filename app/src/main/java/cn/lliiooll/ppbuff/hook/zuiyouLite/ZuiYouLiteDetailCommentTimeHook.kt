package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.view.ViewGroup
import android.widget.TextView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PViewType
import cn.lliiooll.ppbuff.utils.PJavaUtils
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.sync
import cn.lliiooll.ppbuff.utils.toastShort
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.paramCount
import de.robv.android.xposed.XposedHelpers

object ZuiYouLiteDetailCommentTimeHook : BaseHook(
    "评论显示详细时间", "comment_time_detail", PHookType.PLAY
) {
    override fun init(): Boolean {
        if (!ZuiYouLiteDetailLocationHook.isEnable()) {
            "cn.xiaochuankeji.zuiyouLite.ui.slide.ab.holder.ReviewBasicViewControl"
                .findClass()
                .findMethod {
                    this.paramCount == 2
                            && this.parameterTypes[0].name.contains("CommentBean")
                            && java.lang.reflect.Modifier.isFinal(modifiers)
                            && !java.lang.reflect.Modifier.isStatic(modifiers)
                            && returnType == Void.TYPE
                }
                .hookAfter {
                    val commentData = it.args[0]
                    val createTime = XposedHelpers.getLongField(commentData, "createTime")
                    val nameMultiView = XposedHelpers.getObjectField(it.thisObject, "nameMultiView")
                    val ipAtribution = XposedHelpers.getObjectField(commentData, "ipAtribution")
                    var i = 0
                    for (f in nameMultiView.javaClass.declaredFields) {
                        if (f.type == TextView::class.java) {
                            if (i != 0) {
                                val textView = XposedHelpers.getObjectField(
                                    nameMultiView,
                                    f.name
                                ) as TextView
                                val time = PJavaUtils.commentDetailTime(
                                    PConfig.string("config_time_format", "yyyy年MM月dd日HH:mm:ss"),
                                    createTime
                                )
                                sync {
                                    val lp = textView.layoutParams
                                    lp.width = ViewGroup.LayoutParams.MATCH_PARENT
                                    textView.layoutParams = lp
                                    textView.maxLines = 10
                                    textView.isSingleLine = false
                                    textView.onWindowFocusChanged(true)
                                    textView.text = "$time · $ipAtribution"
                                }
                                break
                            } else {
                                i++
                            }
                        }
                    }
                }
        }
        return true
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun compose(navController: NavHostController) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()

        ) {
            Column(modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 0.dp)) {
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

                Text(text = "时间格式说明:", fontSize = TextUnit(14f, TextUnitType.Sp))
                Text(text = "yyyy = 年", fontSize = TextUnit(14f, TextUnitType.Sp))
                Text(text = "MM = 月", fontSize = TextUnit(14f, TextUnitType.Sp))
                Text(text = "dd = 日", fontSize = TextUnit(14f, TextUnitType.Sp))
                Text(text = "HH = 小时", fontSize = TextUnit(14f, TextUnitType.Sp))
                Text(text = "mm = 分钟", fontSize = TextUnit(14f, TextUnitType.Sp))
                Text(text = "ss = 秒", fontSize = TextUnit(14f, TextUnitType.Sp))
                Text(text = "在下方输入时间格式:", fontSize = TextUnit(14f, TextUnitType.Sp))

                Row(modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 0.dp)) {// 自定义格式
                    var timeFormat by remember {
                        mutableStateOf(PConfig.string("config_time_format", "yyyy年MM月dd日HH:mm:ss"))
                    }
                    TextField(modifier = Modifier
                        .weight(1f, true)
                        .padding(0.dp, 0.dp, 10.dp, 0.dp)
                        .align(Alignment.CenterVertically),
                        value = timeFormat,
                        onValueChange = {
                            timeFormat = it
                        })

                    Button(onClick = {
                        if (timeFormat.isBlank()){
                            timeFormat = "yyyy年MM月dd日HH:mm:ss"
                        }
                        PConfig.set("config_time_format", timeFormat)
                        "保存成功".toastShort(ctx)
                    }) {
                        Text(text = "保存")
                    }
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
}