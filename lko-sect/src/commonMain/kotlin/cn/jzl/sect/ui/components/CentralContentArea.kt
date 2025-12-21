package cn.jzl.sect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cn.jzl.sect.ui.MenuOption

@Composable
fun CentralContentArea(modifier: Modifier, currentMenu: MenuOption) {
    val backgroundColor = Color(0xFFFFFFFF)
    val borderColor = Color(0xFFBDBDBD)
    val textColor = Color(0xFF212121)
    val sectionTitleColor = Color(0xFF2196F3)

    Column(
        modifier = modifier
            .background(backgroundColor)
            .border(1.dp, borderColor)
            .padding(12.dp)
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
