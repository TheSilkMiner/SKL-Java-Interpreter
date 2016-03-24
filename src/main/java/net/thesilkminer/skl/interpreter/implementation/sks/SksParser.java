package net.thesilkminer.skl.interpreter.implementation.sks;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.thesilkminer.skl.interpreter.api.sks.holder.IScriptHolder;
import net.thesilkminer.skl.interpreter.api.sks.language.ComponentArguments;
import net.thesilkminer.skl.interpreter.api.sks.language.IllegalScriptException;
import net.thesilkminer.skl.interpreter.api.sks.language.Location;
import net.thesilkminer.skl.interpreter.api.sks.language.components.ILanguageComponent;
import net.thesilkminer.skl.interpreter.api.sks.listener.IScriptListener;
import net.thesilkminer.skl.interpreter.api.sks.listener.ISubsequentListener;
import net.thesilkminer.skl.interpreter.api.sks.listener.Result;
import net.thesilkminer.skl.interpreter.api.sks.parser.ISksParser;
import net.thesilkminer.skl.interpreter.implementation.sks.components.decisionals.EndIfDeclaration;
import net.thesilkminer.skl.interpreter.implementation.sks.components.decisionals.IfDeclaration;
import net.thesilkminer.skl.interpreter.implementation.sks.components.declaration.ScriptDeclaration;
import net.thesilkminer.skl.interpreter.implementation.sks.components.language.LanguageDeclaration;
import net.thesilkminer.skl.interpreter.implementation.sks.components.listeners.FallBackListenersDeclaration;
import net.thesilkminer.skl.interpreter.implementation.sks.components.listeners.ListenerDeclaration;
import net.thesilkminer.skl.interpreter.implementation.sks.components.listeners.MultiListenerDeclaration;
import net.thesilkminer.skl.interpreter.implementation.sks.components.listeners.NoListenerDeclaration;
import net.thesilkminer.skl.interpreter.implementation.sks.components.listeners.SubSequentListenersDeclaration;
import net.thesilkminer.skl.interpreter.implementation.sks.components.markers.ScriptEndDeclaration;
import net.thesilkminer.skl.interpreter.implementation.sks.components.markers.ScriptStartDeclaration;
import net.thesilkminer.skl.interpreter.implementation.sks.listeners.c.CMainListener;
import net.thesilkminer.skl.interpreter.implementation.sks.listeners.custom.bw.BlacklistWhitelistListener;
import net.thesilkminer.skl.interpreter.implementation.sks.listeners.custom.register.listeners.ListenerRegisterListener;
import net.thesilkminer.skl.interpreter.implementation.sks.listeners.java.JavaMainListener;
import net.thesilkminer.skl.interpreter.implementation.sks.listeners.skl.SklMainListener;

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
 * every script which is found in them.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public final class SksParser implements ISksParser {

	private enum FixedExpressions {

		LANGUAGE("<#language \"", "\">", "<#language \"" + USER_VARIABLE + "\""),
		FORCE_LISTENER("<#listener \"", "\">", "<#listener \"" + USER_VARIABLE + "\""),
		SCRIPT_DECLARATION("<#script \"", ">", "<#script \""
				     + USER_VARIABLE + "\" declare " + USER_VARIABLE + ">"),
		SCRIPT_START("<#start \"script\">", null, "<#start \"script\">"),
		SCRIPT_END("<#end script>", null, "<#end script>"),
		;

		private final String before;
		private final String after;
		private final String full;

		FixedExpressions(final String before, final String after, final String full) {

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

	private static final Map<IScriptHolder, SksParser> MAP = Maps.newHashMap();
	private static final Map<String, IScriptListener> LISTENERS = Maps.newHashMap();
	private static final List<ILanguageComponent> COMPONENTS = Lists.newArrayList();
	private boolean hasInit;
	private boolean hasErrored;
	private final IScriptHolder file;
	private BufferedReader fileReader;

	/* -- Internal stuff -- */
	private boolean hasReachedStart;
	private boolean hasReachedEndOfScript;
	private boolean doWeKnowLanguage;
	private boolean isListenerForced;
	private String actualLanguage;
	private String listenerClass;
	private String scriptName;
	private final List<String> listenersClasses;

	/* -- 0.2 rendition stuff -- */
	/**
	 * Simply needed to pass VarArg argument.
	 */
	private boolean wasVarArg;
	private final List<ILanguageComponent> argsBefore;
	private boolean hasScriptLineBefore;
	@SuppressWarnings("CanBeFinal") private boolean shallIgnore; //Edited with reflection
	private ISubsequentListener listenerTmp;
	private final List<ISubsequentListener> previousSsListener;

	/* -- 0.3 rendition stuff (I think) -- */
	/**
	 * Holds all the data that previously was stored in different variables.
	 *
	 * <p>First argument is the identifier, second is the value assigned.</p>
	 */
	@SuppressWarnings({"FieldCanBeLocal", "unused"}) // For 0.3 (I guess)
	private final Map<String, Object> datas; // TODO

	private static final String USER_VARIABLE = "@USER@";

	private SksParser(final IScriptHolder file) {

		this.file = file;
		this.datas = Maps.newLinkedHashMap();
		this.listenersClasses = Lists.newArrayList();
		this.argsBefore = Lists.newArrayList();
		this.shallIgnore = false;
		this.previousSsListener = Lists.newArrayList();
	}

	static {

		listener(new JavaMainListener());
		listener(new SklMainListener());
		listener(new CMainListener());
		listener(new BlacklistWhitelistListener());
		listener(new ListenerRegisterListener());
	}

	static {

		component(new LanguageDeclaration());
		component(new ScriptDeclaration());
		component(new ListenerDeclaration());
		component(new MultiListenerDeclaration());
		component(new FallBackListenersDeclaration());
		component(new SubSequentListenersDeclaration());
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

		COMPONENTS.add(component);

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
	 * 		The IScriptHolder you need to create the parser for.
	 * @return
	 * 		A new SksParser.
	 */
	public static SksParser of(@Nonnull IScriptHolder file) {

		Preconditions.checkNotNull(file, "IScriptHolder must not be null");

		if (MAP.containsKey(file) && MAP.get(file) != null) {

			return MAP.get(file);
		}

		if (MAP.containsKey(file)) {

			MAP.remove(file);
		}

		final SksParser parser = new SksParser(file);
		MAP.put(file, parser);
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
	@SuppressWarnings({"UnusedReturnValue", "WeakerAccess"}) //API Method
	public static boolean listener(@Nonnull IScriptListener listener) {

		Preconditions.checkNotNull(listener, "Listener must not be null");

		final String listenerFor = listener.listenerFor();

		if (listenerFor.equalsIgnoreCase("sks")) {

			SksLogger.logger().warn("Invalid listener call");
			SksLogger.logger().warn("\"sks\" is not a valid language");

			return false;
		}

		if (LISTENERS.containsKey(listenerFor)) {

			SksLogger.logger().warn("A listener for " + listenerFor
					      + " is already available.");
			SksLogger.logger().warn("Skipping registration...");

			return false;
		}

		if (LISTENERS.get(listenerFor) != null
				          && LISTENERS.get(listenerFor).equals(listener)) {

			SksLogger.logger().warn("Listener already registered.");
			return false;
		}

		LISTENERS.put(listenerFor, listener);

		SksLogger.logger().info("Listener added for language " + listenerFor);

		return true;
	}

	@Override
	public void initParser(boolean force) {

		if (this.init()) {

			throw new IllegalStateException("The parser has already been initialized");
		}

		SksLogger.logger().info("Initialising Parser");

		if (!(this.file instanceof ScriptFile)) {

			SksLogger.logger().error("Currently only ScriptFile is"
					      + "supported as a script holder");
		}

		@SuppressWarnings("ConstantConditions")
		// Already checked. See above
		ScriptFile scriptFile = (ScriptFile) this.file;

		try {

			this.checkFile(force);

			this.fileReader = new BufferedReader(new FileReader(scriptFile));

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

	@Override
	public boolean init() {

		return this.hasInit;
	}

	@Override
	public boolean errored() {

		return this.hasErrored;
	}

	//private boolean checkFile(boolean force) {
	private void checkFile(final boolean force) {

		if (this.file == null) {

			throw new IllegalStateException("File was null");
		}

		SksLogger.logger().info("Checking file");

		if (!((ScriptFile) this.file).getFileExtension().equalsIgnoreCase("sks")) {

			if (!force) {

				SksLogger.logger().error("File specified does not end with .sks");
				SksLogger.logger().error("Aborting process");

				throw new IllegalStateException("File must end with .sks to be "
						+ "able to be parsed");
			}

			SksLogger.logger().warn("File specified does not end with .sks");
			SksLogger.logger().warn("Forced to accept it...");

			//return true;
		}

		//return false;
	}

	@Override
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

		if (COMPONENTS.isEmpty()
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

		for (final ILanguageComponent component : COMPONENTS) {

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
			final boolean ss = this.listenerClass.startsWith("SS");

			if (ss) {

				this.handleSubsequentListenersBefore();
			}

			this.sendToListener(lines);

			if (ss) {

				this.handleSubsequentListenersAfter();
			}

			this.listenerClass = "";
			++ind;
		}
	}

	private void handleSubsequentListenersBefore() {

		ISubsequentListener[] prev = new ISubsequentListener[
				this.previousSsListener.size()];
		prev = this.previousSsListener.toArray(prev);

		int beginIndex = 0;

		for (int i = 0; i < this.listenerClass.length(); ++i) {

			if (this.listenerClass.charAt(i) == '#') {

				beginIndex = i;
			}
		}

		this.listenerClass = this.listenerClass.substring(beginIndex);

		if (this.listenerClass.startsWith("#")) {

			this.listenerClass = this.listenerClass.substring(1);
		}

		ISubsequentListener listener;

		try {

			Class<?> listenerClazz = Class.forName(this.listenerClass);

			if (!ISubsequentListener.class.isAssignableFrom(listenerClazz)) {

				throw new IllegalScriptException("Invalid listener");
			}

			Object classInstance = listenerClazz.newInstance();

			if (!(classInstance instanceof ISubsequentListener)) {

				throw new IllegalScriptException("Invalid listener");
			}

			listener = (ISubsequentListener) classInstance;

		} catch (ClassNotFoundException e) {

			try {

				throw new IllegalStateException("Listener class "
						+ "not found", e);
			} catch (IllegalStateException exc) {

				throw new IllegalScriptException("Invalid script", exc);
			}
		} catch (InstantiationException | IllegalAccessException e) {

			try {

				throw new IllegalStateException("Unable to "
						+ "instantiate listener",
						e);
			} catch (IllegalStateException exc) {

				throw new IllegalScriptException("Invalid script", exc);
			}
		}

		IScriptListener[] prevSl = new IScriptListener[prev.length];

		for (int i = 0; i < prev.length; ++i) {

			prevSl[i] = prev[i].getListenerForSuccessor(listener.getListener());
		}

		if (!listener.canApply(prevSl)) {

			throw new IllegalScriptException("Specified chain of listener "
					+ "can't be applied.");
		}

		listener.obtainPreviousListenersInformation(prevSl);
	}

	private void handleSubsequentListenersWhile(final IScriptListener listener) {

		if (!(listener instanceof ISubsequentListener)) {

			return;
		}

		this.listenerTmp = (ISubsequentListener) listener;
	}

	private void handleSubsequentListenersAfter() {

		this.previousSsListener.add(this.listenerTmp);
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

				this.handleSubsequentListenersWhile(realListener);

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

		this.sendToListener(LISTENERS.get(this.actualLanguage), lines);
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

	@Override
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
