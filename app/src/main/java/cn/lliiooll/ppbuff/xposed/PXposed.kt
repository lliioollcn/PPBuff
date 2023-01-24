package cn.lliiooll.ppbuff.xposed

import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.startup.BuffEntrance
import cn.lliiooll.ppbuff.utils.debug

class PXposed {
    companion object {
        private var entrence: PXposedEntrance = BuffEntrance

        /**
         * 模块初始化
         */
        fun init(lpparam: PXposedParam) {
            if (PPBuff.isDebug()) "入口 @开始初始化模块".debug()
            entrence.init(lpparam)
        }

        /**
         * Zygote初始化
         */
        fun initZygote(startupParam: PZygoteParam) {
            if (PPBuff.isDebug()) "入口 @开始初始化Zygote".debug()
            PPBuff.initModulePath(startupParam.modulePath)
            entrence.initZygote(startupParam)
        }

        /**
         * 设置Xposed入口
         */
        fun entrance(xposedEntrance: PXposedEntrance) {
            if (PPBuff.isDebug()) "入口 @设置入口为: ${xposedEntrance.javaClass.name}".debug()
            this.entrence = xposedEntrance
        }
    }
}