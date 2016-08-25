package net.thesilkminer.skl.interpreterx.base.interfaces;

/**
 * Marks extensions which need initialization before
 * being able to be used.
 *
 * @author TheSilkMiner
 *
 * @since 0.2.1 (SKL Interpreter)
 */
public interface INeedsInit {

	/**
	 * Called when initializing the extension.
	 *
	 * @since 0.2.1 (SKL Interpreter)
	 */
	void init();
}
