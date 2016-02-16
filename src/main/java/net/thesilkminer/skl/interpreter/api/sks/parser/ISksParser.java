package net.thesilkminer.skl.interpreter.api.sks.parser;

/**
 * Represents the API entry for an Sks parser.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public interface ISksParser {

	/**
	 * Initializes the parser previously created.
	 *
	 * <p>Initializing means checking if the file is valid to be processed.</p>
	 *
	 * @param force
	 * 		Whether to allow non-sks-ending files to be parsed.
	 * 		    True is used to allow, false to avoid.
	 */
	void initParser(final boolean force);

	/**
	 * Gets if the parser has been initialized.
	 *
	 * @return
	 * 		If the parser has been initialized.
	 */
	boolean init();

	/**
	 * Gets if the parser has errored.
	 *
	 * @return
	 * 		If the parser has errored.
	 */
	boolean errored();

	/**
	 * Parses the file and automatically runs the script in it.
	 *
	 * @throws net.thesilkminer.skl.interpreter.api.sks.language.IllegalScriptException
	 * 		If the file is not syntactically correct.
	 */
	void parse();

	/**
	 * Gets the name of the script.
	 *
	 * @return The script's name
	 */
	String getScriptName();
}
