package cn.lliiooll.ppbuff.ui.components

import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
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
import cn.hutool.json.JSONUtil
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.data.bean.PUpdateDetails
import cn.lliiooll.ppbuff.utils.UpdateUtils
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.openUrl
import java.util.Date
import kotlin.concurrent.thread


@RequiresApi(Build.VERSION_CODES.O)
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
                                text = "????????????",
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
                                        "????????????"
                                    } else {
                                        "???????????????..."
                                    }
                                )
                            }
                            var update by remember {
                                mutableStateOf(false)
                            }
                            var detailData by remember {
                                mutableStateOf(PUpdateDetails())
                            }
                            Text(
                                text = title,
                                textAlign = TextAlign.Left,
                                fontSize = TextUnit(13f, TextUnitType.Sp),
                            )
                            if (update) {
                                Text(
                                    text = if (detailData.version != null) "???????????????: ${detailData.version}" else "???????????????",
                                    textAlign = TextAlign.Left,
                                    fontSize = TextUnit(13f, TextUnitType.Sp),
                                )
                                Text(
                                    text = if (detailData.msg != null) detailData.msg else "??????????????????????????????????????????????????????",
                                    textAlign = TextAlign.Left,
                                    fontSize = TextUnit(13f, TextUnitType.Sp),
                                )
                                Text(
                                    text = "????????????: ${
                                        if (detailData.time != null) DateUtil.format(
                                            Date(
                                                detailData.time
                                            ), "yyyy???MM???dd???HH:mm:ss"
                                        ) else "unknown"
                                    }",
                                    textAlign = TextAlign.Left,
                                    fontSize = TextUnit(13f, TextUnitType.Sp),
                                )
                                Text(
                                    text = "???????????????AppCenter???",
                                    textAlign = TextAlign.Left,
                                    fontSize = TextUnit(13f, TextUnitType.Sp),
                                    modifier = Modifier.clickable {
                                        if (detailData.downloadUrlAppCenter != null) {
                                            detailData?.downloadUrlAppCenter!!.openUrl(activity)
                                        } else {
                                            Toast.makeText(
                                                activity,
                                                "???????????????",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                                Text(
                                    text = "???????????????Github???",
                                    textAlign = TextAlign.Left,
                                    fontSize = TextUnit(13f, TextUnitType.Sp),
                                    modifier = Modifier.clickable {
                                        if (detailData.downloadUrlGithub != null) {
                                            detailData?.downloadUrlGithub!!.openUrl(activity)
                                        } else {
                                            Toast.makeText(
                                                activity,
                                                "???????????????",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                            }
                            if (!PPBuff.checked) {
                                PPBuff.checked = true
                                thread {
                                    if (UpdateUtils.hasUpdate()) {
                                        val details = UpdateUtils.getUpdateDetails()
                                        if (details == null) {
                                            "????????????".debug()
                                            title = "??????????????????"
                                        } else {
                                            "??????: ${JSONUtil.toJsonPrettyStr(details)}".debug()
                                            detailData = details
                                            update = true
                                            title = "????????????"
                                        }
                                        "????????????".debug()
                                    } else {
                                        "???????????????".debug()
                                        if (PPBuff.isDebug()) {
                                            title = "????????????"
                                        } else {
                                            title = "????????????"
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

}