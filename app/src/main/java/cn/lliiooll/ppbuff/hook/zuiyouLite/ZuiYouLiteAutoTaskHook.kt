package cn.lliiooll.ppbuff.hook.zuiyouLite

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PViewType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.hook.isValid
import cn.lliiooll.ppbuff.utils.PJavaUtils
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.sync
import cn.lliiooll.ppbuff.utils.toastShort
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.findField
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.paramCount
import de.robv.android.xposed.XposedHelpers

object ZuiYouLiteAutoTaskHook : BaseHook(
    "自动任务", "auto_sign", PHookType.PLAY
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
                    //it.dump()
                    // RuntimeException().catch()
                }
            val c = "cn.xiaochuankeji.zuiyouLite.ui.slide.ab.ReviewDetailLikeView"
                .findClass()

            c.findAllMethods {
                name == "T"
            }
                .hookAfter {
                    if (PConfig.boolean("auto_task_like_comment_god", false)) {
                        var count = PConfig.number("auto_task_like_comment_god_count")
                        val time = PConfig.numberEx("auto_task_like_comment_god_time")
                        if (PJavaUtils.isPassDay(time)) {
                            val format =
                                PJavaUtils.commentDetailTime("yyyy年MM月dd日HH:mm:ss", time)
                            "尝试进行自动点赞神评任务，上次执行时间: $format".debug()
                            count = 0
                            PConfig.set("auto_task_like_comment_god_count", 0)
                            PConfig.set(
                                "auto_task_like_comment_god_time",
                                System.currentTimeMillis()
                            )
                        }
                        //if (count < 6){
                        //h0
                        val f = c.findField {
                            type.name.contains("CommentBean")
                        }
                        val commentBean = XposedHelpers.getObjectField(it.thisObject, f.name)
                        sync {
                            if (commentBean != null) {
                                val isGod = XposedHelpers.getIntField(commentBean, "isGod")
                                if (isGod == 1) {
                                    while (count < 6){
                                        XposedHelpers.callMethod(it.thisObject, "a0")
                                        count++
                                        PConfig.set("auto_task_like_comment_god_count", count)
                                        "已经完成点赞神评 $count/6".toastShort()
                                    }
                                } else {
                                    "不是神评，跳过".debug()
                                }
                            }
                        }

                        //}
                    }
                }
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