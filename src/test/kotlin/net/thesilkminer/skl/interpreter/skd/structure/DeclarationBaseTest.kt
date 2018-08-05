package net.thesilkminer.skl.interpreter.skd.structure

import net.thesilkminer.skl.interpreter.skd.api.structure.Declaration
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class DeclarationBaseTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            println("Starting test for ${this::class.java.name}")
        }
    }

    private var doctypeDeclaration: DeclarationBase? = null
    private var versionDeclaration: DeclarationBase? = null
    private var customDeclaration: DeclarationBase? = null

    @Before
    fun setUp() {
        this.doctypeDeclaration = DeclarationBase("DOCTYPE", "skd")
        this.versionDeclaration = DeclarationBase("SKD", "0.3")
        this.customDeclaration = DeclarationBase("CUSTOM", "IT'S LIT FAM!")
    }

    @After
    fun tearDown() {
        this.doctypeDeclaration = null
        this.versionDeclaration = null
        this.customDeclaration = null
    }

    @Test(expected = IllegalArgumentException::class)
    fun createDeclarationNonUppercaseKeyTest() {
        DeclarationBase("non-uppercase-key", "")
    }

    @Test(expected = IllegalArgumentException::class)
    fun createDeclarationInvalidKeyTest() {
        DeclarationBase("skd\$catch-all", "")
    }

    @Test
    fun getDeclarationTypeTest() {
        Assert.assertEquals(Declaration.DeclarationType.DOCUMENT_TYPE, this.doctypeDeclaration?.getDeclarationType())
        Assert.assertEquals(Declaration.DeclarationType.SKD_VERSION, this.versionDeclaration?.getDeclarationType())
        Assert.assertEquals(Declaration.DeclarationType.CUSTOM, this.customDeclaration?.getDeclarationType())
    }

    @Test
    fun getKeyTest() {
        Assert.assertEquals("DOCTYPE", this.doctypeDeclaration?.getKey())
        Assert.assertEquals("SKD", this.versionDeclaration?.getKey())
        Assert.assertEquals("CUSTOM", this.customDeclaration?.getKey())
    }

    @Test
    fun getValueTest() {
        Assert.assertEquals("skd", this.doctypeDeclaration?.getValue())
        Assert.assertEquals("0.3", this.versionDeclaration?.getValue())
        Assert.assertEquals("IT'S LIT FAM!", this.customDeclaration?.getValue())
    }

    @Test
    fun setValueTest() {
        val newValue = "https://github.com/"
        this.customDeclaration?.setValue(newValue)
        Assert.assertEquals(newValue, this.customDeclaration?.getValue())
    }

    @Test(expected = IllegalArgumentException::class)
    fun setValueEmptyStringTest() {
        this.customDeclaration?.setValue("")
    }
}
