package cn.lliiooll.ppbuff.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import cn.hutool.http.HttpUtil
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.R
import cn.lliiooll.ppbuff.ffmpeg.FFmpeg
import java.io.File
import java.net.URL

object PDownload {
    fun downloadTemp(url: String): File {
        val ctx = PPBuff.getApplication()
        val tempDir = ctx.getExternalFilesDir("buffTemp")!!
        val tempFile = File(tempDir, "${System.currentTimeMillis()}.temp")
        if (!tempDir.exists()) {
            tempDir.mkdirs()
        }
        if (!tempFile.exists()) {
            tempFile.createNewFile()
        }
        try {
            val conn = URL(url).openConnection()
            IOUtils.copy(
                conn.getInputStream(),
                tempFile,
                conn.contentLengthLong
            )
        } catch (_: Throwable) {

        }

        return tempFile
    }
}
