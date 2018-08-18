@file:JvmName("UtilitiesKt")
@file:JvmMultifileClass

package net.thesilkminer.skl.interpreter.skd

inline infix fun <T: Throwable> Throwable.rethrowAs(builder: (Throwable) -> T): Nothing = throw builder(this)
inline infix fun <T: Throwable> Throwable.rethrowAs(builder: (String, Throwable) -> T): Nothing = throw builder(this.extractMessage(), this)
fun Throwable?.extractMessage(ifNull: String = "null"): String = this?.message ?: ifNull
