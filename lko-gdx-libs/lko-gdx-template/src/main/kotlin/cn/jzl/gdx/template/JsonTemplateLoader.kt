package cn.jzl.gdx.template

import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.*
import ktx.log.Logger
import java.io.Reader

private val ACCEPTED_TPL_FIELDS = mutableListOf<String?>("tpl:extends", "tpl:removeFields", "tpl:comment")
private val log = Logger("JsonTemplateLoader")

fun loadTemplateFromString(jsonString: String?, resolver: FileHandleResolver): JsonValue {
    val originalJson = JsonReader().parse(jsonString)
    return resolveTemplate(originalJson, resolver)
}

fun loadTemplateFromFile(file: String?, resolver: FileHandleResolver): JsonValue {
    return loadTemplateFromFile(resolver.resolve(file), resolver)
}

fun loadTemplateFromFile(reader: Reader?, resolver: FileHandleResolver): JsonValue {
    val originalJson = JsonReader().parse(reader)
    return resolveTemplate(originalJson, resolver)
}

fun loadTemplateFromFile(fileHandle: FileHandle?, resolver: FileHandleResolver): JsonValue {
    val originalJson = JsonReader().parse(fileHandle)
    return resolveTemplate(originalJson, resolver)
}

private fun resolveTemplate(originalJson: JsonValue, resolver: FileHandleResolver): JsonValue {
    val jsonValue = resolveJson(originalJson, resolver)
    log.debug { jsonValue.toJson(JsonWriter.OutputType.json) }
    return jsonValue
}

private fun resolveJson(json: JsonValue, resolver: FileHandleResolver): JsonValue {
    return when (json.type()) {
        JsonValue.ValueType.`object` -> resolveValueForObject(json, resolver)
        JsonValue.ValueType.array -> resolveValueForArray(json, resolver)
        JsonValue.ValueType.booleanValue -> JsonValue(json.asBoolean())
        JsonValue.ValueType.doubleValue -> JsonValue(json.asDouble())
        JsonValue.ValueType.longValue -> JsonValue(json.asLong())
        JsonValue.ValueType.stringValue -> JsonValue(json.asString())
        JsonValue.ValueType.nullValue -> JsonValue(JsonValue.ValueType.nullValue)
        else -> throw GdxRuntimeException("Unknown type of Json type")
    }
}

private fun resolveValueForObject(json: JsonValue, resolver: FileHandleResolver): JsonValue {
    val result = JsonValue(JsonValue.ValueType.`object`)

    var removeFields: ObjectSet<String?>? = null
    for (jsonValue in json) {
        val name = jsonValue.name()
        if (name.startsWith("tpl:") && !ACCEPTED_TPL_FIELDS.contains(name)) throw GdxRuntimeException("Unknown template field: $name, accepted fields: $ACCEPTED_TPL_FIELDS")
    }

    if (json.has("tpl:removeFields")) {
        if (!json.has("tpl:extends")) throw GdxRuntimeException("tpl:removeFields can only be used in combination with tpl:extends")

        val jsonValue = json.get("tpl:removeFields")
        if (jsonValue.isString) {
            removeFields = ObjectSet<String?>()
            removeFields.add(jsonValue.asString())
        } else if (jsonValue.isArray) {
            removeFields = ObjectSet<String?>()
            removeFields.addAll(*jsonValue.asStringArray())
        } else {
            throw GdxRuntimeException("tpl:removeFields is neither a String or an array of Strings")
        }
    }
    if (json.has("tpl:extends")) {
        val jsonValue = json.get("tpl:extends")
        if (jsonValue.isString) {
            appendTemplateWithFilter(jsonValue.asString(), result, removeFields, resolver)
        } else if (jsonValue.isArray) {
            for (template in jsonValue.asStringArray()) {
                appendTemplateWithFilter(template, result, removeFields, resolver)
            }
        }
    }
    for (jsonValue in json) {
        val fieldName = jsonValue.name()
        if (!fieldName.startsWith("tpl:")) {
            appendField(jsonValue, result, removeFields, resolver)
        }
    }

    return result
}

private fun appendTemplateWithFilter(
    template: String?,
    result: JsonValue,
    removeFields: ObjectSet<String?>?,
    resolver: FileHandleResolver
) {
    for (value in loadTemplateFromFile(template, resolver)) {
        appendField(value, result, removeFields, resolver)
    }
}

private fun appendField(
    field: JsonValue,
    result: JsonValue,
    removeFields: ObjectSet<String?>?,
    resolver: FileHandleResolver
) {
    val fieldName = field.name()
    if (removeFields == null || !removeFields.contains(fieldName)) {
        if (result.has(fieldName)) {
            val existingChild = result.get(fieldName)
            if (existingChild.isObject && field.isObject) {
                mergeIn(field, existingChild, resolver)
            } else {
                result.remove(fieldName)
                result.addChild(fieldName, resolveJson(field, resolver))
            }
        } else {
            result.addChild(fieldName, resolveJson(field, resolver))
        }
    }
}

private fun mergeIn(merge: JsonValue, into: JsonValue, resolver: FileHandleResolver) {
    for (fieldInObject in merge) {
        val fieldName = fieldInObject.name()
        if (into.has(fieldName)) {
            val existingChild = into.get(fieldName)
            if (existingChild.isObject && fieldInObject.isObject) {
                mergeIn(fieldInObject, existingChild, resolver)
            } else {
                into.remove(fieldName)
                into.addChild(fieldName, resolveJson(fieldInObject, resolver))
            }
        } else {
            into.addChild(fieldName, resolveJson(fieldInObject, resolver))
        }
    }
}

private fun resolveValueForArray(json: JsonValue, resolver: FileHandleResolver): JsonValue {
    val result = JsonValue(JsonValue.ValueType.array)
    for (child in json) result.addChild(resolveJson(child, resolver))
    return result
}