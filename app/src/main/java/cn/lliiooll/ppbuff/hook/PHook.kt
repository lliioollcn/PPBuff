package cn.lliiooll.ppbuff.hook

import cn.lliiooll.ppbuff.PPBuff
import cn.lliiooll.ppbuff.hook.loader.BaseLoader
import cn.lliiooll.ppbuff.hook.loader.ZuiyouLiteLoader
import cn.lliiooll.ppbuff.hook.loader.ZuiyouLoader
import cn.lliiooll.ppbuff.utils.error

class PHook {
    companion object {

        private val hookLoaders = hashMapOf<String, BaseLoader>()
        var thisLoader: BaseLoader? = null

        init {
            hookLoaders[PPBuff.HostInfo.ZuiyouLite.PACKAGE_NAME] = ZuiyouLiteLoader
            hookLoaders[PPBuff.HostInfo.TieBa.PACKAGE_NAME] = ZuiyouLoader
        }

        fun init(packageName: String) {
            if (hookLoaders.containsKey(packageName)) {
                thisLoader = hookLoaders[packageName]
                thisLoader?.load()
            } else {
                "没有找到合适的Hook加载器，不进行加载".error()
            }
        }


    }
}