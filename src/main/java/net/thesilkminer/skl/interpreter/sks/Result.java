package net.thesilkminer.skl.interpreter.sks;

/**
 * Result of the parsing.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public enum Result {

	/**
	 * Everything went correctly. No errors were thrown.
	 *
	 * @since 0.1
	 */
	SUCCESSFUL(),
	/**
	 * The parser has thrown an error, either because of the script
	 * or itself.
	 *
	 * @since 0.1
	 */
	ERRORED(),
	/**
	 * Some strange things happened, but the parsing was completed
	 * successfully in the end.
	 *
	 * @since 0.1
	 */
	WARNING(),
	;
}
