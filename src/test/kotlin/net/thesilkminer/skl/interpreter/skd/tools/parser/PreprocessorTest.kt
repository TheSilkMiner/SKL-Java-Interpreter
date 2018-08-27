package net.thesilkminer.skl.interpreter.skd.tools.parser

import net.thesilkminer.skl.interpreter.skd.expectExceptions
import net.thesilkminer.skl.interpreter.skd.l
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import kotlin.reflect.KClass

class PreprocessorTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            println("Starting test for ${this::class.java.name}")
        }
    }

    class NoMessageException: Exception()

    @JvmField
    @Rule
    val expectedException = ExpectedException.none()!!

    @Before
    fun setUp() = println("\n============ TEST BEGIN ============\n")

    @After
    fun tearDown() = println("\n============ TEST END ============\n\n")

    @Test
    fun testNormalPreprocessorRun() {
        val lines = l("<!DOCTYPE skd>", "<#syntax xml>", "<#def EXAMPLE>", "<entity>", "<#nameassign one two>", "</entity>")
        val preprocessor = Preprocessor(lines)
        preprocessor.parse()
        val list = preprocessor.instructions
        Assert.assertEquals(3, list.count())
        Assert.assertEquals("def EXAMPLE", PIWrapper(list[1]).getInstruction())
    }

    @Test
    fun testIfClauses() {
        val lines = l("<#def DAT>", "<#ifdef DAT>", "<#syntax json>", "<#else>", "<#syntax xml>", "<#endif>")
        val pp = Preprocessor(lines)
        pp.parse()
        val query = pp.query<String>(QueryType.SYNTAX)
        Assert.assertEquals("json", query)
    }

    @Test
    fun testInvertedIfClauses() {
        val lines = l("<#def DAT>", "<#ifndef DAT>", "<#syntax json>", "<#else>", "<#syntax xml>", "<#endif>")
        val pp = Preprocessor(lines)
        pp.parse()
        val query = pp.query<String>(QueryType.SYNTAX)
        Assert.assertEquals("xml", query)
    }

    @Test
    fun testMacroDeclarationAndReplacement() {
        val lines = l("<#mac DATA(a, b, c, data) a; b && c + data>", "<entity DATA(1, 2, 3, lol) />")
        Preprocessor(lines).parse()
        Assert.assertEquals("<entity 1; 2 && 3 + lol />", lines[1])
    }

    @Test
    fun testVariableDeclarationAndReplacement() {
        val lines = l("<#var SILK is_awesome>", "<entity test=\" SILK \">", "<entity test=\"SILK\">", "<entity test=\"olo\">")
        Preprocessor(lines).parse()
        Assert.assertEquals("<entity test=\" is_awesome \">", lines[1])
        Assert.assertEquals("<entity test=\"is_awesome\">", lines[2])
        Assert.assertEquals("<entity test=\"olo\">", lines[3])
    }

    @Test(expected = NotImplementedError::class)
    fun testVariableWithSpacesDeclarationAndReplacement() {
        TODO("Still needs support in PreprocessorInstructionBase -> refer to it for TODO")
    }

    @Test
    fun testQuerySyntax() {
        val lines = l("<#syntax json>")
        val pp = Preprocessor(lines)
        pp.parse()
        val syntax = pp.query<String>(QueryType.SYNTAX)
        Assert.assertEquals("json", syntax)
    }

    @Test
    fun testNameAssignQuery() {
        val lines = l("<#nameassign one two>", "<#nameassign three four>", "<#nameassign five one>")
        val pp = Preprocessor(lines)
        pp.parse()
        val nameAssign = pp.query<Map<String, String>>(QueryType.NAME_ASSIGN)
        Assert.assertNotNull(nameAssign)
        Assert.assertEquals(3, nameAssign!!.count())
        val map = mapOf("one" to "two", "three" to "four", "five" to "one")
        map.entries.forEachIndexed { i, it ->
            nameAssign.entries.forEachIndexed { j, them ->
                if (i == j) {
                    Assert.assertEquals(it.key, them.key)
                    Assert.assertEquals(it.value, them.value)
                }
            }
        }
    }

    @Test
    fun testImplicitEndIfAtEndOfFile() {
        val lines = l("<#ifdef DATA>", "<#ifndef JSON>", "<#ifdef NADA>", "<#endif>", "<#ifndef ASD>")
        Preprocessor(lines).parse()
        // Check the logs to actually see if test passed
    }

    @Test(expected = NotImplementedError::class) // TODO()
    fun testFileInclusion() {
        TODO("Need to implement the entirety of the mechanism to allow pseudo-mocking or anyway something similar")
    }

    @Test
    fun testDeletedLineInternalOnlyDeclaration() = this.expectException(InternalOnlyPreprocessorInstructionException::class,
            "<#dln whatever>")

    @Test
    fun testIncludeBeginInternalOnlyDeclaration() = this.expectException(InternalOnlyPreprocessorInstructionException::class,
            "<#ibg>", "bla bla bla", "<#ied>")

    @Test
    fun testThrowErrorWithOnlyMessage() = this.expectException(ThrowException::class, "<#throw test>")

    @Test
    fun testThrowErrorWithClass() = this.expectException(NoWhenBranchMatchedException::class,
            "<#throw kotlin.NoWhenBranchMatchedException no, not really>")

    @Test
    fun testThrowErrorWithReflectiveClass() = this.expectException(NoMessageException::class,
            "<#throw ${NoMessageException::class.java.name} hey, this should be in the cause>")

    @Test
    fun testUnmatchedElse() = this.expectException(IllegalElseLocationException::class, "<#else>")

    @Test
    fun testMultipleElse() = this.expectException(IllegalElseLocationException::class,
            "<#ifdef __C_XML_SUPPORTED>", "<#else>", "<#else>")

    @Test
    fun testUnmatchedEndIf() = this.expectException(IllegalEndIfLocationException::class,
            "<#ifndef __C_XML_SUPPORTED>", "<#endif>", "<#endif>")

    @Test
    fun testUnknownInstruction() = this.expectException(InvalidPreprocessorInstructionException::class, "<#hello>")

    @Test
    fun testFailureOnMultipleInvalidSyntaxDeclaration() = this.expectException(IllegalStateException::class,
            "<#syntax xml>", "<#syntax json>")

    private fun <T: Exception> expectException(exception: KClass<T>, vararg lines: String) {
        expectExceptions(this.expectedException, exception, *lines) {
            Preprocessor(it).parse()
        }
    }
}
