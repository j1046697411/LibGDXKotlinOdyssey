## 目标

* 完成 `lko-ecs3` 中 `Table` 的列式存储实现，用于 `Archetype` 的数据承载关系（`HOLDS_DATA`）

* 提供增删实体行、按关系读写数据、稠密布局（swap-remove）操作

* 与现有 `EntityType`/`Archetype` 的关系索引保持一致（二分查找）

## 技术要点

* 仅为 `HOLDS_DATA` 的关系建立列，列类型为 `ObjectFastList<Any>`（后续可演进到泛型/特定类型）

* 行表示一个实体在该原型中的位置，所有列的行数始终一致

* 关系到列索引通过 `dataHoldingType.indexOf(relation)`，保持稳定顺序与高效查找

* 删除行采用 swap-remove，维持稠密数组并 O(1) 删除

## 具体实现

1. 重构 `Table` 构造

   * 入参改为 `archetype: Archetype` 或使用其 `dataHoldingType`

   * 初始化 `columns = Array<ObjectFastList<Any>>(dataHoldingType.size) { ObjectFastList() }`
2. 列索引映射

   * `private fun colIndex(relation: Relation): Int` 使用 `dataHoldingType.indexOf(relation)`，若为 -1 抛错（非数据承载关系）
3. 行管理 API

   * `fun addRow(): Int`：在所有列 `ensureCapacity(row+1, null)` 并返回新行索引

   * `fun removeRow(row: Int)`：将最后一行数据交换至 `row`，并在所有列移除末尾项

   * `val rowCount: Int`：当前行数量（任取一列的 size）
4. 数据读写 API

   * `fun set(relation: Relation, row: Int, value: Any)`：按列索引写入指定行

   * `fun get(relation: Relation, row: Int): Any?`：按列索引读取指定行

   * 可选：提供 `ComponentId`/`Relation.withRole(HOLDS_DATA)` 辅助方法与重载
5. 校验与异常

   * 非 `HOLDS_DATA` 的关系操作直接 `IllegalArgumentException`

   * 行越界进行边界检查
6. 与 `Archetype` 关联（最小接入）

   * 在 `Archetype` 内部惰性创建 `table: Table`

   * 暂不修改 `World` 迁移逻辑（保持独立），后续可在实体迁移时进行数据搬移

## 测试用例

* 在 `commonTest` 新增 `TableTest`

  * 创建 `World` 与 `Archetype`，构造一个携带数据的关系（`RELATION | HOLDS_DATA`）

  * `addRow` 后对该关系列 `set/get` 验证

  * 多行写入后执行 `removeRow` 验证 swap-remove 正确性

  * 非数据承载关系调用 `set/get` 触发异常

## 变更范围

* 修改 `lko-ecs3/src/commonMain/kotlin/cn/jzl/ecs/Relations.kt` 中 `Table` 定义

* 在 `Archetype` 增加惰性 `table` 引用（最小改动）

* 新增 `commonTest`：`TableTest`

## 后续扩展（不在本次交付）

* 为每列提供类型化泛型封装，避免 `Any`

* 在 `World` 的原型迁移中集成行搬移（保持数据与实体一致）

* 增加批量读写、列视图（按角色过滤）与迭代器

