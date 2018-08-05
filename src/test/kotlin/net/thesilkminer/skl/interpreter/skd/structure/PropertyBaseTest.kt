package net.thesilkminer.skl.interpreter.skd.structure

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class PropertyBaseTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            println("Starting test for ${this::class.java.name}")
        }
    }

    object Temporary

    class TemporaryClass

    @Before
    fun setUp() = Unit

    @After
    fun tearDown() = Unit

    @Test(expected = IllegalArgumentException::class)
    fun createPropertyEmptyNameTest() {
        this.buildPropertyEmptyValue("")
    }

    @Test(expected = IllegalArgumentException::class)
    fun createPropertyNameIdTest() {
        this.buildPropertyEmptyValue("Id")
    }

    @Test(expected = IllegalArgumentException::class)
    fun createPropertyInvalidCharactersInNameTest() {
        this.buildPropertyEmptyValue("\u0001")
    }

    @Test
    fun getNameTest() {
        val name = "property-name"
        val property = this.buildPropertyEmptyValue(name)
        Assert.assertEquals(name, property.getName())
    }

    @Test
    fun getValueTest() {
        val propertyString = PropertyBase("string", "a string")
        val propertyLong = PropertyBase("long", 100L)
        val propertyTemp = PropertyBase("temp", Temporary)
        Assert.assertEquals("a string", propertyString.getValue())
        Assert.assertEquals(100L, propertyLong.getValue())
        Assert.assertEquals(Temporary, propertyTemp.getValue())
    }

    @Test
    fun getUnitValueTest() {
        val property = this.buildPropertyEmptyValue("temporary")
        Assert.assertEquals(Unit, property.getValue())
    }

    @Test
    fun setValueTest() {
        val newValue = TemporaryClass()
        val property = PropertyBase("temp", TemporaryClass())
        property.setValue(newValue)
        Assert.assertEquals(newValue, property.getValue())
    }

    @Test
    fun getValueAsStringTest() {
        val propertyByte = PropertyBase("byte", 10.toByte())
        val propertyChar = PropertyBase("char", 'c')
        val propertyUnit = this.buildPropertyEmptyValue("unit")
        Assert.assertEquals("10B", propertyByte.getValueAsString())
        Assert.assertEquals("c\$", propertyChar.getValueAsString())
        Assert.assertEquals("kotlin.Unit", propertyUnit.getValueAsString())
    }

    @Test
    fun setValueFromStringTest() {
        val propertyByte = PropertyBase("byte", 0.toByte())
        val propertyChar = PropertyBase("char", 0.toChar())
        val propertyString = PropertyBase("string", "")
        propertyByte.setValueFromString("10B")
        propertyChar.setValueFromString("l\$")
        propertyString.setValueFromString("string")
        Assert.assertEquals(10.toByte(), propertyByte.getValue())
        Assert.assertEquals('l', propertyChar.getValue())
        Assert.assertEquals("string", propertyString.getValue())
    }

    @Test(expected = NullPointerException::class)
    fun setValueFromStringNullTest() {
        try {
            PropertyBase("string", "non-null string").setValueFromString("skd\$null")
        } catch (e: IllegalArgumentException) {
            throw if (e.cause != null) e.cause as Throwable else e
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun setValueFromStringNotParsableTest() {
        PropertyBase("test", Temporary).setValueFromString(Temporary.toString())
    }

    @Test
    fun setValueFromStringUnitTest() {
        this.buildPropertyEmptyValue("lol").setValueFromString(Unit.toString())
    }

    private fun buildPropertyEmptyValue(name: String): PropertyBase<Unit> = PropertyBase(name, Unit)
}
