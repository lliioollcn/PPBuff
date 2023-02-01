package cn.lliiooll.ppbuff.data.types

enum class PRecordType(show: String, label: String) {

    SAF("SAF", "saf"),
    TRADITIONAL("传统方式", "traditional"),
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