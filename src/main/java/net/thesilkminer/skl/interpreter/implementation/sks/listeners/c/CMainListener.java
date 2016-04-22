package net.thesilkminer.skl.interpreter.implementation.sks.listeners.c;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.thesilkminer.skl.interpreter.api.sks.holder.IScriptHolder;
import net.thesilkminer.skl.interpreter.api.sks.language.IllegalScriptException;
import net.thesilkminer.skl.interpreter.api.sks.listener.IScriptListener;
import net.thesilkminer.skl.interpreter.api.sks.listener.Result;
import net.thesilkminer.skl.interpreter.api.sks.parser.ISksParser;
import net.thesilkminer.skl.interpreter.implementation.sks.listeners.java.JavaMainListener;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Represents the C listener provided by the interpreter.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
// TODO
public class CMainListener implements IScriptListener {

	private static class CompilerException extends RuntimeException {

		private static class PreProcessorException extends CompilerException {

			@SuppressWarnings("unused")
			public PreProcessorException() {

				this("The C pre-processor has thrown an error");
			}

			public PreProcessorException(final String message) {

				this(message, null);
			}

			public PreProcessorException(final String message, final Throwable cause) {

				super(message, cause);
			}
		}

		public CompilerException() {

			this("The C compiler has thrown an error");
		}

		@SuppressWarnings("SameParameterValue") // API Method
		public CompilerException(final String message) {

			this(message, null);
		}

		public CompilerException(final String message, final Throwable cause) {

			super(message, cause);
		}
	}

	private enum PreProcessingInstructions {

		INCLUDE,
		DEFINE;

		public static PreProcessingInstructions from(final String instr) {

			try {

				return valueOf(instr.toUpperCase(java.util.Locale.ENGLISH));
			} catch (final IllegalArgumentException ex) {

				throw new CompilerException.PreProcessorException(
						"Unrecognized pre-processor command", ex);
			}
		}

		@Override
		public String toString() {

			return super.toString().toLowerCase(java.util.Locale.ENGLISH);
		}
	}

	private class WrongSyntaxException extends RuntimeException {

		public WrongSyntaxException(final Throwable cause) {

			super("Syntax error in C script", cause);
		}
	}

	private JavaMainListener javaListener;
	private String scriptName;
	private final Map<String, String> definitions = Maps.newLinkedHashMap();
	private final List<String> logLines = Lists.newArrayList();
	private final List<String> libraryCodes = Lists.newArrayList();

	@Override
	public String listenerFor() {

		return "c";
	}

	@Override
	public boolean needsInit() {

		return true;
	}

	@Override
	public boolean hasAlreadyInit() {

		return false;
	}

	@Override
	public void init(ISksParser parser, IScriptHolder scriptFile) {

		this.scriptName = parser.getScriptName();
		this.javaListener = new JavaMainListener();

		if (this.javaListener.needsInit() && !this.javaListener.hasAlreadyInit()) {

			this.javaListener.init(parser, scriptFile);
		}
	}

	@Override
	public void runScript(final List<String> lines) {

		try {

			final List<String> javaLines = Lists.newArrayList();

			// Sanity check
			// Replace lines from C to Java

			for (final String line : lines) {

				this.logLines.add("Parsing line " + line);

				if (line.startsWith("#")) {

					javaLines.add(this.parsePreProcessorLine(line));
					continue;
				}

				final String processedLine = this.processDefinition(line);

				javaLines.add(processedLine);
			}

			this.addLibraryCodes(javaLines);
			this.checkJava(javaLines);

			this.javaListener.runScript(javaLines);
		} catch (final CompilerException ex) {

			// Simply an exception packaging

			try {

				throw new WrongSyntaxException(ex);
			} catch (final WrongSyntaxException exc) {

				try {

					throw new IllegalStateException(exc);
				} catch (final IllegalStateException exception) {

					throw new IllegalScriptException(exception);
				}
			}
		}
	}

	private String parsePreProcessorLine(final String line) {

		try {

			// TODO
			final String instruction = line.substring(1).trim();

			this.logLines.add(
			        String.format("Pre-Processor: Parsing instruction \"%s\"",
							      instruction));

			if (instruction.isEmpty()) {

				return instruction;
			}

			final PreProcessingInstructions ppi = PreProcessingInstructions
					      .from(instruction);

			final String newInstruction = this.removeInstruction(instruction, ppi);

			switch (ppi) {

				case INCLUDE:
					processInclude(newInstruction);
					break;
				case DEFINE:
					processDefine(newInstruction);
					break;
				default:
					throw new CompilerException.PreProcessorException(
							"Unrecognized pre-processor instruction: "
									+ instruction);
			}

			return "";
		} catch (final CompilerException.PreProcessorException ex) {

			throw new CompilerException(ex.getMessage(), ex);
		}
	}

	private String removeInstruction(final String instr, final PreProcessingInstructions ppi) {

		return instr.replace(ppi.toString(), "");
	}

	@SuppressWarnings({"EmptyMethod", "UnusedParameters"}) //TODO
	private void processInclude(final String include) {

		// TODO
		// Add to library codes
		// Use includes in assets directory
		// Includes are with the same name as the libraries
		// Code inside them is pure Java (non compiled) code
		// Path:
		// assets/sks_interpreter/listener_resources/c_listener/libraries/mingw64/
	}

	private void processDefine(final String define) {

		String def = define;

		if (def.startsWith("#")) {

			def = def.replace("#", " ");
		}

		while (def.startsWith(" ")) {

			def = def.substring(1);
		}

		// 0: Defining
		// 1: Definition
		final String[] parts = def.split(Pattern.quote(" "));

		if (parts.length != 2) {

			if (parts.length < 2) {

				logLines.add("[WARN]Strange define statement");
				logLines.add("[WARN]" + define);
				logLines.add("[WARN]This is mainly used for libraries,"
						      + " so I am skipping it.");
				return;
			}

			for (int i = 2; i < parts.length; ++i) {

				parts[1] = parts[1] + " " + parts[i];
			}
		}

		if (parts.length != 2) {

			throw new CompilerException.PreProcessorException(
					"Something wrong has happened w/ the compiler");
		}

		parts[0] = parts[0].trim();

		if (parts[1].startsWith(" ")) {

			parts[1] = parts[1].substring(1);
		}

		this.logLines.add("Definition added: ");
		this.logLines.add(String.format("    %s -> %s", parts[0], parts[1]));

		this.definitions.put(parts[0], parts[1]);
	}

	private String processDefinition(final String line) {

		final String[] words = line.split(Pattern.quote(" "));

		String newLine = line;

		for (final String word : words) {

			if (this.definitions.containsKey(word)) {

				this.logLines.add("Replaced definition:");
				this.logLines.add("    From line: " + newLine);

				newLine = newLine.replace(word, this.definitions.get(word));

				this.logLines.add("    To line: " + newLine);
			}
		}

		return newLine;
	}

	private void addLibraryCodes(final List<String> script) {

		script.addAll(0, this.libraryCodes);
		// Test addition, please
	}

	private void checkJava(final List<String> script) {

		// Add class declaration otherwise we are screwed
		script.add(0, String.format("public class %s {", this.scriptName));
		script.add("}");

		int index = -1;
		boolean bracketAfter = false;
		String changedLine;

		// Replace main declaration with Java's one
		for (int i = 0; i < script.size(); ++i) {

			final String scriptLine = script.get(i);

			if (scriptLine.trim().startsWith("main()")) {

				index = i;
			}

			if (script.get(index + 1).trim().equals("{")) {

				bracketAfter = true;
			}
		}

		if (index != -1) {

			script.set(index, "public static void main(String[] args)");

			if (!bracketAfter) {

				script.add(index + 1, "{");
			}
		}

		changedLine = "";
		index = -1;

		// Check that scanf is equal to the following syntax:
		// scanf("what", variable);
		// If it isn't, replace the &
		for (int i = 0; i < script.size(); ++i) {

			final String line = script.get(i).trim();

			if (line.startsWith("scanf(") && line.endsWith(");")) {

				// TODO?
				changedLine = line.replace("&", "");
				index = i;
			}
		}

		if (index != -1 && !changedLine.isEmpty()) {

			script.set(index, changedLine);
		}

		// Add static to methods declaration
		// TODO
		// FIXME Problem --> Needs to be flexible (can be hardcoded only until 0.4-SNAPSHOT)
	}

	@Override
	public Result result() {

		return Result.SUCCESSFUL;
	}

	@Override
	public Optional<List<String>> toLog() {

		return Optional.of(this.logLines);
	}
}
