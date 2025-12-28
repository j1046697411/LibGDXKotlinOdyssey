package cn.jzl.sect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.jzl.di.instance
import cn.jzl.ecs.Entity
import cn.jzl.ecs.query.Query
import cn.jzl.ecs.query.count
import cn.jzl.sect.currentWorld
import cn.jzl.sect.ecs.attribute.Attribute
import cn.jzl.sect.ecs.attribute.AttributeService
import cn.jzl.sect.ecs.attribute.AttributeValue
import cn.jzl.sect.ecs.attribute.SectAttributes
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.inventory.Amount
import cn.jzl.sect.ecs.inventory.InventoryService
import cn.jzl.sect.ecs.resources.ResourceIcon
import cn.jzl.sect.ecs.resources.Resources
import cn.jzl.sect.ecs.sect.SectService
import cn.jzl.sect.ecs.time.DateSeason
import cn.jzl.sect.ecs.time.TimeService
import cn.jzl.sect.ecs.time.TimeSpeed
import cn.jzl.sect.ui.observeComponent
import cn.jzl.sect.ui.observeEntity
import cn.jzl.sect.ui.observeEntityList
import cn.jzl.sect.ui.observeRelation
import cn.jzl.sect.ui.observeState
import cn.jzl.sect.ui.service

/**
 * 顶部导航栏组件
 *
 * 显示游戏的核心状态信息，包括：
 * 1. 宗门名称和位置
 * 2. 天气、日期和时间流速
 * 3. 搜索框和详情按钮
 * 4. 关键数据统计（弟子总数、资源、设施等）
 * 5. 功能分类导航芯片
 *
 * @param modifier 修饰符
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TopNavigationBar(modifier: Modifier) {
    var searchText by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }

    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        val sectService by currentWorld.di.instance<SectService>()
        val attributes = service<SectAttributes>()
        val sect = sectService.playerSect
        val named by sect.observeComponent<Named>()
        val entities = observeEntityList(sectService.getSectMembers(sect))
        val attributeService = service<AttributeService>()

        LazyColumn {
            items(entities) {
                val cultivation by it.observeRelation<AttributeValue?>(attributes.cultivation)
                val maxCultivation = attributeService.getTotalAttributeValue(it, attributes.maxCultivation)
                Text("${it.id} ${cultivation?.value ?: 0}/${maxCultivation.value}")
            }
        }
        // 第一行：核心状态信息
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🏔️ ${named.name}·千绝谷",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
            TimerView()
            Spacer(modifier = Modifier.weight(1f))

            TerminalTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier.width(200.dp),
                placeholder = "搜索...",
                leadingIcon = "🔍"
            )

            TerminalButton(
                onClick = { /* TODO: 显示详情 */ },
                label = "详情▶",
                variant = ButtonVariant.OUTLINED
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 第二行：关键数据
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SectMemberCountView(sect)
            ResourcesView(sect)
            KeyValueDisplay("灵石", "12500", "💎")
            KeyValueDisplay(label = "设施", value = "12", icon = "🏗️")
            KeyValueDisplay(label = "占领区域", value = "5", icon = "🗺️")
            KeyValueDisplay(label = "声望", value = "8500", icon = "✨")
            KeyValueDisplay(label = "状态", value = "稳定", icon = "✅")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 第三行：功能分类导航
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "核心管理：",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                NavigationChip(icon = "🏠", text = "宗门")
                NavigationChip(icon = "👥", text = "弟子")
                NavigationChip(icon = "📦", text = "资源")
                NavigationChip(icon = "🏗️", text = "设施")

                Text(
                    text = "功能区域：",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp)
                )

                NavigationChip(icon = "🗺️", text = "地图")
                NavigationChip(icon = "📋", text = "任务")
                NavigationChip(icon = "📚", text = "功法")

                Text(
                    text = "生产系统：",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp)
                )

                NavigationChip(icon = "🧪", text = "炼丹")
                NavigationChip(icon = "⚒️", text = "炼器")

                Text(
                    text = "系统：",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 8.dp)
                )

                NavigationChip(icon = "👤", text = "社交")
                NavigationChip(icon = "⚙️", text = "设置")
            }
        }
    }
}

@Composable
fun TimerView() {
    val timeService = service<TimeService>()
    val timeSpeed by timeService.timeEntity.observeComponent<TimeSpeed>()
    val dateSeason by timeService.timeEntity.observeComponent<DateSeason>()
    val speedText = when (timeSpeed) {
        TimeSpeed.Paused -> "暂停"
        TimeSpeed.Normal -> "正常"
        TimeSpeed.Fast -> "2倍速"
        TimeSpeed.VeryFast -> "5倍速"
        else -> "10倍速"
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TerminalBadge(
            text = "☀️ 晴朗",
            color = MaterialTheme.colorScheme.secondary
        )
        TerminalBadge(
            text = "⏳ ${dateSeason.year}年·${dateSeason.month}月·${dateSeason.day}日",
            color = MaterialTheme.colorScheme.tertiary
        )
        TerminalBadge(
            text = "[$speedText]",
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun ResourcesView(sect: Entity) {
    val inventoryService = service<InventoryService>()
    val resources = service<Resources>()
    val query = inventoryService.getAllItems(sect)
    ResourceView(inventoryService, query, sect, resources.spiritStonePrefab)
//    ResourceView(inventoryService, query, sect, resources.stonePrefab)
//    ResourceView(inventoryService, query, sect, resources.foodPrefab)
//    ResourceView(inventoryService, query, sect, resources.clothPrefab)
//    ResourceView(inventoryService, query, sect, resources.woodPrefab)
//    ResourceView(inventoryService, query, sect, resources.medicineHerbPrefab)
//    ResourceView(inventoryService, query, sect, resources.ordinaryPeoplePrefab)
//    ResourceView(inventoryService, query, sect, resources.sectServantsPrefab)
//    ResourceView(inventoryService, query, sect, resources.outerDisciplesPrefab)
}

@Composable
fun ResourceView(inventoryService: InventoryService, query: Query<*>, sect: Entity, resource: Entity) {
    val itemEntity by observeEntity(query, sect, resource, inventoryService) {
        inventoryService.getItem(sect, resource)
    }
    val entity = itemEntity ?: resource
    println("entity $entity")
    val amount by entity.observeComponent<Amount?>()
    val named by entity.observeComponent<Named>()
    val resourceIcon by entity.observeComponent<ResourceIcon>()
    KeyValueDisplay(named.name, "${amount?.value ?: 0}", resourceIcon.icon)
}

@Composable
fun SectMemberCountView(sect: Entity) {
    val sectService = service<SectService>()
    val sectMembers = remember(sect, sectService) { sectService.getSectMembers(sect) }
    val count by sectMembers.observeState { it.count() }
    KeyValueDisplay("弟子总数", "$count", "👥")
}