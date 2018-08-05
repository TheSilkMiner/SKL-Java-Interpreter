package net.thesilkminer.skl.interpreter.skd.structure

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class InfoPairBaseTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            println("Starting test for ${this::class.java.name}")
        }
    }

    object Object

    data class Temp(val value: Int)

    @Before
    fun setUp() = Unit

    @After
    fun tearDown() = Unit

    @Test(expected = IllegalArgumentException::class)
    fun createInfoPairEmptyKeyTest() {
        this.buildInfoPairUnitValue("")
    }

    @Test
    fun getKeyTest() {
        val key = "info-pair"
        val infoPair = this.buildInfoPairUnitValue(key)
        Assert.assertEquals(key, infoPair.getKey())
    }

    @Test
    fun getValueTest() {
        val stringValue = InfoPairBase("string", "a string")
        val longValue = InfoPairBase("long", 100L)
        val objectValue = InfoPairBase("object", Object)
        val unitValue = this.buildInfoPairUnitValue("unit")
        Assert.assertEquals("a string", stringValue.getValue())
        Assert.assertEquals(100L, longValue.getValue())
        Assert.assertEquals(Object, objectValue.getValue())
        Assert.assertEquals(Unit, unitValue.getValue())
    }

    @Test
    fun setValueTest() {
        val infoPair = InfoPairBase("key", Temp(0))
        val newValue = Temp(1)
        infoPair.setValue(newValue)
        Assert.assertEquals(newValue, infoPair.getValue())
        Assert.assertEquals(newValue.value, infoPair.getValue().value)
    }

    @Test
    fun getValueAsStringTest() {
        val infoPairByte = InfoPairBase("byte", 10.toByte())
        val infoPairChar = InfoPairBase("char", 'c')
        val infoPairUnit = this.buildInfoPairUnitValue("unit")
        val infoPairTemp = InfoPairBase("temp", Temp(100))
        Assert.assertEquals("10B", infoPairByte.getValueAsString())
        Assert.assertEquals("c\$", infoPairChar.getValueAsString())
        Assert.assertEquals("kotlin.Unit", infoPairUnit.getValueAsString())
        Assert.assertEquals(Temp(100).toString(), infoPairTemp.getValueAsString())
    }

    @Test
    fun setValueFromStringTest() {
        val infoPairByte = InfoPairBase("byte", 0.toByte())
        val infoPairChar = InfoPairBase("char", 0.toChar())
        val infoPairString = InfoPairBase("string", "")
        infoPairByte.setValueFromString("10B")
        infoPairChar.setValueFromString("w\$")
        infoPairString.setValueFromString("cat")
        Assert.assertEquals(10.toByte(), infoPairByte.getValue())
        Assert.assertEquals('w', infoPairChar.getValue())
        Assert.assertEquals("cat", infoPairString.getValue())
    }

    @Test(expected = NullPointerException::class)
    fun setValueFromStringNullTest() {
        try {
            InfoPairBase("string", "non-null-string").setValueFromString("skd\$null")
        } catch (e: IllegalArgumentException) {
            throw if (e.cause != null) e.cause as Throwable else e
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun setValueFromStringNotParsableTest() {
        InfoPairBase("test", Object).setValueFromString(Object.toString())
    }

    @Test
    fun setValueFromStringUnitTest() {
        this.buildInfoPairUnitValue("empty").setValueFromString(Unit.toString())
    }

    @Test
    fun getIdTest() {
        val noId = this.buildInfoPairUnitValue("no-id")
        val withId = this.buildInfoPairUnitValue("with-id").apply { this.setId("the-id") }
        Assert.assertFalse(noId.getId().isPresent)
        Assert.assertTrue(withId.getId().isPresent)
        Assert.assertEquals("the-id", withId.getId().get())
    }

    @Test
    fun setIdTest() {
        val infoPair = this.buildInfoPairUnitValue("key").apply { this.setId("to-be-replaced") }
        val newId = "replaced"
        infoPair.setId(newId)
        Assert.assertTrue(infoPair.getId().isPresent)
        Assert.assertEquals(newId, infoPair.getId().get())
    }

    @Test
    fun setBlankIdTest() {
        val infoPair = this.buildInfoPairUnitValue("key").apply { this.setId("to-be-removed") }
        infoPair.setId("")
        Assert.assertFalse(infoPair.getId().isPresent)
    }

    private fun buildInfoPairUnitValue(key: String): InfoPairBase<Unit> = InfoPairBase(key, Unit)
}
