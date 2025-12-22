package cn.jzl.sect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cn.jzl.sect.ui.viewmodel.SectViewModel

/**
 * 顶部导航栏组件
 */
@Composable
fun TopNavigationBar(modifier: Modifier) {
    val backgroundColor = Color(0xFF2C3E50)
    val textColor = Color.White
    val highlightColor = Color(0xFFFFA500)
    val sectViewModel = viewModel<SectViewModel>()
    Column(
        modifier = modifier
            .background(backgroundColor)
            .padding(8.dp)
    ) {
        // 第一行：显示当前区域、天气、时间、游戏速度、全局搜索框
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "🏔️ 青云宗·千绝谷 ☀️ ⏳125年·3月·20日·14:30 [x1]",
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "🔍搜索：_ [详情▶]",
                color = highlightColor,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // 第二行：显示宗门关键数据
        Text(
            text = "👥 弟子总数：128 | 💎 资源：灵石25000 | 🏗️ 设施：12 | 🗺️ 占领区域：5 | 🏆 声望：8500 | ✅ 状态：稳定",
            color = textColor,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 第三行：显示功能分类导航
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // 核心管理
            Text(
                text = "🏠宗门·👥弟子·📦资源·🏗️设施",
                color = highlightColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            // 功能区域
            Text(
                text = "🗺️地图·📋任务·📚功法",
                color = textColor,
                fontSize = 12.sp
            )

            // 生产系统
            Text(
                text = "🧪炼丹·⚒️炼器",
                color = textColor,
                fontSize = 12.sp
            )

            // 系统
            Text(
                text = "👤社交·⚙️设置",
                color = textColor,
                fontSize = 12.sp
            )
        }
    }
}
