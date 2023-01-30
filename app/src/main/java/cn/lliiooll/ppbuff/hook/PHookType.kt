package cn.lliiooll.ppbuff.hook

enum class PHookType(label: String,route:String) {




    COMMON("基础","common"),
    PLAY("娱乐","play"),
    SIMPLE("精简","simple"),
    DEBUG("调试","debug"),
    HIDE("???","???");
    private var label: String
    private var route: String
    init{
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