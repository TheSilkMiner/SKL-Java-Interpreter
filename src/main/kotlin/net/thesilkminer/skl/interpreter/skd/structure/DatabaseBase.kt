package net.thesilkminer.skl.interpreter.skd.structure

import com.google.common.base.Preconditions
import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists
import net.thesilkminer.skl.interpreter.skd.api.structure.Database
import net.thesilkminer.skl.interpreter.skd.api.structure.Declaration
import net.thesilkminer.skl.interpreter.skd.api.structure.Entity
import net.thesilkminer.skl.interpreter.skd.api.structure.PreprocessorInstruction
import java.util.Optional

// My God that is an awful play on words!
class DatabaseBase(entity: Entity) : Database {

    private val declarations: MutableList<Declaration> = Lists.newLinkedList()
    private val preprocessor: MutableList<PreprocessorInstruction> = Lists.newLinkedList()
    private var mainEntity: Entity = entity

    override fun getDeclarations(): Collection<Declaration> = ImmutableList.copyOf(this.declarations)

    override fun addDeclaration(declaration: Declaration) {
        Preconditions.checkArgument(this.declarations.any { it.getKey() == declaration.getKey() },
                "There is already a declaration in the database with the same key")
        this.declarations.add(declaration)
    }

    override fun removeDeclaration(declaration: Declaration) {
        if (this.declarations.remove(declaration)) {
            return
        }

        this.removeDeclarationByKey(declaration.getKey())
    }

    override fun removeDeclarationByKey(key: String) {
        this.findDeclarationByKey(key).ifPresent { this.declarations.remove(it) }
    }

    override fun findDeclarationByKey(key: String): Optional<Declaration> {
        for (declaration in this.declarations) {
            if (declaration.getKey() == key) {
                return Optional.of(declaration)
            }
        }

        return Optional.empty()
    }

    override fun getPreprocessorInstructions(): Collection<PreprocessorInstruction> = ImmutableList.copyOf(this.preprocessor)

    override fun addPreprocessorInstruction(preprocessorInstruction: PreprocessorInstruction) {
        var lineNumber = preprocessorInstruction.getLineNumber()
        if (this.preprocessor.any { it.getLineNumber() == lineNumber }) {
            // There is an entry with the same preprocessor line number
            ++lineNumber
        }
        preprocessorInstruction.setLineNumber(lineNumber)
        if (this.preprocessor.any { it.getLineNumber() == lineNumber }) {
            // This line is occupied too: we need to loop on the items until there are
            // no overlapping line numbers

            // Current Line is the line we are checking so...
            var currentLine = lineNumber
            // As long as there is an instruction at the line we are checking
            while (this.preprocessor.any { it.getLineNumber() == currentLine }) {
                // Loop over every instruction present
                for (instruction in this.preprocessor) {
                    // Find the one that is conflicting
                    if (instruction.getLineNumber() == currentLine) {
                        // And shift it by one
                        instruction.setLineNumber(instruction.getLineNumber() + 1)
                    }
                }
                // Then augment by one the current line number and check again
                ++currentLine
            }

            // Now, there are no multiple matching values, in theory
        }

        // Add the instruction and then sort the list
        this.preprocessor.add(preprocessorInstruction)
        this.preprocessor.sortBy { it.getLineNumber() }
        // This way we are placing negative numbers at the top of the list:
        // the parser will account for that.
    }

    override fun removePreprocessorInstruction(preprocessorInstruction: PreprocessorInstruction) {
        if (this.preprocessor.remove(preprocessorInstruction)) {
            return
        }

        this.removePreprocessorInstructionByLineNumber(preprocessorInstruction.getLineNumber())
    }

    override fun removePreprocessorInstructionByLineNumber(lineNumber: Long) {
        this.findPreprocessorInstructionByLineNumber(lineNumber).ifPresent { this.preprocessor.remove(it) }
    }

    override fun findPreprocessorInstructionByLineNumber(lineNumber: Long): Optional<PreprocessorInstruction> {
        for (instruction in this.preprocessor) {
            if (instruction.getLineNumber() == lineNumber) {
                return Optional.of(instruction)
            }
        }

        return Optional.empty()
    }

    override fun getMainEntity(): Entity = this.mainEntity

    override fun setMainEntity(entity: Entity) {
        this.mainEntity = entity
    }

    override fun getEntityFromId(id: String): Optional<Entity> {
        val found = Lists.newArrayList<Entity>()

        // Let's use a deep-first search (is that the name, I don't know)
        // Anyway, we check the entity first, then all of its children,
        // recursively.
        try {
            this.checkEntityAndChildrenForId(id, found, this.mainEntity)
        } catch (e: StackOverflowError) {
            // Zoinks, a stack overflow! So the database goes too
            // deep for the various calls... huh... let me assume
            // that the stack is successfully unwound and that
            // there are no other entities to check. Problem is,
            // if we return we violate the contract, if we throw
            // in a certain sense we do too. COMPROMISE (something
            // that makes you half happy - Jim Hopper)!!
            // We throw an IllegalStateException with the message
            // "Too deep" and this error as the cause, then we
            // let the guy handle it. If he does not want this
            // behavior he can simply do the manual work
            // himself. In the end it is just a recursive call
            // on the main entity, which is accessible thanks
            // to getMainEntity(), so no issues here.
            throw IllegalStateException("Database is too deep to traverse completely: try again from a few levels deeper", e)
        }

        if (found.isEmpty()) return Optional.empty()
        if (found.count() > 1) throw IllegalStateException("Multiple entities with the same ID were found")
        return Optional.of(found[0])
    }

    private fun checkEntityAndChildrenForId(id: String, found: MutableList<Entity>, entity: Entity) {
        if (entity.getId().isPresent && entity.getId().get() == id) found.add(entity)
        entity.getChildEntities().forEach { this.checkEntityAndChildrenForId(id, found, it) }
    }
}
