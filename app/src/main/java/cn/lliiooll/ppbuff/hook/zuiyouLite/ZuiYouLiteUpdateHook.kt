package cn.lliiooll.ppbuff.hook.zuiyouLite

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.data.types.PHookType
import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.utils.PDownload
import cn.lliiooll.ppbuff.utils.UpdateUtils
import cn.lliiooll.ppbuff.utils.async
import cn.lliiooll.ppbuff.utils.findClass
import cn.lliiooll.ppbuff.utils.openUrl
import cn.lliiooll.ppbuff.utils.sync
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter
import com.github.kyuubiran.ezxhelper.utils.paramCount
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.style.IOSStyle
import java.io.File

object ZuiYouLiteUpdateHook : BaseHook(
    "检查更新", "check_update", PHookType.DEBUG
) {
    override fun init(): Boolean {
        "cn.xiaochuankeji.zuiyouLite.ui.main.MainActivity"
            .findClass()
            .findMethod {
                this.name == "onCreate" && this.paramCount == 1 && this.parameterTypes[0] == Bundle::class.java
            }
            .hookAfter {
                val activity = it.thisObject as Activity
                async {
                    if (UpdateUtils.hasUpdate()) {
                        val detail = UpdateUtils.getUpdateDetails()
                        if (detail != null) {
                            if (PConfig.string("update_version", "0") != detail.version) {
                                MessageDialog.build()
                                    .setTitle("发现新版本 - ${detail.version}")
                                    .setMessage(detail.msg)
                                    .setStyle(IOSStyle.style())
                                    .setOkButton("下载更新") { _, _ ->
                                        if (activity.packageManager.canRequestPackageInstalls()) {
                                            async {
                                                val file =
                                                    PDownload.downloadTemp(detail.downloadUrlAppCenter)
                                                val apk = File(file.parent, file.name + ".apk")
                                                sync {
                                                    val intent = Intent(Intent.ACTION_VIEW)
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                    val uri: Uri = FileProvider.getUriForFile(
                                                        activity,
                                                        "${PPBuff.getApplication().packageName}.fileprovider",
                                                        apk
                                                    )
                                                    intent.setDataAndType(
                                                        uri,
                                                        "application/vnd.android.package-archive"
                                                    )
                                                    activity.startActivity(intent)
                                                }

                                            }

                                        } else {
                                            detail.downloadUrlAppCenter.openUrl(activity)
                                        }
                                        false
                                    }
                                    .setCancelButton("忽略本次更新") { _, _ ->
                                        PConfig.set("update_version", detail.version)
                                        false
                                    }
                                    .show(activity)
                            }
                        }
                    }
                }
            }
        return true
    }
}