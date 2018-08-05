package net.thesilkminer.skl.interpreter.skd.structure

import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

class DatabaseBaseTest {

    companion object {
        @BeforeClass
        @JvmStatic
        fun beforeClass() {
            println("Starting test for ${this::class.java.name}")
        }
    }

    private lateinit var database: DatabaseBase
    private var mollyEntityId: EntityBase? = null
    private var mainEntity: EntityBase? = null

    @Before
    fun setUp() {
        this.database = DatabaseBase(EntityBase("main").apply {
            this.addChildEntity(EntityBase("child"))
            this.addChildEntity(EntityBase("child-with-properties").apply {
                this.addProperty(PropertyBase("property1", "empty"))
                this.addProperty(PropertyBase("property2", 10))
            })
            this.addChildEntity(EntityBase("child-with-id").apply {
                this.setId("id1")
            })
            this.addChildEntity(EntityBase("child-with-id").apply {
                this.setId("id2")
                this.addChildEntity(EntityBase("another").apply {
                    this.setId("id21")
                    this.addChildInfoPair(InfoPairBase("info-pair", 10))
                    this.addChildInfoPair(InfoPairBase("second-info-pair", "string").apply {
                        this.setId("id21")
                    })
                    this.addChildEntity(EntityBase("inside").apply {
                        this.addChildEntity(EntityBase("deep").apply {
                            this.addChildEntity(EntityBase("very").apply {
                                this.setId("molly")
                                this@DatabaseBaseTest.mollyEntityId = this
                            })
                        })
                    })
                })
            })
            this.addChildInfoPair(InfoPairBase("info-pair", 10).apply {
                this.setId("id21")
            })
            this.addChildInfoPair(InfoPairBase("another-info-pair", "string"))
            this.addChildEntity(EntityBase("array-like").apply {
                this.addChildEntity(EntityBase("item").apply {
                    this.addChildInfoPair(InfoPairBase("data", 1))
                })
                this.addChildEntity(EntityBase("item").apply {
                    this.addChildInfoPair(InfoPairBase("data", 2))
                })
                this.setArray(true)
            })
            this@DatabaseBaseTest.mainEntity = this
        }).apply {
            this.addDeclaration(DeclarationBase("DOCTYPE", "skd"))
            this.addDeclaration(DeclarationBase("SKD", "0.3"))
            this.addPreprocessorInstruction(PreprocessorInstructionBase("syntax json", 3L))
            this.addPreprocessorInstruction(PreprocessorInstructionBase("def TEST", -1L))
        }
    }

    @After
    fun tearDown() {
        database.apply {
            this.setMainEntity(EntityBase("empty"))
            this.getDeclarations().forEach { this.removeDeclaration(it) }
            this.getPreprocessorInstructions().forEach { this.removePreprocessorInstruction(it) }
        }
        this.mollyEntityId = null
        this.mainEntity = null
    }

    @Test
    fun getDeclarationsTest() {
        Assert.assertEquals(2, this.database.getDeclarations().count())
    }

    @Test
    fun addDeclarationTest() {
        val currentList = this.database.getDeclarations().toMutableList()
        val declaration = DeclarationBase("INVALID", "does not exist")
        currentList.add(declaration)
        this.database.addDeclaration(declaration)
        val newList = this.database.getDeclarations()
        Assert.assertEquals(currentList.count(), newList.count())
        Assert.assertEquals(declaration, this.database.findDeclarationByKey("INVALID").get())
    }

    @Test(expected = IllegalArgumentException::class)
    fun addDeclarationInvalidKeyTest() {
        this.database.addDeclaration(DeclarationBase("DOCTYPE", ""))
    }

    @Test
    fun removeDeclarationEqualsTest() {
        val toRemove = this.database.findDeclarationByKey("DOCTYPE").get()
        this.database.removeDeclaration(toRemove)
        Assert.assertFalse(this.database.findDeclarationByKey("DOCTYPE").isPresent)
        Assert.assertEquals(1, this.database.getDeclarations().count())
    }

    @Test
    fun removeDeclarationByKeyTest() {
        this.database.removeDeclarationByKey("DOCTYPE")
        Assert.assertFalse(this.database.findDeclarationByKey("DOCTYPE").isPresent)
        Assert.assertEquals(1, this.database.getDeclarations().count())
        this.database.addDeclaration(DeclarationBase("DOCTYPE", "skd"))
        this.database.removeDeclaration(DeclarationBase("DOCTYPE", "none"))
        Assert.assertFalse(this.database.findDeclarationByKey("DOCTYPE").isPresent)
        Assert.assertEquals(1, this.database.getDeclarations().count())
    }

    @Test
    fun findDeclarationByKeyTest() {
        val declaration = DeclarationBase("TEST", "declaration")
        this.database.addDeclaration(declaration)
        val found = this.database.findDeclarationByKey("TEST")
        Assert.assertTrue(found.isPresent)
        Assert.assertEquals(declaration, found.get())
    }

    @Test
    fun getPreprocessorInstructionsTest() {
        Assert.assertEquals(2, this.database.getPreprocessorInstructions().count())
    }

    @Test
    fun addPreprocessorInstructionTest() {
        val instruction = PreprocessorInstructionBase("def SOMETHING", 4L)
        val collidingInstruction = PreprocessorInstructionBase("def JAVA", 4L)
        this.database.addPreprocessorInstruction(instruction)
        Assert.assertEquals(3, this.database.getPreprocessorInstructions().count())
        Assert.assertEquals(4L, instruction.getLineNumber())
        this.database.addPreprocessorInstruction(collidingInstruction)
        Assert.assertEquals(4, this.database.getPreprocessorInstructions().count())
        Assert.assertEquals(4L, instruction.getLineNumber())
        Assert.assertEquals(5L, collidingInstruction.getLineNumber())
    }

    @Test
    fun removePreprocessorInstructionEqualsTest() {
        val instruction = PreprocessorInstructionBase("throw java", 100L)
        this.database.addPreprocessorInstruction(instruction)
        this.database.removePreprocessorInstruction(instruction)
        Assert.assertFalse(this.database.findPreprocessorInstructionByLineNumber(100L).isPresent)
    }

    @Test
    fun removePreprocessorInstructionSideEffectsNoRestoreTest() {
        val instructionOne = PreprocessorInstructionBase("def DEED", 100L)
        val instructionTwo = PreprocessorInstructionBase("throw java", 100L)
        this.database.addPreprocessorInstruction(instructionOne)
        this.database.addPreprocessorInstruction(instructionTwo)
        Assert.assertEquals(101L, instructionTwo.getLineNumber())
        this.database.removePreprocessorInstruction(instructionOne)
        Assert.assertEquals(101L, instructionTwo.getLineNumber())
    }

    @Test
    fun removePreprocessorInstructionByLineNumberTest() {
        this.database.removePreprocessorInstructionByLineNumber(-1L)
        Assert.assertEquals(1, this.database.getPreprocessorInstructions().count())
    }

    @Test
    fun findPreprocessorInstructionByLineNumberTest() {
        val instruction = PreprocessorInstructionBase("def M", 100L)
        this.database.addPreprocessorInstruction(instruction)
        val found = this.database.findPreprocessorInstructionByLineNumber(100L)
        Assert.assertTrue(found.isPresent)
        Assert.assertEquals(instruction, found.get())
    }

    @Test
    fun getMainEntityTest() {
        Assert.assertFalse(this.mainEntity == null)
        Assert.assertEquals(this.mainEntity!!, this.database.getMainEntity())
    }

    @Test
    fun setMainEntityTest() {
        val entity = EntityBase("replaced")
        this.database.setMainEntity(entity)
        Assert.assertEquals(entity, this.database.getMainEntity())
    }

    @Test
    fun getEntityFromIdTest() {
        val result = this.database.getEntityFromId("molly")
        Assert.assertFalse(this.mollyEntityId == null)
        Assert.assertTrue(result.isPresent)
        Assert.assertEquals(this.mollyEntityId!!, result.get())
    }
}