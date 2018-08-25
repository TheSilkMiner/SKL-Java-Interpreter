@file:JvmName("UtilitiesKt")
@file:JvmMultifileClass

package net.thesilkminer.skl.interpreter.skd

import java.util.Locale

fun String.isUpperCase(): Boolean = this.toUpperCase(Locale.ENGLISH) == this
fun String.isLowerCase(): Boolean = this.toLowerCase(Locale.ENGLISH) == this

operator fun StringBuilder.plusAssign(a: Any?) = this.append(a).unit()
operator fun StringBuilder.plusAssign(i: Int) = this.append(i).unit()
operator fun StringBuilder.plusAssign(c: Char) = this.append(c).unit()
operator fun StringBuilder.plusAssign(f: Float) = this.append(f).unit()
operator fun StringBuilder.plusAssign(d: Double) = this.append(d).unit()
operator fun StringBuilder.plusAssign(l: Long) = this.append(l).unit()
operator fun StringBuilder.plusAssign(b: Boolean) = this.append(b).unit()
operator fun StringBuilder.invoke() = this.toString()
operator fun StringBuilder.unaryMinus() = this.delete(0, this.length + 1)

// So we can return unit on every function that does not return it by default
// but where we need its side effects
@Suppress("unused") private fun Any?.unit(): Unit = Unit
