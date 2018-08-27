@file:JvmName("TUtKt")

package net.thesilkminer.skl.interpreter.skd

import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.junit.rules.ExpectedException
import kotlin.reflect.KClass

fun <T: Exception> expectExceptions(rule: ExpectedException, exception: KClass<T>, vararg lines: String,
                                     function: (MutableList<String>) -> Any) {
    rule.expectCause(isAn(exception))
    try {
        function(lines.spread())
    } catch (e: Exception) {
        e.printStackTrace(System.err)
        throw e
    }
}

private fun <T: Exception> isAn(e: KClass<T>): Matcher<T> = CoreMatchers.isA(e.java)

fun <T> Array<out T>.spread(): MutableList<T> = this.toMutableList()

fun <T> l(vararg args: T) = mutableListOf(*args)
