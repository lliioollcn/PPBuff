package cn.lliiooll.ppbuff.data.types

enum class PHookType(label: String, route: String) {


    COMMON("基础", "common"),
    PLAY("娱乐", "play"),
    SIMPLE("精简", "simple"),
    DEBUG("调试(不懂别乱动，容易打不开应用)", "debug"),
    HIDE("???", "???");

    private var label: String
    private var route: String

    init {
        this.label = label
        this.route = route
    }

    open fun getLabel(): String {
        return label
    }

    open fun getRoute(): String {
        return route
    }
}