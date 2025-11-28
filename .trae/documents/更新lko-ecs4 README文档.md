# 更新lko-ecs4 README文档

## 更新内容

1. **修正World类位置**：将World类的位置从ECS.kt和EntityId.kt改为World.kt
2. **统一术语**：将EntityId改为Entity，保持与代码一致
3. **更新world函数默认绑定**：说明world()函数已经默认绑定了所有必要服务
4. **更新组件数据访问API**：说明已经实现了Accessor相关接口
5. **更新查询系统**：说明已经实现了QueryService和相关查询API
6. **更新事件机制**：说明已经实现了ObserveService和观察者API
7. **添加ShadedComponentService**：在核心服务中添加ShadedComponentService的说明
8. **更新未完成功能列表**：移除已完成的功能，保留未完成的功能
9. **更新快速开始示例**：使用最新的API和术语

## 更新步骤

1. 修正文档中的术语和类位置
2. 更新核心概念和服务部分
3. 更新快速开始示例
4. 更新API与使用建议
5. 更新未完成功能列表
6. 确保文档与实际代码实现一致

## 预期效果

通过更新README文档，使其与实际项目实现保持一致，帮助开发者更好地理解和使用lko-ecs4库。