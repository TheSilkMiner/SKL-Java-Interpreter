package net.thesilkminer.skl.interpreter.implementation.sks.listeners.skl;

import net.thesilkminer.skl.interpreter.api.sks.holder.IScriptHolder;
import net.thesilkminer.skl.interpreter.api.sks.listener.IScriptListener;
import net.thesilkminer.skl.interpreter.api.sks.listener.Result;
import net.thesilkminer.skl.interpreter.implementation.sks.SksParser;

import java.util.List;
import java.util.Optional;

/**
 * Created by TheSilkMiner on 29/09/2015.
 * Package: net.thesilkminer.skl.interpreter.sks.listeners.skl.
 * Project: Java Interpreter.
 */
public class SklMainListener implements IScriptListener {

	@Override
	public String listenerFor() {

		return "skl";
	}

	@Override
	public boolean needsInit() {

		return true;
	}

	@Override
	public boolean hasAlreadyInit() {

		return false;
	}

	@Override
	public void init(SksParser parser, IScriptHolder scriptFile) {

	}

	@Override
	public void runScript(List<String> lines) {

	}

	@Override
	public Result result() {

		return Result.SUCCESSFUL;
	}

	@Override
	public Optional<List<String>> toLog() {

		return Optional.empty();
	}
}
