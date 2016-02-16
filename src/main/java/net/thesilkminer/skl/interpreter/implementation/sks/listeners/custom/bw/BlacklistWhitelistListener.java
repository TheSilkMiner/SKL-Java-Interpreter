package net.thesilkminer.skl.interpreter.implementation.sks.listeners.custom.bw;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.thesilkminer.skl.interpreter.api.sks.holder.IScriptHolder;
import net.thesilkminer.skl.interpreter.api.sks.listener.IScriptListener;
import net.thesilkminer.skl.interpreter.api.sks.listener.ISubsequentListener;
import net.thesilkminer.skl.interpreter.api.sks.listener.Result;
import net.thesilkminer.skl.interpreter.api.sks.parser.ISksParser;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Listener for custom language "blacklist-whitelist".
 *
 * <p>Since this is a custom language, I would like to spend
 * some words about its usage and the syntax.</p>
 *
 * <p><strong>USAGE</strong></p>
 *
 * <p>Its usage is really simple: reads a script file and
 * adds the strings to a whitelist or a blacklist. These
 * lists can then be read with a subsequent listener and
 * then used to perform different operations.</p>
 *
 * <p><strong>SYNTAX</strong></p>
 *
 * <p>The syntax is extremely simple: every entry with a
 * {@code +} is added to the whitelist, every entry with
 * a {@code -} is added to the blacklist.</p>
 *
 * <p>Every item which begins with {@code #} is treated
 * as a comment and is, as such, skipped.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class BlacklistWhitelistListener implements IScriptListener, ISubsequentListener {

	private List<String> whiteListItems;
	private List<String> blackListItems;
	private	Map<Integer, String> unableToBeProcessedLines;
	private int lineNumber;

	@Override
	public String listenerFor() {

		return "blacklist-whitelist";
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
	public void init(final ISksParser parser, final IScriptHolder scriptFile) {

		this.whiteListItems = Lists.newArrayList();
		this.blackListItems = Lists.newArrayList();
		this.unableToBeProcessedLines = Maps.newLinkedHashMap();
		this.lineNumber = 0;
	}

	@Override
	public void runScript(final List<String> lines) {

		lines.stream().forEachOrdered(this::testLine);
	}

	private void testLine(final String line) {

		final char start = line.charAt(0);
		final String item = line.substring(1);

		switch (start) {
			case '+':
				this.whiteListItems.add(item);
				break;
			case '-':
				this.blackListItems.add(item);
				break;
			case '#':
				break;
			default:
				this.unableToBeProcessedLines.put(this.lineNumber, line);
				break;
		}

		++this.lineNumber;
	}

	@Override
	public Result result() {

		this.lineNumber = 0;
		return Result.SUCCESSFUL;
	}

	@Override
	public Optional<List<String>> toLog() {

		if (this.unableToBeProcessedLines.isEmpty()) {

			return Optional.empty();
		}

		final List<String> messages = Lists.newArrayList();

		for (Map.Entry<Integer, String> error : this.unableToBeProcessedLines.entrySet()) {

			String mess = "[ERR]";
			mess += "Syntax of line ";
			mess += error.getKey().toString();
			mess += " is invalid.";
			messages.add(mess);

			mess = "[ERR]    Please check the syntax.";
			messages.add(mess);

			mess = "[ERR]    Line: ";
			mess += error.getValue();
			messages.add(mess);
		}

		return Optional.of(messages);
	}

	@Override
	public boolean canApply(final IScriptListener... previous) {

		return previous.length == 0;
	}

	@Override
	public void obtainPreviousListenersInformation(final IScriptListener... previous) {

		// No previous listener can be applied, so...
	}

	@Override
	public IScriptListener getListener() {

		return this;
	}

	/**
	 * Gets the whitelist.
	 *
	 * @return
	 * 		The whitelist.
	 *
	 * @since 0.2
	 */
	public List<String> getWhiteListItems() {

		return this.whiteListItems;
	}

	/**
	 * Gets the blacklist.
	 *
	 * @return
	 * 		The blacklist.
	 *
	 * @since 0.2
	 */
	public List<String> getBlackListItems() {

		return this.blackListItems;
	}
}
