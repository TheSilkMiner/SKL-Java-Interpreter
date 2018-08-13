@file:JvmName("UtilitiesKt")
@file:JvmMultifileClass

package net.thesilkminer.skl.interpreter.skd

import org.joou.UByte
import org.joou.UInteger
import org.joou.ULong
import org.joou.UShort
import java.util.Optional

enum class SupportedStringConverterTypes {
    STRING,
    ULONG,
    UBYTE,
    UINTEGER,
    USHORT,
    LONG,
    BYTE,
    INT,
    SHORT,
    CHAR,
    FLOAT,
    DOUBLE,
    ANY,
    NULL_TYPE
}

fun convertToSkdPropertyRepresentation(a: Any?) : String {
    return when (a) {
        null -> "skd\$null"
        is String -> a.toSkdPropertyString()
        is ULong -> a.toSkdPropertyString()
        is UByte -> a.toSkdPropertyString()
        is UInteger -> a.toSkdPropertyString()
        is UShort -> a.toSkdPropertyString()
        is Long -> a.toSkdPropertyString()
        is Byte -> a.toSkdPropertyString()
        is Int -> a.toSkdPropertyString()
        is Short -> a.toSkdPropertyString()
        is Char -> a.toSkdPropertyString()
        is Float -> a.toSkdPropertyString()
        is Double -> a.toSkdPropertyString()
        else -> a.toSkdPropertyString()
    }
}

private fun String.toSkdPropertyString() : String = this
private fun ULong.toSkdPropertyString() : String = "${this}UL"
private fun UByte.toSkdPropertyString() : String = "${this}UB"
private fun UInteger.toSkdPropertyString() : String = "${this}U"
private fun UShort.toSkdPropertyString() : String = "${this}US"
private fun Long.toSkdPropertyString() : String = "${this}L"
private fun Byte.toSkdPropertyString() : String = "${this}B"
private fun Int.toSkdPropertyString() : String = "${this}"
private fun Short.toSkdPropertyString() : String = "${this}S"
private fun Char.toSkdPropertyString() : String = "${this}\$"
private fun Float.toSkdPropertyString() : String = "${this}F"
private fun Double.toSkdPropertyString() : String = "${this}D"
private fun Any.toSkdPropertyString() : String = "${this}"

fun getTypeFromAny(a: Any?) : SupportedStringConverterTypes {
    return when (a) {
        null -> SupportedStringConverterTypes.NULL_TYPE
        is String -> SupportedStringConverterTypes.STRING
        is ULong -> SupportedStringConverterTypes.ULONG
        is UByte -> SupportedStringConverterTypes.UBYTE
        is UInteger -> SupportedStringConverterTypes.UINTEGER
        is UShort -> SupportedStringConverterTypes.USHORT
        is Long -> SupportedStringConverterTypes.LONG
        is Byte -> SupportedStringConverterTypes.BYTE
        is Int -> SupportedStringConverterTypes.INT
        is Short -> SupportedStringConverterTypes.SHORT
        is Char -> SupportedStringConverterTypes.CHAR
        is Float -> SupportedStringConverterTypes.FLOAT
        is Double -> SupportedStringConverterTypes.DOUBLE
        else -> SupportedStringConverterTypes.ANY
    }
}

@NullableOptionalByDesign
fun <T> convertFromSkdPropertyRepresentation(s: String, t: SupportedStringConverterTypes) : Optional<T>? {
    if (t == SupportedStringConverterTypes.NULL_TYPE) {
        return if (s == "skd\$null") null else Optional.empty()
    }
    val returnType : Any? = when (t) {
        SupportedStringConverterTypes.STRING -> s
        SupportedStringConverterTypes.ULONG -> s.removeSuffix("UL").toULongOrNull()
        SupportedStringConverterTypes.UBYTE -> s.removeSuffix("UB").toUByteOrNull()
        SupportedStringConverterTypes.UINTEGER -> s.removeSuffix("U").toUIntegerOrNull()
        SupportedStringConverterTypes.USHORT -> s.removeSuffix("US").toUShortOtNull()
        SupportedStringConverterTypes.LONG -> s.removeSuffix("L").toLongOrNull()
        SupportedStringConverterTypes.BYTE -> s.removeSuffix("B").toByteOrNull()
        SupportedStringConverterTypes.INT -> s.toIntOrNull()
        SupportedStringConverterTypes.SHORT -> s.removeSuffix("S").toShortOrNull()
        SupportedStringConverterTypes.CHAR -> {
            val it = s.removeSuffix("\$")
            if (it.count() != 1) null else it[0]
        }
        SupportedStringConverterTypes.FLOAT -> s.removeSuffix("F").toFloatOrNull()
        SupportedStringConverterTypes.DOUBLE -> s.removeSuffix("D").toDoubleOrNull()
        else -> if (s == "kotlin.Unit" && t == SupportedStringConverterTypes.ANY) Unit else null
    }

    return try {
        @Suppress("UNCHECKED_CAST")
        Optional.ofNullable(returnType) as Optional<T>
    } catch (e: ClassCastException) {
        Optional.empty()
    }
}

private inline fun <T> tryParseNumberOrNull(s: String, f: (String) -> T) : T? {
    return try {
        f(s)
    } catch (e: NumberFormatException) {
        null
    }
}

private fun String.toULongOrNull() : ULong? = tryParseNumberOrNull(this, ULong::valueOf)
private fun String.toUByteOrNull() : UByte? = tryParseNumberOrNull(this, UByte::valueOf)
private fun String.toUIntegerOrNull() : UInteger? = tryParseNumberOrNull(this, UInteger::valueOf)
private fun String.toUShortOtNull() : UShort? = tryParseNumberOrNull(this, UShort::valueOf)
