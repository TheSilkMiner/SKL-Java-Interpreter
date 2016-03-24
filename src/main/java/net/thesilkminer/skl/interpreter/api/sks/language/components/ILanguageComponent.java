package net.thesilkminer.skl.interpreter.api.sks.language.components;

import net.thesilkminer.skl.interpreter.api.sks.language.ComponentArguments;
import net.thesilkminer.skl.interpreter.api.sks.language.IllegalScriptException;
import net.thesilkminer.skl.interpreter.api.sks.language.Location;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a component of the SKS language.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public interface ILanguageComponent {

	/**
	 * Gets the name of the language component.
	 *
	 * @return
	 * 		The component's name.
	 *
	 * @since 0.2
	 */
	String getName();

	/**
	 * Gets the string to add to the script to declare this component.
	 *
	 * @return
	 * 		The string to add to the script to declare this component.
	 *
	 * @since 0.2
	 */
	default String getScriptDeclaration() {

		return this.getName();
	}

	/**
	 * Gets the arguments for this parser.
	 *
	 * @return
	 * 		This component's arguments.
	 *
	 * @since 0.2
	 */
	Optional<ComponentArguments> getArguments();

	/**
	 * Gets the component syntax.
	 *
	 * <p>Arguments that should be provided should be marked with their key.</p>
	 *
	 * @return
	 * 		The component's syntax.
	 *
	 * @since 0.2
	 */
	String getSyntax();

	/**
	 * Returns if it the passed in arguments can be applied to this component.
	 *
	 * @param arguments
	 * 		The arguments which need to be checked.
	 * @return
	 * 		If the arguments are applicable to this language component.
	 *
	 * @since 0.2
	 */
	boolean canApply(final ComponentArguments arguments);

	/**
	 * Returns if the specified location is valid for the script file.
	 *
	 * @param location
	 * 		The piece of code's location.
	 * @return
	 * 		If the specified location is valid for the script file.
	 *
	 * @since 0.2
	 */
	boolean isLocationValid(final Location location);

	/**
	 * Parses the specified arguments.
	 *
	 * <p>This is usually called by the parser and all the arguments of the
	 * line are wrapped by the arguments parameter.</p>
	 *
	 * <p>It is always recommended to check if the arguments are correct,
	 * because they are not automatically checked with the syntax.</p>
	 *
	 * @param arguments
	 * 		The arguments present in the script file.
	 * @return
	 * 		If the parse was successful or not.
	 *
	 * @since 0.2
	 */
	boolean parse(final ComponentArguments arguments);

	/**
	 * Parses the specified line.
	 *
	 * <p>This method will only be called after {@link #parse(ComponentArguments)}
	 * if the parsing for it fails.</p>
	 *
	 * <p>With this you are given full accessibility to the script string, so
	 * you can check every part of it without being affected by
	 * {@link ComponentArguments}.</p>
	 *
	 * @param line
	 *		The script line.
	 * @return
	 * 		If the parse was successful.
	 *
	 * @since 0.2
	 */
	boolean parseFallback(final String line);

	/**
	 * Gets if the parser has thrown an error.
	 *
	 * @return
	 * 		If the parser has thrown an error.
	 *
	 * @since 0.2
	 */
	boolean hasErrored();

	/**
	 * Gets the needed edits that have to be performed in the parser class.
	 *
	 * <p>The return type is a wrapped component argument because it is
	 * easier to pass all the new values. Remember, though, that the
	 * field mappings follows the format "key = field name; value = new value.</p>
	 *
	 * @return
	 * 		The edits that have to be performed.
	 *
	 * @since 0.2
	 */
	Optional<ComponentArguments> getNeededEdits();

	/**
	 * Gets the error message in case the parser fails.
	 *
	 * @return
	 * 		The error message in case the parser fails.
	 *
	 * @since 0.2
	 */
	String getErrorMessage();

	/**
	 * Gets the error message in case the location is wrong.
	 *
	 * @return
	 * 		The error message in case the location is wrong.
	 *
	 * @since 0.2
	 */
	String getInvalidLocationMessage();

	/**
	 * Throws a runtime exception.
	 *
	 * @since 0.2
	 */
	default void throwError() {

		throw new UnableToParseException(this.getErrorMessage());
	}

	/**
	 * Throws an {@link InvalidLocationException}.
	 *
	 * @since 0.2
	 */
	default void throwInvalidLocation() {

		throw new InvalidLocationException(this.getInvalidLocationMessage());
	}

	/**
	 * Exception which marks a wrong syntax for the specified language component.
	 *
	 * @since 0.2
	 */
	class WrongSyntaxException extends UnableToParseException {

		/**
		 * Constructs a new instance.
		 *
		 * @param component
		 * 		The component which errored. Can be {@code this}.
		 *
		 * @since 0.2
		 */
		public WrongSyntaxException(@Nonnull ILanguageComponent component) {

			super(String.format(
					"Wrong syntax used!\nExpected: \"%s\"",
					component.getSyntax()));
		}
	}

	class InvalidLocationException extends UnableToParseException {

		/**
		 * Constructs a new instance.
		 *
		 * @param message
		 * 		The message to show.
		 *
		 * @since 0.2
		 */
		public InvalidLocationException(@Nonnull final String message) {

			super(message);
		}
	}

	/**
	 * Exception thrown when a parsing fails.
	 *
	 * @since 0.2
	 */
	class UnableToParseException extends IllegalScriptException {

		/**
		 * Constructs a new instance.
		 *
		 * @param message
		 * 		The message to show.
		 *
		 * @since 0.2
		 */
		public UnableToParseException(@Nonnull final String message) {

			this(message, null);
		}

		/**
		 * Constructs a new instance.
		 *
		 * @param message
		 * 		The message to show.
		 * @param cause
		 * 		The cause which made the parser throw this exception.
		 *
		 * @since 0.2
		 */
		@SuppressWarnings("SameParameterValue")
		public UnableToParseException(@Nonnull final String message,
									  @Nullable final Throwable
											  cause) {

			super(message, cause);
		}
	}
}
