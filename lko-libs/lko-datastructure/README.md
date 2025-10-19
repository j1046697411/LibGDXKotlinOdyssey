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

## 几何形状与 SimpleShape2D
几何相关代码位于包 `cn.jzl.datastructure.math.geom`，提供统一的二维基础形状与操作接口：
- `SimpleShape2D` 核心属性与方法：
  - `closed`：形状是否闭合（如多边形、圆等为 `true`；折线为 `false`）。
  - `area` / `perimeter` / `center`：面积、周长与几何中心（质心）。
  - `distance(point)`：点到形状的最短距离；若 `contains(point)`，返回 `0`。
  - `projectedPoint(point)`：将点投影到形状边界（或最近结构）上的点；若点在形状内部，返回点自身。
  - `normalVectorAt(point)`：在给定点处的单位法线向量（实现约定见下）。
  - `contains(point)`：命中测试，判断点是否在形状内部（含边界）。
  - `getBounds()`：返回外接轴对齐矩形 `Rectangle`（AABB）。
- 已实现形状：
  - `Circle(center, radius)`、`Ellipse(center, radiusX, radiusY)`
  - `Rectangle(x, y, width, height)`、`RectangleInt(x, y, width, height)`（整数版本）
  - `LineSegment(start, end)`、`Polyline(points: List<Point>)`
  - `Triangle(a, b, c)`、`Polygon(vertices: List<Point>)`

### 法线与投影的行为约定
为保证一致的几何语义，各形状的 `projectedPoint` 与 `normalVectorAt` 遵循以下约定：
- 通用规则：
  - 若 `contains(point)` 为真，`projectedPoint(point)` 返回原点；`distance(point)` 等于 `0`。
  - `normalVectorAt(point)` 返回单位向量；若局部法线方向不可唯一确定（如点与投影重合），回退到约定向量。
- 圆 / 椭圆：
  - `projectedPoint` 返回沿径向方向的边界点。
  - `normalVectorAt` 为从中心指向点的单位向量（径向外法线）。
- 矩形（AABB）：
  - `projectedPoint` 夹取到最近的边或角点。
  - `normalVectorAt` 为最近边的外法线（轴对齐，指向点的方向）。角点采用就近边的法线。
- 线段 / 折线：
  - `projectedPoint` 为最近段上的投影（端点处退化为端点）。
  - `normalVectorAt` 为最近段的外法线；在端点/折点处根据最近段选择方向。
- 三角形 / 多边形（闭合）：
  - `projectedPoint` 选取最近边的投影；内部点返回自身。
  - `normalVectorAt` 为“最近边到该点”的单位向量（指向点的外法线）；当投影与点重合（如位于边界且距离为 0）时回退为 `(1, 0)`。

### 使用示例（几何）
```kotlin
import cn.jzl.datastructure.math.geom.*
import cn.jzl.datastructure.math.vector.Point

val rect = Rectangle(0f, 0f, 10f, 5f)
val p = Point(12f, 2f)
val d = rect.distance(p)                // 2.0
val proj = rect.projectedPoint(p)       // Point(10, 2)
val n = rect.normalVectorAt(p)          // Point(1, 0)
val inside = Point(3f, 2f)
check(inside in rect)                   // true
val bounds = rect.getBounds()           // 自身 AABB

val tri = Triangle(Point(0f, 0f), Point(10f, 0f), Point(0f, 10f))
val q = Point(2f, 2f)
val qProj = tri.projectedPoint(q)       // Point(2, 2)（内部点，返回自身）
val qN = tri.normalVectorAt(q)          // Point(0, 1)（最近边 AB 的外法线）
```

## 测试与质量保证
- 源集：所有几何与通用数据结构测试位于 `src/commonTest/kotlin`。
- 运行桌面测试：在仓库根目录执行
  - Windows：`./gradlew.bat :lko-datastructure:desktopTest -q`
  - macOS/Linux：`./gradlew :lko-datastructure:desktopTest -q`
- 测试报告：生成于 `lko-libs/lko-datastructure/build/reports/tests/desktopTest/index.html`。
- 重点用例：覆盖 `projectedPoint` 与 `normalVectorAt` 的边界行为（内部点/外部点/边界点、角点、退化情形等）。

## 性能与实现备注
- 计算几何：
  - 距离与投影采用解析解与夹取（clamp）组合，避免不必要的分支与平方根。
  - 多边形面积与质心使用鞋带公式（O(n)），包含关系使用射线法（奇偶规则）。
- 集合与位操作：尽量减少拷贝与装箱；在热点路径避免临时对象。
- 兼容性：遵循 Kotlin 多平台目录结构（当前目标为 JVM desktop）。

## 设计思路与优势
- 性能优先：原始类型优化与批量操作减少拷贝与分配。
- 内存紧凑：位打包与自定义数据布局提升缓存友好度。
- API 实用：面向高频场景的插入/移动/批处理方法，减少样板代码。

## 贡献与维护
- PR 与 Issue 欢迎就以下方向贡献：
  - 新的数据结构与几何形状、附加算法（如凸包、布尔运算、KD-Tree 等）。
  - 跨平台测试与基准测试覆盖提升。
  - 文档与示例的完善（特别是几何 API 的语义与约定）。
- 许可协议与贡献指南请参考仓库根目录与相关模块说明。