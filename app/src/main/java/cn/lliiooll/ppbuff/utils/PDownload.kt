package cn.lliiooll.ppbuff.utils

import android.os.FileUtils
import cn.hutool.core.io.FileUtil
import cn.hutool.core.io.IoUtil
import cn.hutool.http.HttpUtil
import cn.lliiooll.ppbuff.PPBuff
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

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
            //if (conn.contentLengthLong > 1024 * 1024 * 5) {
            // 超过5MB
            //  downloadMultiThread(url, tempFile, conn.contentLengthLong)
            //  } else {
            IoUtil.copy(conn.getInputStream(), FileOutputStream(tempFile))
            /*
            IOUtils.copy(
                conn.getInputStream(),
                tempFile,
                conn.contentLengthLong
            )

             */
            //  }
        } catch (_: Throwable) {

        }
        return tempFile
    }

    fun downloadMultiThread(urlStr: String, tempFile: File, size: Long) {
        val tc = 5// 线程数
        val td = size / tc // 线程平均下载量
        "准备多线程下载，线程数:$tc, 文件大小:$size".debug()
        var finish = 0
        var index = 0L
        for (i in 0 until tc) {
            thread {
                val tdi = index// 线程下载偏移
                val tl = if (i == (tc - 1)) size - tdi else td// 下载长度
                "线程 $i 准备下载，index:$tdi, len:$tl".debug()
                val url = URL(urlStr).openConnection() as HttpURLConnection
                url.requestMethod = "GET"
                url.connectTimeout = 5000
                url.setRequestProperty("Range", "bytes=$tdi-${tdi + tl}")
                val code = url.responseCode
                val ins = url.inputStream
                val file = RandomAccessFile(tempFile, "rwd")
                if (code == HttpURLConnection.HTTP_PARTIAL) {
                    // 支持断点续传
                    "线程 $i 开始下载，index:$tdi, len:$tl".debug()
                    file.seek(tdi)
                    IOUtils.copy(
                        ins,
                        file,
                        url.getHeaderFieldLong("Content-Length", tl),
                        false
                    )
                    "线程 $i 下载完毕，准备整合文件，index:$tdi, len:$tl, download: ${
                        url.getHeaderFieldLong(
                            "Content-Length",
                            tl
                        )
                    }".debug()
                    "线程 $i 任务完成".debug()
                } else {
                    "线程 $i : 不支持断点续传: $code".debug()
                    if (code == 416) {
                        "线程 $i: 非法的范围: ${url.getHeaderField("Content-Range")}".debug()
                    }
                }
                file.close()
                ins.close()
                finish++
            }
            index += td
        }
        while (finish >= tc) {
            Thread.sleep(500L)
            "等待下载，完成个数: $finish".debug()
        }
    }
}
