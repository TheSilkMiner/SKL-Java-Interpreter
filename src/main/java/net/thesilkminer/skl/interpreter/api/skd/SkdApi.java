package net.thesilkminer.skl.interpreter.api.skd;

/**
 * Represents the access point of the SKD Api.
 *
 * <p>Every call to the API must be performed from here.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class SkdApi {

	private static final SkdApi SINGLETON = new SkdApi();

	private SkdApi() { }

	/**
	 * Gets the singleton instance of the API.
	 *
	 * @return
	 * 		The singleton instance of the API.
	 *
	 * @since 0.2
	 */
	public static SkdApi get() {

		return SINGLETON;
	}
}
