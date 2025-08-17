//@file:Suppress("NOTHING_TO_INLINE")
//
//package cn.jzl.ui.flex
//
//import androidx.compose.runtime.Composable
//import cn.jzl.ui.compose.layerNode
//import cn.jzl.ui.modifier.Modifier
//import cn.jzl.ui.modifier.plus
//import cn.jzl.ui.style.UIUnit
//
//inline fun Modifier.flexBasis(value: UIUnit) = this + FlexBasis(value)
//inline fun Modifier.flexGrow(value: Float) = this + FlexGrow(value)
//inline fun Modifier.flexShrink(value: Float) = this + FlexShrink(value)
//inline fun Modifier.flexDirection(value: FlexDirectionValue) = this + FlexDirection(value)
//inline fun Modifier.flexWrap(value: FlexWrapValue) = this + FlexWrap(value)
//inline fun Modifier.justifyContent(value: JustifyContentValue) = this + JustifyContent(value)
//inline fun Modifier.alignItems(value: AlignItemsValue) = this + AlignItems(value)
//inline fun Modifier.alignSelf(value: AlignSelfValue) = this + AlignSelf(value)
//inline fun Modifier.alignContent(value: AlignContentValue) = this + AlignContent(value)
//inline fun Modifier.order(value: Int) = this + Order(value)
//
//private val flexBoxMeasurePolicy = FlexBoxMeasurePolicy()
//
//@Composable
//fun flexBox(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
//    layerNode(modifier, flexBoxMeasurePolicy, content)
//}
