package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import cn.lliiooll.ppbuff.data.hidePost
import cn.lliiooll.ppbuff.data.isHidePost
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PViewType
import cn.lliiooll.ppbuff.hook.isValid
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.paramCount
import de.robv.android.xposed.XposedHelpers
import io.luckypray.dexkit.DexKitBridge
import io.luckypray.dexkit.descriptor.member.DexClassDescriptor

object ZuiYouLiteSimplePostHook : BaseHook(
    "精简帖子列表", "simplePost", PHookType.SIMPLE

) {

    val DEOBFKEY_ALL_ADAPTER = "cn.xiaochuankeji.zuiyouLite.ui.adapter.Adapters"


    override fun init(): Boolean {

        PConfig.getCache(DEOBFKEY_ALL_ADAPTER).forEach {
            if (it.startsWith("cn.xiaochuankeji")) {
                val clazz = it.findClass()
                for (m in clazz.declaredMethods) {
                    if (m.name == "onCreateViewHolder"
                        && !java.lang.reflect.Modifier.isAbstract(m.modifiers)
                        && m.paramCount == 2
                        && m.parameterTypes[1] == Int::class.java
                    ) {
                        "Hook 在 $it 的方法".debug()
                        m.hookBefore {
                            if ((it.args[1] as Int).isHidePost()) {
                                "被屏蔽的帖子: ${it.args[1]}".debug()
                                it.args[1] = 666999
                            } else {
                                "未被屏蔽的帖子: ${it.args[1]}".debug()
                            }
                        }
                        m.hookAfter {
                            "当前Adapter: ${it.thisObject.javaClass.name}".debug()
                            "当前Holder: ${it.result.javaClass.name}".debug()
                            if (it.args[1] == 666999) {
                                it.result.hideHolder()
                            }
                        }
                    }
                }
            }
        }

        "cn.xiaochuankeji.zuiyouLite.ui.postlist.holder.PostViewHolderSingleVideo"
            .findClass()
            .findMethod {
                paramCount > 0 && parameterTypes[0].name.contains("PostDataBean")
            }
            .hookAfter {
                if (0x3c.isHidePost()) {
                    val postData = it.args[0]
                    val activity = XposedHelpers.getObjectField(postData, "activityBean")
                    if (activity != null) {
                        it.thisObject.hideHolder()
                    }
                }
            }

        return true
    }

    override fun needDeobf(): Boolean {
        return !PConfig.hasCache(DEOBFKEY_ALL_ADAPTER) || !PConfig.getCache(
            DEOBFKEY_ALL_ADAPTER
        ).isValid()
    }

    override fun needCustomDeobf(): Boolean {
        return true
    }

    override fun customDebof(dexkit: DexKitBridge?): Map<String, List<DexClassDescriptor>> {

        return hashMapOf<String, List<DexClassDescriptor>>().apply {
            put(
                DEOBFKEY_ALL_ADAPTER,
                arrayListOf<DexClassDescriptor>().apply {
                    addAll(hashSetOf<DexClassDescriptor>().apply {
                        addAll(dexkit?.findSubClasses("androidx.recyclerview.widget.RecyclerView\$Adapter")!!)
                        addAll(dexkit?.findSubClasses("cn.xiaochuankeji.zuiyouLite.ui.postlist.BasePostListAdapter")!!)
                        addAll(dexkit?.findSubClasses("cn.xiaochuankeji.zuiyouLite.widget.life.LifeCompatAdapter")!!)
                    })
                }
            )
        }
    }

    @Composable
    override fun compose(navController: NavHostController) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(10.dp, 5.dp, 10.dp, 0.dp)
        ) {
            Column {
                ZyLiteTypes.postList.forEach { (t, u) ->
                    Row(modifier = Modifier.clickable {
                        u.hidePost()
                    }) {
                        var hide by remember {
                            mutableStateOf(u.isHidePost())
                        }
                        Text(text = t, modifier = Modifier.weight(1f, true))
                        Checkbox(checked = hide, onCheckedChange = {
                            u.hidePost()
                            hide = u.isHidePost()
                        })
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

fun Any.hideHolder() {
    val view = XposedHelpers.getObjectField(
        this,
        "itemView"
    ) as View?
    if (view != null) {
        val params: ViewGroup.LayoutParams = view.layoutParams
        params.width = 0
        params.height = 0
        view.layoutParams = params
        view.setPadding(0, 0, 0, 0)
        view.visibility = View.GONE
        view.setOnClickListener(null)
    }

}