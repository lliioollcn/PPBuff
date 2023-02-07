package cn.lliiooll.ppbuff.data

import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONUtil
import cn.lliiooll.ppbuff.data.bean.PShareDetail

object ZyLiteApi {
    fun shareDetail(postId: Long, mid: Long): PShareDetail {

        return JSONUtil.toBean(
            HttpUtil.createPost("https://h5.pipigx.com/ppapi/share/fetch_content")
                .body(JSONUtil.toJsonStr(hashMapOf<String, Any>().apply {
                    put("type", "post")
                    put("pid", postId)
                    put("mid", mid)
                }), "application/json")
                .execute()
                .body(), PShareDetail::class.java
        )
    }
}