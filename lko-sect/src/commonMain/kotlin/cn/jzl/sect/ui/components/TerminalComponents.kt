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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

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

enum class ButtonVariant {
    FILLED, OUTLINED, TEXT
}

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
