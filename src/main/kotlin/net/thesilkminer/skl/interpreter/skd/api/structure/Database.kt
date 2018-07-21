package net.thesilkminer.skl.interpreter.skd.api.structure

import java.util.Optional

/**
 * Represents the main structure of an SKD database.
 *
 * A general database is made up of three different parts:
 * declarations, preprocessor instructions and entities.
 *
 * Declarations reside at the top of the file and tell
 * various information about the file itself that may be
 * needed for the parser to actually read the file correctly.
 * Such examples may be the document type or the SKD version
 * the file is currently written in. Every declaration
 * must have a unique key: it is illegal for multiple
 * declarations to share the same key. If that is the case,
 * then the database is considered malformed. Refer to
 * [Declaration] for more information.
 *
 * Preprocessor instructions are instructions that are
 * present all through the database and are read by a component
 * called the preprocessor, that directly modified the file in
 * place. As an example, the preprocessor may wipe certain sections
 * of the file according to environment properties, parser-specific
 * variables or something else. Additionally, the preprocessor
 * can dump the contents of other databases and/or files inside of
 * the current database, thus providing the tools to avoid
 * redundancy of the code. Since a preprocessor instructions
 * occupies the entire line it sits on, it is identified directly by
 * the line number it resides on. Refer directly to
 * [PreprocessorInstruction] for more information.
 *
 * Entities are the main part of the database: they store all the
 * information that must be carried in the database. A database can
 * only have one single top-level entity, but each of them can have
 * an unlimited amount of children. Refer to [Entity] for more
 * specific information.
 *
 * @since 0.3
 */
interface Database {

    /**
     * Gets the list of all the declarations that are present
     * in this database.
     *
     * @return A collection of all the declarations currently
     * registered.
     *
     * @since 0.3
     */
    fun getDeclarations() : Collection<Declaration>

    /**
     * Adds the specified declaration to the ones that are
     * present in this database.
     *
     * @param[declaration] The declaration to add
     * @exception IllegalArgumentException
     *      If a declaration with the same key is already present
     *      in the current database.
     *
     * @since 0.3
     */
    fun addDeclaration(declaration: Declaration)

    /**
     * Removes the given declaration from the ones present
     * in the database.
     *
     * The declaration is removed according first to an
     * `equals` check (both `key` and `value` match), then
     * according to a best-guess method matching only the
     * declaration key.
     *
     * If no matching declaration is found, the database is
     * left as-is. An error message may be logged by implementations
     * if they deem necessary to do so.
     *
     * @param[declaration] The declaration to remove
     *
     * @since 0.3
     */
    fun removeDeclaration(declaration: Declaration)

    /**
     * Removes from the database the declaration with the
     * given key, if present.
     *
     * The declaration is removed only if the given key matches
     * completely the one of a declaration. Otherwise, no
     * declaration is removed.
     *
     * If no declaration is removed, then the database is left
     * as-is. An error message may be logged by implementations
     * if they deem necessary to do so.
     *
     * @param[key] The declaration key to look for.
     *
     * @since 0.3
     */
    fun removeDeclarationByKey(key: String)

    /**
     * Finds the declaration present inside the database that
     * matches the given key.
     *
     * If no declaration is found, then an
     * [empty optional][Optional.empty] is returned.
     *
     * @param[key] The declaration key to look for.
     * @return An [Optional] wrapping either the found declaration
     * or nothing, according to the results.
     *
     * @since 0.3
     */
    fun findDeclarationByKey(key: String) : Optional<Declaration>

    /**
     * Gets a collection of all the preprocessor instructions
     * currently present in the database.
     *
     * The list of preprocessor instructions also contains all
     * lines that have been deleted by the preprocessor itself
     * from the initial input stream (if the database is constructed
     * as a result of the parser). These deleted lines are marked
     * as a special preprocessor instruction that cannot be used in
     * normal files (like `goto` in Java code, for example).
     * If the database needs to be serialized back into an
     * output stream, these lines are then rewritten to the stream
     * exactly as they were.
     *
     * The line numbers the preprocessor instructions are identified
     * with are not necessarily meaningful. Since entities do not store
     * a concept of line number, it is highly impossible to perfectly
     * represent the entirety of preprocessor lines exactly as they
     * appear in the file. That is exactly why some number may seem
     * weird while computation (`-1`, [Long.MAX_VALUE]...). Refer to
     * [PreprocessorInstruction] for more information.
     *
     * @return A collection of all preprocessor instructions
     * found inside the database.
     *
     * @since 0.3
     */
    fun getPreprocessorInstructions() : Collection<PreprocessorInstruction>

    /**
     * Adds the given preprocessor instruction to the database.
     *
     * If the line number of the given preprocessor instruction
     * (let's call it `A` for brevity) is already occupied by
     * another preprocessor instruction (called `B`), then the
     * line number of `A` is augmented by `1` and then the process
     * is repeated. If another preprocessor instruction (called
     * `C`) has the new line number, then `C`'s line number is
     * augmented by one and so on recursively. Then `A` is added
     * in-between `B` and `C`.
     *
     * For a more visual representation:
     * **Before:** `BCDEF`
     * **After:** `BACDEF`
     *
     * @param[preprocessorInstruction] The preprocessor instruction to add.
     *
     * @since 0.3
     */
    fun addPreprocessorInstruction(preprocessorInstruction: PreprocessorInstruction)

    /**
     * Removes the given preprocessor instruction from the database.
     *
     * The instruction is removed according first to an `equals` check
     * (both the line number and the instruction match), then according
     * to a best-guess method matching only the line number.
     *
     * If no matching declaration is found, the database is left as-is.
     * Implementations may output an error message if they deem
     * necessary to do so.
     *
     * If a matching declaration is found, then the preprocessor instruction
     * is removed, but no other preprocessor instruction is modified. In
     * other words, if other line numbers were modified to account for the
     * addition of this preprocessor instruction, then these edits are
     * not rolled back.
     *
     * @param[preprocessorInstruction] The preprocessor instruction to remove.
     *
     * @since 0.3
     */
    fun removePreprocessorInstruction(preprocessorInstruction: PreprocessorInstruction)

    /**
     * Removes from the database the preprocessor instruction that
     * has the specified line number.
     *
     * If no matching declaration is found, no removal happens and the
     * database is left as-is. Implementations may output an error
     * message if they deem necessary to do so.
     *
     * If a matching declaration is found, then the preprocessor
     * instruction is removed, but no other preprocessor instruction
     * is modified. In other words, if other line numbers were
     * modified to account for the addition of the found preprocessor
     * instruction, then these edits are not rolled back.
     *
     * @param[lineNumber] The line number of the preprocessor instruction to remove.
     *
     * @since 0.3
     */
    fun removePreprocessorInstructionByLineNumber(lineNumber: Long)

    /**
     * Finds the preprocessor instruction present in the database that
     * has the specified line number.
     *
     * In case no preprocessor instruction can be found, the method
     * returns an [empty optional][Optional.empty].
     *
     * @param[lineNumber] The line number of the preprocessor instruction to find.
     * @return An [Optional] wrapping either the found preprocessor instruction
     * or nothing, according to the results.
     *
     * @since 0.3
     */
    fun findPreprocessorInstructionByLineNumber(lineNumber: Long) : Optional<PreprocessorInstruction>

    /**
     * Gets the main entity of this database.
     *
     * @return The main entity of this database.
     *
     * @since 0.3
     */
    fun getMainEntity() : Entity

    /**
     * Sets the main entity of this database.
     *
     * @param[entity] The main entity of this database.
     *
     * @since 0.3
     */
    fun setMainEntity(entity: Entity)

    /**
     * Gets the entity declared in the database with the specified ID.
     *
     * If no suitable entity is found, then the lookup is considered
     * failed and an [empty optional][Optional.empty] is returned.
     *
     * If multiple entities are found, then the lookup is considered
     * failed and the database is marked as ill-formed. The method
     * may as such throw an exception.
     *
     * @param[id] The ID of the entity to look up.
     * @return An [Optional] wrapping either the found entity or nothing,
     * according to the lookup result.
     * @exception IllegalStateException If multiple entities with the same
     * ID were found.
     *
     * @since 0.3
     */
    fun getEntityFromId(id: String) : Optional<Entity>
}
