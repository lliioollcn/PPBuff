package cn.lliiooll.ppbuff.hook.loader

import cn.lliiooll.ppbuff.hook.BaseHook
import cn.lliiooll.ppbuff.utils.info

/**
 * 基础hook加载器
 */
open abstract class BaseLoader {

    abstract fun load()
    abstract fun hooks(): List<BaseHook>
}