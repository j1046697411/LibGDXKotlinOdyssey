package cn.jzl.ui

import androidx.compose.runtime.Composable
import cn.jzl.ecs.Entity
import cn.jzl.lko.math.IntSize
import cn.jzl.ui.modifier.Modifier
import cn.jzl.ui.node.ComposeUiLayout

private val EmptyComposable: @Composable () -> Unit = {}

@Composable
fun Spacer(modifier: Modifier = Modifier) {
    ComposeUiLayout(modifier, SpacerMeasurePolicy, EmptyComposable)
}

internal data object SpacerMeasurePolicy : MeasurePolicy {
    override fun MeasureScope.measure(entity: Entity, children: Sequence<Entity>, constraints: Constraints): MeasureResult {
        return layout(entity, IntSize(constraints.minWidth, constraints.minHeight)) {
        }
    }
}

