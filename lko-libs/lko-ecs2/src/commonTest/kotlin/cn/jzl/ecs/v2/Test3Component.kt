package cn.jzl.ecs.v2

class Test3Component : Component<Test3Component>{
    override val type: ComponentType<Test3Component> get() = Test3Component
    companion object : ComponentType<Test3Component>()
}