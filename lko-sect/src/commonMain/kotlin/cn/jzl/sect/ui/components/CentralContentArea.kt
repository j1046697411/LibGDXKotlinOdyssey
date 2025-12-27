package cn.jzl.sect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cn.jzl.sect.ui.MenuOption

/**
 * 中央内容区域组件
 * 
 * 根据当前选中的菜单选项显示不同的内容组件
 * 
 * @param modifier 修饰符
 * @param currentMenu 当前选中的菜单选项
 */
@Composable
fun CentralContentArea(modifier: Modifier, currentMenu: MenuOption) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
    ) {
        // 根据当前菜单显示不同内容
        when (currentMenu) {
            MenuOption.ZONGMEN_OVERVIEW -> {
                SectOverviewContent()
            }
            MenuOption.DISCIPLE_MANAGEMENT -> {
                DiscipleManagementContent()
            }
            MenuOption.RESOURCE_MANAGEMENT -> {
                InventoryManagementContent()
            }
            MenuOption.FACILITY_CONSTRUCTION -> {
                FacilityConstructionContent()
            }
            MenuOption.MAP_EXPLORATION -> {
                MapExplorationContent()
            }
            MenuOption.TASK_HALL -> {
                TaskHallContent()
            }
            MenuOption.TECHNIQUE_HALL -> {
                TechniqueHallContent()
            }
            MenuOption.ALCHEMY_ROOM -> {
                AlchemyRoomContent()
            }
            MenuOption.FORGING_ROOM -> {
                ForgingRoomContent()
            }
            MenuOption.SOCIAL -> {
                SocialContent()
            }
            MenuOption.SETTINGS -> {
                SettingsContent()
            }
        }
    }
}
