package cn.lliiooll.ppbuff.utils

import cn.hutool.core.date.DateUtil
import cn.hutool.http.HttpUtil
import cn.hutool.json.JSONUtil
import cn.lliiooll.ppbuff.BuildConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.data.bean.PUpdateDetails

object UpdateUtils {

    fun hasUpdateAppCenter(): Boolean {
        if (PPBuff.isDebug()) return false
        // https://install.appcenter.ms/api/v0.1/apps/lliioollcn/ppbuff/distribution_groups/alpha/releases/{$id}?is_install_page=true
        val jstr =
            HttpUtil.get("https://install.appcenter.ms/api/v0.1/apps/lliioollcn/ppbuff/distribution_groups/alpha/public_releases?scope=tester&top=10000")
                ?: return false
        val json = JSONUtil.parseArray(jstr)
        val latest = json.getJSONObject(0)
        val versionCode = latest.getStr("version")
        return versionCode == "${BuildConfig.VERSION_CODE}"
    }

    fun hasUpdateGithub(): Boolean {
        if (PPBuff.isDebug()) return false
        // https://api.github.com/repos/lliioollcn/PPBuff/actions/runs
        // https://api.github.com/repos/lliioollcn/PPBuff/actions/runs/{$id}
        // https://api.github.com/repos/lliioollcn/PPBuff/actions/runs/{$id}/artifacts
        val jstr =
            HttpUtil.get("https://api.github.com/repos/lliioollcn/PPBuff/actions/runs")
                ?: return false
        val json = JSONUtil.parseObj(jstr)
        val workflowRuns = json.getJSONArray("workflow_runs")
        val latest = workflowRuns.getJSONObject(0)
        val headSha = latest.getStr("head_sha")
        return headSha == BuildConfig.BUILD_HASH
    }

    fun getUpdateDetails(): PUpdateDetails? {
        val details = PUpdateDetails()
        if (PPBuff.isDebug()) return null
        // https://install.appcenter.ms/api/v0.1/apps/lliioollcn/ppbuff/distribution_groups/alpha/releases/{$id}?is_install_page=true
        var jstr =
            HttpUtil.get("https://install.appcenter.ms/api/v0.1/apps/lliioollcn/ppbuff/distribution_groups/alpha/public_releases?scope=tester&top=10000")
                ?: return null
        val json = JSONUtil.parseArray(jstr)
        var latest = json.getJSONObject(0)
        var id = latest.getInt("id")
        jstr =
            HttpUtil.get("https://install.appcenter.ms/api/v0.1/apps/lliioollcn/ppbuff/distribution_groups/alpha/releases/{$id}?is_install_page=true")
                ?: return null
        val jObjAppCenter = JSONUtil.parseObj(jstr)
        jstr =
            HttpUtil.get("https://api.github.com/repos/lliioollcn/PPBuff/actions/runs")
                ?: return null
        val jObj = JSONUtil.parseObj(jstr)
        val workflowRuns = jObj.getJSONArray("workflow_runs")
        latest = workflowRuns.getJSONObject(0)
        id = latest.getInt("id")
        val checkSuiteId = latest.getLong("check_suite_id")
        jstr =
            HttpUtil.get("https://api.github.com/repos/lliioollcn/PPBuff/actions/runs/{$id}/artifacts")
                ?: return null
        val jObjGithub = JSONUtil.parseObj(jstr).getJSONArray("artifacts")
            .getJSONObject(0)
        val artifactId = jObjGithub.getLong("id")
        details.hash = jObjGithub.getJSONObject("workflow_run").getStr("head_sha")
        details.msg = jObjAppCenter.getStr("release_notes")
        details.downloadUrlAppCenter = jObjAppCenter.getStr("download_url")
        details.downloadUrlGithub =
            "https://github.com/lliioollcn/PPBuff/suites/$checkSuiteId/artifacts/$artifactId"
        details.version = jObjAppCenter.getStr("short_version")
        details.time =
            DateUtil.newSimpleFormat("yyyy-MM-ddTHH:mm:ss.sssZ")
                .parse(jObjAppCenter.getStr("uploaded_at"))?.time ?: 0
        return details
    }
}