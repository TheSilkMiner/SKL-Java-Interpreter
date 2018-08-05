package net.thesilkminer.skl.interpreter.skd.structure

import com.google.common.base.Preconditions
import com.google.common.collect.ImmutableList
import net.thesilkminer.skl.interpreter.skd.SupportedStringConverterTypes
import net.thesilkminer.skl.interpreter.skd.api.structure.Property
import net.thesilkminer.skl.interpreter.skd.convertFromSkdPropertyRepresentation
import net.thesilkminer.skl.interpreter.skd.convertToSkdPropertyRepresentation
import net.thesilkminer.skl.interpreter.skd.getTypeFromAny
import java.util.Locale

class PropertyBase<T>(name: String, value: T) : Property<T> {
    companion object Constants {
        val INTERNAL_PROPERTIES_NAMES : Collection<String> = ImmutableList.of("skd\$id", "skd\$class", "skd\$array")
    }

    private val name : String
    private var value : T

    init {
        Preconditions.checkArgument(name.isNotBlank(), "Provided name is not valid: cannot be blank")
        Preconditions.checkArgument(name.chars().allMatch {
            val c = it.toChar()
            Character.isLetterOrDigit(c) ||
                    c == '-' ||
                    c == '_' ||
                    c == '$' // Allow this for automatic generation of properties
        }, "Provided name is not valid: $name can only contain letters, digits, - and _")
        Preconditions.checkArgument(name.toLowerCase(Locale.ENGLISH) != "id",
                "Property cannot be named $name: variations of ID are not allowed")
        this.name = name
        this.value = value
    }

    override fun getName(): String = this.name

    override fun getValue(): T = this.value

    override fun setValue(value: T) {
        this.value = value
    }

    override fun getValueAsString(): String = convertToSkdPropertyRepresentation(this.value)

    override fun setValueFromString(stringValue: String) {
        val type = getTypeFromAny(this.value)
        val nullProbably = convertFromSkdPropertyRepresentation<T>(stringValue, SupportedStringConverterTypes.NULL_TYPE)
        val optional = if (nullProbably?.isPresent == false) convertFromSkdPropertyRepresentation(stringValue, type) else nullProbably
        // Nullable boolean check, I'm sorry, I know it is ugly
        if (optional?.isPresent == true) {
            // Value found and parsing was successful. Set the value
            this.value = optional.get()
            return
        }
        // Optional is either null or empty
        // Null values are not allowed and empty means that the parsing has failed
        val cause = if (optional == null) NullPointerException("null values not allowed in this property") else null
        throw IllegalArgumentException("Cannot parse string $stringValue", cause)
    }
}