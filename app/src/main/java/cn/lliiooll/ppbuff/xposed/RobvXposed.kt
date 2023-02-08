package cn.lliiooll.ppbuff.xposed

import android.app.Application
import android.util.ArrayMap
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.utils.debug
import cn.lliiooll.ppbuff.utils.findFieldIn
import cn.lliiooll.ppbuff.utils.value
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * 旧版xposed入口
 * - de.robv.android.xposed
 */
class RobvXposed : IXposedHookLoadPackage, IXposedHookZygoteInit {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (PPBuff.isDebug()) {
            "##############################".debug()
            "   RobvXposed开始加载，详细信息:".debug()
            "   包名: [${lpparam?.packageName}]".debug()
            "   进程: [${lpparam?.processName}]".debug()
            "   类加载器: [${lpparam?.classLoader?.javaClass?.name}]".debug()
            "##############################".debug()
        }
        EzXHelperInit.initHandleLoadPackage(lpparam!!)
        PXposed.init(
            PXposedParam(
                packageName = lpparam.packageName,
                processName = lpparam.processName,
                classLoader = lpparam.classLoader,
                appInfo = lpparam.appInfo,
            )
        )
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        if (PPBuff.isDebug()) {
            "##############################".debug()
            "   开始初始化Zygote".debug()
            "   模块路径: ${startupParam?.modulePath}".debug()
            "##############################".debug()
        }
        EzXHelperInit.initZygote(startupParam!!)
        PXposed.initZygote(
            PZygoteParam(
                modulePath = startupParam.modulePath,
            )
        )
    }
}