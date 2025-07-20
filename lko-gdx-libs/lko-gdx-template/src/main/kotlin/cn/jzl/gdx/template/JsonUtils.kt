package cn.jzl.gdx.template

import com.badlogic.gdx.utils.JsonValue

fun <T> convertToJsonArray(t: Iterable<T>, converter: (T)-> JsonValue): JsonValue {
    val resultArray = JsonValue(JsonValue.ValueType.array)
    var lastChild: JsonValue? = null
    var count = 0
    for (value in t) {
        val convertedValue = converter(value)
        if (lastChild == null) {
            lastChild = convertedValue
            resultArray.addChild(lastChild)
        } else {
            lastChild.next = convertedValue
            lastChild = convertedValue
        }
        count++
    }
    resultArray.size = count

    return resultArray
}
