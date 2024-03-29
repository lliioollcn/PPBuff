package cn.lliiooll.ppbuff.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.activity.base.PActivity
import cn.lliiooll.ppbuff.activity.base.theme.PPBuffTheme
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.data.types.PRecordType
import cn.lliiooll.ppbuff.hook.PHook
import cn.lliiooll.ppbuff.hook.zuiyouLite.ZuiYouLiteVoiceSendHook
import cn.lliiooll.ppbuff.ui.components.PTitleBar
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.toastShort
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ConfigActivity : PActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            delay(50)
            window.setBackgroundDrawableResource(android.R.color.transparent)
        }
        setContent {
            PPBuffTheme {
                Column {
                    val navController: NavHostController = rememberNavController()
                    StatusBar()// 沉浸式状态栏
                    PTitleBar()// 标题栏
                    NavHost(
                        navController = navController,
                        startDestination = "main",
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    ) {// 导航界面
                        composable("main") {// 主界面
                            ConfigMainComposable(navController)
                        }
                        PHook.thisLoader?.hooks()?.forEach {
                            if (it.router()) {
                                val hook = it
                                composable(it.label) {
                                    hook.compose(navController = navController)
                                }
                            }
                        }
                    }
                }

            }
        }

        requestVideoRecord =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data = result.data
                if (data != null) {
                    val uri = data.data
                    if (uri != null) {
                        val takeFlags =
                            (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        this.contentResolver.takePersistableUriPermission(
                            uri, takeFlags
                        )
                        PConfig.set("video_record_uri", uri.toString())
                        PConfig.set("video_record", PRecordType.SAF.getLabel())
                        "保存成功".toastShort(this)
                    } else {
                        "错误: 选择的路径不存在".toastShort(this)
                    }
                } else {
                    "错误: 数据为空".toastShort(this)
                }
            }
        readVoiceDir =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data = result.data
                if (data != null) {
                    val uri = data.data
                    if (uri != null) {
                        val takeFlags =
                            (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        this.contentResolver.takePersistableUriPermission(
                            uri, takeFlags
                        )
                        PConfig.set("voicePath", uri.toString())
                        "保存成功".toastShort(this)
                    } else {
                        "错误: 选择的路径不存在".toastShort(this)
                    }
                } else {
                    "错误: 数据为空".toastShort(this)
                }
            }
        saveAudioRecord =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data = result.data
                if (data != null) {
                    val uri = data.data
                    if (uri != null) {
                        val takeFlags =
                            (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        this.contentResolver.takePersistableUriPermission(
                            uri, takeFlags
                        )
                        PConfig.set("voiceSavePath", uri.toString())
                        "保存成功".toastShort(this)
                    } else {
                        "错误: 选择的路径不存在".toastShort(this)
                    }
                } else {
                    "错误: 数据为空".toastShort(this)
                }
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0x3c) {
            if (!Settings.canDrawOverlays(this)) {
                "请开启皮皮搞笑的悬浮窗权限来使用此功能".toastShort()
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                intent.data =
                    Uri.parse("package:" + PPBuff.getApplication().packageName)
                this.startActivityForResult(
                    intent,
                    0x3c
                )
            } else {
                "请选择一个读取路径".toastShort()
                if (ConfigActivity.readVoiceDir != null) {
                    ConfigActivity.readVoiceDir!!.launch(Intent(Intent.ACTION_OPEN_DOCUMENT_TREE))
                }
                "启动完毕".debug()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    companion object {
        var requestVideoRecord: ActivityResultLauncher<Intent>? = null
        var saveAudioRecord: ActivityResultLauncher<Intent>? = null
        var readVoiceDir: ActivityResultLauncher<Intent>? = null
    }
}

@Composable
fun ConfigMainComposable(navController: NavHostController) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(), color = if (isSystemInDarkTheme()) {
            Color.DarkGray
        } else {
            Color.White
        }
    ) {
        LazyColumn {
            PHookType.values().forEach {
                if (it != PHookType.HIDE) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp, 5.dp, 10.dp, 5.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(
                                modifier = Modifier.padding(15.dp, 5.dp, 15.dp, 5.dp),
                            ) {
                                Text(
                                    text = it.getLabel(),
                                    fontSize = TextUnit(17f, TextUnitType.Sp),
                                    color = if (isSystemInDarkTheme()) {
                                        Color(0xff1CB5E0)
                                    } else {
                                        Color(0xFF0000C8)
                                    }
                                )

                                Spacer(
                                    modifier = Modifier
                                        .padding(3.dp, 3.dp, 3.dp, 3.dp)
                                        .background(
                                            if (isSystemInDarkTheme()) {
                                                Color.LightGray
                                            } else {
                                                Color.DarkGray
                                            }
                                        )
                                        .height(1.dp)
                                )

                                when (it) {
                                    PHookType.COMMON -> {
                                        CommonHookList(navController)
                                    }

                                    PHookType.SIMPLE -> {
                                        SimpleHookList(navController)
                                    }

                                    PHookType.DEBUG -> {
                                        DebugHookList(navController)
                                    }

                                    PHookType.PLAY -> {
                                        PlayHookList(navController)
                                    }

                                    else -> {}
                                }
                            }
                        }
                    }
                }
            }
        }
    }


}
