package cn.lliiooll.ppbuff.startup

import android.app.Application
import android.content.Context
import cn.lliiooll.ppbuff.PConfig
import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.ffmpeg.FFmpeg
import cn.lliiooll.ppbuff.hook.PHook
import cn.lliiooll.ppbuff.utils.*
import cn.lliiooll.ppbuff.xposed.PXposedEntrance
import cn.lliiooll.ppbuff.xposed.PXposedParam
import cn.lliiooll.ppbuff.xposed.PZygoteParam
import com.github.kyuubiran.ezxhelper.init.EzXHelperInit
import com.github.kyuubiran.ezxhelper.utils.findMethod
import com.github.kyuubiran.ezxhelper.utils.hookAfter


object BuffEntrance : PXposedEntrance() {

    @JvmStatic
    var modulePath: String? = null

    override fun init(param: PXposedParam) {
        if (param.packageName == "cn.lliiooll.ppbuff") {
            // 激活状态检测
        } else if (PPBuff.isSupportApp(param.packageName) && param.processName == param.packageName) {
            "PPBuff 正在加载...".info()
            "当前进程: ${param.processName}".info()
            val appClazz =
                param.classLoader.loadClass(PPBuff.getHostApplicationClassName(param.packageName))
            appClazz.findMethod(true) {
                name == "onCreate"
            }.hookAfter {
                "宿主应用Application加载完毕，开始加载模块......".debug()

                val app = it.thisObject as Application
                PPBuff.init(app)
                "尝试初始化Native".debug()
                PNative.init(app)
                "尝试注入classLoader".debug()
                PPBuffClassLoader
                    .withXposed(param.classLoader)
                    .withApplition(app.classLoader)
                    .withContext(Context::class.java.classLoader)
                    .inject()

                "尝试注入界面代理".debug()
                EzXHelperInit.initActivityProxyManager(
                    PPBuff.getModulePackName(),
                    PPBuff.getHostApplicationSettingClassName(param.packageName),
                    BuffEntrance.javaClass.classLoader!!,
                    app.classLoader
                )
                "尝试注入资源文件".debug()
                EzXHelperInit.initAppContext(app, true, true)
                "尝试启用未注册界面".debug()
                EzXHelperInit.initSubActivity()
                PConfig.init(false)
                "开始初始化hook".debug()
                PHook.init(param.packageName)
            }


        } else {
            "不是支持的应用/进程，不进行加载...".error()
        }

    }

    override fun initZygote(pZygoteParam: PZygoteParam) {

        BuffEntrance.modulePath = pZygoteParam.modulePath
    }


}