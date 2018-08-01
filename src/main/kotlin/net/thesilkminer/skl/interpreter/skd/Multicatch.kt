@file:JvmName("UtilitiesKt")
@file:JvmMultifileClass

package net.thesilkminer.skl.interpreter.skd

import kotlin.reflect.KClass

fun <T> Throwable.multicatch(vararg classes: Class<*>, block: () -> T): T {
    if (classes.any { this::class.java.isInstance(it) }) {
        return block()
    }
    throw this
}

@Suppress("NO_REFLECTION_IN_CLASS_PATH", "UNUSED")
fun <T> Throwable.multicatch(vararg classes: KClass<*>, block: () -> T): T {
    if (classes.any { this::class.isInstance(it) }) {
        return block()
    }
    throw this
}
