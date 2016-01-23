package net.thesilkminer.skl.interpreter.sks;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.thesilkminer.skl.interpreter.sks.components.decisionals.EndIfDeclaration;
import net.thesilkminer.skl.interpreter.sks.components.decisionals.IfDeclaration;
import net.thesilkminer.skl.interpreter.sks.components.declaration.ScriptDeclaration;
import net.thesilkminer.skl.interpreter.sks.components.language.LanguageDeclaration;
import net.thesilkminer.skl.interpreter.sks.components.listeners.FallBackListenersDeclaration;
import net.thesilkminer.skl.interpreter.sks.components.listeners.ListenerDeclaration;
import net.thesilkminer.skl.interpreter.sks.components.listeners.MultiListenerDeclaration;
import net.thesilkminer.skl.interpreter.sks.components.listeners.NoListenerDeclaration;
import net.thesilkminer.skl.interpreter.sks.components.markers.ScriptEndDeclaration;
import net.thesilkminer.skl.interpreter.sks.components.markers.ScriptStartDeclaration;
import net.thesilkminer.skl.interpreter.sks.listeners.c.CMainListener;
import net.thesilkminer.skl.interpreter.sks.listeners.java.JavaMainListener;
import net.thesilkminer.skl.interpreter.sks.listeners.skl.SklMainListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

/**
 * This class is the main SKS parser.
 *
 * <p>This class's scope is parse a specified file and run
 * every script which is found in them.
 */
public final class SksParser {

	private enum FixedExpressions {

		LANGUAGE("<#language \"", "\">", "<#language \"" + USER_VARIABLE + "\""),
		FORCE_LISTENER("<#listener \"", "\">", "<#listener \"" + USER_VARIABLE + "\""),
		SCRIPT_DECLARATION("<#script \"", ">", "<#script \""
				     + USER_VARIABLE + "\" declare " + USER_VARIABLE + ">"),
		SCRIPT_START("<#start \"script\">", null, "<#start \"script\">"),
		SCRIPT_END("<#end script>", null, "<#end script>"),
		;

		private String before;
		private String after;
		private String full;

		FixedExpressions(String before, String after, String full) {

			this.before = before;
			this.after = after;
			this.full = full;
		}

		public String getBefore() {

			return before;
		}

		public String getAfter() {

			return after;
		}

		public String getFull() {

			return full;
		}
	}

	private static Map<ScriptFile, SksParser> map = Maps.newHashMap();
	private static Map<String, IScriptListener> listeners = Maps.newHashMap();
	private static List<ILanguageComponent> components = Lists.newArrayList();
	private boolean hasInit;
	private boolean hasErrored;
	private ScriptFile file;
	private BufferedReader fileReader;

	/* -- Internal stuff -- */
	private boolean hasReachedStart;
	private boolean hasReachedEndOfScript;
	private boolean doWeKnowLanguage;
	private boolean isListenerForced;
	private String actualLanguage;
	private String listenerClass;
	private String scriptName;
	private List<String> listenersClasses;

	/* -- 0.2 rendition stuff -- */
	/**
	 * Simply needed to pass VarArg argument.
	 */
	private boolean wasVarArg;
	private List<ILanguageComponent> argsBefore;
	private boolean hasScriptLineBefore;
	private boolean shallIgnore;

	/* -- 0.3 rendition stuff (I think) -- */
	/**
	 * Holds all the data that previously was stored in different variables.
	 *
	 * <p>First argument is the identifier, second is the value assigned.</p>
	 */
	private Map<String, Object> datas; // TODO

	private static final String USER_VARIABLE = "@USER@";

	private SksParser(ScriptFile file) {

		this.file = file;
		this.datas = Maps.newLinkedHashMap();
		this.listenersClasses = Lists.newArrayList();
		this.argsBefore = Lists.newArrayList();
	}

	static {

		listener(new JavaMainListener());
		listener(new SklMainListener());
		listener(new CMainListener());
	}

	static {

		component(new LanguageDeclaration());
		component(new ScriptDeclaration());
		component(new ListenerDeclaration());
		component(new MultiListenerDeclaration());
		component(new FallBackListenersDeclaration());
		// TODO subsequentlisteners
		// TODO? addonlisteners
		component(new NoListenerDeclaration());
		// TODO multiscript
		component(new ScriptStartDeclaration());
		component(new IfDeclaration());
		// TODO else
		component(new EndIfDeclaration());
		component(new ScriptEndDeclaration());
	}

	private static void component(@Nonnull ILanguageComponent component) {

		components.add(component);

		SksLogger.logger().info("Registered language component:");
		SksLogger.logger().info("    Name: " + component.getName());
		SksLogger.logger().info("    Declaration: " + component.getScriptDeclaration());
		SksLogger.logger().info("    Syntax: " + component.getSyntax());
	}

	/**
	 * Creates a new Parser for the specified script file.
	 *
	 * <p>If a parser for the file has already been created,
	 * that one is returned instead.</p>
	 *
	 * @param file
	 * 		The ScriptFile you need to create the parser for.
	 * @return
	 * 		A new SksParser.
	 */
	public static SksParser of(@Nonnull ScriptFile file) {

		Preconditions.checkNotNull(file, "ScriptFile must not be null");

		if (map.containsKey(file) && map.get(file) != null) {

			return map.get(file);
		}

		if (map.containsKey(file)) {

			map.remove(file);
		}

		final SksParser parser = new SksParser(file);
		map.put(file, parser);
		return parser;
	}

	/**
	 * Registers a listener for the specified language.
	 *
	 * @param listener
	 * 		The listener to register.
	 * @return
	 * 		If the registration was successful
	 */
	public static boolean listener(@Nonnull IScriptListener listener) {

		Preconditions.checkNotNull(listener, "Listener must not be null");

		final String listenerFor = listener.listenerFor();

		if (listenerFor.equalsIgnoreCase("sks")) {

			SksLogger.logger().warn("Invalid listener call");
			SksLogger.logger().warn("\"sks\" is not a valid language");

			return false;
		}

		if (listeners.containsKey(listenerFor)) {

			SksLogger.logger().warn("A listener for " + listenerFor
					      + " is already available.");
			SksLogger.logger().warn("Skipping registration...");

			return false;
		}

		if (listeners.get(listenerFor) != null
				          && listeners.get(listenerFor).equals(listener)) {

			SksLogger.logger().warn("Listener already registered.");
			return false;
		}

		listeners.put(listenerFor, listener);

		SksLogger.logger().info("Listener added for language " + listenerFor);

		return true;
	}

	/**
	 * Initializes the parser previously created.
	 *
	 * <p>Initializing means checking if the file is valid to be processed.</p>
	 *
	 * @param force
	 * 		Whether to allow non-sks-ending files to be parsed.
	 * 		    True is used to allow, false to avoid.
	 */
	public void initParser(boolean force) {

		if (this.init()) {

			throw new IllegalStateException("The parser has already been initialized");
		}

		SksLogger.logger().info("Initialising Parser");

		try {

			this.checkFile(force);

			this.fileReader = new BufferedReader(new FileReader(this.file));

		} catch (Throwable throwable) {

			SksLogger.logger().stacktrace("An error"
					      + "has occurred during initialization", throwable);
			this.hasErrored = true;
		}

		if (this.errored()) {

			return;
		}

		this.hasInit = true;
	}

	/**
	 * Gets if the parser has been initialized.
	 *
	 * @return
	 * 		If the parser has been initialized.
	 */
	public boolean init() {

		return this.hasInit;
	}

	/**
	 * Gets if the parser has errored.
	 *
	 * @return
	 * 		If the parser has errored.
	 */
	public boolean errored() {

		return this.hasErrored;
	}

	private boolean checkFile(boolean force) {

		if (this.file == null) {

			throw new IllegalStateException("File was null");
		}

		SksLogger.logger().info("Checking file");

		if (!this.file.getFileExtension().equalsIgnoreCase("sks")) {

			if (!force) {

				SksLogger.logger().error("File specified does not end with .sks");
				SksLogger.logger().error("Aborting process");

				throw new IllegalStateException("File must end with .sks to be "
						+ "able to be parsed");
			}

			SksLogger.logger().warn("File specified does not end with .sks");
			SksLogger.logger().warn("Forced to accept it...");

			return true;
		}

		return false;
	}

	/**
	 * Parses the file and automatically runs the script in it.
	 *
	 * @throws IllegalScriptException If the file is not syntactically correct.
	 */
	public void parse() {

		if (!this.init() || this.errored()) {

			throw new IllegalStateException();
		}

		List<String> lines = Lists.newArrayList();
		String string;

		try {

			while ((string = this.fileReader.readLine()) != null) {

				if (!this.parseString(string)) {

					lines.add(string);
				}
			}
		} catch (IOException e) {

			SksLogger.logger().stacktrace(e);
		}

		this.sendToListeners(lines);
	}

	private boolean parseString(String line) {

		if (components.isEmpty()
				      || System.getProperty("skl.sks.useLegacyParsing", "false")
				               .equals("true")) {

			SksLogger.logger().warn("Using legacy parsing");
			SksLogger.logger().warn("New scripts may not work with it");
			return this.parseHardcodedString(line);
		}

		final String format = String.format("<#%s>", EndIfDeclaration.DECLARATION);

		if (!line.equals(format) && this.shallIgnore) {

			return true;
		}

		if (!(line.startsWith("<#") && line.endsWith(">"))) {

			this.hasScriptLineBefore = true;
			return false;
		}

		final String rawLine = line.substring(2, line.length() - 1);
		final String[] parts = rawLine.split(Pattern.quote(" "));
		final String cmd = parts[0];
		final String[] args = new String[parts.length - 1];

		System.arraycopy(parts, 1, args, 0, parts.length - 1);

		SksLogger.logger().info("Found script command");
		SksLogger.logger().info("  Command: " + cmd);

		final ComponentArguments arguments = ComponentArguments.of();
		boolean flag = false;

		for (final ILanguageComponent component : components) {

			if (!this.canApply(component, cmd, args)) {

				continue;
			}

			final Location location = Location.from(this.hasScriptLineBefore,
								      this.argsBefore);

			if (!component.isLocationValid(location)) {

				try {

					component.throwInvalidLocation();
				} catch (IllegalScriptException e) {

					throw new IllegalScriptException(
							String
							.format("Invalid location for command %s!",
									cmd),
							e);
				}
			}

			this.tryParseString(component, line, arguments, args);

			this.performChanges(component);

			this.addListenerToListeners();

			flag = true;
			this.argsBefore.add(component);
			break;
		}

		if (flag) {

			return true;
		}

		throw new IllegalScriptException("Command not recognized");
	}

	private boolean canApply(final ILanguageComponent component,
							  final String command,
							  final String[] args) {

		if (!command.equals(component.getScriptDeclaration())) {

			return false;
		}

		final Optional<ComponentArguments> optionalArguments = component.getArguments();

		if (!optionalArguments.isPresent() && args.length != 0) {

			return false;
		}

		final ComponentArguments arguments = ComponentArguments.of(optionalArguments.get());

		String key = ComponentArguments.INIT;
		String value = "";
		final boolean flag = arguments.isVarArg(key);

		for (int i = 0; i < args.length; ++i) {

			if (flag) {

				value += args[i];

				if (i == args.length - 1) {

					arguments.pairValue(ComponentArguments.asVararg(key),
							            value);
				}

				continue;
			}

			if (i % 2 == 0) {

				value = args[i];
				arguments.pairValue(key, value);
			} else if (i % 2 == 1) {

				key = args[i];
			}
		}

		this.wasVarArg = flag;

		return component.canApply(arguments);
	}

	private void tryParseString(final ILanguageComponent component,
								 final String line,
								 final ComponentArguments arguments,
								 final String[] args) {

		String key = ComponentArguments.INIT;
		String value = "";
		final boolean flag = this.wasVarArg;
		this.wasVarArg = false;

		for (int i = 0; i < args.length; ++i) {

			if (flag) {

				value += args[i];

				if (i == args.length - 1) {

					arguments.addArgument(ComponentArguments.asVararg(key),
							            value);
				}

				continue;
			}

			if (i % 2 == 0) {

				value = args[i];
				arguments.addArgument(key, value);
			} else if (i % 2 == 1) {

				key = args[i];
			}
		}

		try {

			if (!component.parse(arguments)) {

				if (!component.parseFallback(line)) {

					throw new IllegalScriptException(
							"Unable to parse line " + line);
				}
			}
		} catch (ILanguageComponent.UnableToParseException e) {

			throw new IllegalScriptException(e.getLocalizedMessage(), e);
		}
	}

	private void performChanges(ILanguageComponent component) {

		if (component.hasErrored()) {

			component.throwError();
		}

		try {

			Optional<ComponentArguments> neededEditsOptional = component
					      .getNeededEdits();

			if (!neededEditsOptional.isPresent()) {

				return;
			}

			Set<Map.Entry<String, String>> edits = neededEditsOptional
					      .get()
					      .getArguments()
					      .entrySet();

			for (Map.Entry<String, String> edit : edits) {

				Field field = this.getClass().getDeclaredField(edit.getKey());

				if (field != null) {

					try {

						field.set(this, edit.getValue());
					} catch (IllegalArgumentException e) {

						field.set(this, Boolean.valueOf(edit.getValue()));
					}
				}
			}

		} catch (ReflectiveOperationException e) {

			SksLogger.logger().error("Unable to set all data values");
		}
	}

	private void addListenerToListeners() {

		if (this.listenerClass == null || this.listenerClass.isEmpty()) {

			return;
		}

		String[] listeners;

		if (this.listenerClass.contains(";")) {

			listeners = this.listenerClass.split(Pattern.quote(";"));
		} else {

			listeners = new String[] {this.listenerClass};
		}

		for (String listener : listeners) {

			if (listener.endsWith(";")) {

				listener = listener.substring(0, listener.length() - 1);
			} else if (listener.startsWith(";")) {

				listener = listener.substring(1);
			}

			this.listenersClasses.add(listener);
		}

		this.listenerClass = "";
	}

	private boolean parseHardcodedString(String line) {

		if (line.equals(FixedExpressions.SCRIPT_END.getFull())) {

			if (!this.hasReachedStart) {

				throw new IllegalScriptException();
			}

			this.hasReachedEndOfScript = true;

			SksLogger.logger().info("Found script end...");

			return true;
		}

		if (line.equals(FixedExpressions.SCRIPT_START.getFull())) {

			if (this.hasReachedEndOfScript || !this.doWeKnowLanguage) {

				throw new IllegalScriptException();
			}

			this.hasReachedStart = true;

			SksLogger.logger().info("Found script start...");

			return true;
		}

		if (this.hasReachedStart && !this.hasReachedEndOfScript) {

			SksLogger.logger().info("Script line. Parsing later...");

			return false;
		}

		if (line.startsWith(FixedExpressions.LANGUAGE.getBefore())
				      && line.endsWith(FixedExpressions.LANGUAGE.getAfter())) {

			if (this.doWeKnowLanguage) {

				throw new IllegalScriptException("Language is already specified");
			}

			this.doWeKnowLanguage = true;

			int start = FixedExpressions.LANGUAGE.getBefore().length();
			int end = FixedExpressions.LANGUAGE.getAfter().length();

			SksLogger.logger().info(line.substring(start, line.length() - end));

			this.actualLanguage = line.substring(start, line.length() - end);

			if (this.actualLanguage.equals("sks")) {

				throw new IllegalScriptException("SKS is not a valid language");
			}

			return true;
		}

		if (line.startsWith(FixedExpressions.FORCE_LISTENER.getBefore())
		                && line.endsWith(FixedExpressions.FORCE_LISTENER.getAfter())) {

			if (this.isListenerForced) {

				throw new IllegalScriptException(
						"Listener has already been forced");
			}

			this.isListenerForced = true;

			int start = FixedExpressions.FORCE_LISTENER.getBefore().length();
			int end = FixedExpressions.FORCE_LISTENER.getAfter().length();

			SksLogger.logger().info(line.substring(start, line.length() - end));

			this.listenerClass = line.substring(start, line.length() - end);

			return true;
		}

		if (line.startsWith(FixedExpressions.SCRIPT_DECLARATION.getBefore())
				        && line.endsWith(FixedExpressions.SCRIPT_DECLARATION
				        .getAfter())) {

			line = line.substring(FixedExpressions.SCRIPT_DECLARATION
					.getBefore().length());

			SksLogger.logger().info(line);

			int indexOfApix = line.lastIndexOf("\"");

			String lineFirstPart = line.substring(0, indexOfApix);
			String lineSecondPart = line.substring(lineFirstPart.length() + 1,
					          line.length() - 1);

			SksLogger.logger().info(lineFirstPart);

			this.scriptName = lineFirstPart;

			if (lineSecondPart.startsWith(" ")) {

				lineSecondPart = lineSecondPart.substring(1);
			}

			String declare = "declare ";

			lineSecondPart = lineSecondPart.substring(declare.length());

			if (lineSecondPart.endsWith(">")) {

				lineSecondPart = lineSecondPart.substring(0,
						lineSecondPart.length() - 1);
			}

			SksLogger.logger().info(lineSecondPart);

			String[] accessModifiers = new String[] {

				"public",
				"protected",
				"packet",
				"private",
				"script"
			};

			for (String modifier : accessModifiers) {

				if (lineSecondPart.equals(modifier)) {

					SksLogger.logger().info("Script modifier is " + modifier);

					if (modifier.equals(accessModifiers[0])) {

						break;
					}

					SksLogger.logger().severe("In SKL 0.1, the modifier \""
							      + modifier + "\" is not allowed as "
							      + "modifier of a script");

					SksLogger.logger().severe("Reroute to \"public\"");

					throw new IllegalScriptException();
				}
			}

			return true;
		}

		throw new IllegalScriptException();
	}

	private void sendToListeners(List<String> lines) {

		SksLogger.logger().info("Sending script to listeners...");
		int ind = 1;

		if (listenersClasses.isEmpty()) {

			SksLogger.logger().info(
					      String.format("Sending script to listener #%d...",
							         ind));
			this.sendToListener(lines);
		}

		for (String listenerClass : listenersClasses) {

			SksLogger.logger().info(
					      String.format("Sending script to listener #%d...",
							         ind));
			this.listenerClass = listenerClass;
			this.sendToListener(lines);
			this.listenerClass = "";
			++ind;
		}
	}

	private void sendToListener(List<String> lines) {

		if (this.listenerClass != null && !this.listenerClass.isEmpty()) {

			try {

				Class<?> listener = Class.forName(this.listenerClass);

				if (!IScriptListener.class.isAssignableFrom(listener)) {

					throw new IllegalScriptException("Invalid listener");
				}

				Object classInstance = listener.newInstance();

				if (!(classInstance instanceof IScriptListener)) {

					throw new IllegalScriptException("Invalid listener");
				}

				IScriptListener realListener = (IScriptListener) classInstance;
				this.sendToListener(realListener, lines);

			} catch (ClassNotFoundException e) {

				if (FallBackListenersDeclaration.wasFallBack(this.listenerClass)) {

					SksLogger.logger().info("Attempting to send script to "
							       + this.listenerClass);
					SksLogger.logger().info("Failed: class not found");
					SksLogger.logger().info("Since it is a fallback listener, "
							       + "skipping to the next");

					return;
				}

				try {

					throw new IllegalStateException("Listener class "
							+ "not found", e);
				} catch (IllegalStateException exc) {

					throw new IllegalScriptException("Invalid script", exc);
				}
			} catch (InstantiationException | IllegalAccessException e) {

				if (FallBackListenersDeclaration.wasFallBack(this.listenerClass)) {

					SksLogger.logger().info("Attempting to send script to "
							       + this.listenerClass);
					SksLogger.logger().info("Failed: cannot initialize class");
					SksLogger.logger().info("Since it is a fallback listener, "
							       + "skipping to the next");

					return;
				}


				try {

					throw new IllegalStateException("Unable to "
							+ "instantiate listener",
							e);
				} catch (IllegalStateException exc) {

					throw new IllegalScriptException("Invalid script", exc);
				}
			}

			return;
		}

		this.sendToListener(listeners.get(this.actualLanguage), lines);
	}

	private void sendToListener(IScriptListener listener, List<String> lines) {

		Preconditions.checkNotNull(listener, "No listener specified for language "
				                + this.actualLanguage);

		SksLogger.logger().info(String.format("Sending to listener %s...",
				      listener.toString()));

		if (!listener.listenerFor().equalsIgnoreCase(this.actualLanguage)) {

			throw new IllegalScriptException("The specified listener is not valid");
		}

		if (listener.needsInit() && !listener.hasAlreadyInit()) {

			SksLogger.logger().info("Initialising listener...");
			listener.init(this, this.file);
		}

		SksLogger.logger().info("Sending...");

		listener.runScript(lines);

		SksLogger.logger().info("Checking listener result...");

		Result result = listener.result();

		if (!result.equals(Result.SUCCESSFUL)) {

			SksLogger.logger().warn("An error has occurred while processing "
					        + "the script");

			if (result.equals(Result.ERRORED)) {

				SksLogger.logger().warn("The process will be terminated");
			} else if (result.equals(Result.WARNING)) {

				SksLogger.logger().warn("Retry to send the script.");
			}
		} else {

			SksLogger.logger().info("Execution went correctly");
		}

		SksLogger.logger().info("Logging listener info...");
		SksLogger.logger().info("#### LISTENER LOG ####");

		Optional<List<String>> toLog = listener.toLog();

		if (toLog.isPresent()) {

			List<String> msgList = toLog.get();

			for (String msg : msgList) {

				if (msg.startsWith("[WARN]")) {

					SksLogger.logger().warn(msg.substring(5));
				} else if (msg.startsWith("[SEVERE]")) {

					SksLogger.logger().severe(msg.substring(7));
				} else if (msg.startsWith("[ERR]")) {

					SksLogger.logger().severe(msg.substring(4));
				} else if (msg.startsWith("[THR]")) {

					SksLogger.logger().stacktrace(msg,
							         new Exception("Stack trace"));
				} else if (msg.startsWith("[FINE]")) {

					SksLogger.logger().info(msg.substring(5));
				} else {

					SksLogger.logger().info(msg);
				}
			}
		} else {

			SksLogger.logger().info("Nothing to log");
		}

		SksLogger.logger().info("######################");
	}

	/**
	 * Gets the name of the script.
	 *
	 * @return
	 * 		The script's name
	 */
	public String getScriptName() {

		return this.scriptName;
	}

	/**
	 * Test method.
	 *
	 * @param args
	 * 		Args
	 */
	public static void main(String[] args) {

		SksParser parser = SksParser.of(ScriptFile.of(new File("user.dir", "Test.sks")));
		parser.parseString("<#language \"java\">");
		parser.parseString("<#listener "
				      + "\"net.thesilkminer.skl.interpreter.sks."
				      + "listeners.java.JavaMainListener\">");
		parser.parseString("<#fallbacklisteners "
				      + "\"net.thesilkminer.skl.interpreter.sks."
				      + "listeners.java.JavaMainListener\" "
				      + "\"net.thesilkminer.skl.interpreter.sks."
				      + "listeners.ccd.ADS\">");
		parser.parseString("<#listeners "
				      + "\"net.thesilkminer.skl.interpreter.sks."
				      + "listeners.java.JavaMainListener\" "
				      + "\"net.thesilkminer.skl.interpreter.sks."
				      + "listeners.ccd.ADSA\">");
		parser.parseString("<#nolistener>");
		parser.parseString("<#ifdef \"debugTest=true\" type javaProperty>");
		parser.parseString("<#script \"Test\" visibility private>");
		parser.parseString("<#endif>");
		//parser.parseString("<#listener \"com.example.MyListener\">");
		//parser.parseString("<#language \"skl\">");
		//parser.parseString("package aloha;");
		//parser.parseString("<#script \"Test\" declare public>");
		parser.parseString("<#script \"Test\" visibility public>");
		//parser.parseString("<#script \"Test\" visibility private>");
		//parser.parseString("<#script \"JustBecauseICan\" declare private>");
		parser.parseString("<#start script>");
		parser.parseString("package aloha;");
		parser.parseString("<#end script>");
		//parser.parseString("package aloha;");

		List<String> list = Lists.newArrayList();
		list.add("import javax.swing.*;");
		list.add("public class Test {");
		list.add("	public static void main (String[] args) {");
		list.add("		System.out.println(\"I am a pro\");");
		list.add("		System.out.println(\"You can come to me every time\");");
		list.add("		int a = 3;");
		list.add("		int b = 5;");
		list.add("		int c = a + b;");
		list.add("		System.out.println(c);");
		list.add("		try {");
		list.add("			Thread.sleep(10000);");
		list.add("		} catch (InterruptedException ignore) {}");
		list.add("		JOptionPane.showMessageDialog(null,"
				      + "\"You're computer has been hacked!\", \"WARNING!\","
				      + "JOptionPane.WARNING_MESSAGE);");
		list.add("      System.out.println(String.format("
				      + "\"# generated by %s on %d.%d.%d\", \"a\", 1, 2, 3));");
		list.add("	}");
		list.add("}");

		parser.sendToListeners(list);
	}
}
