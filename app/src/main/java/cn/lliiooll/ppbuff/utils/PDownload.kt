package cn.lliiooll.ppbuff.utils

import cn.lliiooll.ppbuff.PPBuff
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File

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
            IOUtils.copy(
                OkHttpClient().newCall(Request.Builder()
                    .url(url)
                    .get()
                    .build()).execute().body?.byteStream(),
                tempFile
            )
        }catch (_:Throwable){

        }

        return tempFile
    }
}
