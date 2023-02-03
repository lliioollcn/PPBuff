package cn.lliiooll.ppbuff.hook.zuiyouLite

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.data.ZyLiteTypes
import cn.lliiooll.ppbuff.data.hideMine
import cn.lliiooll.ppbuff.data.isHideMine
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PViewType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.hook.isValid
import cn.lliiooll.ppbuff.utils.catch
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.dump
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.toastShort
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.hookReplace
import com.github.kyuubiran.ezxhelper.utils.paramCount

object ZuiYouLiteAutoTaskHook : BaseHook(
    "自动签到", "auto_sign", PHookType.PLAY
) {

    val DEOBF_API_SHARE_POST = "cn.xiaochuankeji.zuiyouLite.api.SharePost"


    override fun init(): Boolean {
        PConfig.getCache(DEOBF_API_SHARE_POST).forEach {
            "@自动分享类: $it".debug()
            it
                .findClass()
                .findMethod {
                    paramCount == 4
                            && parameterTypes[0] == Int::class.java
                            && parameterTypes[1] == Int::class.java
                }
                .hookAfter {
                    it.dump()
                    RuntimeException().catch()
                }
        }

        "cn.xiaochuankeji.zuiyouLite.ui.postlist.holder.PostOperator"
            .findClass()
            .findAllMethods {
                returnType == "cn.xiaochuankeji.zuiyouLite.ui.postlist.holder.PostOperator".findClass()
            }
            .hookReplace {
                it.dump()
            }
        return ZuiYouLiteWebTokenHook.init()
    }

    @Composable
    override fun compose(navController: NavHostController) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp, 5.dp, 10.dp, 0.dp)
        ) {

            LazyColumn {
                ZyLiteTypes.taskList.forEach { (t, u) ->
                    item {
                        var hookEnable by remember {
                            mutableStateOf(PConfig.boolean(u, false))
                        }
                        var lastSwitch by remember {
                            mutableStateOf(0L)
                        }
                        var quickSwitch by remember {
                            mutableStateOf(0)
                        }
                        Row {
                            Text(
                                text = t,
                                fontSize = TextUnit(15f, TextUnitType.Sp),
                                modifier = Modifier
                                    .weight(1f, true)
                                    .align(Alignment.CenterVertically)
                            )
                            Switch(checked = hookEnable, onCheckedChange = {
                                PConfig.set(u, it)
                                hookEnable = it
                                if (System.currentTimeMillis() - lastSwitch > 5000L) {
                                    "重启应用生效".toastShort()
                                    quickSwitch = 0
                                } else {
                                    quickSwitch++
                                }
                                if (quickSwitch > 100) {
                                    "点点点，都nm快点坏了还点".toastShort()
                                }
                                lastSwitch = System.currentTimeMillis()
                            })
                        }
                    }
                }

            }
        }
    }

    override fun isEnable(): Boolean {
        return true
    }

    override fun deobfMap(): Map<String, List<String>> {
        return hashMapOf<String, List<String>>().apply {
            put(DEOBF_API_SHARE_POST, arrayListOf<String>().apply {
                add(":socialType = [")
            })
        }
    }

    override fun needDeobf(): Boolean {
        return !PConfig.hasCache(DEOBF_API_SHARE_POST) || !PConfig.getCache(
            DEOBF_API_SHARE_POST
        )?.isValid()!!
    }

    override fun router(): Boolean {
        return true
    }

    override fun view(): PViewType {
        return PViewType.CUSTOM
    }
}