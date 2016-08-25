package net.thesilkminer.skl.interpreter.api.skd;

import net.thesilkminer.skl.interpreter.api.skd.service.ISkdService;
import net.thesilkminer.skl.interpreter.api.skd.service.ServiceManager;

import org.jetbrains.annotations.Contract;

import java.util.Optional;
import javax.annotation.Nonnull;

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

	private SkdApi() {
		try {
			final Class<?> def = Class
					.forName("net.thesilkminer.skl.interpreter."
							+ "implementation.skd.service."
							+ "DefaultService");
			final ServiceManager manager = ServiceManager.get();
			final ISkdService service = ISkdService.class.cast(def.newInstance());
			manager.provide(SkdApi.class, service);
		} catch (final ReflectiveOperationException ignored) {
			// We don't really care
		}
	}

	/**
	 * Gets the singleton instance of the API.
	 *
	 * @return
	 * 		The singleton instance of the API.
	 *
	 * @since 0.2
	 */
	@Contract(value = "-> !null", pure = true)
	@Nonnull
	public static SkdApi get() {
		return SINGLETON;
	}

	/**
	 * Gets a service providing the API.
	 *
	 * @return
	 *      The currently registered.
	 *
	 * @throws RuntimeException If there is no available service.
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	public ISkdService api() {
		final Optional<ISkdService> service = this.serviceManager().get(SkdApi.class);
		return service.orElseThrow(() ->
				new RuntimeException("No available service found"));
	}

	/**
	 * Gets the service manager.
	 *
	 * @return
	 *      The service manager
	 *
	 * @since 0.2.1
	 */
	@Nonnull
	@SuppressWarnings("WeakerAccess")
	public ServiceManager serviceManager() {
		return ServiceManager.get();
	}
}
