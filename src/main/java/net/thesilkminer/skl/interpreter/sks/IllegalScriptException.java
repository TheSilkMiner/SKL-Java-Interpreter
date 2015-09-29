package net.thesilkminer.skl.interpreter.sks;

/**
 * Created by TheSilkMiner on 13/09/2015.
 * Package: net.thesilkminer.skl.interpreter.sks.
 * Project: Java Interpreter.
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
