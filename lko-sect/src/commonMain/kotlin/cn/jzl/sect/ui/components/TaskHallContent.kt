package cn.jzl.sect.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 任务大厅内容
@Composable
fun TaskHallContent() {
    TerminalCard(
        title = "【📋 任务大厅】",
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {

            // 任务筛选
        Text(
            text = "【任务筛选】",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 类型：【主线】【日常】【委托】【试炼】【宗门】【个人】", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 难度：【无要求】【炼气】【筑基】【金丹】【元婴】【化神】", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 状态：【可接受】【进行中】【已完成】【已放弃】【已过期】", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
        TerminalDivider()

        // 任务列表
        Text(
            text = "【任务列表】",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // 主线任务
        TerminalCard(
            title = "主线任务",
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "⭐ 【主线】前往千绝谷采集灵草 (筑基)",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text(text = "   要求：修为≥筑基中期 | 奖励：贡献500, 筑基丹×1", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                }
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text(text = "   状态：可接受 | [接受] [放弃] [查看详情]", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        TerminalDivider()

        // 日常任务
        TerminalCard(
            title = "日常任务",
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "◆ 【日常】宗门巡逻 (无要求)",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text(text = "   要求：无 | 奖励：贡献100, 灵石×50", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                }
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text(text = "   状态：可接受 | [接受] [放弃] [查看详情]", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        TerminalDivider()

        // 委托任务
        TerminalCard(
            title = "委托任务",
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "▲ 【委托】帮李长老寻找丢失的玉简 (筑基)",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text(text = "   要求：神识≥200 | 奖励：贡献300, 低级功法×1", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                }
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text(text = "   状态：进行中 | [放弃] [查看详情] [加速]", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        TerminalDivider()

        // 试炼任务
        TerminalCard(
            title = "试炼任务",
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = "▶ 【试炼】黑风林妖兽猎杀 (炼气)",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text(text = "   要求：炼气期以上 | 奖励：贡献200, 妖兽材料×5", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                }
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text(text = "   状态：已完成 | [领取奖励]", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        TerminalDivider()

        // 功能标签页
            Text(
                text = "【功能标签页】",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(modifier = Modifier.padding(4.dp)) {
                Text(
                    text = "▶ 任务列表     ▶ 任务详情     ▶ 任务日志     ▶ 任务统计     ▶ 任务建议",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
