package net.thesilkminer.skl.interpreter.skd.structure

import net.thesilkminer.skl.interpreter.skd.api.structure.Entity
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

// Note for get, set and size tests: we are going to test only the
// integer based methods because the ULong based ones practically
// rely on the same implementations just with a different indexing
// type
class ArrayEntityBaseTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            println("Starting test for ${this::class.java.name}")
        }
    }

    private lateinit var arrayEntity: EntityBase

    @Before
    fun setUp() {
        this.arrayEntity = EntityBase("array-like").apply {
            this.setArray(true)
            this.addProperty(PropertyBase("length", "some"))
            this.addChildEntity(EntityBase("item").apply {
                this.addProperty(PropertyBase("some", 100L))
                this.addChildInfoPair(InfoPairBase("data", 0))
                this.addChildInfoPair(InfoPairBase("data", '0'))
                this.addChildEntity(EntityBase("more-data").apply {
                    this.setId("additional0")
                })
            })
            this.addChildEntity(EntityBase("item").apply {
                this.addProperty(PropertyBase("some", 50L))
                this.addChildInfoPair(InfoPairBase("data", 1))
                this.addChildInfoPair(InfoPairBase("data", '1'))
                this.addChildEntity(EntityBase("more-data").apply {
                    this.setId("additional1")
                    this.addChildEntity(EntityBase("entity").apply {
                        this.setId("1")
                    })
                })
            })
            this.addChildEntity(EntityBase("item").apply {
                this.addProperty(PropertyBase("some", 25L))
                this.addChildInfoPair(InfoPairBase("data", 2))
                this.addChildInfoPair(InfoPairBase("data", '2'))
                this.addChildEntity(EntityBase("more-data").apply {
                    this.setId("additional2")
                    this.addChildEntity(EntityBase("entity").apply {
                        this.setId("1")
                    })
                    this.addChildEntity(EntityBase("entity").apply {
                        this.setId("2")
                    })
                })
            })
            this.addChildEntity(EntityBase("item").apply {
                this.addProperty(PropertyBase("some", 13L))
                this.addChildInfoPair(InfoPairBase("data", 3))
                this.addChildInfoPair(InfoPairBase("data", '3'))
                this.addChildEntity(EntityBase("more-data").apply {
                    this.setId("additional3")
                    this.addChildEntity(EntityBase("entity").apply {
                        this.setId("1")
                    })
                    this.addChildEntity(EntityBase("entity").apply {
                        this.setId("2")
                    })
                    this.addChildEntity(EntityBase("entity").apply {
                        this.setId("3")
                    })
                })
            })
        }
    }

    @After
    fun tearDown() {
        this.arrayEntity = EntityBase("not-an-array")
    }

    @Test
    fun asEntityTest() {
        // This because if the cast fails, then the method throws, so...
        Assert.assertTrue(with(this.arrayEntity.asEntity()) { true })
    }

    @Test
    fun getTest() {
        val indexZero = this.arrayEntity[0]
        val indexThree = this.arrayEntity[3]
        Assert.assertEquals(0, this.moreData(indexZero).getChildEntities().count())
        Assert.assertEquals(3, this.moreData(indexThree).getChildEntities().count())
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun getOutOfBoundsOverTest() {
        this.arrayEntity[100]
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun getOutOfBoundsLowerTest() {
        this.arrayEntity[-1]
    }

    @Test
    fun setTest() {
        val indexZero = this.buildItemWithId("replaced")
        this.arrayEntity[0] = indexZero
        Assert.assertEquals(indexZero, this.arrayEntity[0])
        Assert.assertTrue(this.moreData(this.arrayEntity[0]).getId().isPresent)
        Assert.assertEquals("replaced", this.moreData(this.arrayEntity[0]).getId().get())
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun setOutOfBoundsOverTest() {
        this.arrayEntity[37289] = EntityBase("IoObE")
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun setOutOfBoundsLowerTest() {
        this.arrayEntity[-621] = EntityBase("IoObE")
    }

    @Test
    fun intSizeTest() {
        Assert.assertEquals(4, this.arrayEntity.intSize())
    }

    @Test()
    fun clearTest() {
        this.arrayEntity.clear()
        Assert.assertEquals(0, this.arrayEntity.intSize())
    }

    @Test
    fun validateTest() {
        // This because if validation fails, then the method throws, so...
        Assert.assertTrue(with(this.arrayEntity.validate()) { true })
    }

    @Test(expected = IllegalStateException::class)
    fun validateInvalidTest() {
        this.arrayEntity[2].removeChildEntity(this.moreData(this.arrayEntity[2]))
        this.arrayEntity.validate()
    }

    @Test
    fun validateAutoFixTest() {
        this.arrayEntity[3].removeChildEntity(this.moreData(this.arrayEntity[3]))
        this.arrayEntity[2].addProperty(PropertyBase("stripped", true))
        this.arrayEntity[2].addProperty(PropertyBase("this-too", -16L))
        this.arrayEntity.validate(true)
        Assert.assertEquals(3, this.arrayEntity.intSize())
        Assert.assertFalse(this.arrayEntity[2].findPropertyByName("stripped").isPresent)
        Assert.assertFalse(this.arrayEntity[2].findPropertyByName("this too").isPresent)
    }

    @Test
    fun addChildEntityTest() {
        val child = this.buildItemWithId("empty-but-should-be-4")
        this.arrayEntity.addChildEntity(child)
        Assert.assertEquals(5, this.arrayEntity.intSize())
        Assert.assertEquals(child, this.arrayEntity[4])
        Assert.assertEquals(this.moreData(child).getId().get(), this.moreData(this.arrayEntity[4]).getId().get())
    }

    @Test(expected = IllegalArgumentException::class)
    fun addChildEntityInvalidTest() {
        this.arrayEntity.addChildEntity(this.buildItemWithId("cannot-add").apply {
            this.addChildEntity(EntityBase("invalid"))
        })
    }

    @Test
    fun removeChildEntityTest() {
        val child = this.buildItemWithId("data")
        this.arrayEntity.addChildEntity(child)
        Assert.assertEquals(5, this.arrayEntity.intSize())
        this.arrayEntity.removeChildEntity(child)
        Assert.assertEquals(4, this.arrayEntity.intSize())
        val indexTwo = this.arrayEntity[2]
        val previousIndexThree = this.arrayEntity[3]
        this.arrayEntity.removeChildEntity(indexTwo)
        Assert.assertEquals(3, this.arrayEntity.intSize())
        Assert.assertEquals(previousIndexThree, this.arrayEntity[2])
        this.arrayEntity.removeChildEntity(child)
        Assert.assertEquals(3, this.arrayEntity.intSize())
    }

    @Test
    fun removeChildEntityIdTest() {
        val id = "to-remove"
        val indexTwo = this.arrayEntity[2]
        indexTwo.setId(id)
        this.arrayEntity.removeChildEntity(id)
        Assert.assertEquals(3, this.arrayEntity.intSize())
        Assert.assertNotEquals(indexTwo, this.arrayEntity[2])
    }

    @Test(expected = UnsupportedOperationException::class)
    fun removeChildEntityByNameTest() {
        this.arrayEntity.removeChildEntityByName("item")
    }

    @Test
    fun removeChildEntityByNameNoMatchesTest() {
        // This because if removing "succeeds", then the method throws, so...
        Assert.assertTrue(with(this.arrayEntity.removeChildEntityByName("not-present")) { true })
    }

    @Test
    fun removeAllChildEntitiesByNameTest() {
        this.arrayEntity.removeAllChildEntitiesByName("item")
        Assert.assertEquals(0, this.arrayEntity.intSize())
    }

    @Test
    fun removeChildEntityByIndexTest() {
        val indexTwo = this.arrayEntity[2]
        this.arrayEntity.removeChildEntityByIndex(2)
        Assert.assertEquals(3, this.arrayEntity.intSize())
        Assert.assertNotEquals(indexTwo, this.arrayEntity[2])
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun removeChildEntityByIndexOutOfBoundsTest() {
        this.arrayEntity.removeChildEntityByIndex(1000)
    }

    @Test(expected = UnsupportedOperationException::class)
    fun addChildInfoPairTest() {
        this.arrayEntity.addChildInfoPair(InfoPairBase("will", "crash"))
    }

    @Test(expected = UnsupportedOperationException::class)
    fun getChildInfoPairsTest() {
        this.arrayEntity.getChildInfoPairs()
    }

    @Test(expected = UnsupportedOperationException::class)
    fun removeChildInfoPairTest() {
        this.arrayEntity.removeChildInfoPair(InfoPairBase("will", "crash"))
    }

    @Test(expected = UnsupportedOperationException::class)
    fun removeChildInfoPairByIdTest() {
        this.arrayEntity.removeChildInfoPair("will-crash")
    }

    @Test(expected = UnsupportedOperationException::class)
    fun removeChildInfoPairByKeyTest() {
        this.arrayEntity.removeChildInfoPairByKey("will") // crash
    }

    @Test(expected = UnsupportedOperationException::class)
    fun removeAllChildrenInfoPairsByKeyTest() {
        this.arrayEntity.removeAllChildrenInfoPairByKey("will") // crash
    }

    @Test(expected = UnsupportedOperationException::class)
    fun findChildInfoPairsByKeyTest() {
        this.arrayEntity.findChildInfoPairsByKey("will") // crash
    }

    @Test(expected = UnsupportedOperationException::class)
    fun findChildInfoPairTest() {
        this.arrayEntity.findChildInfoPair("will") // crash
    }

    private fun moreData(w: Entity): Entity = w.findChildEntitiesByName("more-data").toMutableList()[0]

    private fun buildItemWithId(id: String): Entity {
        return EntityBase("item").apply {
            this.addProperty(PropertyBase("some", 100L))
            this.addChildInfoPair(InfoPairBase("data", 0))
            this.addChildInfoPair(InfoPairBase("data", '0'))
            this.addChildEntity(EntityBase("more-data").apply {
                this.setId(id)
            })
        }
    }
}
