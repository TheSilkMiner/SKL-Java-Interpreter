package net.thesilkminer.skl.interpreter.api.sks.listener;

import net.thesilkminer.skl.interpreter.api.sks.holder.IScriptHolder;
import net.thesilkminer.skl.interpreter.api.sks.parser.ISksParser;

import java.util.List;
import java.util.Optional;

/**
 * Represents a listener for a script.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public interface IScriptListener {

	/**
	 * Returns the language this listener was made for.
	 *
	 * <p>The returned string is used to register the listener
	 * and to check if the script language is applicable.</p>
	 *
	 * <p>Remember that the parser is Case-Sensitive!</p>
	 *
	 * @return
	 * 		The language this listener can process.
	 *
	 * @since 0.1
	 */
	String listenerFor();

	/**
	 * Returns if the script needs to be initialized.
	 *
	 * @return
	 * 		If the script needs to be initialized.
	 *
	 * @since 0.1
	 */
	@SuppressWarnings("SameReturnValue")
	boolean needsInit();

	/**
	 * Returns if the script has already been initialized.
	 *
	 * <p>This can be used for listeners which have to be singletons
	 * (almost impossible) or on external calls.</p>
	 *
	 * @return
	 * 		If the script has already been initialized.
	 *
	 * @since 0.1
	 */
	boolean hasAlreadyInit();

	/**
	 * Initializes this listener.
	 *
	 * @param parser
	 * 		The current script parser. See also {@link ISksParser}.
	 * @param scriptFile
	 * 		The file that is currently being parsed.
	 *
	 * @since 0.1
	 */
	void init(ISksParser parser, IScriptHolder scriptFile);

	/**
	 * Runs the script.
	 *
	 * @param lines
	 * 		The list of lines which are contained in the script.
	 *
	 * @since 0.1
	 */
	void runScript(List<String> lines);

	/**
	 * Returns the result of the processing.
	 *
	 * <p>It must be either a {@link Result#SUCCESSFUL},
	 * {@link Result#WARNING} or {@link Result#ERRORED}.
	 * Refer to the specific Javadoc for more information.</p>
	 *
	 * @return
	 * 		The result of the processing.
	 *
	 * @since 0.1
	 */
	Result result();

	/**
	 * Returns a list of messages which needs to be logged or
	 * {@link Optional#empty()} if none.
	 *
	 * @return
	 * 		A list of messages which needs to be logged or
	 * 		{@link Optional#empty()} if none.
	 *
	 * @since 0.1
	 */
	Optional<List<String>> toLog();

	/*
	 * The following methods are reported by IntelliJ as:
	 *
	 * - Overriders of the methods in java.lang.Object
	 * - Implemented by java.lang.Object
	 *
	 *
	 * What????
	 */

	@Override
	boolean equals(Object object);

	@Override
	int hashCode();

	@Override
	String toString();
}
