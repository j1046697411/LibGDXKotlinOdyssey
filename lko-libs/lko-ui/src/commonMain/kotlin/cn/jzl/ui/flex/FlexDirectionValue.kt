package cn.jzl.ui.flex

enum class FlexDirectionValue(val reverse: Boolean, val isRow: Boolean) {
    Row(false, true),
    RowReverse(true, true),
    Column(false, false),
    ColumnReverse(true, false)
}