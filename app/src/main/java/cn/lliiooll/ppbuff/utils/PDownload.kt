package cn.lliiooll.ppbuff.utils

import cn.hutool.http.HttpUtil
import cn.lliiooll.ppbuff.PPBuff
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
                HttpUtil.createGet(url)
                    .execute().bodyStream(),
                tempFile
            )
        }catch (_:Throwable){

        }

        return tempFile
    }
}
