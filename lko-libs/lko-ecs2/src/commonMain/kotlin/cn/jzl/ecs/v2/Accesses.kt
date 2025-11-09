/**
 * Accesses.kt 定义了组件访问权限的核心类型
 * 
 * 实现逻辑：
 * 1. 提供两级访问权限抽象：只读和可写
 * 2. 使用值类（value class）优化内存使用和运行时性能
 * 3. 通过接口继承表示权限包含关系（写权限包含读权限）
 * 4. 为每个访问权限关联组件类型，确保类型安全
 * 
 * 设计考虑：
 * - 内存优化：使用@JvmInline标记值类，避免额外的对象包装开销
 * - 类型安全：通过泛型参数确保访问权限与组件类型的正确关联
 * - 权限语义：清晰区分读和写操作，支持依赖分析
 * - 组合性：接口设计支持权限的组合和推理
 * 
 * 系统作用：
 * - 提供调度系统的依赖分析基础
 * - 防止数据竞争，确保并发安全
 * - 优化调度顺序，提高系统性能
 * - 实现自动依赖注入和解析
 */
package cn.jzl.ecs.v2

import kotlin.jvm.JvmInline

/**
 * 表示对组件类型的只读访问权限
 * 
 * 实现逻辑：
 * 1. 使用值类（value class）包装组件类型，减少内存开销
 * 2. 实现ComponentReadAccesses接口，提供类型安全的访问声明
 * 3. 存储组件类型引用，用于调度系统的依赖分析
 * 
 * 设计特点：
 * - 轻量级：作为值类编译后会被内联，不产生额外对象
 * - 类型安全：通过泛型参数C确保访问权限与组件类型一致
 * - 封装性：将组件类型和访问意图封装在一起
 * - 内部可见性：仅在ECS系统内部使用，外部通过工厂方法创建
 * 
 * @property type 组件类型元数据，包含组件的唯一索引和类型信息
 * @param C 组件类型参数，确保访问权限的类型安全
 */
@JvmInline
internal value class ComponentReadAccessesImpl<C>(
    override val type: ComponentType<C>
) : ComponentReadAccesses<C>

/**
 * 表示对组件类型的写访问权限
 * 
 * 实现逻辑：
 * 1. 使用值类包装组件类型，优化内存和性能
 * 2. 实现ComponentWriteAccesses接口，表示可写访问权限
 * 3. 通过接口继承自动获得读权限语义
 * 
 * 设计特点：
 * - 权限继承：通过接口继承关系，写权限自动包含读权限
 * - 内存优化：值类实现避免额外的对象创建
 * - 语义明确：清晰表示调度器需要修改组件数据的意图
 * - 并发安全：用于调度系统检测潜在的数据竞争
 * 
 * 并发控制作用：
 * - 允许调度系统检测对同一组件的冲突访问
 * - 确保写操作不会与其他写操作并发执行
 * - 支持调度器的正确排序
 * 
 * @property type 组件类型元数据，包含组件的唯一索引等信息
 * @param C 组件类型参数
 */
@JvmInline
internal value class ComponentWriteAccessesImpl<C>(
    override val type: ComponentType<C>
) : ComponentWriteAccesses<C>

/**
 * 组件只读访问权限接口
 * 
 * 接口设计：
 * 1. 通过泛型参数C确保访问权限的类型安全
 * 2. 提供type属性访问对应的组件类型
 * 3. 作为所有只读访问权限的基础接口
 * 
 * 设计意图：
 * - 定义访问权限的基本契约
 * - 支持类型安全的组件访问声明
 * - 为写访问权限接口提供继承基础
 * - 允许调度系统对访问权限进行统一处理
 * 
 * @param C 访问的组件类型，确保类型安全
 */
interface ComponentReadAccesses<C> {
    val type: ComponentType<C>
}

/**
 * 组件写访问权限接口
 * 
 * 接口设计：
 * 1. 继承自ComponentReadAccesses接口，表示写权限包含读权限
 * 2. 保持相同的泛型参数和type属性声明
 * 3. 通过接口继承建立权限层次关系
 * 
 * 设计意图：
 * - 通过继承表达"写包含读"的语义关系
 * - 保持API一致性，简化权限处理逻辑
 * - 支持调度系统进行权限推断和冲突检测
 * - 遵循最小权限原则的反面：请求写权限时隐含读权限
 * 
 * 权限语义：
 * - 实现此接口的访问权限表示需要修改组件数据
 * - 同时也具备读取组件数据的能力
 * - 与只读权限共同构成完整的权限体系
 * 
 * @param C 访问的组件类型
 */
interface ComponentWriteAccesses<C> : ComponentReadAccesses<C> {
    override val type: ComponentType<C>
}
