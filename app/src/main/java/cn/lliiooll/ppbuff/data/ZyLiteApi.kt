package cn.lliiooll.ppbuff.data

import cn.lliiooll.ppbuff.data.bean.PShareDetail
import cn.lliiooll.ppbuff.utils.async
import cn.lliiooll.ppbuff.utils.catch
import cn.lliiooll.ppbuff.utils.sync
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object ZyLiteApi {
    val gson = GsonBuilder().serializeNulls().create()
    fun shareDetail(postId: Long, mid: Long): PShareDetail {
        return gson.fromJson(
            OkHttpClient().newCall(
                Request.Builder()
                    .url("https://h5.pipigx.com/ppapi/share/fetch_content")
                    .post(gson.toJson(hashMapOf<String, Any>().apply {
                        put("type", "post")
                        put("pid", postId)
                        put("mid", mid)
                    }).toRequestBody("application/json".toMediaType()))
                    .build()
            ).execute().body?.string(),
            PShareDetail::class.java
        )
    }
}