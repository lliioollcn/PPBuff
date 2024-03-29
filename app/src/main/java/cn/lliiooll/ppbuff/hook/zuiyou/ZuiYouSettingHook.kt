package cn.lliiooll.ppbuff.hook.zuiyou

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.RequiresApi
import cn.lliiooll.ppbuff.BuildConfig
import cn.lliiooll.ppbuff.R
import cn.lliiooll.ppbuff.activity.ConfigActivity
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.findId
import cn.lliiooll.ppbuff.utils.jumpTo
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.paramCount

object ZuiYouSettingHook : BaseHook(
    "PPBuff设置", "setting", PHookType.DEBUG
) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun init(): Boolean {
        "cn.xiaochuankeji.tieba.ui.home.setting.SettingActivity"
            .findClass()
            .findMethod {
                this.name == "onCreate" && this.paramCount == 1 && this.parameterTypes[0] == Bundle::class.java
            }
            .hookAfter {
                val activity = it.thisObject as Activity
                val root = activity.findViewById<RelativeLayout>("rootView".findId())
                val scroll = root.getChildAt(1) as ScrollView
                val content = scroll.getChildAt(0) as LinearLayout
                EzXHelperInit.addModuleAssetPath(activity)
                EzXHelperInit.addModuleAssetPath(activity)
                EzXHelperInit.addModuleAssetPath(activity)
                EzXHelperInit.addModuleAssetPath(activity)
                EzXHelperInit.addModuleAssetPath(activity)
                EzXHelperInit.addModuleAssetPath(activity)
                EzXHelperInit.addModuleAssetPath(activity)
                // 初始化界面
                val view = LayoutInflater.from(activity).inflate(R.layout.pp_setting, null, false)
               val version = view.findViewById<TextView>(R.id.pp_setting_version)
                  version.text = BuildConfig.VERSION_NAME

                content.addView(view, 0)
                val host = view.findViewById<LinearLayout>(R.id.pp_setting_root)
                host.setOnClickListener {
                    activity.jumpTo(ConfigActivity::class.java)
                }
            }
        return true
    }
}