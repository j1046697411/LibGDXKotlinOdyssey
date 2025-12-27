package cn.jzl.sect.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * 终端风格卡片组件
 * 
 * 用于显示带有标题和边框的内容卡片，适用于终端风格的UI界面
 * 
 * @param modifier 修饰符
 * @param title 卡片标题，显示在卡片顶部
 * @param borderColor 卡片边框颜色，默认使用主题的主色调
 * @param contentPadding 卡片内容的内边距
 * @param content 卡片内容的Composable函数
 */
@Composable
fun TerminalCard(
    modifier: Modifier = Modifier,
    title: String,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            if (title.isNotEmpty()) {
                Text(
                    text = "【$title】",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = borderColor,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            content()
        }
    }
}

/**
 * 终端风格按钮组件
 * 
 * 支持三种按钮变体，适用于终端风格的UI界面
 * 
 * @param onClick 点击事件回调
 * @param modifier 修饰符
 * @param enabled 是否启用按钮
 * @param label 按钮文本
 * @param shortcut 快捷键提示，显示在按钮文本右侧
 * @param variant 按钮变体，默认为OUTLINED
 */
@Composable
fun TerminalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    label: String,
    shortcut: String? = null,
    variant: ButtonVariant = ButtonVariant.OUTLINED
) {
    val colors = when (variant) {
        ButtonVariant.FILLED -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
        ButtonVariant.OUTLINED -> ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
        ButtonVariant.TEXT -> ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary
        )
    }
    
    val shape = RoundedCornerShape(4.dp)
    
    when (variant) {
        ButtonVariant.FILLED -> Button(
            onClick = onClick,
            modifier = modifier.height(32.dp),
            enabled = enabled,
            colors = colors,
            shape = shape
        ) {
            Text(label)
            shortcut?.let {
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "($it)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }
        ButtonVariant.OUTLINED -> OutlinedButton(
            onClick = onClick,
            modifier = modifier.height(32.dp),
            enabled = enabled,
            colors = colors,
            shape = shape
        ) {
            Text(label)
            shortcut?.let {
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "($it)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        ButtonVariant.TEXT -> TextButton(
            onClick = onClick,
            modifier = modifier.height(32.dp),
            enabled = enabled,
            colors = colors,
            shape = shape
        ) {
            Text(label)
            shortcut?.let {
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "($it)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 按钮变体枚举
 * 
 * - FILLED: 填充式按钮，使用主题主色调作为背景
 * - OUTLINED: 轮廓式按钮，只有边框，没有背景
 * - TEXT: 文本式按钮，只有文本，没有边框和背景
 */
enum class ButtonVariant {
    FILLED, OUTLINED, TEXT
}

/**
 * 终端风格分隔线组件
 * 
 * 用于分隔不同内容区域，适用于终端风格的UI界面
 * 
 * @param modifier 修饰符
 * @param thickness 分隔线厚度，单位为dp
 * @param color 分隔线颜色，默认使用主题的轮廓变体颜色
 */
@Composable
fun TerminalDivider(
    modifier: Modifier = Modifier,
    thickness: Int = 1,
    color: Color = MaterialTheme.colorScheme.outlineVariant
) {
    Divider(
        modifier = modifier,
        thickness = thickness.dp,
        color = color
    )
}

/**
 * 终端风格徽章组件
 * 
 * 用于显示小型状态或标签信息，适用于终端风格的UI界面
 * 
 * @param text 徽章文本
 * @param color 徽章颜色，默认使用主题的主色调
 * @param modifier 修饰符
 */
@Composable
fun TerminalBadge(
    text: String,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.2f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

/**
 * 终端风格芯片组件
 * 
 * 用于显示可选择的标签或选项，适用于终端风格的UI界面
 * 
 * @param text 芯片文本
 * @param selected 是否已选中
 * @param onClick 点击事件回调
 * @param modifier 修饰符
 * @param icon 可选的图标文本
 */
@Composable
fun TerminalChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: String? = null
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    } else {
        Color.Transparent
    }
    
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }
    
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            icon?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

/**
 * 终端风格进度条组件
 * 
 * 用于显示任务或操作的进度，适用于终端风格的UI界面
 * 
 * @param progress 进度值，范围为0.0到1.0
 * @param modifier 修饰符
 * @param color 进度条颜色，默认使用主题的主色调
 * @param backgroundColor 进度条背景颜色，默认使用主题的表面变体颜色
 */
@Composable
fun TerminalProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
    }
}

/**
 * 终端风格文本输入框组件
 * 
 * 用于接收用户输入，适用于终端风格的UI界面
 * 
 * @param value 当前输入值
 * @param onValueChange 输入值变化回调
 * @param modifier 修饰符
 * @param label 可选的标签文本
 * @param placeholder 可选的占位符文本
 * @param leadingIcon 可选的前置图标文本
 */
@Composable
fun TerminalTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    leadingIcon: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.height(40.dp),
        label = label?.let { 
            { Text(
                text = it,
                style = MaterialTheme.typography.labelMedium
            ) }
        },
        placeholder = placeholder?.let {
            { Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            ) }
        },
        leadingIcon = leadingIcon?.let {
            { Text(
                text = it,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            ) }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outlineVariant,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground
        ),
        shape = RoundedCornerShape(4.dp),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            fontFamily = FontFamily.Monospace
        ),
        singleLine = true
    )
}

/**
 * 终端风格标签行组件
 * 
 * 用于切换不同的标签页，适用于终端风格的UI界面
 * 
 * @param selectedTabIndex 当前选中的标签索引
 * @param modifier 修饰符
 * @param tabs 标签文本列表
 * @param onTabSelected 标签选择回调
 */
@Composable
fun TerminalTabRow(
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit
) {
    TabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        divider = {
            TerminalDivider()
        },
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                height = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(
                        text = "▶ $title",
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 键值对显示组件
 * 
 * 用于显示带有图标和标签的数值信息，适用于终端风格的UI界面
 * 
 * @param label 标签文本
 * @param value 数值文本
 * @param icon 图标文本
 */
@Composable
fun KeyValueDisplay(label: String, value: String, icon: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = icon,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "$label：",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * 导航芯片组件
 * 
 * 用于显示导航选项，适用于终端风格的UI界面
 * 
 * @param icon 图标文本
 * @param text 芯片文本
 */
@Composable
fun NavigationChip(icon: String, text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = icon,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 终端风格表格组件
 * @param headers 表格列标题
 * @param rows 表格行数据，每行是一个字符串列表
 */
@Composable
fun TerminalTable(
    headers: List<String>,
    rows: List<List<String>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        shape = RoundedCornerShape(6.dp)
    ) {
        Column {
            // 表头
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                headers.forEachIndexed { index, header ->
                    Text(
                        text = header,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Start
                    )
                }
            }
            TerminalDivider()
            
            // 表格内容
            rows.forEachIndexed { rowIndex, row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    row.forEachIndexed { columnIndex, cell ->
                        Text(
                            text = cell,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Start
                        )
                    }
                }
                if (rowIndex < rows.size - 1) {
                    TerminalDivider()
                }
            }
        }
    }
}
