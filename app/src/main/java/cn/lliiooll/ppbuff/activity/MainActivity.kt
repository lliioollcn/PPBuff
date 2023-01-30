package cn.lliiooll.ppbuff.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import cn.lliiooll.ppbuff.activity.base.PActivity
import cn.lliiooll.ppbuff.activity.base.theme.PPBuffTheme
import cn.lliiooll.ppbuff.ui.components.PHookStatus
import cn.lliiooll.ppbuff.ui.components.PMainContent
import cn.lliiooll.ppbuff.ui.components.PTitleBar

class MainActivity : PActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PPBuffTheme {
                Column {
                    StatusBar()// 沉浸式状态栏
                    PTitleBar()// 标题栏
                    PHookStatus()// 模块激活状态
                    PMainContent()// 主要内容
                }

            }
        }

    }
}