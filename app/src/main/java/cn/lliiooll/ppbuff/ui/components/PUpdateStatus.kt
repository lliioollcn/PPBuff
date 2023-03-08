package cn.lliiooll.ppbuff.ui.components

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import cn.hutool.core.date.DateUtil
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.data.bean.PUpdateDetails
import cn.lliiooll.ppbuff.utils.UpdateUtils
import cn.lliiooll.ppbuff.utils.openUrl
import java.util.Date
import kotlin.concurrent.thread


@Preview(showBackground = true)
@Composable
fun PUpdateStatus() {
    Surface(
        color = if (isSystemInDarkTheme()) Color.DarkGray else Color.White,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp, 10.dp, 10.dp, 10.dp)

        ) {
            val activity = LocalView.current.context as Activity
            Surface(
                color = if (isSystemInDarkTheme()) Color.DarkGray else Color.White,
                modifier = Modifier.padding(0.dp, 20.dp, 0.dp, 0.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 0.dp, 10.dp, 0.dp),
                    color = MaterialTheme.colorScheme.background,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.background),
                    shape = MaterialTheme.shapes.medium
                ) {


                    Surface(
                        color = Color.Unspecified,
                        modifier = Modifier.padding(10.dp, 0.dp, 0.dp, 0.dp)
                    ) {
                        Column {
                            Text(
                                text = "模块更新",
                                textAlign = TextAlign.Center,
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 5.dp)
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(1.dp)
                                    .fillMaxWidth()
                                    .padding(1.dp, 0.dp, 1.dp, 0.dp)
                                    .background(Color.Black)
                            )
                            var title by remember {
                                mutableStateOf(
                                    if (PPBuff.isDebug()) {
                                        "调试模式"
                                    } else {
                                        "检查更新中..."
                                    }
                                )
                            }
                            var update by remember {
                                mutableStateOf(false)
                            }
                            var detailData: PUpdateDetails? = null
                            Text(
                                text = title,
                                textAlign = TextAlign.Left,
                                fontSize = TextUnit(13f, TextUnitType.Sp),
                            )
                            if (update) {
                                Text(
                                    text = if (detailData != null) "发现新版本: ${detailData.msg}" else "发现新版本",
                                    textAlign = TextAlign.Left,
                                    fontSize = TextUnit(13f, TextUnitType.Sp),
                                )
                                Text(
                                    text = if (detailData != null) detailData.msg else "更新一些你可能知道的，也可能不知道的",
                                    textAlign = TextAlign.Left,
                                    fontSize = TextUnit(13f, TextUnitType.Sp),
                                )
                                Text(
                                    text = "更新日期: ${
                                        if (detailData != null) DateUtil.format(
                                            Date(
                                                detailData.time
                                            ), "yyyy年MM月dd日HH:mm:ss"
                                        ) else "unknown"
                                    }",
                                    textAlign = TextAlign.Left,
                                    fontSize = TextUnit(13f, TextUnitType.Sp),
                                )
                                Text(
                                    text = "下载链接（AppCenter）",
                                    textAlign = TextAlign.Left,
                                    fontSize = TextUnit(13f, TextUnitType.Sp),
                                    modifier = Modifier.clickable {
                                        if (detailData != null) {
                                            detailData?.downloadUrlAppCenter!!.openUrl(activity)
                                        } else {
                                            Toast.makeText(
                                                activity,
                                                "链接走丢了",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                                Text(
                                    text = "下载链接（Github）",
                                    textAlign = TextAlign.Left,
                                    fontSize = TextUnit(13f, TextUnitType.Sp),
                                    modifier = Modifier.clickable {
                                        if (detailData != null) {
                                            detailData?.downloadUrlGithub!!.openUrl(activity)
                                        } else {
                                            Toast.makeText(
                                                activity,
                                                "链接走丢了",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                            }
                            thread {
                                if (UpdateUtils.hasUpdateAppCenter() && UpdateUtils.hasUpdateGithub()) {
                                    val details = UpdateUtils.getUpdateDetails()
                                    if (details == null) {
                                        title = "网络连接失败"
                                    } else {
                                        detailData = details
                                        update = true
                                        title = "检查完毕"
                                    }
                                }else{
                                   if (PPBuff.isDebug()){
                                       title = "调试模式"
                                   }else{
                                       title = "暂无更新"
                                   }
                                }
                            }
                        }
                    }


                }
            }

        }
    }

}