package cn.lliiooll.ppbuff.data.types

enum class PWebTaskType(show: String, label: String) {

    COMMON1("皮皮自动任务", "web_task_common_1"),
    OTHERS("自定义", "web_task_custom");


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
        fun getValue(string: String): PWebTaskType {
            return if (string.contentEquals(COMMON1.label)) {
                COMMON1
            } else {
                OTHERS
            }
        }
    }
}