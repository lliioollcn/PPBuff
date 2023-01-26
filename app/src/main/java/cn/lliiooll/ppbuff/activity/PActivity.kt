package cn.lliiooll.ppbuff.activity

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.utils.toDp
import cn.lliiooll.ppbuff.utils.toastShort
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit

abstract class PActivity : ComponentActivity() {

    init {
        if (PPBuff.isInHostApp())
            EzXHelperInit.addModuleAssetPath(this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


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
