# lko-datastructure

一个面向游戏与图形应用的高性能通用数据结构库，服务于 ECS、渲染、图形与 UI
等上层模块。重点在于紧凑内存布局、批量操作效率与原始类型优化。

## 模块定位
- 为 `lko-ecs`、`lko-gdx-render`、`lko-graph-*`、`lko-ecs-ui` 提供底层数据结构支持。
- 提供位运算工具、二维数组、快速列表（对象与原始类型）、BVH（层次包围体树）、矩阵工具。
- 适用于高频增删、空间查询、批量处理与数据压缩等场景。

## 构建信息
- 目标平台：`jvm("desktop")`
- 依赖：`kotlinx.atomics`（原子操作，利于并发与性能优化）
- 源码结构：`commonMain` 与 `commonTest`，遵循 Kotlin 多平台布局

## 关键特性
- 高性能集合：`FastList` 系列对对象与原始类型进行专门优化，降低拷贝与 GC 压力。
- 紧凑位操作：`Bits.kt` 提供丰富的位提取、插入、掩码与压缩字段工具。
- 空间加速结构：完整 `BVH` 实现，支持插入、移除、更新、批量构建、射线与范围查询。
- 二维数组族：`IArray2<T>` 与各原始类型二维数组，支持矩形范围批量填充与索引检查；提供可观察包装器。
- 矩阵：`Matrix<T>` 通用接口，`Matrix3`/`Matrix4` 值类（行主序、紧凑存储）。

## 提供的数据结构与工具
- array2
  - `IArray2<T>` 与 `IntIArray2`、`FloatIArray2`、`ByteIArray2` 等原始类型版本
  - `ObservableArray<T>` 与 `ObservableArray2<T>`（支持版本控制与批量变更聚合）
  - 支持 `get/set(x, y)`、矩形填充、范围与索引校验
- list（FastList）
  - `ObjectFastList<T>`、`IntFastList`、`FloatFastList`、`DoubleFastList`、`LongFastList`、`ByteFastList`、`ShortFastList`、`CharFastList`
  - 接口族：`MutableFastList<T>`、`PrimitiveMutableFastList<T>`、`CompositeMutableFastList<V, T>`、以及各原始类型专用接口
  - 提供批量插入、区间移动、`insertLastAll` 与 `safeInsert`/`safeInsertLast` 等高效 API
- math / Bits
  - 位操作工具：`extractXX`、`insertXX`、`mask`、`signExtend`、`reverseBits/Bytes`、`fastInsert`
  - `IntMaskRange`：掩码区间描述，适合打包解析复合数据
- matrix
  - `Matrix<T>` 通用接口；`Matrix3`/`Matrix4` 行主序浮点矩阵，使用紧凑 `FloatArray` 存储
- bvh
  - `BVH<T>`：层次包围体树，含 `LeafNode`、`InternalNode`、`BVHRect`、`BVHRay`、`BVHVector`
  - 支持插入/移除、更新与重插、批量构建、射线相交、范围搜索与统计信息收集

## FastList 设计与 API 语义
- 底层存储与扩容
  - 对象列表：`ObjectFastList<T>` 使用 `Array<Any?>` 顺序存储
  - 原始类型列表：如 `IntFastList` 使用 `IntArray` 等顺序存储
  - 扩容策略：在需要扩容时取 “原容量两倍” 或 “刚好满足需要”，减少拷贝次数
- 插入与批量操作
  - `insertLast(...)`/`insert(index, ...)`：支持一次性插入 1~6 个元素，减少多次边界检查与复制
  - `insertLastAll(elements: Iterable<T>)` 与 `insertAll(index, elements: Iterable<T>)`：
    - 对同类型 FastList 做零拷贝复制（直接 `copyInto`）
    - 对 `Collection` 走 `forEachIndexed`，其他可迭代对象先 `toList()`
  - 面向数组的批量操作：对象版支持 `Array<out T>`；各原始类型版支持对应的 `XxxArray`
- 安全批量写入（减少检查与拷贝）
  - `safeInsertLast(count) { editor -> ... }` 与 `safeInsert(index, count) { editor -> ... }`
  - 回调中通过 `editor.unsafeInsert(element)` 顺序写入，必须恰好写入 `count` 个元素；否则触发一致性校验
  - 内部流程：预扩容 → 区间移动（如需）→ 回调顺序填充 → 校验写入数量 → 更新 `size`
- 容量与填充扩展接口
  - `PrimitiveMutableFastList<T>`：
    - `ensureCapacity(capacity, element)`：将列表扩展到指定容量，并用 `element` 初始化新增区间
    - `fill(element, startIndex, endIndex)`：对现有区间批量赋值
  - `CompositeMutableFastList<V, T>`：以复合值 `V` 初始化/填充元素类型 `T` 的列表，适合向量/结构体等复合元素场景
- 复杂度与注意事项
  - 尾部插入摊销近似 O(1)；中间插入为 O(n)（涉及区间移动）
  - `checkIndex` 对读写/移除做边界校验；批量 API 内部使用 `copyInto` 保证正确移动

### 快速示例（FastList）
```kotlin
import cn.jzl.datastructure.list.*

val ints = IntFastList()
ints.safeInsertLast(count = 3) {
    unsafeInsert(1); unsafeInsert(2); unsafeInsert(3)
}
ints.insert(index = 1, element1 = 9, element2 = 8) // [1, 9, 8, 2, 3]
ints.ensureCapacity(10, element = -1)               // 填充到容量 10
```

## Array2 与可观察包装器
- 核心接口：`IArray2<T>`（含 `width`/`height`、索引映射与范围检查），提供 `get/set(x, y)` 与矩形范围批量操作
- 可观察接口：`ObservableArray<T>`
  - `version`：变更版本号；每次 `flush()` 后递增
  - `lock()`/`unlock()`：批量变更的临界区控制；`unlock()` 时计数归零则触发 `flush()`
  - `flush(minX, minY, maxX, maxY)`：将变更范围推送给观察者（实现自行处理回调）
- 二维包装器：`ObservableArray2<T>`
  - 包装 `IArray2<T>`，在 `lock` 期间累积最小包围矩形；当 `unlock()` 使锁计数归零时触发一次 `flush()`
  - 适合 UI 布局更新、局部图像变更等场景

### 快速示例（Array2 + Observable）
```kotlin
import cn.jzl.datastructure.array2.*
import cn.jzl.datastructure.math.geom.RectangleInt

val arr = Array2(width = 4, height = 4, data = Array(16) { 0 })
val obs = ObservableArray2(arr) { minX, minY, maxX, maxY ->
    println("flushed: [$minX,$minY]-[$maxX,$maxY], version=${obs.version}")
}
obs.lock()
arr.set(RectangleInt(0, 0, 2, 2), 1)
arr[3, 3] = 2
obs.unlock() // 触发一次 flush，范围为两个变更的最小包围矩形
```

## 矩阵（Matrix）
- 通用接口：`Matrix<T>`
  - 语义：二维矩阵元素访问与写入；行列索引从 `0` 开始
- 值类矩阵：`Matrix3` 与 `Matrix4`
  - 存储：行主序，使用紧凑 `FloatArray(9)`/`FloatArray(16)`
  - 索引映射：`index = row * N + col`（`N=3/4`）
  - 通过扩展方法提供常用操作（置单位、乘法、转置、向量变换等）

### 快速示例（Matrix3）
```kotlin
import cn.jzl.datastructure.matrix.Matrix3

val m = Matrix3(floatArrayOf(
    1f, 0f, 0f,
    0f, 1f, 0f,
    0f, 0f, 1f
))
// 行主序访问：m[row, col]
val a00 = m[0, 0]
```

## BVH（层次包围体树）
- 节点与基础类型
  - 叶节点：`LeafNode<T>` 持有数据项与边界矩形
  - 内部节点：`InternalNode`（父节点接口 `BVHParentNode`），维护左右子树与父子关系
  - 边界矩形：`BVHRect`（按维度存储 `min/max`），提供包含与面积计算
  - 射线/向量：`BVHRay`（方向可归一化）、`BVHVector`
- 插入与更新策略
  - 插入：选择“面积增量较小”的子树插入；必要时将两个叶子组合为内部节点
  - `updateBounds()`：父节点边界更新为左右子树边界的最小包围矩形
- 维度与查询
  - 支持通用维度 `dimensions`（二维/三维及更高维）
  - 提供射线相交、范围搜索、批量构建与统计信息（节点计数、深度、面积利用率等）

### 快速示例（二维 BVH）
```kotlin
import cn.jzl.datastructure.bvh.*

val bvh = BVH<Any>(dimensions = 2)
val obj = Any()

bvh.insert(obj) { rect ->
    rect.min(bvh, 0, 10f); rect.max(bvh, 0, 20f)
    rect.min(bvh, 1, 30f); rect.max(bvh, 1, 40f)
}
```

## 几何形状与 SimpleShape2D
几何相关代码位于包 `cn.jzl.datastructure.math.geom`，提供统一的二维基础形状与操作接口：
- `SimpleShape2D` 核心属性与方法：
  - `closed`：形状是否闭合（如多边形、圆等为 `true`；折线为 `false`）
  - `area` / `perimeter` / `center`：面积、周长与几何中心（质心）
  - `distance(point)`：点到形状的最短距离；若 `contains(point)`，返回 `0`
  - `projectedPoint(point)`：将点投影到形状边界（或最近结构）上的点；若点在形状内部，返回点自身
  - `normalVectorAt(point)`：在给定点处的单位法线向量（实现约定见下）
  - `contains(point)`：命中测试，判断点是否在形状内部（含边界）
  - `getBounds()`：返回外接轴对齐矩形 `Rectangle`（AABB）
- 已实现形状：`Circle`、`Ellipse`、`Rectangle`、`RectangleInt`、`LineSegment`、`Polyline`、`Triangle`、`Polygon`

### 法线与投影的行为约定（摘要）
- 若 `contains(point)` 为真，`projectedPoint(point)` 返回原点；`distance(point)` 等于 `0`
- 圆/椭圆：`projectedPoint` 沿径向；`normalVectorAt` 为径向外法线
- 矩形：`projectedPoint` 夹取最近边或角；`normalVectorAt` 为最近边的外法线（轴对齐）
- 线段/折线：`projectedPoint` 为最近段投影；`normalVectorAt` 为最近段外法线
- 三角形/多边形：`projectedPoint` 选取最近边投影；边界退化时法线回退为 `(1, 0)`

### 使用示例（几何）
```kotlin
import cn.jzl.datastructure.math.geom.*
import cn.jzl.datastructure.math.vector.Point

val rect = Rectangle(0f, 0f, 10f, 5f)
val p = Point(12f, 2f)
val d = rect.distance(p)                // 2.0
val proj = rect.projectedPoint(p)       // Point(10, 2)
val n = rect.normalVectorAt(p)          // Point(1, 0)
```

## 测试与质量保证
- 源集：所有几何与通用数据结构测试位于 `src/commonTest/kotlin`
- 运行桌面测试：在仓库根目录执行
  - Windows：`./gradlew.bat :lko-datastructure:desktopTest -q`
  - macOS/Linux：`./gradlew :lko-datastructure:desktopTest -q`
- 测试报告：生成于 `lko-libs/lko-datastructure/build/reports/tests/desktopTest/index.html`
- 重点用例：覆盖 `projectedPoint` 与 `normalVectorAt` 的边界行为（内部点/外部点/边界点、角点、退化情形等）

## 性能与实现备注
- 计算几何：解析解 + 夹取（clamp），避免不必要的分支与开销；多边形面积与质心采用鞋带公式（O(n)）
- 集合与位操作：尽量减少拷贝与装箱；在热点路径避免临时对象
- 矩阵：行主序紧凑存储，提高缓存友好度；索引映射简单高效（`row * N + col`）
- 兼容性：遵循 Kotlin 多平台目录结构（当前目标为 JVM desktop）

## 与其他模块的关系
- `lko-ecs`：使用位集与高效集合管理实体与组件索引
- `lko-gdx-render` / `lko-graph-*`：批量渲染排序、空间查询与数据打包
- `lko-ecs-ui`：布局计算与命中测试可用二维数组与几何工具

## 依赖方式（示例）
```kotlin
dependencies {
    implementation(project(":lko-libs:lko-datastructure"))
}
```

## 贡献与维护
- PR 与 Issue 欢迎就以下方向贡献：
  - 新的数据结构与几何形状、附加算法（如凸包、布尔运算、KD-Tree 等）
  - 跨平台测试与基准测试覆盖提升
  - 文档与示例的完善（特别是几何与集合 API 的语义与约定）
- 许可协议与贡献指南请参考仓库根目录与相关模块说明。