# lko-datastructure

一个面向游戏与图形应用的高性能通用数据结构库，服务于 ECS、渲染、图形与 UI
等上层模块。重点在于紧凑内存布局、批量操作效率与原始类型优化。

## 模块定位
- 为 `lko-ecs`、`lko-gdx-render`、`lko-graph-*`、`lko-ecs-ui` 提供底层数据结构支持。
- 提供位运算工具、二维数组、快速列表（对象与原始类型）、BVH（层次包围体树）。
- 适用于高频增删、空间查询、批量处理与数据压缩等场景。

## 构建信息
- 目标平台：`jvm("desktop")`
- 依赖：`kotlinx.atomics`（原子操作，利于并发与性能优化）
- 源码结构：`commonMain` 与 `commonTest`，遵循 Kotlin 多平台布局

## 关键特性
- 高性能集合：`FastList` 系列对对象与原始类型进行专门优化，降低拷贝与 GC 压力。
- 紧凑位操作：`Bits.kt` 提供丰富的位提取、插入、掩码与压缩字段工具。
- 空间加速结构：完整 `BVH` 实现，支持插入、移除、更新、批量构建、射线与范围查询。
- 二维数组族：`Array2<T>` 与各原始类型二维数组，支持矩形范围批量填充与索引检查。

## 提供的数据结构与工具
- array2
  - `Array2<T>` 与 `IntArray2`、`FloatArray2`、`ByteArray2` 等原始类型版本
  - 支持 `get/set(x, y)`、矩形填充、范围与索引校验
- list（FastList）
  - `ObjectFastList<T>`、`IntFastList`、`FloatFastList` 等
  - 提供批量插入、区间移动、`insertLastAll` 与 `safeInsert` 等高效 API
- math / Bits
  - 位操作工具：`extractXX`、`insertXX`、`mask`、`signExtend`、`reverseBits/Bytes`、`fastInsert`
  - `IntMaskRange`：掩码区间描述，适合打包解析复合数据
- bvh
  - `BVH<T>`：层次包围体树，含 `LeafNode`、`InternalNode`、`BVHRect`、`BVHRay`、`BVHVector`
  - 支持插入/移除、更新与重插、批量构建、射线相交、范围搜索与统计信息收集

## 与其他模块的关系
- `lko-ecs`：使用位集与高效集合管理实体与组件索引。
- `lko-gdx-render` / `lko-graph-*`：批量渲染排序、空间查询与数据打包。
- `lko-ecs-ui`：布局计算与命中测试可用二维数组与几何工具。

## 依赖方式（示例）
在同仓库项目中，可直接在模块的 `build.gradle.kts` 添加：
```kotlin
dependencies {
    implementation(project(":lko-libs:lko-datastructure"))
}
```

## 使用示例

### Array2
```kotlin
import cn.jzl.datastructure.array2.Array2
import cn.jzl.datastructure.math.geom.RectangleInt

val arr = Array2(width = 3, height = 3, data = Array(9) { 0 })
arr[1, 2] = 10
arr.set(RectangleInt(x = 0, y = 0, width = 2, height = 2), value = 1)
```

### ObjectFastList / IntFastList
```kotlin
import cn.jzl.datastructure.list.ObjectFastList
import cn.jzl.datastructure.list.IntFastList

val objs = ObjectFastList<String>()
objs.insertLast("a")
objs.insert(index = 0, element = "b")
objs.insertLastAll(listOf("c", "d", "e"))

val ints = IntFastList()
ints.insertLast(1, 2, 3)
ints.insert(index = 1, element1 = 9, element2 = 8)
```

### BVH（二维示例）
```kotlin
import cn.jzl.datastructure.bvh.BVH
import cn.jzl.datastructure.bvh.BVHRect

// 使用二维 BVH（dimensions=2）
val bvh = BVH<Any>(dimensions = 2)
val obj = Any()

// 插入：通过回调设置矩形边界（min/max，两个维度）
bvh.insert(obj) { rect: BVHRect ->
    rect.min(bvh, 0, 10f); rect.max(bvh, 0, 20f)
    rect.min(bvh, 1, 30f); rect.max(bvh, 1, 40f)
}

// 射线/范围搜索、更新、移除等 API 可配合使用
```

## 设计思路与优势
- 性能优先：原始类型优化与批量操作减少拷贝与分配。
- 内存紧凑：位打包与自定义数据布局提升缓存友好度。
- API 实用：面向高频场景的插入/移动/批处理方法，减少样板代码。

## 许可与贡献
- 许可协议与贡献指南请参考仓库根目录与相关模块说明。
- 欢迎为更多数据结构、算法与基准测试贡献代码与文档。