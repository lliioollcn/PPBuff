package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import cn.lliiooll.ppbuff.data.ZyLiteTypes
import cn.lliiooll.ppbuff.data.hideMine
import cn.lliiooll.ppbuff.data.isHideMine
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PViewType
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.findId
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import de.robv.android.xposed.XposedHelpers

object ZuiYouLiteSimpleMeHook : BaseHook(
    "精简\"我的界面\"", "simpleMe", PHookType.SIMPLE

) {
    override fun init(): Boolean {

        "cn.xiaochuankeji.zuiyouLite.ui.me.FragmentMyTab"
            .findClass()
            .findMethod {
                name == "onCreateView"
            }
            .hookAfter {
                val ins = it.thisObject
                val root = it.result as ViewGroup
                ZyLiteTypes.mineList.forEach { (t, u) ->

                    if (u.isHideMine()) {
                        var viewGroup: ViewGroup? = null
                        if (u.startsWith("!")) {
                            if (!"myTabDataLayout".isHideMine()) {
                                val vg =
                                    XposedHelpers.getObjectField(
                                        ins,
                                        "myTabDataLayout"
                                    ) as ViewGroup
                                viewGroup = vg.findViewById(u.replace("!", "").findId())
                            }
                        } else if (u.contains("_")) {
                            viewGroup = root.findViewById(u.findId())
                        } else {
                            viewGroup = XposedHelpers.getObjectField(ins, u) as ViewGroup
                        }

                        if (viewGroup != null) {

                            val view = viewGroup as View
                            val lp = view.layoutParams ?: ViewGroup.LayoutParams(0, 0)
                            lp.width = 0
                            lp.height = 0
                            view.layoutParams = lp
                            view.layout(0,0,0,0)
                            view.measure(0,0)
                            viewGroup.clearAnimation()
                            viewGroup.removeAllViews()
                            viewGroup.setOnClickListener(null)
                            viewGroup.visibility = View.GONE
                            view.setPadding(0, 0, 0, 0)
                            view.visibility = View.GONE
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