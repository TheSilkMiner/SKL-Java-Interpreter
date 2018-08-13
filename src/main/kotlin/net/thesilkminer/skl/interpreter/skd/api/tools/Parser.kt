package net.thesilkminer.skl.interpreter.skd.api.tools

import net.thesilkminer.skl.interpreter.skd.api.structure.ArrayEntity
import net.thesilkminer.skl.interpreter.skd.api.structure.Database

/**
 * Represents a database parser.
 *
 * The parser goal is to read a previously serialized
 * representation of an SKD database, along with all its
 * declarations, preprocessor directives and entities. The
 * parser must thus create an in-memory representation of
 * the entirety of the database. Refer to [Database]
 * and the entirety of the structure API for more
 * information.
 *
 * A parser must not assume that the input is well-formed.
 * Every database representation must be checked while
 * loading it and every error reported. Every parser
 * implementation can choose the preferred way to output
 * errors and/or warnings. For a list of standard errors
 * and warnings, refer to the file with the same name in
 * the Tools API. Parsers may choose to add other errors
 * and warnings in addition to the ones that are already
 * present in the standard API.
 *
 * A parser may also provide the possibility to directly
 * load into an unspecified object (that must be considered
 * of type [Any]) the list of properties and data of the
 * database. This support can use techniques such as
 * reflection to do this. Also, support for this functionality
 * is provided as-is and no guarantees that the operation
 * will succeed are given by the parser. For a more
 * specific contract, refer to [loadIntoAny].
 *
 * The parser should read the database representation
 * through the given [support]. Parsers should not rely
 * on implementation details of the passed in support.
 *
 * Parser implementations are allowed to use techniques
 * such as reflection and rely on implementation details
 * of the implementations that they use to represent
 * the database. For no reason, a parser should assume
 * implementation details of other implementations.
 *
 * Any parser must provide support for the entirety of the
 * toolchain, that is the declarator, the preprocessor
 * and the parser itself.
 *
 * @since 0.3
 */
interface Parser {

    /**
     * The support where the database representation is
     * stored.
     *
     * @since 0.3
     *
     * @getter Gets the support where the database
     * representation is stored.
     *
     * @since 0.3
     */
    val support: Support

    /**
     * Parses the database representation provided by the
     * support and returns the in-memory representation
     * of the database.
     *
     * The parser must account for every error that may be
     * present in the representation and terminate every
     * processing in case it finds an error. Implementations
     * may also choose to log a message if they deem necessary
     * to do so. In case an error is found, every implementation
     * must throw an exception, though.
     *
     * In presence of certain conditions, implementations may
     * also choose to warn the end user for some exceptional
     * conditions that do not cause a parsing error per-se.
     *
     * Also, parsers are allowed to cache the result of this
     * method for performance concerns.
     *
     * @return The in-memory representation of the database per
     * the specifications of the Structure API. Refer to
     * [Database] and all the mentioned interfaces for more
     * information.
     * @exception InvalidDatabaseException If the database is
     * malformed.
     *
     * @since 0.3
     */
    fun parse(): Database

    /**
     * Tries to parse the database and populate the given object
     * instance with the database-provided data.
     *
     * This operation provides no guarantees that the operation
     * will actually succeed. In fact, implementations may choose
     * to require the same object or a more loose structure.
     * At the same time, implementations may choose to use
     * techniques such as reflection or other APIs that assume
     * the implementation of the object passed in. Also,
     * every implementation can choose the representation of
     * data that most satisfies itself. E.g., an implementation
     * may require that [array-like entities][ArrayEntity] are
     * stored in [List]s, while others may require an actual
     * [Array].
     *
     * If an implementation provides both a parser and a
     * serializer, the output of the serializer must be compatible
     * with the parser and vice-versa.
     *
     * Any errors in the structure need to be signalled to
     * the user through the use of an exception. Implementations
     * may choose to log an error too if they want.
     *
     * @param[any] An [Any] instance representing the object
     * structure where the parser should load the database
     * representation in.
     * @exception InvalidObjectStructureException If the structure
     * of the object passed in is not considered valid by the
     * parser.
     *
     * @since 0.3
     */
    fun loadIntoAny(any: Any)
}
