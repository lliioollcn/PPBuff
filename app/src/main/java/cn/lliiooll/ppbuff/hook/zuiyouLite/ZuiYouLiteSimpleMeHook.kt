package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.data.ZyLiteTypes
import cn.lliiooll.ppbuff.data.hideMine
import cn.lliiooll.ppbuff.data.isHideMine
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PViewType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.findId
import com.github.kyuubiran.ezxhelper.utils.findAllConstructors
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.findFieldObject
import com.github.kyuubiran.ezxhelper.utils.findFieldObjectOrNull
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.paramCount
import de.robv.android.xposed.XposedHelpers

object ZuiYouLiteSimpleMeHook : BaseHook(
    "精简\"我的界面\"", "simpleMe", PHookType.SIMPLE

) {
    override fun init(): Boolean {
        "cn.xiaochuankeji.zuiyouLite.ui.me.FragmentMyTab"
            .findClass()
            .findMethod {
                name == "onResume"
            }
            .hookBefore {
                ZyLiteTypes.extraMineList.forEach { (t, u) ->

                    if (t.isHideMine()) {
                        XposedHelpers.setObjectField(it.thisObject, u, null)
                    }
                }

            }
        "cn.xiaochuankeji.zuiyouLite.ui.me.MyTabHeaderView"
            .findClass()
            .findAllMethods {
                paramCount > 0 && parameterTypes[0].name.contains("MemberInfoBean")
            }
            .hookAfter {
                val infoBean = it.args[0]
                if (infoBean != null) {
                    val id = XposedHelpers.getLongField(infoBean, "id")
                    val pyid = XposedHelpers.getObjectField(infoBean, "pyID") as String
                    "当前id: $id ; 当前皮友号: $pyid".debug()
                    if (PConfig.numberEx("account_id_now", 0L) != id) {
                        PConfig.set("account_id_now", id)
                        PConfig.set(ZuiYouLiteAutoFollowHook.label, false)
                        ZuiYouLiteAutoFollowHook.init()
                    }
                }
            }
        "cn.xiaochuankeji.zuiyouLite.ui.me.FragmentMyTab"
            .findClass()
            .findMethod {
                name == "onCreateView"
            }
            .hookAfter {
                val ins = it.thisObject
                val root = it.result as ViewGroup
                //val vg = it.args[1] as ViewGroup
                ZyLiteTypes.mineList.forEach { (t, u) ->
                    if (u.isHideMine()) {
                        var view: View? = null
                        if (u.startsWith("!")) {
                            if (!"myTabDataLayout".isHideMine()) {
                                val vg =
                                    XposedHelpers.getObjectField(
                                        ins,
                                        "myTabDataLayout"
                                    ) as LinearLayout
                                view = vg.findViewById(u.replace("!", "").findId())
                            }
                        } else if (u.contains("_")) {
                            view = root.findViewById(u.findId())
                        } else {
                            view = XposedHelpers.getObjectField(ins, u) as View
                            //XposedHelpers.setObjectField(ins, u, null)
                        }
                        if (view != null) {
                            if (view is ViewGroup) {
                                val viewGroup = view
                                viewGroup.clearAnimation()
                                viewGroup.removeAllViews()
                                viewGroup.setOnClickListener(null)
                                viewGroup.visibility = View.GONE
                                root.removeView(viewGroup)
                            }
                            view.clearAnimation()
                            view.clearFocus()
                            val lp = view.layoutParams ?: ViewGroup.LayoutParams(0, 0)
                            lp.width = 0
                            lp.height = 0
                            view.layoutParams = lp
                            view.layout(0, 0, 0, 0)
                            view.measure(0, 0)
                            view.setPadding(0, 0, 0, 0)
                            view.visibility = View.GONE
                            root.removeView(view)
                            "移除屏蔽的view: $u".debug()
                        } else {
                            "view: $u 为null".debug()
                        }
                    }

                }
            }



        return true
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
                ZyLiteTypes.mineList.forEach { (t, u) ->
                    item {
                        Row(modifier = Modifier.clickable {
                            u.hideMine()
                        }) {
                            var hide by remember {
                                mutableStateOf(u.isHideMine())
                            }
                            Text(text = t, modifier = Modifier.weight(1f, true))
                            Checkbox(checked = hide, onCheckedChange = {
                                u.hideMine()
                                hide = u.isHideMine()
                            })
                        }
                    }
                }
            }
        }
    }

    override fun router(): Boolean {
        return true
    }

    override fun view(): PViewType {
        return PViewType.CUSTOM
    }
}