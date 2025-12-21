package cn.jzl.sect.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 底部状态栏组件
 */
@Composable
fun BottomStatusBar(modifier: Modifier) {
    val backgroundColor = Color(0xFF263238)
    val textColor = Color(0xFFECEFF1)
    val highlightColor = Color(0xFFFFEB3B)
    val warningColor = Color(0xFFFF9800)
    val infoColor = Color(0xFF4FC3F7)
    
    Column(
        modifier = modifier
            .background(backgroundColor)
            .padding(8.dp)
    ) {
        // 第一行：通知和基本状态
        Text(
            text = "🔔新任务(3) | 💬弟子传讯(5) | 📈宗门事件 | 自动存档:开 | F1帮助 | ESC菜单 | 当前模式：任务大厅 | 游戏速度：正常×1 | 运行时间：8小时30分钟",
            color = textColor,
            fontSize = 12.sp
        )
        
        // 第二行：快捷键提示
        Text(
            text = "🔹 快捷键提示：Shift+1-9切换功能 | /搜索 | F1帮助 | S设置 | ESC菜单 | 空格跳过 | A自动战斗 | Tab切换标签 | 上下箭头导航 | Enter确认 | Ctrl+C复制 | Ctrl+V粘贴",
            color = highlightColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        
        // 第三行：消息提示
        Text(
            text = "🔹 消息提示：1. 弟子张无忌已完成巡逻任务 | 2. 千绝谷灵草成熟 | 3. 玄水阁使者来访 | 4. 血魔宗在附近活动 | 5. 新弟子报名参加宗门",
            color = infoColor,
            fontSize = 12.sp
        )
        
        // 第四行：系统状态
        Text(
            text = "🔹 系统状态：内存使用：1.2GB | CPU使用率：15% | 网络：正常 | 存档：自动 (上次：5分钟前) | 日志：正常 | 音效：开启 | 音乐：开启",
            color = warningColor,
            fontSize = 12.sp
        )
    }
}