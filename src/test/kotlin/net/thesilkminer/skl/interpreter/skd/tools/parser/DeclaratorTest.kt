package net.thesilkminer.skl.interpreter.skd.tools.parser

import net.thesilkminer.skl.interpreter.skd.expectExceptions
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import kotlin.reflect.KClass

class DeclaratorTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            println("Starting test for ${this::class.java.name}")
        }
    }

    @JvmField
    @Rule
    val expectedException = ExpectedException.none()!!

    @Before
    fun setUp() = println("\n============ TEST BEGIN ============\n")

    @After
    fun tearDown() = println("\n============ TEST END ============\n\n")

    @Test
    fun testAllDeclarationsParsing() {
        val lines = mutableListOf("<!DOCTYPE skd>", "<!SKD 0.3>", "", "<!KEY value>", "", "<#no-parse>")
        val declarator = Declarator(lines)
        declarator.parse()
        val declarations = declarator.declarations
        Assert.assertEquals(3, declarations.count())
        Assert.assertEquals("DOCTYPE", declarations[0].getKey())
    }

    @Test
    fun testLegacyParsingForZeroPointOne() {
        println("*** 0.1 legacy parsing")
        val lines = mutableListOf("<!DOCTYPE skd>", "<SKD 0.1>")
        Declarator(lines).parse()
        // Examine logs to ensure test passes - best done in INTELLIJ IDEA
    }

    @Test
    fun testLegacyParsingForZeroPointTwo() {
        println("*** 0.2 legacy parsing")
        val lines = mutableListOf("<!DOCTYPE skd>", "<SKD 0.2>")
        Declarator(lines).parse()
    }

    @Test
    fun testLegacyParsingForZeroPointThreeAndFollowing() {
        println("***0.3 legacy parsing")
        this.expectExceptionWhileParsing(UnsupportedLegacyDeclarationException::class, "<!DOCTYPE skd>", "<SKD 0.3>")
    }

    @Test
    fun testRequiredDeclarationMissing() =
            this.expectExceptionWhileParsing(RequiredDeclarationNotFoundException::class, "<!DOCTYPE skd>")

    @Test
    fun testMultipleDeclarationsWithSameKey() =
            this.expectExceptionWhileParsing(MultipleDeclarationsWithSameKeyFoundException::class,
                "<!DOCTYPE skd>", "<!SKD 0.3>", "<!SKD 0.3>")

    @Test
    fun testDeclarationsInPlacesTheyShouldNotBe() =
            this.expectExceptionWhileParsing(MisplacedDeclarationException::class,
                "<!DOCTYPE skd>", "<!SKD 0.3>", "", "<#syntax json>", "", "<!KEY value>", "<#endif>")

    @Test
    fun testDocTypeDeclarationMissingOnFirstLine() = this.expectExceptionWhileParsing(DoctypeNotFoundException::class,
            "", "<!DOCTYPE skd>", "<!SKD 0.3>")

    @Test
    fun testDocTypeNotValid() = this.expectExceptionWhileParsing(DoctypeInvalidException::class, "<!DOCTYPE html>", "<!SKD 0.3>")

    @Test
    fun testInvalidDeclaration() = this.expectExceptionWhileParsing(DeclarationNotValidException::class,
            "<!DOCTYPE skd>", "<!SKD 0.3>", "<!INVALID>")

    private fun <T: Exception> expectExceptionWhileParsing(exception: KClass<T>, vararg lines: String) {
        expectExceptions(this.expectedException, exception, *lines) {
            Declarator(it).parse()
        }
    }
}