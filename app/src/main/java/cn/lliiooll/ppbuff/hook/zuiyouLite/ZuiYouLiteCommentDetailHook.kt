package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.TextView
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.data.ZyLiteApi
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PViewType
import cn.lliiooll.ppbuff.utils.PJavaUtils
import cn.lliiooll.ppbuff.utils.async
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.sync
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.paramCount
import de.robv.android.xposed.XposedHelpers

object ZuiYouLiteCommentDetailHook : BaseHook(
    "评论区详细", "comment_detail", PHookType.HIDE
) {


    override fun init(): Boolean {
        if (ZuiYouLiteDetailLocationHook.isEnable() || ZuiYouLiteDetailCommentTimeHook.isEnable()) {
            "cn.xiaochuankeji.zuiyouLite.ui.slide.ab.holder.ReviewBasicViewControl"
                .findClass()
                .findMethod {
                    this.paramCount == 2
                            && this.parameterTypes[0].name.contains("CommentBean")
                            && java.lang.reflect.Modifier.isFinal(modifiers)
                            && !java.lang.reflect.Modifier.isStatic(modifiers)
                            && returnType == Void.TYPE
                }
                .hookAfter {
                    val commentData = it.args[0]
                    val mid = XposedHelpers.getLongField(commentData, "mid")
                    val postId = XposedHelpers.getLongField(commentData, "postId")
                    val createTime = XposedHelpers.getLongField(commentData, "createTime")
                    val nameMultiView = XposedHelpers.getObjectField(it.thisObject, "nameMultiView")
                    var ipAtribution = XposedHelpers.getObjectField(commentData, "ipAtribution")
                    val time =
                        if (ZuiYouLiteDetailCommentTimeHook.isEnable()) PJavaUtils.commentDetailTime(
                            PConfig.string(
                                "config_time_format",
                                "yyyy年MM月dd日HH:mm:ss"
                            ),
                            createTime
                        ) else PJavaUtils.commentTime(createTime)
                    sync {

                        async {
                            if (ZuiYouLiteDetailLocationHook.isEnable()) {
                                "请求数据: pid@$postId, mid@$mid".debug()
                                val data = ZyLiteApi.shareDetail(postId, mid)
                                if (data.data.user.position != null) {
                                    val pos = data.data.user.position
                                    val province =
                                        if (pos.province != null && pos.province.isNotEmpty()) "·${pos.province}" else ""
                                    val country =
                                        if (pos.country != null && pos.country.isNotEmpty()) pos.country else ""
                                    val city =
                                        if (pos.city != null && pos.city.isNotEmpty()) "·${pos.city}" else ""
                                    var i = 0
                                    ipAtribution = "$time·$country$province$city"
                                }
                            }
                            var i = 0
                            for (f in nameMultiView.javaClass.declaredFields) {
                                if (f.type == TextView::class.java) {
                                    if (i != 0) {
                                        val textView = XposedHelpers.getObjectField(
                                            nameMultiView,
                                            f.name
                                        ) as TextView
                                        sync {
                                            val lp = textView.layoutParams
                                            lp.width = ViewGroup.LayoutParams.MATCH_PARENT
                                            textView.layoutParams = lp
                                            textView.maxLines = 10
                                            textView.isSingleLine = false
                                            textView.onWindowFocusChanged(true)
                                            textView.text = "$time · $ipAtribution"
                                        }
                                        break
                                    } else {
                                        i++
                                    }
                                }
                            }
                        }

                    }
                }
        }

        return true
    }

    override fun isEnable(): Boolean {
        return true
    }

    override fun view(): PViewType {
        return PViewType.NONE
    }

}