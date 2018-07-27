package net.thesilkminer.skl.interpreter.skd.structure

import com.google.common.base.Preconditions
import net.thesilkminer.skl.interpreter.skd.api.structure.InfoPair
import net.thesilkminer.skl.interpreter.skd.convertFromSkdPropertyRepresentation
import net.thesilkminer.skl.interpreter.skd.convertToSkdPropertyRepresentation
import net.thesilkminer.skl.interpreter.skd.getTypeFromAny
import java.util.Optional

class InfoPairBase<T>(key: String, value: T) : InfoPair<T> {

    private val key: String
    private var value: T

    private var id: String? = null

    init {
        Preconditions.checkArgument(key.isNotBlank(), "Key cannot be empty")
        this.key = key
        this.value = value
    }

    override fun getKey(): String = this.key

    override fun getValue(): T = this.value

    override fun setValue(value: T) {
        this.value = value
    }

    override fun getValueAsString(): String = convertToSkdPropertyRepresentation(this.value)

    override fun setValueFromString(value: String) {
        val type = getTypeFromAny(this.value)
        val optional = convertFromSkdPropertyRepresentation<T>(value, type)
        // Nullable boolean check, I'm sorry, I know it is ugly
        if (optional?.isPresent == true) {
            // Value found and parsing was successful. Set the value
            this.value = optional.get()
            return
        }
        // Optional is either null or empty
        // Null values are not allowed and empty means that the parsing has failed
        val cause = if (optional == null) NullPointerException("null values not allowed in this information pair") else null
        throw IllegalArgumentException("Cannot parse string $value", cause)
    }

    override fun getId(): Optional<String> = Optional.ofNullable(this.id)

    override fun setId(id: String) {
        this.id = if (id.isBlank()) null else id
    }
}
