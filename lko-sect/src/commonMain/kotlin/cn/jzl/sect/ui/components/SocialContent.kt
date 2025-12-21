package cn.jzl.sect.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 社交内容
@Composable
fun SocialContent() {
    val sectionTitleColor = Color(0xFF2196F3)
    val textColor = Color(0xFF212121)
    val highlightColor = Color(0xFFFF9800)
    val borderColor = Color(0xFFBDBDBD)
    val warningColor = Color(0xFFFFC107)
    val successColor = Color(0xFF4CAF50)

    Column {
        Text(
            text = "【👤 社交】",
            color = sectionTitleColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 宗门关系
        Text(
            text = "【宗门关系】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 友好宗门：2个 | 敌对宗门：2个 | 中立宗门：5个", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = "✅ 玄水宗 | 友好度：85 | 关系：盟友 | 最近互动：2小时前 | [查看详情] [派遣使者] [礼物]", color = successColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = "✅ 青风谷 | 友好度：72 | 关系：友好 | 最近互动：1天前 | [查看详情] [派遣使者] [礼物]", color = successColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = "❌ 血魔宗 | 友好度：-65 | 关系：敌对 | 最近互动：3天前 | [查看详情] [宣战] [议和]", color = warningColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = "❌ 鬼阴门 | 友好度：-48 | 关系：敌对 | 最近互动：5天前 | [查看详情] [宣战] [议和]", color = warningColor, fontSize = 13.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 弟子社交
        Text(
            text = "【弟子社交】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 社交活动：12项 | 师徒关系：8对 | 好友关系：45对 | 敌对关系：12对", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 筛选：【全部】【师徒】【好友】【敌对】【最近互动】【社交等级】", color = textColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(4.dp)) {
            Text(text = "🔹 操作：[发起社交] [批量互动] [刷新列表] [导出数据]", color = textColor, fontSize = 13.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 消息通知
        Text(
            text = "【消息通知】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = "🔔 玄水宗使者来访 | 2小时前 | [查看] [回复] [忽略]", color = highlightColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = "🔔 弟子张无忌突破筑基中期 | 5小时前 | [查看] [祝贺] [奖励]", color = highlightColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = "🔔 青风谷赠送灵草×100 | 1天前 | [查看] [感谢] [回礼]", color = highlightColor, fontSize = 13.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 社交活动
        Text(
            text = "【社交活动】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = "🎉 宗门大比 | 时间：3天后 | 参与人数：85人 | 奖励：筑基丹×5 | [报名] [查看详情] [取消]", color = highlightColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = "🔥 联合探索 | 时间：5天后 | 参与宗门：3个 | 目标：青灵山脉 | [报名] [查看详情] [取消]", color = highlightColor, fontSize = 13.sp)
        }
        Row(modifier = Modifier.padding(8.dp)) {
            Text(text = "📚 功法交流 | 时间：7天后 | 主讲：林玄风 | 主题：木系功法 | [报名] [查看详情] [取消]", color = highlightColor, fontSize = 13.sp)
        }

        // 分隔线
        Text(
            text = "─".repeat(95),
            color = borderColor,
            fontSize = 12.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 功能标签页
        Text(
            text = "【功能标签页】",
            color = sectionTitleColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Row(modifier = Modifier.padding(4.dp)) {
            Text(
                text = "▶ 宗门关系     ▶ 弟子社交     ▶ 消息通知     ▶ 社交活动     ▶ 互动记录",
                color = highlightColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
