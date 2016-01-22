package net.thesilkminer.skl.interpreter.sks;

/**
 * Represents an exception thrown when the script does not comply to the syntax.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public class IllegalScriptException extends RuntimeException {

	private static final String DEFAULT_MESSAGE =
			      "The specified script is not a valid SKL script";

	public IllegalScriptException(String msg) {

		this(msg, null);
	}

	public IllegalScriptException() {

		this(DEFAULT_MESSAGE);
	}

	public IllegalScriptException(Throwable th) {

		this(DEFAULT_MESSAGE, th);
	}

	public IllegalScriptException(String msg, Throwable th) {

		super(msg, th);
	}
}
