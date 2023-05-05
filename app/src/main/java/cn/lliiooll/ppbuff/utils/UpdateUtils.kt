package cn.lliiooll.ppbuff.utils

import android.os.Build
import androidx.annotation.RequiresApi
import cn.hutool.core.date.DateUtil
import cn.hutool.core.date.LocalDateTimeUtil
import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONUtil
import cn.lliiooll.ppbuff.BuildConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.data.bean.PUpdateDetails
import cn.lliiooll.ppbuff.tracker.PLog
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

object UpdateUtils {
    var acu = false

    fun hasUpdateAppCenter(): Boolean {
        try {
            if (PPBuff.isDebug()) return false
            if (acu) return PPBuff.hasUpdate
            // https://install.appcenter.ms/api/v0.1/apps/lliioollcn/ppbuff/distribution_groups/alpha/releases/{$id}?is_install_page=true
            val jstr =
                HttpUtil.get("https://install.appcenter.ms/api/v0.1/apps/lliioollcn/ppbuff/distribution_groups/alpha/public_releases?scope=tester&top=10000")
                    ?: return false
            val json = JSONUtil.parseArray(jstr)
            if (json == null || json.size < 1) {
                return false
            }
            val latest = json.getJSONObject(0)
            val versionCode = latest.getStr("version")
            "最新版本: $versionCode,本地版本: ${BuildConfig.VERSION_CODE}".debug()
            PPBuff.hasUpdate = (versionCode != "${BuildConfig.VERSION_CODE}")
            acu = true
            return PPBuff.hasUpdate
        }catch (e:Throwable){
            return false
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getUpdateDetails(): PUpdateDetails? {
        try {
            val details = PUpdateDetails()
            if (PPBuff.isDebug()) return null
            // https://install.appcenter.ms/api/v0.1/apps/lliioollcn/ppbuff/distribution_groups/alpha/releases/{$id}?is_install_page=true
            var jstr =
                HttpUtil.get("https://install.appcenter.ms/api/v0.1/apps/lliioollcn/ppbuff/distribution_groups/alpha/public_releases?scope=tester&top=10000")
                    ?: return null
            val json = JSONUtil.parseArray(jstr)
            if (json.size < 1) {
                return null
            }
            var latest = json.getJSONObject(0)
            val id = latest.getLong("id")
            jstr =
                HttpUtil.get("https://install.appcenter.ms/api/v0.1/apps/lliioollcn/ppbuff/distribution_groups/alpha/releases/$id?is_install_page=true")
                    ?: return null
            val jObjAppCenter = JSONUtil.parseObj(jstr)
            details.hash = "unknown"
            details.msg = jObjAppCenter.getStr("release_notes")
            details.downloadUrlAppCenter = jObjAppCenter.getStr("download_url")
            details.version = jObjAppCenter.getStr("short_version")
            val date = jObjAppCenter.getStr("uploaded_at").replace("Z", "")
            details.time =
                Date.from(LocalDateTime.parse(date).atZone(ZoneId.systemDefault()).toInstant()).time
            return details
        }catch (e:Throwable){
            return null
        }

    }

    fun hasUpdate(): Boolean {
        return hasUpdateAppCenter()

    }
}