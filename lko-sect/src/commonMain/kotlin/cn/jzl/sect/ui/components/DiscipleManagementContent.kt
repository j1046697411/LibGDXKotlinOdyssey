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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * 弟子管理内容组件
 * 
 * 显示弟子管理的详细信息，包括：
 * 1. 弟子统计（总数、在线状态、境界分布等）
 * 2. 弟子分类与筛选（按境界、资质、年龄等筛选）
 * 3. 弟子列表表格（显示弟子基本信息）
 * 4. 弟子详情（当前选中弟子的详细信息）
 * 5. 功能标签页（快速切换不同功能）
 */
@Composable
fun DiscipleManagementContent() {
    TerminalCard(
        title = "【👥 弟子管理】",
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // 弟子统计
            Text(
                text = "【弟子统计】",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 弟子总数：128/200 | 在线弟子：89/128 | AI活跃：112/128 | 可招收：72人", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 炼气期：85人 | 筑基期：35人 | 金丹期：8人 | 元婴期：0人", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 男弟子：78人 | 女弟子：50人 | 平均年龄：18岁 | 平均资质：中", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            TerminalDivider()

            // 弟子分类与筛选
            Text(
                text = "【弟子分类与筛选】",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 分类：【全部】【炼气期】【筑基期】【金丹期】【元婴期】【化神期】", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 筛选：【资质】【年龄】【性别】【属性】【状态】【贡献】", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 操作：[招收弟子] [批量管理] [批量培养] [刷新列表] [导出数据]", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
            TerminalDivider()

            // 弟子列表
            Text(
                text = "【弟子列表】",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // 弟子列表表格
            val discipleHeaders = listOf("弟子名称", "性别", "年龄", "境界", "资质", "属性", "状态", "贡献", "战斗力", "操作")
            val discipleRows = listOf(
                listOf("张无忌", "男", "18", "筑基中期", "中", "金", "在线", "2450", "1280", "[查看] [培养] [指派]"),
                listOf("赵敏", "女", "17", "炼气后期", "高", "火", "在线", "1850", "980", "[查看] [培养] [指派]"),
                listOf("周芷若", "女", "18", "筑基初期", "中", "水", "离线", "1650", "850", "[查看] [培养] [指派]"),
                listOf("杨逍", "男", "25", "金丹初期", "高", "风", "在线", "4250", "2450", "[查看] [培养] [指派]"),
                listOf("小昭", "女", "16", "炼气中期", "中", "木", "在线", "1250", "720", "[查看] [培养] [指派]")
            )
            TerminalTable(headers = discipleHeaders, rows = discipleRows, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(8.dp))
            TerminalDivider()

            // 弟子详情
            Text(
                text = "【弟子详情】",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 名称：张无忌 | 性别：男 | 年龄：18岁 | 境界：筑基中期 | 资质：中", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 属性：金 | 战斗力：1280 | 贡献：2450 | 状态：在线", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 功法：青灵心法 (木·灵级) | 玄水诀 (水·灵级) | 烈火掌 (火·凡级)", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 装备：玄铁剑 (一阶) | 玄铁护甲 (一阶) | 聚气戒指 (二阶)", color = MaterialTheme.colorScheme.onSurface, fontSize = 13.sp)
            }
            Row(modifier = Modifier.padding(4.dp)) {
                Text(text = "🔹 操作：[培养] [指派任务] [传授功法] [赐给装备] [逐出宗门]", color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
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
                    text = "▶ 弟子列表     ▶ 弟子详情     ▶ 培养管理     ▶ 任务指派     ▶ 功法传授     ▶ 装备管理     ▶ 招收弟子",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
