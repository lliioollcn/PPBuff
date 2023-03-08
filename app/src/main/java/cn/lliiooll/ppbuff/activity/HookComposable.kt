package cn.lliiooll.ppbuff.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.navigation.NavHostController
import cn.lliiooll.ppbuff.R
import cn.lliiooll.ppbuff.hook.PHook
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PViewType
import cn.lliiooll.ppbuff.utils.toastShort

class CommonComposable


@Composable
fun CommonHookList(navController: NavHostController) {
    HoolList(type = PHookType.COMMON, navController)
}

@Composable
fun SimpleHookList(navController: NavHostController) {
    HoolList(type = PHookType.SIMPLE, navController)
}

@Composable
fun PlayHookList(navController: NavHostController) {
    HoolList(type = PHookType.PLAY, navController)
}

@Composable
fun DebugHookList(navController: NavHostController) {
    HoolList(type = PHookType.DEBUG, navController)
}


@Composable
fun HoolList(type: PHookType, navController: NavHostController) {
    Column {
        PHook.thisLoader?.hooks()?.forEach {
            val hook = it
            val ctx = LocalContext.current
            if (hook.type == type) {
                // 判断视图类型
                if (hook.view() == PViewType.SWITCH) {
                    var hookEnable by remember {
                        mutableStateOf(hook.isEnable())
                    }
                    var lastSwitch by remember {
                        mutableStateOf(0L)
                    }
                    var quickSwitch by remember {
                        mutableStateOf(0)
                    }
                    Row {
                        Text(
                            text = hook.name,
                            fontSize = TextUnit(15f, TextUnitType.Sp),
                            modifier = Modifier
                                .weight(1f, true)
                                .align(Alignment.CenterVertically)
                        )
                        Switch(checked = hookEnable, onCheckedChange = {
                            hook.setEnable(it)
                            if (hook.needCustomClick()) {
                                hook.click()
                            }
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
                } else if (hook.view() == PViewType.CUSTOM) {
                    Row(modifier = Modifier
                        .clickable {
                            if (hook.needCustomClick()) {
                                hook.click()
                            }
                            if (hook.router()) {
                                navController.navigate(hook.label)
                            }
                        }
                        .padding(0.dp, 5.dp, 0.dp, 5.dp)) {
                        Text(
                            text = hook.name,
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
    }
}