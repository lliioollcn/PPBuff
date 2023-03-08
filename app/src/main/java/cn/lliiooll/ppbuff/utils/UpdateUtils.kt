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
    }

    fun hasUpdateGithub(): Boolean {
        if (PPBuff.isDebug()) return false
        if (acu) return PPBuff.hasUpdate
        // https://api.github.com/repos/lliioollcn/PPBuff/actions/runs
        // https://api.github.com/repos/lliioollcn/PPBuff/actions/runs/{$id}
        // https://api.github.com/repos/lliioollcn/PPBuff/actions/runs/{$id}/artifacts
        val jstr =
            HttpUtil.get("https://api.github.com/repos/lliioollcn/PPBuff/actions/runs")
                ?: return false
        val json = JSONUtil.parseObj(jstr)
        val workflowRuns = json.getJSONArray("workflow_runs")
        if (workflowRuns == null || workflowRuns.size < 1) {
            return false
        }
        val latest = workflowRuns.getJSONObject(0)
        val headSha = latest.getStr("head_sha")
        "最新哈希: $headSha,本地哈希: ${BuildConfig.BUILD_HASH}".debug()
        PPBuff.hasUpdate = (headSha != BuildConfig.BUILD_HASH)
        acu = true
        return PPBuff.hasUpdate
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getUpdateDetails(): PUpdateDetails? {
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
        jstr =
            HttpUtil.get("https://api.github.com/repos/lliioollcn/PPBuff/actions/runs")
                ?: return null
        val jObj = JSONUtil.parseObj(jstr)
        val workflowRuns = jObj.getJSONArray("workflow_runs")
        if (workflowRuns != null) {
            if (workflowRuns.size < 1) {
                return null
            }
            latest = workflowRuns.getJSONObject(0)
            val runId = latest.getLong("id")
            val checkSuiteId = latest.getLong("check_suite_id")
            "https://api.github.com/repos/lliioollcn/PPBuff/actions/runs/$runId/artifacts".debug()
            jstr =
                HttpUtil.get("https://api.github.com/repos/lliioollcn/PPBuff/actions/runs/$runId/artifacts")
                    ?: return null
            jstr.debug()
            val jArrays = JSONUtil.parseObj(jstr).getJSONArray("artifacts")
            if (jArrays.size < 1) {
                return null
            }
            val jObjGithub = jArrays
                .getJSONObject(0)
            val artifactId = jObjGithub.getLong("id")
            details.hash = jObjGithub.getJSONObject("workflow_run").getStr("head_sha")
            details.downloadUrlGithub =
                "https://github.com/lliioollcn/PPBuff/suites/$checkSuiteId/artifacts/$artifactId"
        }
        details.hash = "unknown"
        details.msg = jObjAppCenter.getStr("release_notes")
        details.downloadUrlAppCenter = jObjAppCenter.getStr("download_url")
        details.version = jObjAppCenter.getStr("short_version")
        val date = jObjAppCenter.getStr("uploaded_at").replace("Z", "")
        details.time =
            Date.from(LocalDateTime.parse(date).atZone(ZoneId.systemDefault()).toInstant()).time
        return details
    }

    fun hasUpdate(): Boolean {
        return hasUpdateAppCenter() || hasUpdateGithub()

    }
}