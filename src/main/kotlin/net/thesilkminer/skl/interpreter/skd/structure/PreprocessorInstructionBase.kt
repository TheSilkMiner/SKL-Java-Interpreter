package net.thesilkminer.skl.interpreter.skd.structure

import net.thesilkminer.skl.interpreter.skd.api.structure.PreprocessorInstruction

class PreprocessorInstructionBase(instruction: String, lineNumber: Long) : PreprocessorInstruction {

    private var instruction: String
    private var type: PreprocessorInstruction.InstructionType
    private var lineNumber: Long

    init {
        val keyword = if (instruction.contains(' ')) instruction.split(' ', limit = 2)[0] else instruction
        try {
            this.type = PreprocessorInstruction.InstructionType.fromKeyword(keyword)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Given instruction is not recognized by the preprocessor", e)
        }
        val exception = this.ensureInstructionIsValidForType(instruction, this.type)
        if (exception != null) {
            throw IllegalArgumentException("Instruction $instruction and type ${this.type} mismatch", exception)
        }
        this.instruction = instruction
        this.lineNumber = lineNumber
    }

    private fun ensureInstructionIsValidForType(instruction: String, type: PreprocessorInstruction.InstructionType): Exception? {
        val keyword = if (instruction.contains(' ')) instruction.split(' ', limit = 2)[0] else instruction
        val foundType : PreprocessorInstruction.InstructionType
        try {
            foundType = PreprocessorInstruction.InstructionType.fromKeyword(keyword)
        } catch (e: IllegalArgumentException) {
            return IllegalArgumentException("Given instruction is not recognized by the preprocessor", e)
        }

        if (foundType != type) {
            return IllegalArgumentException("Given type and instruction mismatch: found $foundType, expected $type")
        }

        // TODO("Allow quoting to circumvent spaces (e.g. <#var STRING \"with spaces\">)")
        val parameters = instruction.removePrefix(keyword).trim().split(' ').toMutableList()
        parameters.removeIf { it.isBlank() }
        val parameterCount = parameters.count()

        when (type) {
            PreprocessorInstruction.InstructionType.DEFINE_ENVIRONMENT_PROPERTY -> {
                if (parameterCount != 1) return IllegalArgumentException("You need one and only one parameter for def")
            }
            PreprocessorInstruction.InstructionType.DEFINE_ENVIRONMENT_VARIABLE -> {
                if (parameterCount != 2) return IllegalArgumentException("var needs two parameters")
            }
            PreprocessorInstruction.InstructionType.DEFINE_MACRO -> {
                if (parameterCount < 2) return IllegalArgumentException("Macro needs at least two parameters")
            }
            PreprocessorInstruction.InstructionType.SYNTAX -> {
                if (parameterCount != 1) return IllegalArgumentException("syntax instruction only supports one parameter")
            }
            PreprocessorInstruction.InstructionType.THROW_ERROR -> {
                if (parameterCount < 1) return IllegalArgumentException("throw needs at least one parameter")
            }
            PreprocessorInstruction.InstructionType.IF_VARIABLE_OR_PROPERTY_DEFINED,
            PreprocessorInstruction.InstructionType.IF_VARIABLE_OR_PROPERTY_NOT_DEFINED -> {
                if (parameterCount != 1) return IllegalArgumentException("If construct only accepts one parameter")
            }
            PreprocessorInstruction.InstructionType.ELSE -> {
                if (parameterCount != 0) return IllegalArgumentException("else does not want parameters")
            }
            PreprocessorInstruction.InstructionType.END_IF_STATEMENT -> {
                if (parameterCount != 0) return IllegalArgumentException("End if construct marker must not have parameters")
            }
            PreprocessorInstruction.InstructionType.INCLUDE_FILE -> {
                if (parameterCount < 1) return IllegalArgumentException("Missing file inclusion specification")
            }
            PreprocessorInstruction.InstructionType.SUB_ENTITY_NAME_ASSIGNATION -> {
                if (parameterCount != 2) return IllegalArgumentException("nameassign needs two parameters")
            }
            else -> {
                // Let's not check internal instructions
                // The preprocessor wouldn't parse them anyway
            }
        }

        return null
    }

    override fun getInstructionType(): PreprocessorInstruction.InstructionType = this.type

    override fun getInstruction(): String = this.instruction

    override fun setInstructionAndType(instructionType: PreprocessorInstruction.InstructionType, instruction: String) {
        val e = this.ensureInstructionIsValidForType(instruction, instructionType)
        if (e != null) {
            throw IllegalArgumentException("Instruction $instruction and type $instructionType mismatch: unable to set", e)
        }
        this.instruction = instruction
        this.type = instructionType
    }

    override fun getLineNumber(): Long = this.lineNumber

    override fun setLineNumber(lineNumber: Long) {
        this.lineNumber = lineNumber
    }
}
