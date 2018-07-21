package net.thesilkminer.skl.interpreter.skd.api.structure

/**
 * A property carries some entity-related information
 * inside a database.
 *
 * A property is used in a database to represent data
 * that is more strictly related to entities than most
 * other data. They share some traits with
 * [information pairs][InfoPair], but are not related
 * to them at all.
 *
 * Properties are made up of a name-value pair, where
 * the name is a [String] and the value is of type `T`,
 * according to the consumer.
 *
 * Properties are uniquely identified by their name,
 * that must be present only once inside the declaration
 * of an entity. Properties are in fact declared directly
 * in the declaration of an entity. As such, they cannot
 * have IDs or children.
 *
 * A property name also has some restrictions. Namely,
 * it cannot contain special characters apart from `_`
 * and `-`. `$` are forbidden. A property cannot be
 * named `id`, `ID` or all the upper-case and lower-case
 * combinations of these names.
 *
 * It is suggested that the end user prefers information
 * pairs instead of properties due to the wider support the
 * language offers. Properties should be in fact considered
 * legacy where possible. This does not imply that properties
 * are deprecated, though. It is entirely possible for an
 * entity to have both information pairs and properties,
 * but in the SKD the former are more encouraged as a
 * matter of style.
 *
 * @param[T] The type of the property value.
 *
 * @since 0.3
 */
interface Property<T> {

    /**
     * Gets the name of this property.
     *
     * The name of a property has some restrictions: refer
     * to the [Property] documentation for more information.
     *
     * @return The name of this property.
     *
     * @since 0.3
     */
    fun getName() : String

    /**
     * Gets the value of this property.
     *
     * @return The value of this property.
     *
     * @since 0.3
     */
    fun getValue() : T

    /**
     * Sets the value of this property.
     *
     * @param[value] The new value of this property.
     *
     * @since 0.3
     */
    fun setValue(value: T)

    /**
     * Gets the value of this property converted to a [String].
     *
     * Implementations are free to choose the best way to represent
     * data and to encode the type of the data itself.
     *
     * @return The value of this property converted to a [String].
     *
     * @since 0.3
     */
    fun getValueAsString() : String

    /**
     * Sets the value of this property from the given [String].
     *
     * Implementations are allowed to throw an exception
     * (namely an [IllegalArgumentException]) if they cannot
     * convert the string to the type of this property.
     * Loose conversion is allowed and implementation-dependant
     * (much like in Javascript, for example).
     *
     * @param[stringValue] The value to set this property to in
     * a `String`-ified form.
     * @exception IllegalArgumentException If the `value` cannot
     * be converted from a `String` to the type of this property.
     *
     * @since 0.3
     */
    fun setValueFromString(stringValue: String)
}