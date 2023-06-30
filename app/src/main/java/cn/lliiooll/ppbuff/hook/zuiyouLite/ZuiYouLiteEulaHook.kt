package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.app.Activity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import cn.hutool.core.date.DateUtil
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.R
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PViewType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.requireMinVersion
import cn.lliiooll.ppbuff.utils.sync
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.paramCount
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.interfaces.OnBindView
import com.kongzue.dialogx.style.IOSStyle
import java.util.Date

object ZuiYouLiteEulaHook : BaseHook(
    "打赏作者", "support", PHookType.COMMON
) {
    override fun init(): Boolean {
        if (requireMinVersion(
                PPBuff.HostInfo.ZuiyouLite.PP_2_67_10
            )
        ) {
            "cn.xiaochuankeji.zuiyouLite.ui.main.MainTest"
        } else {
            "cn.xiaochuankeji.zuiyouLite.ui.main.MainActivity"
        }
            .findClass()
            .findMethod {
                this.name == "onCreate" && this.paramCount == 1 && this.parameterTypes[0] == Bundle::class.java
            }
            .hookAfter {
                val activity = it.thisObject as Activity
                if (DateUtil.betweenDay(
                        Date(),
                        Date(PConfig.numberEx("last_pop", 0L)),
                        true
                    ) >= 7 || PConfig.isUpdateHost()
                ) {
                    showPop(activity)

                    PConfig.set("last_pop", System.currentTimeMillis())

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

    fun showPop(activity: Activity) {
        sync {
            EzXHelperInit.addModuleAssetPath(activity)
            EzXHelperInit.addModuleAssetPath(activity)
            EzXHelperInit.addModuleAssetPath(activity)
            EzXHelperInit.addModuleAssetPath(activity)
            val image = ImageView(activity)
            image.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                800
            )
            image.foregroundGravity = Gravity.CENTER_HORIZONTAL
            EzXHelperInit.addModuleAssetPath(activity)
            EzXHelperInit.addModuleAssetPath(activity)
            EzXHelperInit.addModuleAssetPath(activity)
            EzXHelperInit.addModuleAssetPath(activity)
            image.setImageResource(R.drawable.wechat)
            MessageDialog
                .build()
                .setStyle(IOSStyle.style())
                .setTitle("捐赠")
                .setMaxHeight(1500)
                .setMinHeight(700)
                .setMessage("开发不易，可否打赏一二~")
                .setButtonOrientation(LinearLayout.VERTICAL)
                .setCancelable(true)
                .setCustomView(object : OnBindView<MessageDialog>(image) {
                    override fun onBind(dialog: MessageDialog?, v: View?) {

                    }
                })
                .setOkButton("微信") { dialog, view ->
                    EzXHelperInit.addModuleAssetPath(dialog.resources)
                    EzXHelperInit.addModuleAssetPath(activity)
                    EzXHelperInit.addModuleAssetPath(view.resources)
                    image.setImageResource(R.drawable.wechat)
                    dialog.setCustomView(object : OnBindView<MessageDialog>(image) {
                        override fun onBind(dialog: MessageDialog?, v: View?) {

                        }
                    })
                    true
                }.setCancelButton("支付宝") { dialog, view ->
                    EzXHelperInit.addModuleAssetPath(dialog.resources)
                    EzXHelperInit.addModuleAssetPath(activity)
                    EzXHelperInit.addModuleAssetPath(view.resources)
                    image.setImageResource(R.drawable.alipay)
                    dialog.setCustomView(object : OnBindView<MessageDialog>(image) {
                        override fun onBind(dialog: MessageDialog?, v: View?) {

                        }
                    })
                    true
                }.show(activity)
            "弹出提示".debug()

        }
    }

    override fun isEnable(): Boolean {
        return true
    }

    override fun needCustomClick(): Boolean {
        return true
    }

    override fun view(): PViewType {
        return PViewType.CUSTOM
    }

    override fun click() {
        showPop(super.ctx as Activity)
    }
}