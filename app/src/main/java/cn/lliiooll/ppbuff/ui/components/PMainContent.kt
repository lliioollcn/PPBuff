package cn.lliiooll.ppbuff.ui.components

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import cn.lliiooll.ppbuff.BuildConfig
import cn.lliiooll.ppbuff.R
import cn.lliiooll.ppbuff.utils.HookStatus
import cn.lliiooll.ppbuff.utils.PColor
import cn.lliiooll.ppbuff.utils.PJavaUtils
import cn.lliiooll.ppbuff.utils.openUrl

@Preview(showBackground = true)
@Composable
fun PMainContent() {
    Surface(
        color = if (isSystemInDarkTheme()) Color.DarkGray else Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp, 10.dp, 10.dp, 10.dp)

        ) {
            val activity = LocalView.current.context as Activity
            // 主体
            Surface(
                color = if (isSystemInDarkTheme()) Color.DarkGray else Color.White,
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 0.dp, 10.dp, 0.dp),
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.background),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(modifier = Modifier.padding(20.dp, 18.dp, 0.dp, 18.dp)) {
                        Surface(
                            color = Color.Unspecified,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {

                            Image(
                                painter = painterResource(if (isSystemInDarkTheme()) R.drawable.ic_round_info_dark else R.drawable.ic_round_info_light),
                                contentDescription = "icon",
                                modifier = Modifier.size(30.dp),
                                alignment = Alignment.BottomStart
                            )
                        }

                        Surface(
                            color = Color.Unspecified,
                            modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)
                        ) {
                            Column {
                                Row {
                                    Text(
                                        text = "模块适用于 皮皮搞笑、最右. 请在应用内设置进行模块设置",
                                        textAlign = TextAlign.Left,
                                        fontSize = TextUnit(15f, TextUnitType.Sp)
                                    )
                                }
                                Row {
                                    Text(
                                        text = "模块版本: ",
                                        textAlign = TextAlign.Center,
                                        fontSize = TextUnit(13f, TextUnitType.Sp)
                                    )
                                    Text(
                                        text = BuildConfig.VERSION_NAME,
                                        textAlign = TextAlign.Center,
                                        fontSize = TextUnit(13f, TextUnitType.Sp)
                                    )
                                }
                                Row {
                                    Text(
                                        text = "构建时间: ",
                                        textAlign = TextAlign.Center,
                                        fontSize = TextUnit(13f, TextUnitType.Sp)
                                    )
                                    Text(
                                        text = PJavaUtils.commentDetailTime(
                                            "yyyy年MM月dd日 HH:mm:ss",
                                            BuildConfig.BUILD_TIMESTAMP / 1000
                                        ),
                                        textAlign = TextAlign.Center,
                                        fontSize = TextUnit(13f, TextUnitType.Sp)
                                    )
                                }
                                Row {
                                    Text(
                                        text = "模块作者: lliiooll",
                                        textAlign = TextAlign.Center,
                                        fontSize = TextUnit(13f, TextUnitType.Sp)
                                    )
                                }
                                Row {
                                    Text(
                                        text = "模块Q群(一群): 1028233124 (已满)",
                                        textAlign = TextAlign.Center,
                                        fontSize = TextUnit(13f, TextUnitType.Sp)
                                    )
                                }
                                Row(modifier = Modifier.clickable {
                                    "https://qun.qq.com/qqweb/qunpro/share?inviteCode=1YB7zAnuZAu".openUrl(
                                        activity
                                    )
                                }) {
                                    Text(
                                        text = "模块频道: 点击加入",
                                        textAlign = TextAlign.Center,
                                        fontSize = TextUnit(13f, TextUnitType.Sp)
                                    )
                                }

                                Row(modifier = Modifier.clickable {
                                    "https://t.me/helperppgx".openUrl(activity)
                                }) {
                                    Text(
                                        text = "TG群组: 点击加入",
                                        textAlign = TextAlign.Center,
                                        fontSize = TextUnit(13f, TextUnitType.Sp)
                                    )
                                }
                                Row(modifier = Modifier.clickable {
                                    "https://t.me/ppbuff".openUrl(activity)
                                }) {
                                    Text(
                                        text = "TG频道: 点击加入",
                                        textAlign = TextAlign.Center,
                                        fontSize = TextUnit(13f, TextUnitType.Sp)
                                    )
                                }
                                /*
                                Row(modifier = Modifier.clickable {
                                    "https://t.me/ppbuffci".openUrl(activity)
                                }) {
                                    Text(
                                        text = "TGCI频道: 点击加入",
                                        textAlign = TextAlign.Center,
                                        fontSize = TextUnit(13f, TextUnitType.Sp)
                                    )
                                }

                                 */
                                Row(modifier = Modifier.clickable {
                                    "https://discord.gg/TQNvSv9Na9".openUrl(activity)
                                }) {
                                    Text(
                                        text = "Discord: 点击加入",
                                        textAlign = TextAlign.Center,
                                        fontSize = TextUnit(13f, TextUnitType.Sp)
                                    )
                                }
                                Row(modifier = Modifier.clickable {
                                    "https://github.com/lliioollcn/PPBuff".openUrl(activity)
                                }) {
                                    Text(
                                        text = "模块源码: 点击获取",
                                        textAlign = TextAlign.Center,
                                        fontSize = TextUnit(13f, TextUnitType.Sp)
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