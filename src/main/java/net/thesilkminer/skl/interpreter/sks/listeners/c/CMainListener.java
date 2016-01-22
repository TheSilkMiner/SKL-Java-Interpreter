package net.thesilkminer.skl.interpreter.sks.listeners.c;

import com.google.common.collect.Lists;

import net.thesilkminer.skl.interpreter.sks.IScriptListener;
import net.thesilkminer.skl.interpreter.sks.Result;
import net.thesilkminer.skl.interpreter.sks.ScriptFile;
import net.thesilkminer.skl.interpreter.sks.SksParser;
import net.thesilkminer.skl.interpreter.sks.listeners.java.JavaMainListener;

import java.util.List;
import java.util.Optional;

/**
 * Created by TheSilkMiner on 14/01/2016.
 * Package: net.thesilkminer.skl.interpreter.sks.listeners.c.
 * Project: Java Interpreter.
 */
public class CMainListener implements IScriptListener {

	private JavaMainListener javaListener;

	@Override
	public String listenerFor() {

		return "c";
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
	public void init(SksParser parser, ScriptFile scriptFile) {

		this.javaListener = new JavaMainListener();

		if (this.javaListener.needsInit() && !this.javaListener.hasAlreadyInit()) {

			this.javaListener.init(parser, scriptFile);
		}
	}

	@Override
	public void runScript(List<String> lines) {

		final List<String> javaLines = Lists.newArrayList();

		// Sanity check
		// Replace lines from C to Java

		boolean inComment = false;

		for (final String line : lines) {

			if (inComment) {

				javaLines.add(line);
			}

			if (line.startsWith("#")) {

				javaLines.add(this.parsePreProcessorLine(line));
				continue;
			}

			if (line.contains("/*")) {

				inComment = true;
			}

			if (line.contains("*/")) {

				inComment = false;
			}
		}

		this.javaListener.runScript(javaLines);
	}

	private String parsePreProcessorLine(String line) {

		// TODO

		return line;
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
