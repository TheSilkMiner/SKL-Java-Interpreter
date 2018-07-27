package net.thesilkminer.skl.interpreter.skd.structure

import com.google.common.base.Preconditions
import net.thesilkminer.skl.interpreter.skd.api.structure.Declaration

class DeclarationBase(key: String, value: String) : Declaration {

    private val key: String
    private val declarationType: Declaration.DeclarationType

    private var value: String

    init {
        Preconditions.checkArgument(key.chars().allMatch {
            Character.isUpperCase(it.toChar()) || it.toChar() == '_'
        }, "Key $key does not respect constraints: it must be all uppercase and cannot contain symbols but _")
        Preconditions.checkArgument(value.isNotBlank(), "Value cannot be left blank")
        this.key = key
        this.declarationType = Declaration.DeclarationType.fromKey(this.key)
        this.value = value
    }

    override fun getDeclarationType(): Declaration.DeclarationType = this.declarationType

    override fun getKey(): String = this.key

    override fun getValue(): String = this.value

    override fun setValue(value: String) {
        if (value.isBlank()) throw IllegalArgumentException("Value cannot be left blank")
        this.value = value
    }
}
