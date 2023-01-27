package cn.lliiooll.ppbuff.app

import android.app.Application
import com.tencent.mmkv.MMKV

class PPBuffApp : Application() {

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
    }
}