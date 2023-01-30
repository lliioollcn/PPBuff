package cn.lliiooll.ppbuff.activity.base

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.utils.toDp
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit

abstract class PActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PPBuff.isInHostApp())
            EzXHelperInit.addModuleAssetPath(this)
    }


    /**
     * 沉浸式状态栏
     */
    @Composable
    fun StatusBar() {
        val ctx = LocalView.current.context as Activity

        Surface(
            modifier = Modifier.height(Dp(PPBuff.getStatusBarHeight(ctx).toDp(ctx))).fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {

        }
    }


}

fun hideIcon(ctx: Context) {
    val pm = ctx.packageManager
    PConfig.setHideConfig()
    pm.setComponentEnabledSetting(
        ComponentName(ctx, "cn.lliiooll.ppbuff.activity.AliasMainActivity"),
        if (PConfig.isHideConfig()) {
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
        } else {
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        },
        PackageManager.DONT_KILL_APP
    )
}


