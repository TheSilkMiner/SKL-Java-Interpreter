package net.thesilkminer.skl.interpreter.api.sks.listener;

/**
 * Represents a listener which supports the subsequent architecture.
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public interface ISubsequentListener {

	/**
	 * Returns if the listener can be a subsequent of the previous listeners.
	 *
	 * @param previous
	 * 		The previous listeners.
	 * @return
	 * 		If the listeners can be run.
	 *
	 * @since 0.2
	 */
	boolean canApply(final IScriptListener... previous);

	/**
	 * Gets the previous listener information.
	 *
	 * <p>In other words, this method is used to obtain
	 * all the necessary information for previous listeners.</p>
	 *
	 * @param previous
	 * 		The previous listeners.
	 *
	 * @since 0.2
	 */
	void obtainPreviousListenersInformation(final IScriptListener... previous);

	/**
	 * Gets the current listener.
	 *
	 * @return
	 * 		The current listener.
	 *
	 * @since 0.2
	 */
	IScriptListener getListener();

	/**
	 * Gets the current listener for its specified successor.
	 *
	 * @param successor
	 * 		The successor.
	 * @return
	 * 		The current listener.
	 *
	 * @since 0.2
	 */
	default IScriptListener getListenerForSuccessor(final IScriptListener successor) {

		return this.getListener();
	}
}
