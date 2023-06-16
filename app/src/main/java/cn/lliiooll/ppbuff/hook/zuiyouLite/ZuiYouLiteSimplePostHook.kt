package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.app.Activity
import android.view.View
import android.view.ViewGroup
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
import cn.lliiooll.ppbuff.data.hidePost
import cn.lliiooll.ppbuff.data.isHidePost
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PViewType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.hook.isValid
import cn.lliiooll.ppbuff.utils.PJavaUtils
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.toastShort
import com.github.kyuubiran.ezxhelper.utils.findAllMethods
import com.github.kyuubiran.ezxhelper.utils.findField
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
            if (it.startsWith("cn.xiaochuankeji")
                && !it.contains("SlideDetailAdapter")
                && !it.startsWith("cn.xiaochuankeji.zuiyouLite.widget")
                && !it.startsWith("cn.xiaochuankeji.zuiyouLite.ui.message")
                && !it.startsWith("cn.xiaochuankeji.zuiyouLite.ui.user")
                && !it.startsWith("cn.xiaochuankeji.zuiyouLite.ui.me")
                && !it.startsWith("cn.xiaochuankeji.zuiyouLite.ui.follow")
                && !it.startsWith("cn.xiaochuankeji.interaction.sdk")
                && it != "cn.xiaochuankeji.zuiyouLite.ui.slide.ReviewsFilterAdapter"
            ) {
                val clazz = it.findClass()
                for (m in clazz.declaredMethods) {
                    if (m.name == "onCreateViewHolder" && !java.lang.reflect.Modifier.isAbstract(m.modifiers) && m.parameterTypes.size == 2 && m.parameterTypes[1] == Int::class.java) {
                        "Hook 在 $it 的方法".debug()
                        m.hookBefore {
                            if ((it.args[1] as Int).isHidePost()) {
                                "被屏蔽的帖子: ${it.args[1]}".debug()
                                it.args[1] = 111222333
                            } else {
                                if (it.args[1] == 111222333) {
                                    "异常的帖子: ${it.args[1]}".debug()
                                } else {
                                    "未被屏蔽的帖子: ${it.args[1]}".debug()
                                }

                            }
                        }
                        m.hookAfter {
                            "当前Adapter: ${it.thisObject.javaClass.name}".debug()
                            "当前Holder: ${it.result.javaClass.name}".debug()
                            if (it.args[1] == 111222333) {
                                it.result.hideHolder()
                            }
                        }
                    }
                }
            }
        }

        "cn.xiaochuankeji.zuiyouLite.ui.postlist.holder.PostViewHolderSingleVideo".findClass()
            .findAllMethods {
                paramCount > 0 && parameterTypes[0].name.contains("PostDataBean")
            }.hookAfter {
                val postData = it.args[0]
                if (0x3c.isHidePost()) {
                    val activity = XposedHelpers.getObjectField(postData, "activityBean")
                    if (activity != null) {
                        "存在游戏宣传的帖子".debug()
                        it.thisObject.hideHolder()
                    }
                }
                if (0x3d.isHidePost()) {
                    val member = XposedHelpers.getObjectField(postData, "member")
                    if (member != null) {
                        val liveOn = XposedHelpers.getIntField(member, "liveOn")
                        if (liveOn == 1) {
                            it.thisObject.hideHolder()
                        }
                        /*
                        val liveRank = XposedHelpers.getIntField(member, "liveRank")
                        val nickName = XposedHelpers.getObjectField(member, "nickName")
                        "用户 $nickName 是否正在直播: $liveOn".debug()
                        "用户 $nickName 是否直播: $liveRank".debug()
                         */
                    }
                }
                if (PConfig.boolean("auto_task_share_post", false)) {
                    var count = PConfig.number("auto_task_share_post_count")
                    val time = PConfig.numberEx("auto_task_share_post_time")
                    if (PJavaUtils.isPassDay(time)) {
                        val format = PJavaUtils.commentDetailTime("yyyy年MM月dd日HH:mm:ss", time)
                        "尝试进行自动分享帖子任务，上次执行时间: $format".debug()
                        count = 0
                        PConfig.set("auto_task_share_post_count", 0)
                        PConfig.set("auto_task_share_post_time", System.currentTimeMillis())
                    }
                    if (count < 2) {
                        val clazz =
                            "cn.xiaochuankeji.zuiyouLite.ui.postlist.holder.PostOperator".findClass()
                        val m = clazz
                            .findMethod {
                                paramCount == 4
                                        && parameterTypes[0] == Activity::class.java
                                        && parameterTypes[1] == Long::class.java
                                        && parameterTypes[2] == Int::class.java
                                        && parameterTypes[3] == Object::class.java
                            }
                        val f = it.thisObject.javaClass.findField(true) {
                            type == Activity::class.java
                        }
                        val pid = XposedHelpers.getLongField(postData, "postId")
                        val activity = XposedHelpers.getObjectField(it.thisObject, f.name)
                        XposedHelpers.callStaticMethod(
                            clazz,
                            m.name,
                            activity,
                            pid,
                            18,
                            it.thisObject
                        )
                        count++
                        PConfig.set("auto_task_share_post_count", count)
                        "已经完成分享帖子 $count/2".toastShort()
                    }
                }
            }

        return true
    }

    override fun isEnable(): Boolean {
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
            put(DEOBFKEY_ALL_ADAPTER, arrayListOf<DexClassDescriptor>().apply {
                addAll(hashSetOf<DexClassDescriptor>().apply {
                    addAll(dexkit?.findSubClasses("androidx.recyclerview.widget.RecyclerView\$Adapter")!!)
                    addAll(dexkit?.findSubClasses("cn.xiaochuankeji.zuiyouLite.ui.postlist.BasePostListAdapter")!!)
                    addAll(dexkit?.findSubClasses("cn.xiaochuankeji.zuiyouLite.widget.life.LifeCompatAdapter")!!)
                })
            })
        }
    }

    @Composable
    override fun compose(navController: NavHostController) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()

        ) {
            Surface(modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 0.dp)) {
                LazyColumn {
                    ZyLiteTypes.postList.forEach { (t, u) ->
                        item {
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
        this, "itemView"
    ) as View?
    if (view != null) {
        val params = view.layoutParams ?: ViewGroup.LayoutParams(0, 0)
        params.width = 0
        params.height = 0
        view.layoutParams = params
        view.setPadding(0, 0, 0, 0)
        view.visibility = View.GONE
        view.setOnClickListener(null)
    }

}