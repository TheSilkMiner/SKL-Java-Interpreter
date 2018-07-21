package net.thesilkminer.skl.interpreter.skd.api.structure

/**
 * Represents a declaration of a database.
 *
 * Declarations reside at the top of a database file and
 * provide various information on the file itself, such
 * as the document type or the SKD version the file is
 * using. These pieces of information are then used by
 * the parser to actually know how the file should be
 * parsed in order to guarantee a well-formed output
 * (provided a well-formed input is specified).
 *
 * Every declaration specified inside a database must be
 * uniquely identifiable thanks to its key. Multiple
 * declarations with the same key cannot coexist, but
 * multiple declarations with the same value and different
 * key are allowed to exist.
 *
 * There is no specification on how the value of a declaration
 * should be structured. The only guarantee is that it must
 * be able to be converted from and to a [String].
 *
 * Keys, on the other hand, are case sensitive and should
 * be written in all capital letters (e.g. `DOCTYPE`, `SKD`).
 * Underscore characters (`_`) are allowed as separators
 * between words, but their use should be limited only when
 * really necessary.
 *
 * The syntax of a complete declaration is thus: `<!KEY value>`.
 * As an example, consider the following document type
 * declaration that specifies that the current document is
 * an SKD database file: `<!DOCTYPE skd>`.
 *
 * Only key declarations are not allowed per the standard.
 * It is impossible to declare the following: `<!SOME_KEY>`
 *
 * @since 0.3
 */
interface Declaration {

    /**
     * Gets the key of this declaration.
     *
     * The key must be in all capital letters.
     * Underscore characters are allowed as depicted
     * in the [main `Declaration` documentation][Declaration].
     *
     * @return The key of this declaration.
     *
     * @since 0.3
     */
    fun getKey() : String

    /**
     * Gets the value currently present inside this
     * declaration.
     *
     * @return The value of this declaration.
     *
     * @since 0.3
     */
    fun getValue() : String

    /**
     * Sets the value inside this declaration.
     *
     * It is not allowed to set an empty string as a value.
     *
     * @param[value] The new value to set.
     *
     * @since 0.3
     */
    fun setValue(value: String)
}
