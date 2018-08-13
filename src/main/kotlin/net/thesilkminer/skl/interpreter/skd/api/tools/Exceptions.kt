@file:JvmName("ExceptionsKt")

package net.thesilkminer.skl.interpreter.skd.api.tools

/**
 * Identifies a generic exception that consumers of the library
 * may want to catch as a catch-all exceptions for all the possible
 * exceptions thrown by the library.
 *
 * In other words, every exception thrown by the library but a
 * handful (that is [IllegalArgumentException], [IllegalStateException],
 * [UnsupportedOperationException] and all the ones thrown by the
 * runtime on exceptional conditions) are subclasses of this
 * class. Consumers are allowed and encouraged to make subclasses
 * of this class for their specific exception needs.
 *
 * When using this exception in a Java-based software, remember
 * this is a checked exception.
 *
 * @since 0.3
 *
 * @constructor Constructs an instance of this exception with the
 * given data.
 * @param[message] A more detailed message that may tip off users
 * about causes or any other details that may be needed. It can
 * be `null`, in which case no detail message will be provided.
 * @param[cause] A [Throwable] instance representing the exception
 * that caused this exception instance to be thrown. If no cause
 * is present, then this parameter can be `null`.
 *
 * @since 0.3
 */
open class SkdException @JvmOverloads constructor(message: String?, cause: Throwable? = null) : Exception(message, cause)

/**
 * Thrown when a database is not deemed valid due to syntax errors
 * or conditions not verified, such as an array-like entity
 * not respecting the necessary restrictions.
 *
 * The message of this exception should identify the issue and give
 * some directions as to where the issue arises. Implementations may
 * also choose to suggest possible resolutions. An example of such a
 * message is the following:
 * ```text
 * Illegal information pair specified: expected a value, but got nothing.
 * At line 4
 * <wrong-info-pair />
 * ----------------^
 * ```
 *
 * When using this library in a Java-based consumer, remember that
 * this is a checked exception.
 *
 * @since 0.3
 *
 * @constructor Constructs an instance of this exception with the given
 * data.
 * @param[message] The message of this exception, according to the rules
 * given [in the documentation][InvalidDatabaseException], if possible.
 * @param[cause] A [Throwable] instance representing the exception that
 * caused this exception instance to be thrown. If no cause is present,
 * then this parameter can be `null`.
 *
 * @since 0.3
 */
class InvalidDatabaseException @JvmOverloads constructor(message: String, cause: Throwable? = null) : SkdException(message, cause)

/**
 * Thrown when an object instance passed into a parser cannot be
 * populated with the data read by the parser itself due to
 * mismatch in the object structure.
 *
 * The message of this exception must identify the issue and
 * provide an expected versus found report. Implementations
 * may also suggest what to change either in the database or
 * the object structure itself in order to allow the
 * population to succeed. An example of such a message may
 * be:
 * ```text
 * Expected Array<*> but got List<*> in object structure.
 * ---> val List<*> phones;
 * Did you mistype the variable type?
 * ```
 *
 * @since 0.3
 *
 * @constructor Constructs an instance of this exception with
 * the given data.
 * @param[message] The message of this exception, according to
 * the rules given [in the documentation][InvalidObjectStructureException],
 * if possible.
 * @param[cause] A [Throwable] instance representing the exception
 * that caused this exception instance to be thrown. If no cause
 * is present, then this parameter can be `null`.
 */
class InvalidObjectStructureException @JvmOverloads constructor(message: String, cause: Throwable? = null) : SkdException(message, cause)
