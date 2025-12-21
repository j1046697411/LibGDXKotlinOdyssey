package cn.jzl.sect.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 顶部导航栏组件
 */
@Composable
fun TopNavigationBar(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color.DarkGray)
            .padding(8.dp)
    ) {
        // 第一行：显示当前区域、天气、时间、游戏速度、全局搜索框
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "🔹 青云宗·千绝谷 ☀️ ⏳125年·3月·20日·14:30 [x1]",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                Text(
                    text = "🔍搜索：_",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "[详情▶]",
                    color = Color.Yellow,
                    fontSize = 16.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        // 第二行：显示宗门关键数据
        Text(
            text = "🔹 弟子总数：128 | 资源：灵石25000 | 设施：12 | 占领区域：5 | 声望：8500 | 状态：稳定",
            color = Color.LightGray,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        // 第三行：显示功能分类导航
        Text(
            text = "🔹 核心管理：🏠宗门·👥弟子·📦资源·🏗️设施 | 功能区域：🗺️地图·📋任务·📚功法 | 生产系统：🧪炼丹·⚒️炼器 | 系统：👤社交·⚙️设置",
            color = Color.LightGray,
            fontSize = 14.sp
        )
    }
}