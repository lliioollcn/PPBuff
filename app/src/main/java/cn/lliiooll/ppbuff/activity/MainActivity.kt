package cn.lliiooll.ppbuff.activity

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.Modifier
import cn.lliiooll.ppbuff.activity.base.PActivity
import cn.lliiooll.ppbuff.activity.base.theme.PPBuffTheme
import cn.lliiooll.ppbuff.ui.components.PHookStatus
import cn.lliiooll.ppbuff.ui.components.PMainContent
import cn.lliiooll.ppbuff.ui.components.PTitleBar
import cn.lliiooll.ppbuff.ui.components.PUpdateStatus

class MainActivity : PActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PPBuffTheme {
                LazyColumn(modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth()) {
                    item {
                        StatusBar()// 沉浸式状态栏

                    }
                    item {
                        PTitleBar()// 标题栏
                    }
                    item {
                        PHookStatus()// 模块激活状态
                    }
                    item {
                        PMainContent()// 主要内容
                    }
                    item {
                        PUpdateStatus()// 更新状态
                    }
                }

            }
        }

    }
}