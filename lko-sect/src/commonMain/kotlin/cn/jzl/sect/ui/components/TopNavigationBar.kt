package cn.jzl.sect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.jzl.di.instance
import cn.jzl.sect.currentWorld
import cn.jzl.sect.ecs.inventory.Amount
import cn.jzl.sect.ecs.time.DateSeason
import cn.jzl.sect.ecs.inventory.InventoryService
import cn.jzl.sect.ecs.core.Named
import cn.jzl.sect.ecs.resources.Resources
import cn.jzl.sect.ecs.time.Season
import cn.jzl.sect.ecs.time.TimeService
import cn.jzl.sect.ecs.time.Timer
import cn.jzl.sect.ecs.sect.SectService
import cn.jzl.sect.ui.observeComponent
import cn.jzl.sect.ui.observeEntity
import cn.jzl.sect.ui.service
import kotlin.time.Duration.Companion.seconds

/**
 * 顶部导航栏组件
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
        val resources = service<Resources>()
        val sect = sectService.playerSect
        val named by sect.observeComponent<Named>(Named("宗门名称"))
        val inventoryService = service<InventoryService>()
        val timeService = service<TimeService>()

        val entity by observeEntity(inventoryService.getAllItems(sect)) {
            inventoryService.getItem(sect, resources.spiritStonePrefab)
        }
        val amount by entity.observeComponent<Amount>(Amount(0))
        val timer by timeService.timeEntity.observeComponent(Timer(0.seconds))
        val dateSeason by timeService.timeEntity.observeComponent(DateSeason(Season.SPRING, 1, 3, 20))


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
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TerminalBadge(
                    text = "☀️ 晴朗",
                    color = MaterialTheme.colorScheme.secondary
                )
                TerminalBadge(
                    text = "⏳ ${dateSeason.year}年·${dateSeason.month}月·${dateSeason.day}日",
                    color = MaterialTheme.colorScheme.tertiary
                )
                TerminalBadge(
                    text = "[x1]",
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
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
            KeyValueDisplay(label = "弟子总数", value = "128", icon = "👥")
            KeyValueDisplay(label = "资源", value = "灵石${amount.value}", icon = "💰")
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
