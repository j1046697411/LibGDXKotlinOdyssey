package cn.jzl.ecs.v2

class Test1Component : Component<Test1Component>{
    override val type: ComponentType<Test1Component> get() = Test1Component
    companion object : ComponentType<Test1Component>()
}