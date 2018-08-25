@file:JvmName("PUtK")
@file:JvmMultifileClass

package net.thesilkminer.skl.interpreter.skd.tools.parser

import com.google.common.collect.Lists
import net.thesilkminer.skl.interpreter.skd.api.tools.Support
import net.thesilkminer.skl.interpreter.skd.extractMessage
import net.thesilkminer.skl.interpreter.skd.rethrowAs

internal class Loader(private val support: Support) {

    companion object {
        private val l = L(this)
    }

    fun load(): List<String> {
        l.d("Initialized loader {}, now loading file", this)
        return try {
            this.loadImplementation()
        } catch (e: Exception) {
            l.e("An exception has been thrown while attempting to load file into memory - parsing cannot continue")
            e rethrowAs ::LoadingException
        }
    }

    private fun loadImplementation(): List<String> {
        val list = Lists.newArrayList<String>()
        var str = this.support.readLine()
        while (str != null) {
            list.add(str)
            str = this.support.readLine()
        }
        l.t("Read a total of {} lines from support {}", list.count(), this.support)
        return list
    }
}

internal class LoadingException(cause: Throwable?) : Exception(cause.extractMessage(), cause)
