@file:JvmName("UtilitiesKt")
@file:JvmMultifileClass

package net.thesilkminer.skl.interpreter.skd

import java.util.Locale

fun String.isUpperCase(): Boolean = this.toUpperCase(Locale.ENGLISH) == this
fun String.isLowerCase(): Boolean = this.toLowerCase(Locale.ENGLISH) == this
