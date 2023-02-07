package cn.lliiooll.ppbuff.hook.zuiyouLite

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONUtil
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.R
import cn.lliiooll.ppbuff.data.ZyLiteTypes
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PViewType
import cn.lliiooll.ppbuff.data.types.PWebTaskType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.utils.PJavaUtils
import cn.lliiooll.ppbuff.utils.async
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.toastShort

object ZuiYouLiteWebTaskHook : BaseHook(
    "云端自动任务(未成熟，不建议使用)", "web_task", PHookType.PLAY
) {
    override fun init(): Boolean {
        async {
            val lastTime = PConfig.numberEx("web_task_last_time", 0L)
            if (PJavaUtils.isPassDay(lastTime)) {
                val type = PWebTaskType.getValue(
                    PConfig.string(
                        "web_task_now",
                        PWebTaskType.COMMON1.getLabel()
                    )
                )
                val url: String
                if (type == PWebTaskType.OTHERS) {
                    url = PConfig.string("web_task_custom", "")
                } else {
                    url = ZyLiteTypes.webTaskList.getOrDefault(type.getLabel(), "")
                }
                if (url.isBlank()) {
                    "推送失败，原因: 空URL".debug()
                    "推送失败，原因: 不存在的Url".toastShort()
                } else {
                    if (PJavaUtils.isConnected(url)) {
                        "尝试获取Token".debug()
                        ZuiYouLiteWebTokenHook.getWebToken {

                            val result = HttpUtil.createPost(url)
                                .body(it, "application/json;charset=utf-8")
                                .execute()
                                .body()
                            "返回数据: $result".debug()
                            if (result.isBlank()){
                                "推送失败，原因: 服务器未返回数据".debug()
                                "推送失败，原因: 服务器未返回数据".toastShort()
                            }else{
                                val rd = JSONUtil.parseObj(result)
                                if (rd.getInt("code", 666) == 0) {
                                    PConfig.set("web_task_last_time", System.currentTimeMillis())
                                    "推送成功".debug()
                                    "推送成功".toastShort()
                                } else {
                                    val msg = rd.getStr("msg", "服务器未返回数据")
                                    "推送失败，原因: $msg".debug()
                                    "推送失败，原因: $msg".toastShort()
                                }
                            }
                        }
                    } else {
                        "推送失败，原因: Url连接失败".debug()
                        "推送失败，原因: Url连接失败".toastShort()
                    }
                }
            }
        }
        return true
    }

    @Composable
    @OptIn(ExperimentalMaterial3Api::class)
    override fun compose(navController: NavHostController) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp, 5.dp, 10.dp, 0.dp)
        ) {
            Column {
                val ctx = LocalContext.current
                Text(
                    text = "如果你启用了这一项，模块将会在你每一天第一次启动模块时向云端平台推送你的Token",
                    fontSize = TextUnit(14f, TextUnitType.Sp)
                )
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

                Text(text = "选择你要推送Token的平台:", fontSize = TextUnit(14f, TextUnitType.Sp))
                Text(text = "注意:", fontSize = TextUnit(14f, TextUnitType.Sp), color = Color.Red)
                Text(
                    text = "请确保你所选的平台是否可信",
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    color = Color.Red
                )
                Text(
                    text = "如果你不信任此平台请不要向此平台推送你的Token",
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    color = Color.Red
                )
                var openMenu by remember {
                    mutableStateOf(false)
                }
                Row(modifier = Modifier
                    .clickable {
                        openMenu = !openMenu
                    }
                    .padding(0.dp, 15.dp, 0.dp, 15.dp)) {
                    Text(
                        text = "选择平台",
                        fontSize = TextUnit(17f, TextUnitType.Sp),
                        modifier = Modifier
                            .weight(1f, true)
                            .align(Alignment.CenterVertically)
                    )
                    Text(
                        text = PWebTaskType.getValue(
                            PConfig.string(
                                "web_task_now",
                                PWebTaskType.COMMON1.getLabel()
                            )
                        ).getShow(),
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        fontSize = TextUnit(12f, TextUnitType.Sp),
                    )
                    DropdownMenu(expanded = openMenu, onDismissRequest = { openMenu = false }) {
                        PWebTaskType.values().forEach {
                            DropdownMenuItem(text = {
                                Text(text = it.getShow())
                            }, onClick = {
                                openMenu = false
                                PConfig.set("web_task_now", it.getLabel())
                                "保存成功".toastShort()
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

                Text(
                    text = "如果你在上面一项中选中了自定义，请在这里填写推送url",
                    fontSize = TextUnit(14f, TextUnitType.Sp)
                )
                Text(text = "注意:", fontSize = TextUnit(14f, TextUnitType.Sp), color = Color.Red)
                Text(
                    text = "请确保你所选的平台是否可信",
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    color = Color.Red
                )
                Text(
                    text = "如果你不信任此平台请不要向此平台推送你的Token",
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    color = Color.Red
                )
                Text(
                    text = "将Token推送给自定义平台所造成的的任何问题我们不负任何责任",
                    fontSize = TextUnit(14f, TextUnitType.Sp),
                    color = Color.Red
                )
                Row(modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 0.dp)) {// 自定义格式
                    var timeFormat by remember {
                        mutableStateOf(
                            PConfig.string(
                                "web_task_custom",
                                ""
                            )
                        )
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
                        if (timeFormat.isBlank()) {
                            timeFormat = ""
                        }
                        PConfig.set("web_task_custom", timeFormat)
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

    override fun isEnable(): Boolean {
        //return PConfig.boolean(label,false)
        return false
    }
}