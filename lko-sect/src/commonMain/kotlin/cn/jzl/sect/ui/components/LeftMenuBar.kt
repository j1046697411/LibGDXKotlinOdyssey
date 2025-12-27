package cn.jzl.sect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.jzl.sect.ui.MenuOption

@Composable
fun LeftMenuBar(
    modifier: Modifier,
    currentMenu: MenuOption,
    onMenuChange: (MenuOption) -> Unit
) {
    // 菜单选项点击处理
    @Composable
    fun MenuItem(text: String, shortcut: String, menuOption: MenuOption, hasNotification: Boolean = false) {
        val isSelected = currentMenu == menuOption
        
        val backgroundColor = if (isSelected) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        } else {
            Color.Transparent
        }
        
        val borderColor = if (isSelected) {
            MaterialTheme.colorScheme.primary
        } else {
            Color.Transparent
        }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .background(backgroundColor)
                .border(1.dp, borderColor, MaterialTheme.shapes.small)
                .clickable(onClick = { onMenuChange(menuOption) })
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    
                    if (hasNotification) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(MaterialTheme.shapes.extraSmall)
                                .background(MaterialTheme.colorScheme.error)
                        )
                    }
                }
                
                Text(
                    text = shortcut,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                )
            }
        }
    }
    
    // 菜单分类
    @Composable
    fun MenuCategory(title: String, content: @Composable () -> Unit) {
        Column {
            Text(
                text = "【${title}】",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)
            )
            content()
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
    ) {
        // 面板标题
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📋 导航菜单",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        TerminalDivider(modifier = Modifier.padding(vertical = 8.dp))
        
        // 核心管理
        MenuCategory(title = "核心管理") {
            MenuItem(text = "🏠 宗门总览", shortcut = "1", menuOption = MenuOption.ZONGMEN_OVERVIEW)
            MenuItem(text = "👥 弟子管理", shortcut = "2", menuOption = MenuOption.DISCIPLE_MANAGEMENT)
            MenuItem(text = "📦 资源管理", shortcut = "3", menuOption = MenuOption.RESOURCE_MANAGEMENT)
            MenuItem(text = "🏗️ 设施建设", shortcut = "4", menuOption = MenuOption.FACILITY_CONSTRUCTION)
        }
        
        // 功能区域
        MenuCategory(title = "功能区域") {
            MenuItem(text = "🗺️ 地图探索", shortcut = "5", menuOption = MenuOption.MAP_EXPLORATION)
            MenuItem(text = "📋 任务大厅", shortcut = "6", menuOption = MenuOption.TASK_HALL, hasNotification = true)
            MenuItem(text = "📚 功法堂", shortcut = "7", menuOption = MenuOption.TECHNIQUE_HALL)
        }
        
        // 生产系统
        MenuCategory(title = "生产系统") {
            MenuItem(text = "🧪 炼丹房", shortcut = "8", menuOption = MenuOption.ALCHEMY_ROOM)
            MenuItem(text = "⚒️ 炼器房", shortcut = "9", menuOption = MenuOption.FORGING_ROOM)
        }
        
        // 系统
        MenuCategory(title = "系统") {
            MenuItem(text = "👤 社交", shortcut = "0", menuOption = MenuOption.SOCIAL)
            MenuItem(text = "⚙️ 设置", shortcut = "S", menuOption = MenuOption.SETTINGS)
        }
    }
}