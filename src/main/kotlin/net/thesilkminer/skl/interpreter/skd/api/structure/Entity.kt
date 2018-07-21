package net.thesilkminer.skl.interpreter.skd.api.structure

import java.util.Optional

/**
 * Represents an entity of a database.
 *
 * An entity is the main construct of a database because
 * they store the entirety of the information in their
 * structure.
 *
 * An entity stores information with a combination of children
 * entities, properties and information pairs.
 *
 * An entity can have one, multiple or no other entities
 * "inside" it: these sub-entities are named children. Every
 * children entity is an entity on itself and as such can be
 * considered separately from the parent.
 *
 * Entities are also allowed to have one, multiple or no
 * properties. Every property is a combination of key and
 * value pairs that directly specifies entity data. Properties
 * reside directly in the declaration of the entity and can
 * either be generated automatically (like with IDs) or added
 * manually. Every property must be unique inside the entity
 * it is referring to. See [Property] for more information.
 *
 * An entity can also have one, multiple or no information
 * pairs. Much like properties, information pairs are a
 * combination of a key and a value. They are also entities,
 * though, because you can have multiple information pairs
 * with the same name inside a single entity. Information
 * pairs, unlike properties, do not reside on the declaration
 * of the entity, but are considered as children of the
 * entity itself. Refer to [InfoPair] for more information.
 *
 * Entities can be uniquely identified by an ID defined by
 * the user of the entity. Every ID must be unique inside
 * the database. An exception are the IDs that are
 * automatically generated, such as the ones for entities
 * that are inside an array-like entity.
 *
 * An entity may also be an array-like entity. Array-like
 * entities place restrictions on the type of children
 * items. Specifically they cannot have information pairs
 * as direct children and all the children entities must
 * have the same name, same properties and same first-level
 * children. If any other situation is identified, then the
 * database is ill-formed. For more information about
 * array-like entities, refer to [ArrayEntity].
 *
 * As an example, the following is a declaration of multiple
 * entities according to the XML-like syntax of SKD.
 *
 * ```xml
 * <entity property="1S" another="property" id="0">
 *     <info-pair value="nothing else" id="first" />
 *     <child id="second">
 *     </child>
 *     <array-like skd$array="">
 *         <array-item skd$id="0UL">
 *             <name value="something" />
 *         </array-item>
 *         <array-item skd$id="1UL">
 *             <name value="something else" />
 *         </array-item>
 *     </array-like>
 * </entity>
 * ```
 *
 * @since 0.3
 */
interface Entity {

    /**
     * Gets the name of this entity.
     *
     * The name of the entity can only be made up of alphanumeric
     * characters, hyphens and underscores.
     *
     * @return The name of this entity.
     *
     * @since 0.3
     */
    fun getName() : String

    /**
     * Gets the unique ID assigned to this entity.
     *
     * If this entity has no unique ID associated, then the
     * method returns an [empty optional][Optional.empty].
     *
     * This method does not allow the user to get the value
     * of an automatically generated ID. That value is in fact
     * internal and as such cannot be accessed by outside
     * consumers.
     *
     * @return An [Optional] wrapping either the id or nothing,
     * according to the results.
     *
     * @since 0.3
     */
    fun getId() : Optional<String>

    /**
     * Sets the unique ID of this entity.
     *
     * If this entity has an automatic ID associated to it,
     * this method will not modify it, but will add a manual
     * ID alongside it. This happens because the automatically
     * generated ID is internal and as such is transparent
     * to consumers.
     *
     * Explicitly passing an empty string to this method
     * will remove the ID from this entity.
     *
     * @param[id] An string representing the new ID, or an empty
     * string to remove it.
     *
     * @since 0.3
     */
    fun setId(id: String)

    /**
     * Gets whether this entity is an array-like entity.
     *
     * For more information about array-like entities, please
     * refer to [Entity].
     *
     * @since 0.3
     */
    fun isArray() : Boolean

    /**
     * Sets whether this entity is an array-like entity.
     *
     * This entity can only be set as an array-like entity
     * if it respects the contract of array-like entities.
     * More specifically, it cannot have information pairs
     * as children and all the children entities must have
     * exact same name, same properties and same first-level
     * children. If these conditions are not met, the entity
     * cannot be an array-like entity and the method throws
     * an exception.
     *
     * To set an array-like entity as a "normal" entity, there
     * are no restrictions. Implementations may choose to
     * restrict this possibility, in which case an
     * [UnsupportedOperationException] may be thrown.
     *
     * For more information about array-like entities, refer
     * to [Entity].
     *
     * @param[isArray] Whether this entity should be considered
     * an array-like entity or not.
     * @exception IllegalStateException If the entity cannot be set
     * as an array-like entity due to contract violations.
     * @exception UnsupportedOperationException If this array-like
     * entity cannot be set as a "normal" entity due to implementation
     * specific restrictions.
     *
     * @since 0.3
     */
    fun setArray(isArray: Boolean)

    /**
     * Attempts to get this entity as an instance of an
     * array-like entity.
     *
     * @return This entity as an array-like entity.
     * @exception ClassCastException If this entity cannot be
     * considered as an array-like entity.
     *
     * @since 0.3
     */
    fun asArrayEntity() : ArrayEntity

    /**
     * Gets a collection of all the properties of the entity.
     *
     * Automatically generated properties are stripped from the
     * collection that is returned.
     *
     * @return A collection of all the entity's properties.
     *
     * @since 0.3
     */
    fun getProperties() : Collection<Property<*>>

    /**
     * Adds the given property to the current entity properties.
     *
     * Properties cannot have the same names of automatically
     * generated properties or be named `id`. If the caller attempts
     * to add a property that does not respect these characteristics,
     * then the method should throw an exception.
     *
     * @param[property] The property to add.
     * @exception IllegalArgumentException If the property does not
     * respect the characteristics above.
     *
     * @since 0.3
     */
    fun addProperty(property: Property<*>)

    /**
     * Removes the given property from the entity.
     *
     * The property is removed first according to an `equals` check,
     * which means matching both `name` and `value`. If such a
     * property cannot be found, then the removal resorts to a
     * best-guess check, matching only the property name.
     *
     * If no matching properties are found, the entity is left
     * as-is. Implementations may log an error message if they
     * deem necessary to do so.
     *
     * All the properties restrictions apply.
     *
     * @param[property] The property to remove.
     * @exception IllegalArgumentException If the property does not
     * respect the property naming constraints.
     *
     * @since 0.3
     */
    fun removeProperty(property: Property<*>)

    /**
     * Removes the property which has the same name as the one
     * provided, if present.
     *
     * The property is removed only if the given name matches
     * completely the one of a property. Otherwise, no property
     * is removed.
     *
     * If no property is removed, then the entity is left
     * as-is. An error message may be logged by implementations
     * if they deem necessary to do so.
     *
     * All the properties naming restrictions apply.
     *
     * @param[name] The name of the property to remove.
     * @exception IllegalArgumentException If the property does not
     * respect the property naming constraints.
     *
     * @since 0.3
     */
    fun removePropertyByName(name: String)

    /**
     * Finds the property of this entity that has the given name.
     *
     * If no property is found, then an [empty optional][Optional.empty]
     * is returned.
     *
     * All the properties naming restrictions apply.
     *
     * @param[name] The name of the property to find.
     * @return An [Optional] wrapping either the found property or
     * nothing, according to the look-up results.
     * @exception IllegalArgumentException If the property does not
     * respect the property naming constraints.
     *
     * @since 0.3
     */
    fun findPropertyByName(name: String) : Optional<Property<*>>

    /**
     * Gets a collection of all the children entities of this
     * entity.
     *
     * @return A collection of all the children entities of this
     * entity.
     *
     * @since 0.3
     */
    fun getChildEntities() : Collection<Entity>

    /**
     * Adds an entity as a child of this entity.
     *
     * @param[entity] The entity to add.
     *
     * @since 0.3
     */
    fun addChildEntity(entity: Entity)

    /**
     * Removes the given entity from the children of this entity.
     *
     * The entity is removed first according to an `equals` check,
     * so only if the two entities match completely in all their
     * elements.
     *
     * If this fails, then a lookup is performed where
     * all child entities with the same name as the passed in
     * entity are considered. If only one entity matches, then that
     * entity is removed. If there are multiple matches, then
     * implementations need throw an exception to signal that
     * multiple matches were found and leave the entity as-is.
     *
     * If no matching entities are found in both the attempts,
     * then the entity is left as-is. Implementations may log
     * an error message if they deem necessary to do so.
     *
     * @param[entity] The entity to remove.
     * @exception UnsupportedOperationException If multiple matches
     * are found during the second look-up.
     *
     * @since 0.3
     */
    fun removeChildEntity(entity: Entity)

    /**
     * Removes the child entity identified by the given ID from
     * the children of this entity.
     *
     * If no entities with the given ID are found, then the
     * entity is left as-is. Implementations may log an error
     * message if they deem necessary to do so.
     *
     * @param[id] The ID of the entity to remove.
     *
     * @since 0.3
     */
    fun removeChildEntity(id: String)

    /**
     * Removes the child entity with the given name from the
     * children of this entity.
     *
     * If no entities with the given name are found, the entity
     * is left as-is. Implementations may log an error message
     * if they deem necessary to do so.
     *
     * If multiple entities with the given name are found, then
     * the process is marked as failed due to ambiguity. The
     * entity is thus left as-is and an exception signaling this
     * fact is thrown. If the intention is to actually delete
     * all the entities matching the given name, then refer to
     * [removeAllChildEntitiesByName].
     *
     * @param[name] The name of the entity to be removed.
     * @exception UnsupportedOperationException If multiple matches
     * are found.
     *
     * @since 0.3
     */
    fun removeChildEntityByName(name: String)

    /**
     * Removes all children entities with the given name from the
     * children of this entity.
     *
     * If no entities with the given name are found, the entity
     * is left as-is. Implementations may log an error message if
     * they deem necessary to do so.
     *
     * @param[name] The name of the entities to remove.
     *
     * @since 0.3
     */
    fun removeAllChildEntitiesByName(name: String)

    /**
     * Finds all the children entities of this entity with the given
     * name.
     *
     * If no matching entity is found, then the returned collection
     * is empty.
     *
     * @param[name] The name of the entities to find.
     * @return A collection containing all children entities with
     * the given name. The collection may be empty.
     *
     * @since 0.3
     */
    fun findChildEntitiesByName(name: String): Collection<Entity>

    /**
     * Finds the child entity of this entity with the given ID.
     *
     * If no matching entity is found, then an
     * [empty optional][Optional.empty] is returned.
     *
     * @param[id] The ID of the entity to look for.
     * @return An [Optional] wrapping either the found entity
     * or nothing, according to the results.
     *
     * @since 0.3
     */
    fun findChildEntity(id: String): Optional<Entity>

    /**
     * Gets a collection of all information pairs that are
     * children of this entity.
     *
     * @return A collection of all information pairs that are
     * children of this entity.
     *
     * @since 0.3
     */
    fun getChildInfoPairs() : Collection<InfoPair<*>>

    /**
     * Adds an information pair to the information pairs
     * children of this entity.
     *
     * @param[infoPair] The information pair to add.
     *
     * @since 0.3
     */
    fun addChildInfoPair(infoPair: InfoPair<*>)

    /**
     * Removes the given information pair from the information
     * pairs children of this entity.
     *
     * The information pair is removed first according to an
     * `equals` check, so only if the two information pairs
     * match completely in all their elements.
     *
     * If this fails, then a lookup is performed where all
     * children information pairs with the same key as the
     * passed in information pair are considered. If only one
     * information pair matches, then that information pair
     * is removed. If there are multiple matches, then
     * implementations need to throw an exception to signal that
     * multiple matches were found and leave the entity as-is.
     *
     * If no matching information pairs are found in both the
     * attempts, then the entity is left as-is. Implementations
     * may log an error message if they deem necessary to do so.
     *
     * @param[infoPair] The information pair to remove.
     * @exception UnsupportedOperationException If multiple matches
     * are found during the second look-up.
     *
     * @since 0.3
     */
    fun removeChildInfoPair(infoPair: InfoPair<*>)

    /**
     * Removes the child information pair identified by the given
     * ID from the information pairs children of this entity.
     *
     * If no information pairs with the given ID are found, then
     * the entity is left as-is. Implementations may log an error
     * message if they deem necessary to do so.
     *
     * @param[id] The ID of the information pair to remove.
     *
     * @since 0.3
     */
    fun removeChildInfoPair(id: String)

    /**
     * Removes the child information pair with the given key from
     * the information pairs children of this entity.
     *
     * If no information pairs with the given key are found, the
     * entity is left as-is. Implementations may log an error message
     * if they deem necessary to do so.
     *
     * If multiple information pairs with the given key are found,
     * then the process is marked as failed due to ambiguity. The
     * entity is thus left as-is and an exception signaling this
     * fact is thrown. If the intention is to actually delete
     * all the information pairs matching the given key, then refer
     * to [removeAllChildrenInfoPairByKey].
     *
     * @param[key] The key of the information pair to be removed.
     * @exception UnsupportedOperationException If multiple matches
     * are found.
     *
     * @since 0.3
     */
    fun removeChildInfoPairByKey(key: String)

    /**
     * Removes all children information pairs with the given key
     * from the information pairs children of this entity.
     *
     * If no information pairs with the given key are found, the
     * entity is left as-is. Implementations may log an error
     * message if they deem necessary to do so.
     *
     * @param[key] The key of the information pairs to remove.
     *
     * @since 0.3
     */
    fun removeAllChildrenInfoPairByKey(key: String)

    /**
     * Finds all the children information pairs of this entity with
     * the given key.
     *
     * If no matching information pair is found, then the returned
     * collection is empty.
     *
     * @param[key] The key of the information pairs to find.
     * @return A collection containing all children information pairs
     * with the given name. The collection may be empty.
     *
     * @since 0.3
     */
    fun findChildInfoPairsByKey(key: String) : Collection<InfoPair<*>>

    /**
     * Finds the child information pair of this entity with the
     * given ID.
     *
     * If no matching information pair is found, then an
     * [empty optional][Optional.empty] is returned.
     *
     * @param[id] The ID of the information pair to look for.
     * @return An [Optional] wrapping either the found entity
     * or nothing, according to the results.
     *
     * @since 0.3
     */
    fun findChildInfoPair(id: String) : Optional<InfoPair<*>>
}
