package cn.lliiooll.ppbuff.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.lliiooll.ppbuff.activity.base.PActivity
import cn.lliiooll.ppbuff.activity.base.theme.PPBuffTheme
import cn.lliiooll.ppbuff.hook.PHookType
import cn.lliiooll.ppbuff.ui.components.PTitleBar

class ConfigActivity : PActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PPBuffTheme {
                Column {
                    val navController: NavHostController = rememberNavController()
                    StatusBar()// 沉浸式状态栏
                    PTitleBar()// 标题栏
                    NavHost(
                        navController = navController,
                        startDestination = "main"
                    ) {// 导航界面
                        composable("main") {// 主界面
                            ConfigMainComposable(navController)
                        }
                    }
                }

            }
        }

    }
}

@Composable
fun ConfigMainComposable(navController: NavHostController) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        color = if (isSystemInDarkTheme()) {
            Color.DarkGray
        } else {
            Color.White
        }
    ) {
        Column {
            PHookType.values().forEach {
                if (it != PHookType.HIDE) {

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp, 5.dp, 10.dp, 5.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(15.dp, 5.dp, 15.dp, 5.dp),
                        ) {
                            Text(
                                text = it.getLabel(),
                                modifier = Modifier.weight(1f, true),
                                fontSize = TextUnit(15f, TextUnitType.Sp),
                                color = if (isSystemInDarkTheme()) {
                                    Color(0xff1CB5E0)
                                } else {
                                    Color(0xFF0000C8)
                                }
                            )

                            if (it == PHookType.COMMON) {
                                CommonHookList()
                            } else if (it == PHookType.SIMPLE) {
                                SimpleHookList()
                            } else if (it == PHookType.DEBUG) {
                                DebugHookList()
                            } else if (it == PHookType.PLAY) {
                                PlayHookList()
                            }
                        }
                    }
                }
            }
        }
    }
}
