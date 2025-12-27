package cn.jzl.sect.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// 设置内容
@Composable
fun SettingsContent() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 标题
        Text(
            text = "【⚙️ 设置】",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // 游戏设置
        TerminalCard(title = "游戏设置") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🔹 游戏速度：【正常×1】【快速×2】【极速×3】 | 当前：正常×1")
                Text("🔹 自动存档：【开启】【关闭】 | 当前：开启 | 存档间隔：30分钟")
                Text("🔹 战斗模式：【自动】【手动】【半自动】 | 当前：自动")
                Text("🔹 显示效果：【高级】【中级】【低级】 | 当前：中级")
            }
        }

        // 界面设置
        TerminalCard(title = "界面设置") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🔹 语言：【中文】【英文】【日文】 | 当前：中文")
                Text("🔹 分辨率：【1920×1080】【1280×720】【1024×768】 | 当前：1920×1080")
                Text("🔹 UI缩放：【100%】【125%】【150%】 | 当前：100%")
                Text("🔹 显示FPS：【开启】【关闭】 | 当前：关闭")
            }
        }

        // 音效设置
        TerminalCard(title = "音效设置") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🔹 背景音乐：【开启】【关闭】 | 当前：开启 | 音量：75%")
                Text("🔹 音效：【开启】【关闭】 | 当前：开启 | 音量：80%")
                Text("🔹 语音：【开启】【关闭】 | 当前：关闭 | 音量：0%")
            }
        }

        // 账号设置
        TerminalCard(title = "账号设置") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🔹 账号：linxuanfeng@qingyunzong.com | 角色：林玄风 (金丹中期)")
                Text("🔹 绑定邮箱：已绑定 | 绑定手机：未绑定 | 安全等级：中等")
                Text(
                    "🔹 操作：[修改密码] [绑定手机] [解绑邮箱] [退出登录]",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 功能标签页
        TerminalCard(title = "功能标签页") {
            Text(
                text = "▶ 游戏设置     ▶ 界面设置     ▶ 音效设置     ▶ 账号设置     ▶ 关于游戏",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
