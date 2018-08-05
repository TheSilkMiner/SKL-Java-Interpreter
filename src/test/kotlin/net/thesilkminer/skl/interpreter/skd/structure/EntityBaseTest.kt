package net.thesilkminer.skl.interpreter.skd.structure

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

// Actually, this class test only "normal" entities methods
// Refer to ArrayEntityBaseTest for the array-like behavior
// tests
class EntityBaseTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            println("Starting test for ${this::class.java.name}")
        }
    }

    private lateinit var entity: EntityBase
    private lateinit var entityWithId: EntityBase
    private lateinit var childWithProperties: EntityBase
    private lateinit var arrayEntity: EntityBase

    @Before
    fun setUp() {
        this.entity = EntityBase("main").apply {
            this@EntityBaseTest.reflectProperty(this) { PropertyBase("skd\$class", "EntityBase") }
            this.addChildEntity(EntityBase("empty-child"))
            this.addChildEntity(EntityBase("child-with-properties").apply {
                this.addProperty(PropertyBase("string", "string"))
                this.addProperty(PropertyBase("another", 1))
                this@EntityBaseTest.childWithProperties = this
            })
            this.addChildEntity(EntityBase("another-child").apply {
                this.setId("best")
                this.addProperty(PropertyBase("network", "LIT"))
                this.addChildEntity(EntityBase("phones").apply {
                    this.addChildEntity(EntityBase("phone").apply {
                        this.addChildInfoPair(InfoPairBase("model", "SM-G955F"))
                        this.addChildInfoPair(InfoPairBase("manufacturer", "Samsung"))
                    })
                    this.addChildEntity(EntityBase("phone").apply {
                        this.addChildInfoPair(InfoPairBase("model", "SM-G965FD"))
                        this.addChildInfoPair(InfoPairBase("manufacturer", "Samsung"))
                    })
                    this.addChildEntity(EntityBase("phone").apply {
                        this.addChildInfoPair(InfoPairBase("model", "SM-N955F"))
                        this.addChildInfoPair(InfoPairBase("manufacturer", "Samsung"))
                    })
                    this.setArray(true)
                    this.setId("array-like")
                    this@EntityBaseTest.arrayEntity = this
                })
                this.addChildInfoPair(InfoPairBase("network", "LIT").apply {
                    this.setId("network")
                })
                this.addChildInfoPair(InfoPairBase("another-data", "FAM"))
                this@EntityBaseTest.entityWithId = this
            })
            this.addChildEntity(EntityBase("doge").apply {
                this.addChildInfoPair(InfoPairBase("network", "DOG").apply {
                    this.setId("network")
                })
            })
        }
    }

    @After
    fun tearDown() {
        this.entity = EntityBase("nada")
        this.entityWithId = this.entity
        this.arrayEntity = this.entity
    }

    @Test(expected = IllegalArgumentException::class)
    fun constructEntityInvalidNameTest() {
        EntityBase("\u0304")
    }

    @Test(expected = IllegalArgumentException::class)
    fun constructEntityEmptyNameTest() {
        EntityBase("")
    }

    @Test
    fun getNameTest() {
        Assert.assertEquals("main", this.entity.getName())
    }

    @Test
    fun getIdTest() {
        Assert.assertFalse(this.entity.getId().isPresent)
        Assert.assertTrue(this.entityWithId.getId().isPresent)
        Assert.assertEquals("best", this.entityWithId.getId().get())
    }

    @Test
    fun setIdTest() {
        val newId = "new-id"
        this.entityWithId.setId(newId)
        Assert.assertTrue(this.entityWithId.getId().isPresent)
        Assert.assertEquals(newId, this.entityWithId.getId().get())
    }

    @Test
    fun setBlankIdTest() {
        this.entityWithId.setId("")
        Assert.assertFalse(this.entityWithId.getId().isPresent)
    }

    @Test
    fun isArrayTest() {
        Assert.assertFalse(this.entity.isArray())
        Assert.assertTrue(this.arrayEntity.isArray())
    }

    @Test
    fun setArrayTest() {
        try {
            this.arrayEntity.setArray(false)
            Assert.assertFalse(this.arrayEntity.isArray())
        } catch (e: UnsupportedOperationException) {
            // It can be thrown by implementations: log it and continue anyway
            println("EntityImpl#setArray(false) has thrown $e: this is valid anyway and as such it is not an error")
        }
        this.arrayEntity.setArray(true)
        Assert.assertTrue(this.arrayEntity.isArray())
    }

    @Test(expected = IllegalStateException::class)
    fun setArrayFailTest() {
        this.entity.setArray(true)
    }

    @Test
    fun setArrayFromScratchTest() {
        val entity = EntityBase("array-like").apply {
            this.addProperty(PropertyBase("no", "not really"))
            this.addChildEntity(EntityBase("item").apply {
                this.addChildInfoPair(InfoPairBase("data", 0))
                this.addChildInfoPair(InfoPairBase("data", '0'))
            })
            this.addChildEntity(EntityBase("item").apply {
                this.addChildInfoPair(InfoPairBase("data", 1))
                this.addChildInfoPair(InfoPairBase("data", '1'))
            })
            this.addChildEntity(EntityBase("item").apply {
                this.addChildInfoPair(InfoPairBase("data", 2))
                this.addChildInfoPair(InfoPairBase("data", '2'))
            })
            this.addChildEntity(EntityBase("item").apply {
                this.addChildInfoPair(InfoPairBase("data", 3))
                this.addChildInfoPair(InfoPairBase("data", '3'))
            })
        }

        entity.setArray(true)
        Assert.assertTrue(entity.isArray())
    }

    @Test
    fun asArrayEntityTest() {
        // This because if the cast fails, then the method throws, so...
        Assert.assertTrue(with(this.arrayEntity.asArrayEntity()) { true })
    }

    @Test(expected = ClassCastException::class)
    fun asArrayEntityFailTest() {
        this.entity.asArrayEntity()
    }

    @Test
    fun getPropertiesTest() {
        Assert.assertEquals(2, this.childWithProperties.getProperties().count())
    }

    @Test
    fun getPropertiesStrippingTest() {
        Assert.assertEquals(0, this.entity.getProperties().count())
    }

    @Test
    fun addPropertyTest() {
        val prop = PropertyBase("prop", Unit)
        this.childWithProperties.addProperty(prop)
        Assert.assertEquals(3, this.childWithProperties.getProperties().count())
        Assert.assertTrue(this.childWithProperties.findPropertyByName(prop.getName()).isPresent)
        Assert.assertEquals(prop, this.childWithProperties.findPropertyByName(prop.getName()).get())
    }

    @Test(expected = IllegalArgumentException::class)
    fun addPropertyInvalidTest() {
        this.childWithProperties.addProperty(PropertyBase("another", 2))
    }

    @Test
    fun removePropertyTest() {
        val prop = PropertyBase("long", 10L)
        this.childWithProperties.addProperty(prop)
        Assert.assertEquals(3, this.childWithProperties.getProperties().count())
        this.childWithProperties.removeProperty(prop)
        Assert.assertEquals(2, this.childWithProperties.getProperties().count())
        Assert.assertFalse(this.childWithProperties.findPropertyByName(prop.getName()).isPresent)
    }

    @Test
    fun removePropertyByNameTest() {
        val prop = PropertyBase("data", "application/json")
        val previousProps = this.childWithProperties.getProperties().toMutableList()
        this.childWithProperties.addProperty(prop)
        Assert.assertTrue(this.childWithProperties.findPropertyByName(prop.getName()).isPresent)
        this.childWithProperties.removePropertyByName(prop.getName())
        Assert.assertFalse(this.childWithProperties.findPropertyByName(prop.getName()).isPresent)
        Assert.assertEquals(previousProps.count(), this.childWithProperties.getProperties().count())
        this.childWithProperties.addProperty(prop)
        this.childWithProperties.removeProperty(PropertyBase("data", "application/xml"))
        Assert.assertFalse(this.childWithProperties.findPropertyByName(prop.getName()).isPresent)
        Assert.assertEquals(previousProps.count(), this.childWithProperties.getProperties().count())
    }

    @Test
    fun findPropertyByNameTest() {
        val property = PropertyBase("type", Unit)
        this.entity.addProperty(property)
        val found = this.entity.findPropertyByName(property.getName())
        Assert.assertTrue(found.isPresent)
        Assert.assertEquals(property, found.get())
    }

    @Test
    fun getChildEntitiesTest() {
        Assert.assertEquals(4, this.entity.getChildEntities().count())
    }

    @Test
    fun addChildEntityTest() {
        val child = EntityBase("cute-child")
        val childPrevious = this.entity.getChildEntities().toMutableList()
        this.entity.addChildEntity(child)
        childPrevious.add(child)
        Assert.assertEquals(childPrevious.count(), this.entity.getChildEntities().count())
        Assert.assertEquals(1, this.entity.findChildEntitiesByName(child.getName()).count())
        Assert.assertEquals(child, this.entity.findChildEntitiesByName(child.getName()).toMutableList()[0])
    }

    @Test
    fun removeChildEntityTest() {
        val child = EntityBase("empty-child")
        this.entity.addChildEntity(child)
        Assert.assertEquals(5, this.entity.getChildEntities().count())
        this.entity.removeChildEntity(child)
        Assert.assertEquals(4, this.entity.getChildEntities().count())
        Assert.assertEquals(1, this.entity.findChildEntitiesByName(child.getName()).count())
    }

    @Test
    fun removeChildEntityByIdTest() {
        this.entity.removeChildEntity("best")
        Assert.assertFalse(this.entity.findChildEntity("best").isPresent)
    }

    @Test
    fun removeChildEntityByNameTest() {
        val initialChildren = this.entity.getChildEntities().toMutableList()
        val child = EntityBase("unique-name")
        this.entity.addChildEntity(child)
        this.entity.removeChildEntityByName(child.getName())
        Assert.assertEquals(initialChildren.count(), this.entity.getChildEntities().count())
        this.entity.addChildEntity(child)
        this.entity.removeChildEntity(EntityBase("unique-name").apply {
            this.addProperty(PropertyBase("same", "but different"))
        })
        Assert.assertEquals(initialChildren.count(), this.entity.getChildEntities().count())
        Assert.assertEquals(0, this.entity.findChildEntitiesByName(child.getName()).count())
    }

    @Test(expected = UnsupportedOperationException::class)
    fun removeChildEntityByNameMultipleEntitiesTest() {
        val child = EntityBase("empty-child")
        this.entity.addChildEntity(child)
        this.entity.removeChildEntityByName(child.getName())
    }

    @Test
    fun removeAllChildEntitiesByNameTest() {
        val child = EntityBase("empty-child")
        this.entity.addChildEntity(child)
        this.entity.removeAllChildEntitiesByName(child.getName())
        Assert.assertEquals(0, this.entity.findChildEntitiesByName(child.getName()).count())
    }

    @Test
    fun findChildEntitiesByNameTest() {
        val child = EntityBase("child-entity")
        this.entity.addChildEntity(child)
        val found = this.entity.findChildEntitiesByName(child.getName()).toMutableList()
        Assert.assertEquals(1, found.count())
        Assert.assertEquals(child, found[0])
    }

    @Test
    fun findChildEntityTest() {
        val child = EntityBase("child-entity-with-id").apply {
            this.setId("grandmother")
        }
        this.entityWithId.addChildEntity(child)
        val found = this.entityWithId.findChildEntity(child.getId().get())
        Assert.assertTrue(found.isPresent)
        Assert.assertEquals(child, found.get())
        Assert.assertTrue(found.get().getId().isPresent)
        Assert.assertEquals(child.getId().get(), found.get().getId().get())
        val empty = this.entityWithId.findChildEntity("this-id-is-surely-not-present-because-of-being-long")
        Assert.assertFalse(empty.isPresent)
    }

    @Test
    fun getChildInfoPairsTest() {
        Assert.assertEquals(2, this.entityWithId.getChildInfoPairs().count())
    }

    @Test
    fun addChildInfoPairTest() {
        val list = this.entity.getChildInfoPairs().toMutableList()
        val infoPair = InfoPairBase("info-pair", Unit)
        list.add(infoPair)
        this.entity.addChildInfoPair(infoPair)
        Assert.assertEquals(list.count(), this.entity.getChildInfoPairs().count())
        Assert.assertEquals(1, this.entity.findChildInfoPairsByKey(infoPair.getKey()).count())
    }

    @Test
    fun removeChildInfoPairTest() {
        val infoPair = InfoPairBase("info-pair", 10L)
        this.entity.addChildInfoPair(infoPair)
        Assert.assertEquals(1, this.entity.getChildInfoPairs().count())
        this.entity.removeChildInfoPair(infoPair)
        Assert.assertEquals(0, this.entity.getChildInfoPairs().count())
        Assert.assertEquals(0, this.entity.findChildInfoPairsByKey(infoPair.getKey()).count())
    }

    @Test
    fun removeChildInfoPairByKeyTest() {
        val infoPair = InfoPairBase("char-pair", 'c')
        this.entity.addChildInfoPair(infoPair)
        Assert.assertEquals(1, this.entity.getChildInfoPairs().count())
        this.entity.removeChildInfoPairByKey(infoPair.getKey())
        Assert.assertEquals(0, this.entity.getChildInfoPairs().count())
        Assert.assertEquals(0, this.entity.findChildInfoPairsByKey(infoPair.getKey()).count())
        this.entity.addChildInfoPair(infoPair)
        this.entity.removeChildInfoPair(InfoPairBase("char-pair", 'w'))
        Assert.assertEquals(0, this.entity.getChildInfoPairs().count())
        Assert.assertEquals(0, this.entity.findChildInfoPairsByKey(infoPair.getKey()).count())
    }

    @Test(expected = UnsupportedOperationException::class)
    fun removeChildInfoPairByKeyMultiplePresentTest() {
        this.entityWithId.addChildInfoPair(InfoPairBase("another-data", "IT'S"))
        Assert.assertEquals(2, this.entityWithId.findChildInfoPairsByKey("another-data").count())
        this.entityWithId.removeChildInfoPairByKey("another-data")
    }

    @Test
    fun removeChildInfoPairByIdTest() {
        this.entityWithId.removeChildInfoPair("network")
        Assert.assertEquals(1, this.entityWithId.getChildInfoPairs().count())
        Assert.assertEquals(0, this.entityWithId.findChildInfoPairsByKey("network").count())
    }

    @Test
    fun removeAllChildrenInfoPairsByKeyTest() {
        this.entityWithId.addChildInfoPair(InfoPairBase("another-data", "IT'S"))
        Assert.assertEquals(2, this.entityWithId.findChildInfoPairsByKey("another-data").count())
        this.entityWithId.removeAllChildrenInfoPairByKey("another-data")
        Assert.assertEquals(0, this.entityWithId.findChildInfoPairsByKey("another-data").count())
    }

    @Test
    fun findChildInfoPairsByKeyTest() {
        val child = InfoPairBase("child-info-pair", Unit)
        this.entity.addChildInfoPair(child)
        val found = this.entity.findChildInfoPairsByKey(child.getKey()).toMutableList()
        Assert.assertEquals(1, found.count())
        Assert.assertEquals(child, found[0])
    }

    @Test
    fun findChildInfoPairTest() {
        val child = InfoPairBase("child-info-pair-with-id", 10L).apply {
            this.setId("grandson")
        }
        this.entity.addChildInfoPair(child)
        val found = this.entity.findChildInfoPair(child.getId().get())
        Assert.assertTrue(found.isPresent)
        Assert.assertEquals(child, found.get())
        Assert.assertTrue(found.get().getId().isPresent)
        Assert.assertEquals(child.getId().get(), found.get().getId().get())
        val empty = this.entity.findChildInfoPair("this-id-is-surely-not-present-because-of-being-long")
        Assert.assertFalse(empty.isPresent)
    }

    private inline fun reflectProperty(entity: EntityBase, property: () -> PropertyBase<*>) {
        val listField = entity::class.java.declaredFields.find { it.name == "properties" }?.apply { this.isAccessible = true }
        val listAsAny = listField?.get(entity)
        @Suppress("UNCHECKED_CAST")
        val list = listAsAny as? MutableList<PropertyBase<*>>?
        list?.add(property()) ?: System.err.println("Unable to add property reflectively")
    }
}
