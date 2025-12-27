package cn.jzl.sect.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// 社交内容
@Composable
fun SocialContent() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 标题
        Text(
            text = "【👤 社交】",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // 宗门关系
        TerminalCard(title = "宗门关系") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🔹 友好宗门：2个 | 敌对宗门：2个 | 中立宗门：5个")
                Text(
                    "✅ 玄水宗 | 友好度：85 | 关系：盟友 | 最近互动：2小时前 | [查看详情] [派遣使者] [礼物]",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "✅ 青风谷 | 友好度：72 | 关系：友好 | 最近互动：1天前 | [查看详情] [派遣使者] [礼物]",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "❌ 血魔宗 | 友好度：-65 | 关系：敌对 | 最近互动：3天前 | [查看详情] [宣战] [议和]",
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    "❌ 鬼阴门 | 友好度：-48 | 关系：敌对 | 最近互动：5天前 | [查看详情] [宣战] [议和]",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        // 弟子社交
        TerminalCard(title = "弟子社交") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("🔹 社交活动：12项 | 师徒关系：8对 | 好友关系：45对 | 敌对关系：12对")
                Text("🔹 筛选：【全部】【师徒】【好友】【敌对】【最近互动】【社交等级】")
                Text("🔹 操作：[发起社交] [批量互动] [刷新列表] [导出数据]")
            }
        }

        // 消息通知
        TerminalCard(title = "消息通知") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "🔔 玄水宗使者来访 | 2小时前 | [查看] [回复] [忽略]",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "🔔 弟子张无忌突破筑基中期 | 5小时前 | [查看] [祝贺] [奖励]",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "🔔 青风谷赠送灵草×100 | 1天前 | [查看] [感谢] [回礼]",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 社交活动
        TerminalCard(title = "社交活动") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "🎉 宗门大比 | 时间：3天后 | 参与人数：85人 | 奖励：筑基丹×5 | [报名] [查看详情] [取消]",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "🔥 联合探索 | 时间：5天后 | 参与宗门：3个 | 目标：青灵山脉 | [报名] [查看详情] [取消]",
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    "📚 功法交流 | 时间：7天后 | 主讲：林玄风 | 主题：木系功法 | [报名] [查看详情] [取消]",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // 功能标签页
        TerminalCard(title = "功能标签页") {
            Text(
                text = "▶ 宗门关系     ▶ 弟子社交     ▶ 消息通知     ▶ 社交活动     ▶ 互动记录",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
