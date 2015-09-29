package net.thesilkminer.skl.interpreter.sks;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.thesilkminer.skl.interpreter.sks.listeners.java.JavaMainListener;
import net.thesilkminer.skl.interpreter.sks.listeners.skl.SklMainListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 * This class is the main SKS parser.
 *
 * <p>This class's scope is parse a specified file and run
 * every script which is found in them.
 */
public class SksParser {

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

	private static final String USER_VARIABLE = "@USER@";

	private SksParser(ScriptFile file) {

		this.file = file;
	}

	static {

		listener(new JavaMainListener());
		listener(new SklMainListener());
	}

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

	public static boolean listener(@Nonnull IScriptListener listener) {

		Preconditions.checkNotNull(listener, "Listener must not be null");

		final String listenerFor = listener.listenerFor();

		if (listenerFor.equals("sks")) {

			SksLogger.logger().warn("Invalid listener call");
			SksLogger.logger().warn("\"SKS\" is not a valid language");

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

		return true;
	}

	public void initParser(boolean force) {

		if (this.init()) {

			throw new IllegalStateException("The parser has already been initialized");
		}

		SksLogger.logger().fine("Initialising Parser");

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

	public boolean init() {

		return this.hasInit;
	}

	public boolean errored() {

		return this.hasErrored;
	}

	private boolean checkFile(boolean force) {

		if (this.file == null) {

			throw new IllegalStateException("File was null");
		}

		SksLogger.logger().fine("Checking file");

		if (!this.file.getFileExtension().equalsIgnoreCase("sks")) {

			if (!force) {

				SksLogger.logger().error("File specified does not end with .sks");
				SksLogger.logger().error("Aborting process");

				throw new IllegalStateException();
			}

			SksLogger.logger().warn("File specified does not end with .sks");
			SksLogger.logger().warn("Forced to accept it...");

			return true;
		}

		return false;
	}

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

		this.sendToListener(lines);
	}

	private boolean parseString(String line) {

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

					SksLogger.logger().severe("In SKL 1.0, the modifier \""
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

				throw new IllegalStateException("Listener class not found");
			} catch (InstantiationException | IllegalAccessException e) {

				throw new IllegalStateException("Unable to instantiate listener");
			}

			return;
		}

		this.sendToListener(listeners.get(this.actualLanguage), lines);
	}

	private void sendToListener(IScriptListener listener, List<String> lines) {

		Preconditions.checkNotNull(listener, "No listener specified for language "
				                + this.actualLanguage);

		SksLogger.logger().info("Sending to listener...");

		if (!listener.listenerFor().equalsIgnoreCase(this.actualLanguage)) {

			throw new RuntimeException("The specified listener is not valid");
		}

		if (listener.needsInit() && !listener.hasAlreadyInit()) {

			SksLogger.logger().fine("Initialising listener...");
			listener.init(this, this.file);
		}

		listener.runScript(lines);

		Result result = listener.result();

		if (!result.equals(Result.SUCCESSFUL)) {

			SksLogger.logger().warn("An error has occurred while processing "
					        + "the script");

			if (result.equals(Result.ERRORED)) {

				SksLogger.logger().warn("The process will be terminated");
			} else if (result.equals(Result.WARNING)) {

				SksLogger.logger().warn("Retry to send the script.");
			}
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

					SksLogger.logger().fine(msg.substring(5));
				} else {

					SksLogger.logger().info(msg);
				}
			}
		} else {

			SksLogger.logger().info("Nothing to log");
		}

		SksLogger.logger().info("######################");
	}

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
		//parser.parseString("<#listener \"com.example.MyListener\">");
		//parser.parseString("<#language \"skl\">");
		//parser.parseString("package aloha;");
		parser.parseString("<#script \"Test\" declare public>");
		//parser.parseString("<#script \"JustBecauseICan\" declare private>");
		parser.parseString("<#start \"script\">");
		parser.parseString("package aloha;");
		parser.parseString("<#end script>");
		//parser.parseString("package aloha;");

		List<String> list = Lists.newArrayList();
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
		list.add("	}");
		list.add("}");

		parser.sendToListener(list);
	}
}
