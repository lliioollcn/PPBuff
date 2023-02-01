package cn.lliiooll.ppbuff.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.R
import cn.lliiooll.ppbuff.activity.base.hideIcon


@Preview(showBackground = true)
@Composable
fun PTitleBar() {
    Surface {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 20.dp, 10.dp, 10.dp),
        ) {
            Text(
                text = "PPBuff",
                modifier = Modifier
                    .weight(1f, true),
                fontSize = TextUnit(20f, TextUnitType.Sp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold,
                /*
                Brush.horizontalGradient(colors = arrayListOf<Color>().apply {
                        add(Color(0xff12c2e9))
                        add(Color(0xffc471ed))
                        add(Color(0xfff64f59))
                    })
                 */
            )
            val ctx = LocalContext.current
            if (!PPBuff.isInHostApp()) {
                var status by remember {
                    mutableStateOf(true)
                }
                Surface(shape = CircleShape) {
                    val f: Float by animateFloatAsState(
                        targetValue = if (status) 0f else 90f,
                        animationSpec = tween(
                            durationMillis = 300
                        ),
                        finishedListener = {

                        }
                    )
                    Image(
                        painter = painterResource(if (isSystemInDarkTheme()) R.drawable.ic_more_dark else R.drawable.ic_more_light),
                        contentDescription = "icon_more",
                        modifier = Modifier
                            .size(25.dp)
                            .clickable {
                                status = !status
                                // 弹出dialog
                            }
                            .graphicsLayer {
                                transformOrigin = TransformOrigin.Center
                                rotationZ = f
                            }
                    )

                    val m_h: Float by animateFloatAsState(
                        targetValue = if (status) 0f else 60f,
                    )

                    DropdownMenu(
                        expanded = !status,
                        onDismissRequest = {
                            status = true
                        },
                        modifier = Modifier
                            .width(100.dp)
                            .height(m_h.dp)
                    ) {

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = if (PConfig.isHideConfig()) {
                                        "显示图标"
                                    } else {
                                        "隐藏图标"
                                    }
                                )
                            },
                            onClick = {
                                status = true
                                hideIcon(ctx)
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }

                }
            }
        }
    }
}