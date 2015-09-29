package net.thesilkminer.skl.interpreter.sks;

import com.google.common.base.Optional;

import java.util.List;

/**
 * Created by TheSilkMiner on 13/09/2015.
 * Package: net.thesilkminer.skl.interpreter.sks.
 * Project: Java Interpreter.
 */
public interface IScriptListener {

	String listenerFor();

	boolean needsInit();

	boolean hasAlreadyInit();

	void init(SksParser parser, ScriptFile scriptFile);

	void runScript(List<String> lines);

	Result result();

	boolean equals(Object object);

	int hashCode();

	Optional<List<String>> toLog();
}
