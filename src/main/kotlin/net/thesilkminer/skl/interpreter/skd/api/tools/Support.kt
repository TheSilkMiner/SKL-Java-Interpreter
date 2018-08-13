package net.thesilkminer.skl.interpreter.skd.api.tools

import java.io.InputStream

/**
 * Represents a support from which a database can be read
 * and/or can be written to.
 *
 * An implementation can either support a physical or an
 * "ethereal" support. The term physical support refers to
 * something such as a file, a magnetic tape, an optical
 * disk or anything else that is tangible or can be considered
 * so - such as files: they are not really tangible, but are
 * physically stored on disks, so they can be considered
 * physical -. On the other hand, "ethereal" support can be
 * considered something that resides only in memory or
 * in some other non-real location, such as the cloud.
 *
 * Every support must provide a way to read the database
 * representation line by line, to account for line-based
 * sections of the parser, such as the declarator or the
 * preprocessor. It is not necessary that line breaks
 * correspond to actual line breaks that are present in
 * the original support. E.g., an implementation for a
 * magnetic tape may manually add line breaks in-between
 * and provide everything else basically as a continuous
 * stream of data.
 *
 * A support may also provide a way to write the in-memory
 * database in its own representation on it. The support
 * must not dictate how the representation is, though: that
 * is left to the serializer bit of the parser. The support
 * only has to deal with the writing of the entire stream
 * of data that the serializer creates. A support may also
 * not support writing to the same output: this may apply
 * to read-only supports, e.g. a CD-ROM. In this case, the
 * support must signal this limitation through its
 * implementation. Refer to the specific methods documentation
 * for more information.
 *
 * @since 0.3
 */
interface Support {

    /**
     * Marks an exceptional condition that caused the writing
     * operation to fail.
     *
     * The failure can either be due to an error in the
     * underlying file system (for a physical support, for
     * example) or any other issues. It can also be due to
     * the fact that this support instance does not support
     * writing operations.
     *
     * Implementations may choose (and they are highly
     * encouraged to do so) to provide a cause for this exception
     * when throwing it, to aid in debugging and/or error
     * recovery.
     *
     * @since 0.3
     *
     * @constructor Creates a new instance of this exception.
     *
     * @param[message] A message that describes the exception in
     * more detail. As an example, it may give some more
     * information about the exact phase where the failure
     * happened. It is initialized to `null` by default.
     * @param[cause] The [Throwable] that caused this exception
     * to be thrown. It is initialized to `null` by default.
     *
     * @since 0.3
     */
    class WritingException @JvmOverloads constructor(message: String? = null, cause: Throwable? = null) : Exception(message, cause)

    /**
     * Reads the next line of input from the given support.
     *
     * If no line is available, then the implementation must
     * return `null`.
     *
     * @return The next line of input or `null` if it is not
     * available.
     *
     * @since 0.3
     */
    fun readLine(): String?

    /**
     * Stores whether a serializer can use this support for
     * writing or not.
     *
     * @since 0.3
     *
     * @getter Gets whether a serializer can use this
     * support for writing or not.
     *
     * @since 0.3
     */
    val isWritable: Boolean

    /**
     * Writes the given string on this support.
     *
     * If writing fails for some reason, either because
     * writing is not supported ([isWritable]) or another
     * error occurs, implementations should throw an instance
     * of [WritingException].
     *
     * @param[str] The string to write on the support.
     *
     * @since 0.3
     */
    fun write(str: String)

    /**
     * Writes the contents present into the given input stream
     * on this support.
     *
     * If writing fails for some reason, either because
     * writing is not supported ([isWritable]) or another
     * error occurs, implementations should throw an instance
     * of [WritingException].
     *
     * @param[str] The stream containing the data to write on
     * the support.
     *
     * @since 0.3
     */
    fun write(str: InputStream)
}
