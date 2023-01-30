package cn.lliiooll.ppbuff.app

import android.app.Application
import cn.lliiooll.ppbuff.PPBuff
import com.tencent.mmkv.MMKV

class PPBuffApp : Application() {

    override fun onCreate() {
        super.onCreate()
        PPBuff.init(this)
        MMKV.initialize(this)
    }
}