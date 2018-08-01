package net.thesilkminer.skl.interpreter.skd

import net.thesilkminer.skl.interpreter.skd.api.structure.ArrayEntity
import net.thesilkminer.skl.interpreter.skd.api.structure.Database
import net.thesilkminer.skl.interpreter.skd.api.structure.Declaration
import net.thesilkminer.skl.interpreter.skd.api.structure.Entity
import net.thesilkminer.skl.interpreter.skd.api.structure.InfoPair
import net.thesilkminer.skl.interpreter.skd.api.structure.PreprocessorInstruction
import net.thesilkminer.skl.interpreter.skd.api.structure.Property
import net.thesilkminer.skl.interpreter.skd.structure.DatabaseBase
import net.thesilkminer.skl.interpreter.skd.structure.DeclarationBase
import net.thesilkminer.skl.interpreter.skd.structure.EntityBase
import net.thesilkminer.skl.interpreter.skd.structure.InfoPairBase
import net.thesilkminer.skl.interpreter.skd.structure.PreprocessorInstructionBase
import net.thesilkminer.skl.interpreter.skd.structure.PropertyBase
import net.thesilkminer.skl.interpreter.skd.api.ApiProvider as ApiProviderInterface

class ApiProvider : ApiProviderInterface {

    override fun buildEntity(name: String): Entity = EntityBase(name)

    override fun buildArrayLikeEntity(name: String): ArrayEntity = EntityBase(name).apply { this.setArray(true) }

    override fun <T> buildInformationPair(key: String, value: T?): InfoPair<T> = InfoPairBase(key, value!!)

    override fun <T> buildProperty(name: String, value: T?): Property<T> = PropertyBase(name, value!!)

    override fun buildPreprocessorInstruction(instruction: String, lineNumber: Long): PreprocessorInstruction =
            PreprocessorInstructionBase(instruction, lineNumber)

    override fun buildDeclaration(key: String, value: String): Declaration = DeclarationBase(key, value)

    override fun buildDatabase(mainEntity: Entity): Database = DatabaseBase(mainEntity)
}
