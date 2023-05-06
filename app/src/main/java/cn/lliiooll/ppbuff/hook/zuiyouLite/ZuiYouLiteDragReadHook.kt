package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.hook.BaseHook
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookBefore
import com.github.kyuubiran.ezxhelper.utils.paramCount
import de.robv.android.xposed.XposedHelpers
import kotlin.math.abs

object ZuiYouLiteDragReadHook : BaseHook(
    "拖动清除未读消息", "msg_drag_read", PHookType.COMMON
) {


    var viewStartX = 0.0f
    var viewStartY = 0.0f
    val location = intArrayOf(0, 0)
    override fun init(): Boolean {
        View::class.java
            .findMethod {
                this.name == "onTouchEvent" && this.paramCount == 1 && this.parameterTypes[0] == MotionEvent::class.java
            }
            .hookBefore {
                if (it.thisObject?.javaClass?.name == "cn.xiaochuankeji.zuiyouLite.widget.BadgeTextView") {
                    it.result = true
                    val view = it.thisObject as View
                    var parent = view.parent
                    while (true) {
                        if (parent is ViewGroup) {
                            parent.clipChildren = false
                        }
                        parent = parent.parent
                        if (parent == null) {
                            break
                        }
                    }
                    if (location[0] == 0 || location[1] == 0) {
                        view.getLocationOnScreen(location)
                    }

                    val event = it.args[0] as MotionEvent
                    val viewLastX = event.rawX - location[0]
                    val viewLastY = event.rawY - location[1]
                    val dx = viewLastX - viewStartX
                    val dy = viewLastY - viewStartY
                    if (event.action == MotionEvent.ACTION_UP) {
                        if (abs(viewLastX) > 0 && abs(viewLastX) > 0) {
                            XposedHelpers.callMethod(view, "setBadgeCount", 0)
                        }
                        val animX = ObjectAnimator.ofFloat(
                            view, "translationX", viewStartX,
                            0f
                        )
                        val animY = ObjectAnimator.ofFloat(
                            view, "translationY", viewStartY,
                            0f
                        )
                        animX.duration = 50
                        animY.duration = 50
                        val set = AnimatorSet()
                        set.play(animX).with(animY)
                        set.start()
                        viewStartX = 0f
                        viewStartY = 0f
                        location[0] = 0
                        location[1] = 0
                    }
                    if (event.action == MotionEvent.ACTION_MOVE
                    ) {

                        val animX = ObjectAnimator.ofFloat(
                            view, "translationX", viewStartX,
                            dx
                        )
                        val animY = ObjectAnimator.ofFloat(
                            view, "translationY", viewStartY,
                            dy
                        )
                        animX.duration = 50
                        animY.duration = 50
                        val set = AnimatorSet()
                        set.play(animX).with(animY)
                        set.start()
                        viewStartX = viewLastX
                        viewStartY = viewLastY
                    }
                }
            }

        /*
        "com.izuiyou.network.NetCrypto"
            .findClass()
            .findMethod { true }
            .hookAfter {
                it.dump()
                //RuntimeException().catch()
            }
        //PPBuff.getApplication().packageManager.checkSignatures()

         */
        return true
    }
}