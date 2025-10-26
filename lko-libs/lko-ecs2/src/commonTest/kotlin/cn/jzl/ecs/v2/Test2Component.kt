package cn.jzl.ecs.v2

class Test2Component : Component<Test2Component>{
    override val type: ComponentType<Test2Component> get() = Test2Component
    companion object : ComponentType<Test2Component>()
}