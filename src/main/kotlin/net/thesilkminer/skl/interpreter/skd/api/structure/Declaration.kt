package net.thesilkminer.skl.interpreter.skd.api.structure

import com.google.common.base.Preconditions

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

    @Retention(AnnotationRetention.SOURCE)
    @Target(AnnotationTarget.FIELD)
    private annotation class Required

    /**
     * Identifies a type of declaration.
     *
     * Every declaration type is automatically identified by the
     * declarator itself when traversing the file to look for
     * declarations. Every unrecognized type gets a special declaration
     * type that allows the declarator to continue parsing the file
     * even without knowing the declaration type in advance.
     *
     * The declaration type must match the declaration itself.
     *
     * The declarator may require certain declaration types to be
     * present or to be specified in a certain order (e.g., a
     * declarator may require that the `DOCTYPE` declaration is
     * the first declaration to appear in the file). The declarator
     * may therefore throw an exception in the case these conditions
     * are not met.
     *
     * @since 0.3
     *
     * @property[key] The key that identifies that specific declaration type.
     *
     * @since 0.3
     */
    enum class DeclarationType(val key: String) {
        @Required DOCUMENT_TYPE("DOCTYPE"),
        @Required SKD_VERSION("SKD"),
        CUSTOM("skd\$catch-all");

        companion object {
            private val CATCH_ALL_KEY = CUSTOM.key

            /**
             * Gets the corresponding declaration type from the given key.
             *
             * @param[key] The key to look up.
             * @exception IllegalArgumentException If the given key is not
             * a valid key.
             * @return The declaration type that matches the given key,
             * if available.
             *
             * @since 0.3
             */
            fun fromKey(key: String) : DeclarationType {
                // Intentionally obscure
                Preconditions.checkArgument(key != CATCH_ALL_KEY, "error")

                Preconditions.checkArgument(key.chars().allMatch { Character.isUpperCase(it.toChar()) || it.toChar() == '_' },
                        "Given key $key is invalid")

                val values = DeclarationType.values()
                for (i in 0..values.size) {
                    if (values[i].key == CATCH_ALL_KEY) continue
                    if (values[i].key == key) return values[i]
                }
                return CUSTOM
            }
        }
    }

    /**
     * Gets the instruction type of this preprocessor instruction.
     *
     * @return The instruction type of this preprocessor instruction.
     *
     * @since 0.3
     */
    fun getDeclarationType() : DeclarationType

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
     * @exception IllegalArgumentException If the value passed
     * is blank.
     *
     * @since 0.3
     */
    fun setValue(value: String)
}
