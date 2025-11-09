package cn.jzl.ecs.v2

/**
 * EntityTag - 实体标签基类
 *
 * 实现逻辑：
 * 1. 继承自ComponentType，使用Boolean类型作为组件数据
 * 2. 作为标记组件的特殊实现，用于表示实体的二进制状态
 * 3. 提供轻量级的实体分类和过滤机制
 * 4. 与ComponentType共享索引分配机制，但有特殊的语义
 *
 * 实体标签是一种轻量级的组件形式，它：
 * - 不存储复杂数据，只表示存在与否
 * - 适用于标记实体状态、类别或特征
 * - 可用于快速过滤和识别特定实体
 * - 比存储完整组件数据更节省资源
 *
 * 典型应用场景包括：玩家实体标记、可交互物体标记、危险实体标记等。
 */
abstract class EntityTag : ComponentType<Boolean>()