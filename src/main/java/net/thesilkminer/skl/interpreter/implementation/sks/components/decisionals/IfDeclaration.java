package net.thesilkminer.skl.interpreter.implementation.sks.components.decisionals;

import net.thesilkminer.skl.interpreter.api.sks.language.ComponentArguments;
import net.thesilkminer.skl.interpreter.api.sks.language.IllegalScriptException;
import net.thesilkminer.skl.interpreter.api.sks.language.Location;
import net.thesilkminer.skl.interpreter.api.sks.language.components.ILanguageComponent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;

/**
 * Represents the "ifdef" keyword of the script.
 *
 * <p>This can be used to make decisions and remove an entire part
 * of the script if some conditions are not met.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class IfDeclaration implements ILanguageComponent {

	private enum ConditionType {

		JAVA_PROPERTY("javaProperty"),
		SPECIFIED_CONDITION("boolean"),
		METHOD_CALL("methodCall"),
		JAVA_FIELD("field");

		private final String type;

		ConditionType(final String type) {

			this.type = type;
		}

		public String getType() {

			return this.type;
		}

		public static ConditionType from(@Nonnull final String from) {

			for (final ConditionType type : ConditionType.values()) {

				if (type.getType().equals(from)) {

					return type;
				}
			}

			return null;
		}
	}

	private static final String TYPE = "type";

	private String test;
	private ConditionType type;
	private boolean error;

	@Override
	public String getName() {

		return "if";
	}

	@Override
	public Optional<ComponentArguments> getArguments() {

		final ComponentArguments args = ComponentArguments.of();
		args.addArgument(ComponentArguments.INIT, null);
		args.addArgument(TYPE, null);
		args.setImmutable();

		return Optional.of(args);
	}

	@Override
	public String getSyntax() {

		return "ifdef <condition> type <conditionType>";
	}

	@Override
	public boolean canApply(final ComponentArguments arguments) {

		return arguments.hasOnly(ComponentArguments.INIT, TYPE);
	}

	@Override
	public boolean isLocationValid(final Location location) {

		return true;
	}

	@Override
	public boolean parse(final ComponentArguments arguments) {

		if (!this.canApply(arguments)) {

			throw new ILanguageComponent.WrongSyntaxException(this);
		}

		this.test = ComponentArguments.removeApixes(
				arguments.get(ComponentArguments.INIT));

		this.type = ConditionType.from(arguments.get(TYPE));

		return this.test != null
				&& !this.test.isEmpty()
				&& this.type != null;
	}

	@Override
	public boolean parseFallback(final String line) {

		final int firstApix = line.indexOf('"');
		final int lastApix = line.lastIndexOf('"');

		if (firstApix == lastApix && firstApix == -1) {

			return false;
		}

		this.test = ComponentArguments.removeApixes(
				line.substring(firstApix, lastApix));

		final String[] parts = line.split(Pattern.quote(" "));

		if (parts.length != 4 || !parts[2].equals(TYPE)) {

			this.error = true;

			return false;
		}

		this.type = ConditionType.from(parts[3]);

		return this.test != null
				&& !this.test.isEmpty()
				&& this.type != null;
	}

	@Override
	public boolean hasErrored() {

		return this.error;
	}

	@Override
	public Optional<ComponentArguments> getNeededEdits() {

		final boolean result = this.test();

		final ComponentArguments edits = ComponentArguments.of();
		edits.addArgument("shallIgnore", Boolean.toString(!result));
		edits.setImmutable();

		return Optional.of(edits);
	}

	@Override
	public String getErrorMessage() {

		return "Check your if statement";
	}

	@Override
	public String getInvalidLocationMessage() {

		return "Location is always valid! WTF HAPPENED?!?!?!";
	}

	@Override
	public String getScriptDeclaration() {

		return "ifdef";
	}

	private boolean test() {

		switch (this.type) {

			case JAVA_PROPERTY:
				return this.testJavaProperty();
			case SPECIFIED_CONDITION:
				return this.testSpecifiedCondition();
			case METHOD_CALL:
				return this.testMethodCall();
			case JAVA_FIELD:
				return this.testJavaField();
			default:
				return false;
		}
	}

	private boolean testJavaProperty() {

		final String[] array = this.test.split(Pattern.quote("="));

		if (array.length != 2) {

			throw new IllegalScriptException("When using javaProperty, "
					+ "condition must be in the form \"property=value\"");
		}

		if (array[0].endsWith("=")) {

			array[0] = array[0].substring(0, array[0].length() - 1);
		}

		if (array[1].startsWith("=")) {

			array[1] = array[1].substring(1);
		}

		try {

			return System.getProperty(array[0]).equals(array[1]);
		} catch (NullPointerException ex) {

			return false;
		}
	}

	@SuppressWarnings("SameReturnValue") //TODO
	private boolean testSpecifiedCondition() {

		net.thesilkminer.skl.interpreter.implementation.sks.SksLogger
				      .logger().warn("Boolean property "
				      + "is not yet implemented");
		return true;
	}

	private boolean testMethodCall() {

		final int hashTag = this.test.lastIndexOf('#');

		if (hashTag == -1) {

			throw new IllegalScriptException("Method call test condition must be in "
					+ "the form \"path/to/compiled/class#method()\"");
		}

		String clazz = this.test.substring(0, hashTag);

		if (clazz.endsWith("#")) {

			clazz = clazz.substring(0, clazz.length() - 1);
		}

		String method = this.test.substring(hashTag);

		if (method.startsWith("#")) {

			method = method.substring(1);
		}

		if (!method.endsWith("()")) {

			throw new IllegalScriptException("Method call must be without arguments");
		}

		method = method.substring(0, method.length() - 2);

		clazz = clazz.replace('/', '.');

		try {

			final Class<?> cl = Class.forName(clazz);
			final Method met = cl.getDeclaredMethod(method);
			final Object result = met.invoke(cl.newInstance());
			final Boolean bool = (Boolean) result;

			if (bool == null) {

				throw new IllegalStateException("Boolean result was null!");
			}

			return bool;
		} catch (ClassNotFoundException ex) {

			throw new IllegalScriptException("Specified class is invalid.", ex);
		} catch (NoSuchMethodException ex) {

			throw new IllegalScriptException("Invalid method specified.", ex);
		} catch (InstantiationException | IllegalAccessException ex) {

			throw new IllegalScriptException("Unable to access method.", ex);
		} catch (InvocationTargetException ex) {

			throw new IllegalScriptException("Method has thrown an exception", ex);
		} catch (ClassCastException ex) {

			throw new IllegalScriptException("Method should return a boolean value",
					                         ex);
		} catch (IllegalStateException ex) {

			throw new IllegalScriptException(ex.getMessage(), ex);
		}
	}

	private boolean testJavaField() {

		final int hashTag = this.test.lastIndexOf('#');

		if (hashTag == -1) {

			throw new IllegalScriptException("Field test condition must be in "
					+ "the form \"path/to/compiled/class#field\"");
		}

		String clazz = this.test.substring(0, hashTag);

		if (clazz.endsWith("#")) {

			clazz = clazz.substring(0, clazz.length() - 1);
		}

		String field = this.test.substring(hashTag);

		if (field.startsWith("#")) {

			field = field.substring(1);
		}

		clazz = clazz.replace('/', '.');

		try {

			final Class<?> cl = Class.forName(clazz);
			final Field fld = cl.getDeclaredField(field);
			final Object value = fld.get(cl.newInstance());
			final Boolean bool = (Boolean) value;

			if (bool == null) {

				throw new IllegalStateException("Boolean result was null!");
			}

			return bool;
		} catch (ClassNotFoundException ex) {

			throw new IllegalScriptException("Specified class is invalid.", ex);
		} catch (NoSuchFieldException ex) {

			throw new IllegalScriptException("Invalid field specified.", ex);
		} catch (InstantiationException | IllegalAccessException ex) {

			throw new IllegalScriptException("Unable to access field.", ex);
		} catch (ClassCastException ex) {

			throw new IllegalScriptException("Field should be a boolean value", ex);
		} catch (IllegalStateException ex) {

			throw new IllegalScriptException(ex.getMessage(), ex);
		}
	}
}
