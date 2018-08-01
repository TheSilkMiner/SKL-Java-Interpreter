package net.thesilkminer.skl.interpreter.skd.api

import net.thesilkminer.skl.interpreter.skd.api.structure.ArrayEntity
import net.thesilkminer.skl.interpreter.skd.api.structure.Database
import net.thesilkminer.skl.interpreter.skd.api.structure.Declaration
import net.thesilkminer.skl.interpreter.skd.api.structure.Entity
import net.thesilkminer.skl.interpreter.skd.api.structure.InfoPair
import net.thesilkminer.skl.interpreter.skd.api.structure.PreprocessorInstruction
import net.thesilkminer.skl.interpreter.skd.api.structure.Property
import net.thesilkminer.skl.interpreter.skd.multicatch
import java.lang.reflect.Method

/**
 * Represents a provider of an implementation for the API.
 *
 * An API provider is the main entry point for a consumer
 * to use this library. As such it must provide access to
 * every important method inside the SKD parsing library.
 * Some examples may be the parser, the serializer or the
 * various entities.
 *
 * The contract of an API provider is strict but at the same
 * time very flexible for the implementations. In fact, most
 * of the behavior of the various methods is strictly defined
 * in the documentation, but at the same time, implementations
 * are free to implement the desired behavior as they like.
 *
 * In other words, this architecture is similar to the one
 * provided by SLF4J and all its facades. The API is the same,
 * but every implementation either provides a completely
 * no-op implementation or a LOG4J-based one, with all its
 * quirks (`log4j.xml` as an example).
 *
 * Every implementation must provide a class named `ApiProvider`
 * implementing this interface in a specific package, that is
 * the following: `net.thesilkminer.skl.interpreter.skd`. This
 * is necessary for all implementations because this API uses
 * what is called a static binding, which relies on that class
 * being present. The class must have a parameter-less constructor
 * used by the binding. Also, if using Kotlin, it is disallowed
 * to use an `object` instead of a `class` due to the way we
 * initialize the provider.
 *
 * @since 0.3
 */
interface ApiProvider {

    /**
     * A no-op implementation of the API provider.
     *
     * This can be used by implementations to provide a basic layer of
     * implementation - that basically throws a not-implemented exception
     * for every method. It is also the default implementation used if
     * the static binding fails. A warning may be logged if a logging
     * facade is found.
     *
     * Implementations that extend this class are allowed to call
     * `super` methods only during tests and their implementation is
     * thus not yet complete.
     *
     * @since 0.3
     */
    open class NoOpImplementation : ApiProvider {

        private val errorLoggingLogger: Any?
        private val errorLoggingMethod: Method?

        init {
            var errorLoggingMethod: Method? = null
            var errorLoggingLogger: Any? = null

            try {
                System.setProperty("slf4j.detectLoggerNameMismatch", "true")

                val cLoggerFactory = Class.forName("org.slf4j.LoggerFactory")
                val cLogger = Class.forName("org.slf4j.Logger")
                val mGetLoggerString = cLoggerFactory.getDeclaredMethod("getLogger", String::class.java)
                val mErrorString = cLogger.getDeclaredMethod("error", String::class.java)

                val loggerAny = mGetLoggerString.invoke(null, "NoOpImplementation")
                val logger = cLogger.cast(loggerAny)

                this.tryLog("Test call to Logger.error(String)", mErrorString, logger)

                errorLoggingMethod = mErrorString
                errorLoggingLogger = logger
            } catch (e: Exception) {
                e.multicatch(ReflectiveOperationException::class.java, NullPointerException::class.java) {
                    errorLoggingMethod = null
                    errorLoggingLogger = null
                }
            }

            this.errorLoggingMethod = errorLoggingMethod
            this.errorLoggingLogger = errorLoggingLogger
        }

        private fun tryLog() {
            if (this.errorLoggingMethod == null || this.errorLoggingLogger == null) return
            this.tryLog("Implementation not available or not found. Resorting to throw-only",
                    this.errorLoggingMethod, this.errorLoggingLogger)
        }

        private fun tryLog(message: String, errorLoggingMethod: Method?, errorLoggingLogger: Any?) {
            if (errorLoggingMethod == null || errorLoggingLogger == null) throw NullPointerException()
            errorLoggingMethod.invoke(errorLoggingLogger, message)
        }

        private fun t(r: String): Nothing = throw NotImplementedError(r)

        override fun buildEntity(name: String): Entity {
            this.tryLog()
            this.t("Unable to build entity with name $name: missing implementation")
        }

        override fun buildArrayLikeEntity(name: String): ArrayEntity {
            this.tryLog()
            this.t("Unable to build array-like entity with name $name: missing implementation")
        }

        override fun <T> buildInformationPair(key: String, value: T?): InfoPair<T> {
            this.tryLog()
            this.t("Unable to build information pair with key $key and value $value: missing implementation")
        }

        override fun <T> buildProperty(name: String, value: T?): Property<T> {
            this.tryLog()
            this.t("Unable to build property with name $name and value $value: missing implementation")
        }

        override fun buildPreprocessorInstruction(instruction: String, lineNumber: Long): PreprocessorInstruction {
            this.tryLog()
            this.t("Unable to build preprocessor instruction $instruction at line $lineNumber: missing implementation")
        }

        override fun buildDeclaration(key: String, value: String): Declaration {
            this.tryLog()
            this.t("Unable to build declaration with key $key and value $value: missing implementation")
        }

        override fun buildDatabase(mainEntity: Entity): Database {
            this.tryLog()
            this.t("Unable to build database with main entity $mainEntity: missing implementation")
        }
    }

    /**
     * Builds an entity with the given name.
     *
     * The given name must be made up of only alphanumeric
     * characters, hyphens and underscores and cannot be
     * blank.
     *
     * @param[name] The name of the entity to build.
     * @return The [entity][Entity] that was built.
     * @exception IllegalArgumentException If the name does
     * not conform to the contract of the entity.
     *
     * @since 0.3
     */
    fun buildEntity(name: String): Entity

    /**
     * Build an array-like entity with the given name.
     *
     * The given name must be made up of only alphanumeric
     * characters, hyphens and underscores and cannot be blank.
     *
     * @param[name] The name of the array-like entity to build.
     * @return The [array-like entity][ArrayEntity] that was built.
     * @exception IllegalArgumentException If the name does
     * not conform to the contract of the entity.
     *
     * @since 0.3
     */
    fun buildArrayLikeEntity(name: String): ArrayEntity

    /**
     * Builds an information pair of type `T` with the given key
     * and value.
     *
     * The supplied key must not be blank.
     *
     * The given value can be either be `null` or not. Implementations
     * are not forced to accept null values. Implementations are thus
     * allowed to throw a [NullPointerException] if `null` values are
     * not allowed.
     *
     * @param[T] The type of the information pair. It matches the value
     * type.
     * @param[key] The key of the information pair to build.
     * @param[value] The value of the information pair to build.
     * @return An [information pair][InfoPair] of the given type `T`.
     * @exception IllegalArgumentException If the supplied key is blank.
     * @exception NullPointerException If the implementation does not
     * allow null values and a null value is passed in.
     *
     * @since 0.3
     */
    fun <T> buildInformationPair(key: String, value: T?): InfoPair<T>

    /**
     * Builds a property of type `T` with the given name and value.
     *
     * The supplied name must not be blank and can only contain letters,
     * digits, hyphens and underscores. The name also cannot be blank or
     * match `id` - ignoring case -.
     *
     * The given value can either be `null` or not. Implementations
     * are not forced to accept null values. Implementations are thus
     * allowed to throw a [NullPointerException] if `null` values are
     * not allowed.
     *
     * @param[T] The type of the property. It matches the value type.
     * @param[name] The name of the property to build.
     * @param[value] The value of the property to build.
     * @return A [property][Property] of the given type `T`.
     * @exception IllegalArgumentException If the supplied key is not
     * valid.
     * @exception NullPointerException If the implementation does not
     * allow null values and a null value is passed in.
     *
     * @since 0.3
     */
    fun <T> buildProperty(name: String, value: T?): Property<T>

    /**
     * Builds a preprocessor instruction placed at the given line
     * number.
     *
     * The instruction must be a valid (and recognizable by the
     * official preprocessor) instruction.
     *
     * @param[instruction] The instruction for the preprocessor.
     * @param[lineNumber] The line number of this preprocessor instruction.
     * @return A [preprocessor instruction][PreprocessorInstruction].
     * @exception IllegalArgumentException If the supplied instruction is
     * not valid.
     *
     * @since 0.3
     */
    fun buildPreprocessorInstruction(instruction: String, lineNumber: Long): PreprocessorInstruction

    /**
     * Builds a database declaration with the given key and value.
     *
     * The key cannot be blank and the value must be entirely
     * uppercase.
     *
     * @param[key] The key of the declaration.
     * @param[value] The value of this declaration.
     * @return The [declaration][Declaration] that was built.
     * @exception IllegalArgumentException If the given key is not
     * a valid key.
     *
     * @since 0.3
     */
    fun buildDeclaration(key: String, value: String): Declaration

    /**
     * Creates a new database with the given main entity.
     *
     * @param[mainEntity] The [Entity] to use as the main entity
     * of this database.
     * @return The newly built [database][Database].
     *
     * @since 0.3
     */
    fun buildDatabase(mainEntity: Entity): Database
}
