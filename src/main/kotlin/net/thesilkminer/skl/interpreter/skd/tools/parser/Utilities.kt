@file:JvmName("PUtK")

package net.thesilkminer.skl.interpreter.skd.tools.parser

import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

class L(c: KClass<*>) {
    constructor(a: Any): this(a::class)

    companion object {
        var h = false
    }

    init {
        if (System.getProperty("slf4j.detectLoggerNameMismatch") == null) {
            System.setProperty("slf4j.detectLoggerNameMismatch", "true")
        }

        try {
            @Suppress("NO_REFLECTION_IN_CLASS_PATH")
            if (c.isCompanion) {
                System.err.println("sL: [WARN] Logger will be initialized on companion object - " +
                        "in Java this means that \$Companion will be appended to the class name")
            }
        } catch (e: Throwable) {
            if (!h && e is KotlinReflectionNotSupportedError) {
                System.out.println("sL: Missing Reflection in class path - unable to check preconditions")
                e.printStackTrace(System.out)
                h = true
            }
        }
    }

    private val logger: org.slf4j.Logger by lazy {
        LoggerFactory.getLogger(c.java)
    }

    fun t(m: String) = this.logger.trace(m)
    fun t(m: String, a: Any) = this.logger.trace(m, a)
    fun t(m: String, a: Any, b: Any) = this.logger.trace(m, a, b)
    fun t(m: String, vararg o: Any) = this.logger.trace(m, *o)
    fun d(m: String) = this.logger.debug(m)
    fun d(m: String, a: Any) = this.logger.debug(m, a)
    fun d(m: String, a: Any, b: Any) = this.logger.debug(m, a, b)
    fun d(m: String, vararg o: Any) = this.logger.debug(m, *o)
    fun i(m: String) = this.logger.info(m)
    fun i(m: String, a: Any) = this.logger.info(m, a)
    fun i(m: String, a: Any, b: Any) = this.logger.info(m, a, b)
    fun i(m: String, vararg o: Any) = this.logger.info(m, *o)
    fun w(m: String) = this.logger.warn(m)
    fun w(m: String, a: Any) = this.logger.warn(m, a)
    fun w(m: String, a: Any, b: Any) = this.logger.warn(m, a, b)
    fun w(m: String, vararg o: Any) = this.logger.warn(m, *o)
    fun e(m: String) = this.logger.error(m)
    fun e(m: String, a: Any) = this.logger.error(m, a)
    fun e(m: String, a: Any, b: Any) = this.logger.error(m, a, b)
    fun e(m: String, vararg o: Any) = this.logger.error(m, *o)
}

class LineNumber private constructor(private val lineNumber: Int) {
    companion object {
        internal operator fun invoke(lineNumber: Int): LineNumber = LineNumber(lineNumber)
    }

    fun toInt(): Int = this.lineNumber - 1

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (this.javaClass != other?.javaClass) return false
        other as LineNumber
        return this.lineNumber == other.lineNumber
    }

    override fun hashCode(): Int = this.lineNumber
    override fun toString(): String = "${this.lineNumber}"
}

fun Int.toLineNumber(): LineNumber = LineNumber(this + 1)

operator fun <T> List<T>.get(lineNumber: LineNumber) = this[lineNumber.toInt()]
operator fun <T> MutableList<T>.set(lineNumber: LineNumber, value: T) {
    this[lineNumber.toInt()] = value
}

const val DECLARATION_BEGIN_MARKER = "<!"
const val PREPROCESSOR_BEGIN_MARKER = "<#"
const val DECLARATION_PREPROCESSOR_END_MARKER = ">"
const val LEGACY_DECLARATION_BEGIN_MARKER = "<"

const val ERROR_MESSAGE_ERROR_LINE_BEFORE = "-"
const val ERROR_MESSAGE_ERROR_MARKER = "^"
