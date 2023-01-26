package cn.lliiooll.ppbuff.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.R

@Preview(showBackground = true)
@Composable
fun PTitleBar() {
    Surface {
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp, 20.dp, 10.dp, 10.dp),
        ) {
            if (PPBuff.isInHostApp())
                Surface(shape = CircleShape) {
                    Image(
                        painter = painterResource(R.mipmap.icon_arrow_left),
                        contentDescription = "icon_more",
                        modifier = Modifier.size(25.dp).clickable {

                        }
                    )

                }
            Text(
                text = "PPHelper",
                modifier = Modifier.weight(1f, true),
                fontSize = TextUnit(20f, TextUnitType.Sp),
                fontStyle = FontStyle.Normal,
                fontWeight = FontWeight.SemiBold
            )
            if (!PPBuff.isInHostApp()) {
                var status by remember {
                    mutableStateOf(true)
                }
                Surface(shape = CircleShape) {
                    val f: Float by animateFloatAsState(
                        targetValue = if (status) 0f else 90f,
                        animationSpec = tween(
                            durationMillis = 300
                        )
                    )
                    Image(
                        painter = painterResource(R.drawable.ic_more),
                        contentDescription = "icon_more",
                        modifier = Modifier.size(25.dp).clickable {
                            status = !status
                            // 弹出dialog
                        }.graphicsLayer {
                            transformOrigin = TransformOrigin.Center
                            rotationZ = f
                        }
                    )
                }
            }
        }
    }
}