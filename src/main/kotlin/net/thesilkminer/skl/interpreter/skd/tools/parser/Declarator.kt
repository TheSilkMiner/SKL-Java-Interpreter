package net.thesilkminer.skl.interpreter.skd.tools.parser

import com.google.common.collect.Lists
import net.thesilkminer.skl.interpreter.skd.api.Skd
import net.thesilkminer.skl.interpreter.skd.api.structure.Declaration
import net.thesilkminer.skl.interpreter.skd.extractMessage
import net.thesilkminer.skl.interpreter.skd.isUpperCase
import net.thesilkminer.skl.interpreter.skd.rethrowAs
import java.util.Locale

internal class Declarator(private val lines: MutableList<String>) {
    companion object {
        private val l = L(this)
    }

    val declarations: MutableList<Declaration> = Lists.newArrayList()!!
    private var isDeclarationSection = true

    init {
        l.i("Successfully initialized declarator {}", this)
    }

    fun parse() {
        l.i("Declarator run begun - reading line by line")
        lines.forEachIndexed(this::parseLine)
        l.i("Declarator run 50% - validating found declarations")
        this.validate()
        l.i("Declarator run completed - found {} declarations", this.declarations.count())
    }

    private fun parseLine(i: Int, s: String) {
        try {
            this.parseLine0(i, s.trimEnd())
        } catch (e: Exception) {
            e rethrowAs ::DeclaratorException
        }
    }

    // So that we do not have another level of indentation due to try-catch
    private fun parseLine0(i: Int, s: String) {
        if (!this.isDeclarationSection) {
            this.ensureNoMisplacedDeclarations(i, s)
            return
        }

        // Currently in declaration section
        if (i == 0) {
            l.t("Line 0 -> Deferring to DOCTYPE identification")
            this.expectDoctype(s)
            return
        }

        // Now we do not expect a doctype declaration anymore
        // We are going to attempt reading all the following lines
        // for more declarations. Blank lines are allowed, anything
        // else terminates the section -> after that only validation
        if (s.isBlank()) return

        if (!this.isDeclaration(s)) {
            if (!this.mayBeLegacyDeclaration(s)) {
                // Not a legacy declaration nor a declaration: let's
                // mark the end of the declaration section
                l.d("Reached end of declaration section")
                this.isDeclarationSection = false
                return
            }

            // This may be a legacy declaration
            // Let's try to parse it
            this.attemptLegacyParse(i, s)
            return
        }

        // Not legacy at all, but a declaration yes
        // Let's parse it here we go
        this.parseDeclaration(i, s)
    }

    private fun ensureNoMisplacedDeclarations(i: Int, s: String) {
        if (s.startsWith(DECLARATION_BEGIN_MARKER)) {
            throw MisplacedDeclarationException(buildMessage("Found declaration in a place it shouldn't be.\n" +
                    "Declarations must be placed at the top of the file, before all preprocessor instructions and " +
                    "entity representations. It is not valid to specify a declaration in any other positions in the file",
                    i.toLineNumber(), s, 1))
        }
    }

    private fun expectDoctype(s: String) {
        if (!this.isDeclaration(s)) {
            l.e("No valid declaration found - DOCTYPE expected! Aborting run")
            this.expectDoctypeError(s)
        }

        val declaration = s.removePrefix(DECLARATION_BEGIN_MARKER).removeSuffix(DECLARATION_PREPROCESSOR_END_MARKER)
        val split = declaration.split(" ", ignoreCase = true, limit = 2)
        val keyword = split[0]

        if (keyword != "DOCTYPE") {
            l.e("Found declaration but was not the expected one: {} instead of DOCTYPE. Aborting run", keyword)
            this.expectDoctypeError(s, 2)
        }
        if (split.count() != 2) {
            l.e("Invalid split data {} -> at least 2 elements expected", split)
            this.expectDoctypeError(s)
        }

        val docType = split[1].toLowerCase(Locale.ENGLISH)
        if (docType != "skd") {
            l.e("Found DOCTYPE declaration, but the element is not an SKD file - aborting")
            throw DoctypeInvalidException(buildMessage("This is not an SKD document: DOCTYPE is $docType",
                    0.toLineNumber(), s, "<!DOCTYPE s".count()))
        }

        l.d("Found valid DOCTYPE declaration: constructing instance")
        val doctypeDeclaration = Skd.provider.buildDeclaration(keyword, docType)
        l.t("{}", doctypeDeclaration)
        this.declarations.add(doctypeDeclaration)
    }

    private fun expectDoctypeError(s: String, errorIndex: Int = 0): Nothing {
        val msg = "Expected DOCTYPE declaration but found $s\n" +
                "The DOCTYPE declaration must be on the first line of the file and there cannot be any spaces " +
                "before it.\nA valid declaration is thus \"<!DOCTYPE skd>\"\nPlease verify your database file and " +
                "try again."
        throw DoctypeNotFoundException(buildMessage(msg, 0.toLineNumber(), s, errorIndex))
    }

    private fun attemptLegacyParse(i: Int, s: String) {
        val declaration = s.removePrefix(LEGACY_DECLARATION_BEGIN_MARKER).removeSuffix(DECLARATION_PREPROCESSOR_END_MARKER)
        val splits = declaration.split(" ", ignoreCase = true, limit = 2)
        val keyword = splits[0]
        if (keyword != "SKD") {
            // This is not a legacy document type: it is probably an entity.
            // Let's not parse it
            l.d("Found what could have been a legacy declaration but in reality it is not. Marking the end of it all")
            this.isDeclarationSection = false
            return
        }
        // This is a legacy skd version declaration
        l.w("Identified legacy declaration {} for SKD version at line {}", s, i.toLineNumber())

        // Now we parse the legacy identifier
        val versionIdentifier = splits[1]
        when (versionIdentifier) {
            "0.1" -> {
                // This is the old and first database version
                // Back in the old glory days, <SKD was simply used, so we need
                // to acknowledge this error and parse it anyway
                l.i("Legacy declaration specifies original version 0.1 - allowing successful parsing")
            }
            "0.2", "0.2.1" -> {
                // Here support for both is provided. 0.2.1 was merely a new parser
                // improvement over 0.2, so there is basically no difference. Anyway
                // <!SKD was encouraged here
                l.w("*************************************************************")
                l.w("* The legacy version declaration <SKD {}> is deprecated. *", versionIdentifier)
                l.w("* Support WILL BE DROPPED in the next version!              *")
                l.w("* Update the version declaration to the current format to   *")
                l.w("* ensure compatibility with all the newer language versions *")
                l.w("* and future parsers! Use <!SKD {}> instead!             *", versionIdentifier)
                l.w("*************************************************************")
            }
            else -> {
                // Whatever else, we simply do not allow it
                l.e("The legacy version declaration <SKD version> is no more supported")
                l.e("Please use the new declaration style <!SKD version> instead")
                l.e("THIS IS A FATAL ERROR, PARSING CANNOT CONTINUE")
                throw UnsupportedLegacyDeclarationException(buildMessage("The old syntax <SKD version> is no more supported." +
                        "Use <!SKD version> instead from now on.", i.toLineNumber(), s, 1))
            }
        }

        // Now we can add the declaration to the list
        this.declarations.add(Skd.provider.buildDeclaration(keyword, versionIdentifier))
    }

    private fun parseDeclaration(i: Int, s: String) {
        val declaration = s.removePrefix(DECLARATION_BEGIN_MARKER).removeSuffix(DECLARATION_PREPROCESSOR_END_MARKER)
        val splitting = declaration.split(" ", ignoreCase = true, limit = 2)

        if (splitting.isEmpty()) {
            throw DeclarationNotValidException(buildMessage(
                    "The declaration is missing both key and value. What kind of declaration is this even?",
                    i.toLineNumber(), s, 2
            ))
        }

        val key = splitting[0]
        val value = if (splitting.count() < 2) "" else splitting[1]

        if (!key.isValidKey()) {
            throw DeclarationNotValidException(buildMessage(
                    "The declaration key must be made up of only uppercase letters and underscores and cannot be blank\n" +
                            "Valid declarations are, e.g., DOCTYPE and SKD. DocType and Skd are invalid.", i.toLineNumber(),
                    s, 2))
        }

        if (value.isBlank()) {
            throw DeclarationNotValidException(buildMessage("The declaration value cannot be left blank",
                    i.toLineNumber(), s, "<!$key v".count()))
        }

        val instance = try {
            Skd.provider.buildDeclaration(key, value)
        } catch (e: IllegalArgumentException) {
            throw DeclarationNotValidException(e.extractMessage(), e)
        }

        l.i("Successfully identified and parsed declaration {} -> {}", key, value)
        l.t("Declaration: {}", instance)

        this.declarations.add(instance)
    }

    private fun validate() {
        try {
            this.validate0()
        } catch (e: Exception) {
            e rethrowAs ::DeclaratorException
        }
    }

    private fun validate0() {
        l.t("Looking up required declarations -> looking for both DOCTYPE and SKD declarations")
        val docType = this.declarations.asSequence()
                .filter { it.getDeclarationType() == Declaration.DeclarationType.DOCUMENT_TYPE }
                .firstOrNull()
        val skd = this.declarations.asSequence()
                .filter { it.getDeclarationType() == Declaration.DeclarationType.SKD_VERSION }
                .firstOrNull()

        if (docType == null) {
            l.e("Required DOCTYPE declaration not found: this is a FATAL error")
            throw RequiredDeclarationNotFoundException("Missing DOCTYPE declaration! This is a REQUIRED declaration!")
        }
        if (skd == null) {
            l.e("Required SKD declaration not found: this is a FATAL error")
            throw RequiredDeclarationNotFoundException("Missing SKD declaration! This is a REQUIRED declaration!")
        }
        l.t("Declarations found successfully: (DOCTYPE -> {}), (SKD -> {})", docType, skd)

        l.t("Ensuring no multiple keys are present")

        for (i in 0 until this.declarations.count()) {
            for (j in (i + 1) until this.declarations.count()) {
                if (this.declarations[i].getKey() == this.declarations[j].getKey()) {
                    val second = this.declarations[j]
                    val first = this.declarations[i]
                    val key = first.getKey()

                    l.e("Found multiple declarations with the same key {}: {} and {}. Parsing cannot continue!", key, first, second)
                    throw MultipleDeclarationsWithSameKeyFoundException(
                            "Found multiple declarations with the same key $key: $first and $second. This is a FATAL error")
                }
            }
        }

        l.t("No multiple declarations found")
        l.d("All checks passed successfully")
    }

    private fun mayBeLegacyDeclaration(s: String): Boolean =
            s.startsWith(LEGACY_DECLARATION_BEGIN_MARKER) && !s.startsWith(PREPROCESSOR_BEGIN_MARKER)
    private fun isDeclaration(s: String): Boolean = s.startsWith(DECLARATION_BEGIN_MARKER)
}

internal class DeclaratorException(message: String, cause: Throwable?): Exception(message, cause)
internal class DoctypeNotFoundException(message: String): Exception(message)
internal class DoctypeInvalidException(message: String): Exception(message)
internal class UnsupportedLegacyDeclarationException(message: String): Exception(message)
internal class DeclarationNotValidException(message: String, cause: Throwable? = null): Exception(message, cause)
internal class MisplacedDeclarationException(message: String): Exception(message)
internal class RequiredDeclarationNotFoundException(message: String): Exception(message)
internal class MultipleDeclarationsWithSameKeyFoundException(message: String): Exception(message)

private fun buildMessage(msg: String, line: LineNumber, found: String, errorIndex: Int): String {
    val builder = StringBuilder()
    builder.append("Error line $line - $msg\n$found\n")
    for (i in 1 until errorIndex) builder.append(ERROR_MESSAGE_ERROR_LINE_BEFORE)
    builder.append(ERROR_MESSAGE_ERROR_MARKER)
    return builder.toString()
}

private fun String.isValidKey(): Boolean = this.isUpperCase() && this.asSequence().filterNot { it.isLetter() }.filter { it == '_' }.none()
