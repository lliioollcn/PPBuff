package cn.lliiooll.ppbuff.activity

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.utils.toDp
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit

abstract class PActivity : ComponentActivity() {

    init {
        if (PPBuff.isInHostApp())
            EzXHelperInit.addModuleAssetPath(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.window.statusBarColor = Color(0, 0, 0, 0).toArgb()
        this.window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

    }


    /**
     * 沉浸式状态栏
     */
    @Composable
    fun StatusBar(ctx: Context) {
        Surface(
            modifier = Modifier.height(Dp(PPBuff.getStatusBarHeight(ctx).toDp(ctx))).fillMaxWidth(),
            color = MaterialTheme.colorScheme.background
        ) {

        }
    }


}
