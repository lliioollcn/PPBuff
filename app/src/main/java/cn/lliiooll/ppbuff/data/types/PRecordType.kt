package cn.lliiooll.ppbuff.data.types

enum class PRecordType(show: String, label: String) {

    SAF("SAF(高版本自定义储存位置)", "saf"),
    TRADITIONAL("传统方式(在高版本不可用,会储存到储存卡\"/PPBuff/Video/\"目录里)", "traditional"),
    STORE("存入相册", "store");


    private var label: String
    private var show: String

    init {
        this.label = label
        this.show = show
    }

    open fun getLabel(): String {
        return label
    }

    open fun getShow(): String {
        return show
    }

    companion object {
        fun getValue(string: String): PRecordType {
            return if (string.contentEquals(SAF.label)) {
                SAF
            } else if (string.contentEquals(TRADITIONAL.label)) {
                TRADITIONAL
            } else {
                STORE
            }
        }
    }
}