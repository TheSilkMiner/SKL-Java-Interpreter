package net.thesilkminer.skl.interpreter.skd.api.structure

/**
 * Represents a preprocessor instruction inside a database.
 *
 * These instructions are present all through the file and are
 * read by a special component called the preprocessor. This
 * component edits directly the input stream applying the needed
 * modifications, deleting lines it has to delete and dumping
 * files it has to include in the database itself.
 *
 * Deleted lines are still present, but are marked by a special
 * and illegal preprocessor instruction that allows them not to
 * be lost, but at the same time prevents their successful
 * parsing.
 *
 * Every preprocessor instruction is uniquely identified by its
 * line number, since every preprocessor instruction must reside
 * on its own line. The line number is not necessarily meaningful,
 * though. Since preprocessor instructions can appear anywhere
 * inside the file and entities do not have a concept of line
 * numbers, the values may return something apparently weird
 * (such as `-1`, [Long.MAX_VALUE]...).
 *
 * The syntax of a preprocessor instruction is extremely simple:
 * `<#instruction>`, where `instruction` is composed of a
 * `keyword` that appears directly beside the dash sign and the
 * rest of the instruction on the rest of the line. The keyword
 * is then parsed and mapped to a specific [InstructionType].
 *
 * @since 0.3
 */
interface PreprocessorInstruction {

    @Retention(AnnotationRetention.SOURCE)
    @Target(AnnotationTarget.FIELD)
    private annotation class InternalInstruction

    /**
     * Identifies a type of preprocessor instruction.
     *
     * Every preprocessor instruction is automatically identified by the
     * preprocessor itself while parsing. Illegal instructions are
     * automatically added by the preprocessor while parsing a file and
     * removed while writing it back to an output stream. If an illegal
     * instruction is identified, then the preprocessor throws an error.
     *
     * The instruction type must match the preprocessor instruction.
     *
     * @since 0.3
     *
     * <!-- Weird, huh? I need to specify it twice because of KDoc special types -->
     *
     * @property[keyword] The keyword that identifies the instruction type.
     *
     * @since 0.3
     */
    enum class InstructionType(val keyword: String) {
        DEFINE_ENVIRONMENT_PROPERTY("def"),
        DEFINE_ENVIRONMENT_VARIABLE("var"),
        DEFINE_MACRO("mac"),
        SYNTAX("syntax"),
        @InternalInstruction DELETED_LINE("dln"), // Invalid instruction
        THROW_ERROR("throw"),
        IF_VARIABLE_OR_PROPERTY_DEFINED("ifdef"),
        IF_VARIABLE_OR_PROPERTY_NOT_DEFINED("ifndef"),
        ELSE("else"),
        END_IF_STATEMENT("endif"),
        INCLUDE_FILE("include"),
        @InternalInstruction INCLUDE_FILE_BEGIN_MARK("ibg"), // Invalid instruction
        @InternalInstruction INCLUDE_FILE_END_MARK("ied"), // Invalid instruction
        SUB_ENTITY_NAME_ASSIGNATION("nameassign");

        companion object {
            /**
             * Gets the corresponding preprocessor instruction type from
             * the given keyword.
             *
             * @param[keyword] The keyword to look up
             * @exception IllegalArgumentException If the given key does
             * not have a match in the enum constants
             * @return The instruction type that matches the given keyword,
             * if available.
             *
             * @since 0.3
             */
            fun fromKeyword(keyword: String) : InstructionType {
                val values = InstructionType.values()
                for (i in 0..values.size) {
                    if (values[i].keyword == keyword) return values[i]
                }
                throw IllegalArgumentException("Invalid keyword specified")
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
    fun getInstructionType() : InstructionType

    /**
     * Gets the instruction that is carried by this preprocessor instruction.
     *
     * @return The instruction carried by this preprocessor instruction.
     *
     * @since 0.3
     */
    fun getInstruction() : String

    /**
     * Sets the instruction carried by this preprocessor instruction.
     *
     * The given `instruction` and `instructionType` have to be compatible
     * in terms of parameters and keywords. Implementations have to check
     * this specification.
     *
     * @param[instructionType] The type of instruction of this preprocessor instruction.
     * @param[instruction] The instruction carried by this preprocessor instruction.
     * @exception IllegalArgumentException If the two parameters are not compatible with each other.
     *
     * @since 0.3
     */
    fun setInstructionAndType(instructionType: InstructionType, instruction: String)

    /**
     * Gets the line number associated with this preprocessor instruction.
     *
     * @return The line number associated with this preprocessor instruction.
     *
     * @since 0.3
     */
    fun getLineNumber() : Long

    /**
     * Sets the line number of this preprocessor instruction.
     *
     * It is generally not advised to manually edit this value because of the
     * constraints of the database where this preprocessor instruction is in.
     * Also, the value already present even if meaningless, may carry some
     * important information for the parser and serializer.
     *
     * @param[lineNumber] The line number of this preprocessor instruction.
     *
     * @since 0.3
     */
    fun setLineNumber(lineNumber: Long)
}