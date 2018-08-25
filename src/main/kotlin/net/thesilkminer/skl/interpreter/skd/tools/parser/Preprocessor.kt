@file:JvmName("PUtK")
@file:JvmMultifileClass

package net.thesilkminer.skl.interpreter.skd.tools.parser

import com.google.common.collect.ImmutableList
import com.google.common.collect.Lists
import net.thesilkminer.skl.interpreter.skd.api.Skd
import net.thesilkminer.skl.interpreter.skd.api.structure.PreprocessorInstruction as PI
import net.thesilkminer.skl.interpreter.skd.extractMessage
import net.thesilkminer.skl.interpreter.skd.plusAssign
import net.thesilkminer.skl.interpreter.skd.invoke
import net.thesilkminer.skl.interpreter.skd.isLowerCase
import net.thesilkminer.skl.interpreter.skd.multicatch
import net.thesilkminer.skl.interpreter.skd.rethrowAs
import net.thesilkminer.skl.interpreter.skd.tools.FileSupport
import net.thesilkminer.skl.interpreter.skd.unaryMinus
import java.util.EmptyStackException
import java.util.Locale
import java.util.Stack

class Preprocessor(private val lines: MutableList<String>) {
    companion object {
        private val l = L(this)
    }

    val instructions: MutableList<PIWrapper> = Lists.newArrayList()!!

    private val ifStack = IfStack()

    private val properties: MutableList<Property> = Lists.newArrayList()!!
    private val variables: MutableList<Variable> = Lists.newArrayList()!!
    private val macros: MutableList<Macro> = Lists.newArrayList()!!

    private val specialAccessInstructions: MutableMap<QueryType, Any> = mutableMapOf()
    private val linesWithReplacements: MutableMap<LineNumber, String> = mutableMapOf()

    init {
        this.populateWithDefaults()
        l.i("Successfully initialized preprocessor {}", this)
    }

    fun <T> query(queryType: QueryType): T? = this.specialAccessInstructions[queryType].toT()

    fun parse() {
        l.i("Started preprocessor")
        this.lines.forEachIndexed(this::parseLine)
        l.t("Replacing lines that need replacement")
        this.linesWithReplacements.forEach { k, v -> this.lines[k] = v }
        this.linesWithReplacements.clear()
        l.t("Starting include lines processing")
        this.processIncludes(this.lines)
        l.i("Terminating preprocessor")
        l.i("Successfully found and parsed {} instructions", this.instructions.count())
        l.t("Logging wrappers")
        this.instructions.forEach { l.t("    {}", it) }
        l.t("    === END ===")
    }

    private fun populateWithDefaults() {
        l.d("Adding default properties, variables and macros")
        this.populateWithDefaultProperties()
        this.populateWithDefaultVariables()
        this.populateWithDefaultMacros()
        l.t("Default properties: ")
        this.properties.forEach { l.t("    {}", it) }
        l.t("Default variables: ")
        this.variables.forEach { l.t("    {}", it) }
        l.t("Default macros: ")
        this.macros.forEach { l.t("    {}", it) }
    }

    private fun populateWithDefaultProperties() {
        l.t("Populating with default properties")
        this.properties.add(Property("__C_XML_SUPPORTED"))
        //this.properties.add(Property("__C_JSON_SUPPORTED")) TODO()
        //this.properties.add(Property("__XML_SUPPORTED")) TODO()
        //this.properties.add(Property("__JSON_SUPPORTED")) TODO()
        //this.properties.add(Property("__RECURSIVE_PREPROCESSOR")) TODO()
    }

    private fun populateWithDefaultVariables() {
        l.t("Populating with default variables")
        this.variables.add(Variable("__PREPROCESSOR_VERSION", "0.3"))
        //this.variables.add(Variable("__RECURSIVE_PREPROCESSOR_DEPTH", "0"))
    }

    private fun populateWithDefaultMacros() {
        l.t("Populating with default macros")
        this.macros.add(Macro("TODO", listOf(), listOf("Not yet implemented")))
        this.macros.add(Macro("TO_DO", listOf("a"), listOf("Not yet implemented: a")))
    }

    private fun parseLine(i: Int, s: String) {
        try {
            this.parseLine0(i, s.trimEnd())
        } catch (e: Exception) {
            e rethrowAs ::PreprocessorException
        }
    }

    private fun parseLine0(i: Int, s: String) {
        // Whatever happens, we are sure to run AFTER the declarator has done its job
        // because of the design of the parser -- see also the Javadocs present --.
        // For this reason, every like that begins with a declarator marker can be
        // freely skipped. We do not need to worry if a legacy SKD declaration is
        // present though: the preprocessor component runs if and only if the SKD
        // version is at least 0.3. All the lower versions did not have such a concept
        // of a preprocessor.
        if (s.startsWith(DECLARATION_BEGIN_MARKER)) return

        // Here, if we are inside a disabled "if" block (whatever that may be, usually
        // an if_def), we need to bypass and basically only check for end_ifs. In other
        // words, we need a new, custom function
        if (this.isInsideDisabledIf()) return this.handleDisabledIf(i, s)

        // Now we need to check if the line is a preprocessor line or not. If it
        // is not, whatever that is, I don't care: the parser will throw if it is
        // an invalid part. I just need to replace stuff if I have to.
        if (!s.startsWith(PREPROCESSOR_BEGIN_MARKER)) return this.handleReplacements(i, s)

        // A preprocessor line! Let's parse its instruction then!
        this.parsePI(i, this.ensureValidPIAndStrip(i, s))
    }

    private fun ensureValidPIAndStrip(i: Int, s: String): String {
        // The line must end with an end of instruction marker, otherwise it is malformed
        if (!s.endsWith(DECLARATION_PREPROCESSOR_END_MARKER)) {
            throw MalformedPreprocessorInstructionException(buildMessage(
                    "Unable to find the declaration end marker!\nA preprocessor instruction must start with <# and " +
                            "end with >. Remember that it cannot span multiple lines!",
                    i.toLineNumber(), s, s.count() - 1
            ))
        }
        return s.removePrefix(PREPROCESSOR_BEGIN_MARKER).removeSuffix(DECLARATION_PREPROCESSOR_END_MARKER)
    }

    private fun parsePI(i: Int, s: String) {
        l.t("Attempting to parse instruction {}", s)
        val pi = try {
            Skd.provider.buildPreprocessorInstruction(s, i.toLineNumber().toLong())
        } catch (e: IllegalArgumentException) {
            val message = StringBuilder()
            message += e.extractMessage("An unknown error has occurred while parsing the instruction")
            message += "\nPlease make sure that the instruction follows the rules and that it is a valid instruction "
            message += "for the preprocessor.\nCheck the preprocessor documentations and the docs directory on GitHub "
            message += "for more information."
            throw InvalidPreprocessorInstructionException(buildMessage(message(), i.toLineNumber(), s, 2), e)
        }
        
        when (pi.getInstructionType()) {
            PI.InstructionType.DELETED_LINE,
            PI.InstructionType.INCLUDE_FILE_BEGIN_MARK,
            PI.InstructionType.INCLUDE_FILE_END_MARK -> {
                l.e("Found @InternalOnly instruction in a database representation. THIS IS AN ERROR!")
                throw InternalOnlyPreprocessorInstructionException(buildMessage(
                        "You cannot use an instruction marked @InternalOnly in a database. These instructions " +
                                "can be used only by the parser and/or the preprocessor itself.",
                        i.toLineNumber(), s, 3
                ))
            }
            PI.InstructionType.DEFINE_ENVIRONMENT_PROPERTY -> {
                val property = pi.getArguments()[0]
                l.d("Found environment property {}", property)
                this.properties.add(Property(property))
            }
            PI.InstructionType.DEFINE_ENVIRONMENT_VARIABLE -> {
                val (name, value) = pi.getArguments()
                l.d("Found environment variable {} -> {}", name, value)
                this.variables.add(Variable(name, value))
            }
            PI.InstructionType.DEFINE_MACRO -> {
                val macro = pi.stringifyArguments()
                val (name, argString, funString) = macro.split(Regex("[()]"), limit = 3)
                val args = argString.split(",").map { it.trim() }
                val `fun` = funString.split(";").map { it.trim() }
                l.d("Found macro {} ({}) { {} }", name, args, `fun`)
                this.macros.add(Macro(name, args, `fun`))
            }
            PI.InstructionType.SYNTAX -> {
                l.i("Found syntax instruction!")
                val syntax = pi.getArguments()[0]
                if (!syntax.isLowerCase()) {
                    l.w("Attention! The syntax specified ({}) is NOT lowercase", syntax)
                    l.w("Lower-casing is NOT enforced, but it is highly suggested for compatibility with parsers")
                    l.w("Other parser implementations may throw an error instead of showing this message")
                    l.w("Lower-casing will now be automatically handled by the parser")
                    l.w("This is NOT an error, but this is also NOT RECOMMENDED")
                    pi.setInstructionAndType(PI.InstructionType.SYNTAX, pi.getInstruction().toLowerCase(Locale.ENGLISH))
                }
                l.i("Identified syntax: {}", syntax)
                this.specialAccessInstructions[QueryType.SYNTAX] = syntax
            }
            PI.InstructionType.THROW_ERROR -> {
                // The syntax of throw is ASTONISHINGLY versatile, wow.
                val argumentsList = pi.getArguments()
                val maybeClass = try {
                    Class.forName(argumentsList[0])
                } catch (e: ClassNotFoundException) {
                    null
                }
                if (maybeClass == null) {
                    val errorMessage = pi.stringifyArguments()
                    throw ThrowException(buildMessage(errorMessage, i.toLineNumber(), s, "<#throw a".count()))
                }
                val errorMessage = pi.stringifyArguments().removePrefix(argumentsList[0]).trimStart()
                val builtMessage = buildMessage(errorMessage, i.toLineNumber(), s, "<#throw a".count())
                try {
                    l.e("FATAL ERROR! The preprocessor was asked to throw an exception!")
                    l.e("FATAL ERROR! {}", errorMessage)
                    try {
                        val throwable = maybeClass.getConstructor(String::class.java).newInstance(builtMessage) as Throwable
                        throw throwable
                    } catch (e: ReflectiveOperationException) {
                        val throwable = maybeClass.getConstructor().newInstance() as Throwable
                        throwable.initCause(ThrowException(builtMessage))
                        throw throwable
                    }
                } catch (e: ReflectiveOperationException) {
                    e.initCause(ThrowException(buildMessage(errorMessage, i.toLineNumber(), s, "<#throw a".count())))
                    throw e
                }
            }
            PI.InstructionType.IF_VARIABLE_OR_PROPERTY_DEFINED,
            PI.InstructionType.IF_VARIABLE_OR_PROPERTY_NOT_DEFINED -> {
                this.handleDisablingIf(pi, pi.getInstructionType() == PI.InstructionType.IF_VARIABLE_OR_PROPERTY_DEFINED)
            }
            PI.InstructionType.ELSE -> {
                l.d("Found else conditional instruction")
                if (this.ifStack.peek() == null) {
                    l.e("FATAL! Found else without a matching if before!")
                    throw IllegalElseLocationException(buildMessage(
                            "Found else instruction without matching if clause. Make sure the syntax is something like " +
                                    "<#if xxx>\n...\n<#else>",
                            i.toLineNumber(), s, 3
                    ))
                }
                l.t("Old if value: disabled? {}", this.ifStack.peek() != null && this.ifStack.peek() == IfStack.DISABLED_IF)
                when (this.ifStack.pop()) {
                    IfStack.DISABLED_IF -> this.ifStack.pushEnabled()
                    IfStack.ENABLED_IF -> this.ifStack.pushDisabled()
                    else -> throw IllegalStateException("How is that even possible? Boolean was both not true and not false!")
                }
                l.t("New if value: disabled? {}", this.ifStack.peek() != null && this.ifStack.peek() == IfStack.DISABLED_IF)
            }
            PI.InstructionType.END_IF_STATEMENT -> {
                l.d("Found termination of conditional statement {}", pi.getInstruction())
                if (this.ifStack.peek() == null) {
                    l.e("FATAL! Found end if without a matching if before!")
                    throw IllegalEndIfLocationException(buildMessage(
                            "Found if terminating instruction without a matching if clause. Make sure you are not missing an " +
                                    "if instruction before.", i.toLineNumber(), s, 3
                    ))
                }
                l.t("Removing if from stack, was {}", this.ifStack.pop())
            }
            PI.InstructionType.INCLUDE_FILE -> {
                l.d("Found include request {}", pi.getInstruction())
                l.d("This will be processed later")
            }
            PI.InstructionType.SUB_ENTITY_NAME_ASSIGNATION -> {
                l.d("Found sub entity name assignation {}", pi.getInstruction())
                val (arrayLike, subEntity) = pi.getArguments()
                l.t("Found mapping for {} to {}", arrayLike, subEntity)
                var map = this.query<MutableMap<String, String>>(QueryType.NAME_ASSIGN)
                if (map == null) {
                    this.specialAccessInstructions[QueryType.NAME_ASSIGN] = mutableMapOf<String, String>()
                    map = this.query<MutableMap<String, String>>(QueryType.NAME_ASSIGN)
                    if (map == null) {
                        throw IllegalStateException("map == null after creation @ name assign")
                    }
                }
                map[arrayLike] = subEntity
            }
        }

        if (pi.getInstructionType() == PI.InstructionType.INCLUDE_FILE) {
            // Include files are handled elsewhere, do not add now
            return
        }

        val lineBefore = this.lines[i - 1]
        val lineAfter = this.lines[i + 1]
        val wrapper = PIWrapper(pi, lineBefore, lineAfter)
        this.instructions.add(wrapper)
        l.t("Added {} to instructions found", wrapper)
    }

    private fun handleDisablingIf(p: PI, b: Boolean) {
        l.d("Found conditional instruction '{}'", p.getInstruction())
        l.t("Is instruction inverted? {}", !b)
        val variable = p.getArguments()[0]
        val isPresentInProperties = this.properties.asSequence().map { it.property }.filter { it == variable }.any()
        val isPresentInVariables = this.variables.asSequence().map { it.name }.filter { it == variable }.any()
        val isPresent = isPresentInProperties || isPresentInVariables
        l.t("Checking on property/variable {} -> {}", variable, isPresent)
        if ((b && isPresent) || (!b && !isPresent)) {
            l.d("if condition satisfied")
            l.t("Pushing IF onto the stack and enabling lines")
            this.ifStack.pushEnabled()
        } else if ((b && !isPresent) || (!b && isPresent)) {
            l.d("if condition not satisfied")
            l.t("Pushing IF onto the stack and disabling lines")
            this.ifStack.pushDisabled()
        } else {
            throw IllegalStateException("How did we even reach this point? $b, $isPresent, $variable, ${p.getInstruction()}")
        }
    }

    private fun handleDisabledIf(i: Int, s: String) {
        val pi = if (s.startsWith(PREPROCESSOR_BEGIN_MARKER)) {
            try {
                Skd.provider.buildPreprocessorInstruction(this.ensureValidPIAndStrip(i, s), -1L)
            } catch (e: Exception) {
                e.multicatch(MalformedPreprocessorInstructionException::class.java, IllegalArgumentException::class.java) {
                    // Do nothing and let the flow continue, it is not my problem
                    null
                }
            }
        } else {
            null
        }

        if (pi != null) {
            // This is a preprocessor instruction
            l.d("Found disabled preprocessor instruction")
            if (pi.getInstructionType() == PI.InstructionType.IF_VARIABLE_OR_PROPERTY_NOT_DEFINED
                    || pi.getInstructionType() == PI.InstructionType.IF_VARIABLE_OR_PROPERTY_DEFINED) {
                // We are inside another if, so when we roll an endif the parser does not throw up
                // Enabled or disabled I don't care, because we are disabled as long as this block
                // is running.
                this.ifStack.pushEnabled()
            } else if (pi.getInstructionType() == PI.InstructionType.ELSE
                    || pi.getInstructionType() == PI.InstructionType.END_IF_STATEMENT) {
                return this.parsePI(i, this.ensureValidPIAndStrip(i, s))
            } else {
                // let's continue like normal, this is not our problem
                l.t("Skipping processing, we do not really need that")
            }
        }

        val newInstruction = Skd.provider.buildPreprocessorInstruction(
                "${PI.InstructionType.DELETED_LINE.keyword} $s", i.toLineNumber().toLong())
        this.instructions.add(PIWrapper(newInstruction, this.lines[i - 1], this.lines[i + 1]))
    }

    private fun handleReplacements(i: Int, s: String) {
        val afterVariables = this.handleVariableReplacements(i, s)
        val afterMacros = this.handleMacroReplacements(i, afterVariables)
        if (s != afterMacros) {
            l.t("Replacements happened -> storing them for later")
            this.linesWithReplacements[i.toLineNumber()] = afterMacros
        }
    }

    private fun handleVariableReplacements(i: Int, s: String): String {
        val builder = StringBuilder()
        val words = s.split(" ")
        words.forEach { builder += this.handleVariableReplacement(it) }
        val result = builder()
        if (s != result) {
            l.t("Performed variable replacements on line {}: '{}' -> '{}'", i.toLineNumber(), s, result)
        }
        return result
    }

    private fun handleVariableReplacement(w: String): String {
        if (this.variables.map { it.name }.contains(w)) return this.variables.first { it.name == w }.value
        return w
    }

    private fun handleMacroReplacements(i: Int, s: String): String {
        val macroWords = mutableListOf<String>()
        val builder = StringBuilder()
        var inParentheses = false
        s.chars().forEach {
            val c = it.toChar()
            if (c == ' ' && !inParentheses) {
                macroWords.add(builder())
                -builder
                return@forEach
            }
            if (c == '(') {
                inParentheses = true
            }
            if (c == ')') {
                inParentheses = false
            }
            builder += c
        }
        if (builder().isNotBlank()) macroWords.add(builder())
        -builder
        macroWords.removeIf { it.isBlank() }
        macroWords.forEach { builder += this.handleMacroReplacement(it) }
        val result = builder()
        if (s != result) {
            l.t("Performed macro replacements on line {}: '{}' -> '{}'", i.toLineNumber(), s, result)
        }
        return result
    }

    private fun handleMacroReplacement(w: String): String {
        val (name, argString) = w.split(Regex("[()]"), limit = 2)
        val args = argString.split(",").map { it.trim() }
        val macro = this.macros.filter { it.name == name }.firstOrNull { it.parameters.count() == args.count() }
        if (macro == null) {
            l.w("Found macro invocation {}, but no valid macro was found", w)
            return w
        }
        val macroArgs = macro.parameters
        val macroFunctions = mutableListOf<String>()
        macro.function.forEach { macroFunctions.add(it) }
        for (i in 0 until macroArgs.count()) {
            val id = macroArgs[i]
            val replacement = args[i]
            val tmp = mutableListOf<String>()
            macroFunctions.forEach { tmp.add(it.replace(id, replacement)) }
            macroFunctions.clear()
            tmp.forEach { macroFunctions.add(it) }
        }
        val builder = StringBuilder()
        macroFunctions.forEach {
            builder += it
            builder += "; "
        }
        l.t("Replaced macro invocation {} with macro body {}", w, builder())
        return builder()
    }

    private fun isInsideDisabledIf() = this.ifStack.seekDisabled()

    private fun processIncludes(lines: MutableList<String>) {
        try {
            this.parseIncludes(lines)
        } catch (e: Exception) {
            e rethrowAs ::InclusionException
        }
    }

    private fun parseIncludes(lines: MutableList<String>) {
        val listWithNewLines = Lists.newArrayList<String>()!!
        lines.forEachIndexed { i, it ->
            listWithNewLines.add(it)
            if (it.trimStart().startsWith("<#include ")) {
                // We need to process this include actually
                val pi = Skd.provider.buildPreprocessorInstruction(it, i.toLineNumber().toLong())
                val argument = pi.stringifyArguments()
                l.t("Found an inclusion request for file {}", argument)
                if (argument.contains(' ')) {
                    l.e("FATAL! Path contains a space! This is NOT supported by include directives!")
                    throw IllegalIncludeFileInstructionException(buildMessage(
                            "Expected a file name, but found a space inside the include instruction arguments.\n" +
                                    "You can only include one file per instruction and the file path cannot contain spaces.",
                            i.toLineNumber(), it.trimStart(), "<#include ".count() + argument.indexOf(' ')
                    ))
                }

                l.t("Deferring file inclusion up to Loader instance")
                val support = FileSupport(argument) // TODO("Use the API ffs")
                val loader = Loader(support)
                l.t("Loader instance built: {}", loader)
                val loadedFile = try {
                    loader.load().toMutableList()
                } catch (e: LoadingException) {
                    e rethrowAs ::InclusionException
                }
                l.t("Successfully loaded {} lines", loadedFile.count())
                l.t("Now stripping declarations and running preprocessor")
                loadedFile.removeIf { it.startsWith(DECLARATION_BEGIN_MARKER) }
                val pp = this.recursePreprocessor(loadedFile)
                if (pp != this) {
                    // This is a recursive enabled preprocessor
                    TODO("recursive enabled preprocessor")
                }
                l.t("Successfully read {} lines", loadedFile.count())
                l.t("Injecting lines")
                val includeBegin = Skd.provider.buildPreprocessorInstruction("ibg", (i + 1).toLineNumber().toLong())
                val includeEnd = Skd.provider.buildPreprocessorInstruction("ied", (i + 2).toLineNumber().toLong())
                listWithNewLines.add("<#ibg>")
                loadedFile.forEach { listWithNewLines.add(it) }
                listWithNewLines.add("<#ied>")
                this.instructions.add(PIWrapper(pi, lines[i - 1], "<#ibg>"))
                this.instructions.add(PIWrapper(includeBegin, lines[i],
                        try { loadedFile.first() } catch (e: NoSuchElementException) { "<#ied>" }))
                this.instructions.add(PIWrapper(includeEnd,
                        try { loadedFile.last() } catch (e: NoSuchElementException) { "<#ibg>" }, lines[i + 1]))
            }
        }
        lines.clear()
        listWithNewLines.forEach { lines.add(it) }
        listWithNewLines.clear()
    }

    private fun recursePreprocessor(lines: MutableList<String>): Preprocessor {
        l.w("The recursive preprocessor is currently not supported: internal preprocessor lines will NOT be parsed")
        l.w("Support will be provided in a newer version of the implementation and API standard")
        l.w("For this reason it is not possible to have more than one include level")
        lines.forEachIndexed(this::runRecursivePreprocessor)
        return this
    }

    private fun runRecursivePreprocessor(i: Int, s: String) {
        if (s.trimStart().startsWith(PREPROCESSOR_BEGIN_MARKER)) {
            l.w("CRITICAL! Found preprocessor instruction '{}' on line {}. This line will NOT be parsed", s, i.toLineNumber())
        }
    }
}

data class PIWrapper(val instruction: PI, val stringBefore: String, val stringAfter: String) {
    companion object {
        operator fun invoke(instance: PIWrapper): PI = instance.instruction
    }
}

enum class QueryType {
    SYNTAX,
    NAME_ASSIGN
}

private data class Property(val property: String)
private data class Variable(val name: String, val value: String)
private data class Macro(val name: String, val parameters: List<String>, val function: List<String>)

private class IfStack {
    companion object {
        const val DISABLED_IF = false
        const val ENABLED_IF = true
    }
    private val stack = Stack<Boolean>()
    private fun push(how: Boolean) = this.stack.push(how)
    private fun seek(how: Boolean) = this.stack.search(how) != -1
    fun pushDisabled() = this.push(DISABLED_IF)
    fun pushEnabled() = this.push(ENABLED_IF)
    fun pop() = this.stack.pop()
    fun peek() = try { this.stack.peek() } catch (e: EmptyStackException) { null } // If it happens, we are already crashing so... no probs
    fun seekDisabled() = this.seek(DISABLED_IF)
    fun seekEnabled() = this.seek(ENABLED_IF)
}

internal class PreprocessorException(message: String, cause: Throwable?): Exception(message, cause)
internal class MalformedPreprocessorInstructionException(message: String): Exception(message)
internal class InvalidPreprocessorInstructionException(message: String, cause: Throwable? = null): Exception(message, cause)
internal class InternalOnlyPreprocessorInstructionException(message: String): Exception(message)
internal class ThrowException(message: String): Exception(message)
internal class IllegalElseLocationException(message: String): Exception(message)
internal class IllegalEndIfLocationException(message: String): Exception(message)
internal class IllegalIncludeFileInstructionException(message: String): Exception(message)
internal class InclusionException(message: String, cause: Throwable?): Exception(message, cause)

private fun PI.getKeyword() = this.getWords()[0]
private fun PI.getArguments() = ImmutableList.copyOf(this.getWords().subList(1, this.getWords().count()))!!
private fun PI.getWords() = this.getInstruction().split(" ", ignoreCase = true)
private fun PI.stringifyArguments() = this.getInstruction().removePrefix(this.getKeyword()).trimStart()

@Suppress("UNCHECKED_CAST") private fun <T> Any?.toT() = if (this == null) null else this as T?
