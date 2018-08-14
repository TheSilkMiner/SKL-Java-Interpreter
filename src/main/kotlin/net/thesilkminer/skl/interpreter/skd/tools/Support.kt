package net.thesilkminer.skl.interpreter.skd.tools

import net.thesilkminer.skl.interpreter.skd.api.tools.Support
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStream
import java.io.InputStreamReader
import java.io.StringReader
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

abstract class ReadOnlySupport : Support {
    final override val isWritable = false
    final override fun write(str: String) = this.`throw`(str)
    final override fun write(str: InputStream) = this.`throw`(str.toString())
    final override fun terminateWriting() = this.throwWithMessage("Unable to close stream: support is not writable")
    private fun `throw`(str: String): Nothing = this.throwWithMessage("Unable to write $str: this support is not writable")
    private fun throwWithMessage(message: String): Nothing = throw Support.WritingException(message)
}

abstract class ReadWriteSupport : Support {
    final override val isWritable = true
}

class FileSupport(path: Path) : ReadWriteSupport() {

    constructor (path: String): this(Paths.get(path).normalize())

    private val reader: BufferedReader by lazy { Files.newBufferedReader(path) }
    private val writer: BufferedWriter by lazy {
        Files.newBufferedWriter(path,StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)
    }

    override fun readLine(): String? = this.reader.readLineOrClose()
    override fun write(str: String) = this.tryCatch { this.writer.write(str) }

    override fun write(str: InputStream) = this.tryCatch {
        val reader = BufferedReader(InputStreamReader(str))
        reader.forEachLine { this.write(it) }
        reader.close()
    }

    override fun terminateWriting() = this.tryCatch { this.writer.close() }
    private inline fun tryCatch(f: () -> Unit) = try { f() } catch (e: Throwable) { throw Support.WritingException(cause = e) }
}

class StringSupport(string: String) : ReadOnlySupport() {
    private val reader: BufferedReader by lazy { BufferedReader(StringReader(string)) }
    override fun readLine(): String? = this.reader.readLineOrClose()
}

private fun BufferedReader.readLineOrClose(): String? = readLine() ?: this.close().run { null }
