package cn.lliiooll.ppbuff.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import cn.lliiooll.ppbuff.activity.ui.theme.PPBuffTheme
import cn.lliiooll.ppbuff.ui.components.PMainContent
import cn.lliiooll.ppbuff.ui.components.PTitleBar

class MainActivity : PActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val ctx = this
        setContent {
            PPBuffTheme {
                Column {
                    StatusBar(ctx)// 沉浸式状态栏
                    PTitleBar()// 标题栏
                    PMainContent()// 主界面内容
                }

            }
        }

    }
}