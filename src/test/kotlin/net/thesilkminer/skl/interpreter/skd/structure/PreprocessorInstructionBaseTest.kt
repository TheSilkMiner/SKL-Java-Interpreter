package net.thesilkminer.skl.interpreter.skd.structure

import net.thesilkminer.skl.interpreter.skd.api.structure.PreprocessorInstruction
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class PreprocessorInstructionBaseTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            println("Starting test for ${this::class.java.name}")
        }
    }

    @Before
    fun setUp() = Unit

    @After
    fun tearDown() = Unit

    @Test
    fun buildInstructionTest() {
        Assert.assertTrue(with(PreprocessorInstructionBase("def __CJSON_SUPPORTED", 3L)) { true })
    }

    @Test(expected = IllegalArgumentException::class)
    fun buildInvalidKeywordTest() {
        PreprocessorInstructionBase("invented ARG0", -1L)
    }

    @Test(expected = IllegalArgumentException::class)
    fun buildInvalidArgumentsAmountTest() {
        PreprocessorInstructionBase("var __IS_SCANNING TRUE" + " __INVALID__", 5L)
    }

    @Test
    fun getInstructionTypeTest() {
        PreprocessorInstruction.InstructionType.values().forEach {
            Assert.assertEquals(it, this.buildFromType(it).getInstructionType())
        }
    }

    @Test
    fun getInstructionTest() {
        val `else` = PreprocessorInstructionBase("else", 10L)
        val `throw` = PreprocessorInstructionBase("throw RuntimeException", 10L)
        val nameAssign = PreprocessorInstructionBase("nameassign phones phone", 10L)
        val ibg = PreprocessorInstructionBase("ibg", 10L)
        Assert.assertEquals("else", `else`.getInstruction())
        Assert.assertEquals("throw RuntimeException", `throw`.getInstruction())
        Assert.assertEquals("nameassign phones phone", nameAssign.getInstruction())
        Assert.assertEquals("ibg", ibg.getInstruction())
    }

    @Test
    fun setInstructionAndTypeTest() {
        val instruction = PreprocessorInstructionBase("syntax cxml", 4L)
        val newInstruction = "ifdef __CXML_SUPPORTED"
        val newType = PreprocessorInstruction.InstructionType.fromKeyword("ifdef")
        Assert.assertEquals(PreprocessorInstruction.InstructionType.IF_VARIABLE_OR_PROPERTY_DEFINED, newType)
        instruction.setInstructionAndType(newType, newInstruction)
        Assert.assertEquals(newInstruction, instruction.getInstruction())
        Assert.assertEquals(newType, instruction.getInstructionType())
    }

    @Test(expected = IllegalArgumentException::class)
    fun setInstructionAndTypeMismatchTest() {
        val instruction = PreprocessorInstructionBase("var json_enabled true", 60L)
        instruction.setInstructionAndType(PreprocessorInstruction.InstructionType.ELSE, "endif")
    }

    @Test
    fun getLineNumberTest() {
        val first = PreprocessorInstructionBase("ifndef __CJSON_SUPPORTED", 100L)
        val second = PreprocessorInstructionBase("def __CJSON_SUPPORTED", 101L)
        val third = PreprocessorInstructionBase("endif", -1L)
        Assert.assertEquals(100L, first.getLineNumber())
        Assert.assertEquals(101L, second.getLineNumber())
        Assert.assertEquals(-1L, third.getLineNumber())
    }

    @Test
    fun setLineNumberTest() {
        val instruction = this.buildFromType(PreprocessorInstruction.InstructionType.THROW_ERROR)
        instruction.setLineNumber(100L)
        Assert.assertEquals(100L, instruction.getLineNumber())
        instruction.setLineNumber(-1L)
        Assert.assertEquals(-1L, instruction.getLineNumber())
        instruction.setLineNumber(Long.MAX_VALUE)
        Assert.assertEquals(Long.MAX_VALUE, instruction.getLineNumber())
    }

    private fun buildFromType(type: PreprocessorInstruction.InstructionType): PreprocessorInstruction {
        return when (type) {
            // No arguments
            PreprocessorInstruction.InstructionType.ELSE,
            PreprocessorInstruction.InstructionType.END_IF_STATEMENT -> {
                PreprocessorInstructionBase(type.keyword, -1L)
            }
            // 1 argument
            PreprocessorInstruction.InstructionType.DEFINE_ENVIRONMENT_PROPERTY,
            PreprocessorInstruction.InstructionType.SYNTAX,
            PreprocessorInstruction.InstructionType.IF_VARIABLE_OR_PROPERTY_DEFINED,
            PreprocessorInstruction.InstructionType.IF_VARIABLE_OR_PROPERTY_NOT_DEFINED,
            PreprocessorInstruction.InstructionType.THROW_ERROR,
            PreprocessorInstruction.InstructionType.INCLUDE_FILE -> {
                PreprocessorInstructionBase("${type.keyword} ARG1", -1L)
            }
            // 2 arguments
            PreprocessorInstruction.InstructionType.DEFINE_ENVIRONMENT_VARIABLE,
            PreprocessorInstruction.InstructionType.SUB_ENTITY_NAME_ASSIGNATION -> {
                PreprocessorInstructionBase("${type.keyword} ARG1 ARG2", -1L)
            }
            // More than 2 arguments
            PreprocessorInstruction.InstructionType.DEFINE_MACRO -> {
                PreprocessorInstructionBase("${type.keyword} ARG1 ARG2 ARG3", -1L)
            }
            // Unspecified
            PreprocessorInstruction.InstructionType.DELETED_LINE,
            PreprocessorInstruction.InstructionType.INCLUDE_FILE_BEGIN_MARK,
            PreprocessorInstruction.InstructionType.INCLUDE_FILE_END_MARK -> {
                PreprocessorInstructionBase("${type.keyword} ARG0", -1L)
            }
        }
    }
}
