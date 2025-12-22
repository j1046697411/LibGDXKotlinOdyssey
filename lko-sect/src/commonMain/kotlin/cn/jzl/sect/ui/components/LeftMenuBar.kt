package cn.jzl.sect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cn.jzl.sect.ui.MenuOption

@Composable
fun LeftMenuBar(
    modifier: Modifier,
    currentMenu: MenuOption,
    onMenuChange: (MenuOption) -> Unit
) {
    val menuColor = Color(0xFFF5F5F5)
    val borderColor = Color(0xFFBDBDBD)
    val textColor = Color(0xFF212121)
    val sectionTitleColor = Color(0xFF2196F3)
    val selectedItemColor = Color(0xFFFF9800)
    val hoverColor = Color(0xFFE0F7FA)
    
    // 菜单选项项点击处理
    @Composable
    fun MenuItem(text: String, menuOption: MenuOption) {
        val isSelected = currentMenu == menuOption
        Text(
            text = text,
            color = if (isSelected) selectedItemColor else textColor,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier
                .padding(4.dp)
                .clickable { onMenuChange(menuOption) }
                .background(if (isSelected) hoverColor.copy(alpha = 0.5f) else Color.Transparent)
                .padding(4.dp)
        )
    }
    
    Column(
        modifier = modifier
            .background(menuColor)
            .border(1.dp, borderColor)
            .padding(8.dp)
            .verticalScroll(androidx.compose.foundation.rememberScrollState())
    ) {
        // 核心管理
        Text(
            text = "【核心管理】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        MenuItem(text = "1🏠宗门总览", menuOption = MenuOption.ZONGMEN_OVERVIEW)
        MenuItem(text = "2👥弟子管理", menuOption = MenuOption.DISCIPLE_MANAGEMENT)
        MenuItem(text = "3📦资源管理", menuOption = MenuOption.RESOURCE_MANAGEMENT)
        MenuItem(text = "4🏗️设施建设", menuOption = MenuOption.FACILITY_CONSTRUCTION)
        
        // 功能区域
        Text(
            text = "\n【功能区域】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        MenuItem(text = "5🗺️地图探索", menuOption = MenuOption.MAP_EXPLORATION)
        MenuItem(text = "6📋任务大厅", menuOption = MenuOption.TASK_HALL)
        MenuItem(text = "7📚功法堂", menuOption = MenuOption.TECHNIQUE_HALL)
        
        // 生产系统
        Text(
            text = "\n【生产系统】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        MenuItem(text = "8🧪炼丹房", menuOption = MenuOption.ALCHEMY_ROOM)
        MenuItem(text = "9⚒️炼器房", menuOption = MenuOption.FORGING_ROOM)
        
        // 系统
        Text(
            text = "\n【系统】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        MenuItem(text = "0👤社交", menuOption = MenuOption.SOCIAL)
        MenuItem(text = "S⚙️设置", menuOption = MenuOption.SETTINGS)
    }
}