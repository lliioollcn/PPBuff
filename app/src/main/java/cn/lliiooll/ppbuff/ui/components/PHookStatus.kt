package cn.lliiooll.ppbuff.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import cn.lliiooll.ppbuff.R
import cn.lliiooll.ppbuff.utils.HookStatus
import cn.lliiooll.ppbuff.utils.PColor


@Preview(showBackground = true)
@Composable
fun PHookStatus() {
    Surface(
        color = if (isSystemInDarkTheme()) Color.DarkGray else Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 10.dp, 10.dp, 10.dp)

        ) {
            // 主体
            /*
            val color = if (HookStatus.isEnable()) PColor.SUCCESS else PColor.ERROR
            Surface(
                color = if (isSystemInDarkTheme()) Color.DarkGray else Color.White,
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 0.dp, 10.dp, 0.dp),
                    color = color,
                    border = BorderStroke(1.dp, color),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(modifier = Modifier.padding(20.dp, 18.dp, 0.dp, 18.dp)) {
                        Surface(
                            color = Color.Unspecified,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {

                            Image(
                                painter = painterResource(if (HookStatus.isEnable()) R.drawable.ic_round_check else R.drawable.ic_round_close),
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
                                Text(
                                    text = if (HookStatus.isEnable()) "模块已激活" else "模块未激活",
                                    textAlign = TextAlign.Center,
                                    fontSize = TextUnit(18f, TextUnitType.Sp),
                                    color = Color.White,
                                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp)
                                )
                                Text(
                                    text = HookStatus.getProvider(),
                                    textAlign = TextAlign.Center,
                                    fontSize = TextUnit(13f, TextUnitType.Sp),
                                    color = Color.White
                                )
                            }
                        }
                    }


                }
            }
            */

            Surface(
                color = if (isSystemInDarkTheme()) Color.DarkGray else Color.White,
                modifier = Modifier.padding(0.dp,20.dp,0.dp,0.dp)
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
                                Text(
                                    text = "特别鸣谢",
                                    textAlign = TextAlign.Center,
                                    fontSize = TextUnit(18f, TextUnitType.Sp),
                                    modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp)
                                )
                                Text(
                                    text = "感谢群友 1181469655@qq.com 提供的自动任务相关接口",
                                    textAlign = TextAlign.Left,
                                    fontSize = TextUnit(13f, TextUnitType.Sp),
                                )
                            }
                        }
                    }


                }
            }

        }
    }

}