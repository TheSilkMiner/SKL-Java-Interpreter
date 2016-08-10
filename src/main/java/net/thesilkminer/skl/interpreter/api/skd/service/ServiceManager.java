package net.thesilkminer.skl.interpreter.api.skd.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import org.jetbrains.annotations.Contract;

import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * Manages the various services, handling the
 * loading and substitution of the various services.
 *
 * @author TheSilkMiner
 *
 * @since 0.2.1
 */
public final class ServiceManager {

	private static final ServiceManager INSTANCE = new ServiceManager();

	private final Map<Class<?>, ISkdService> services;

	private ServiceManager() {
		this.services = Maps.newHashMap();
	}

	/**
	 * Gets the unique instance of the service manager.
	 *
	 * @return
	 *      The unique instance.
	 *
	 * @since 0.2.1
	 */
	@Contract(value = "-> !null", pure = true)
	@Nonnull
	public static ServiceManager get() {
		return INSTANCE;
	}

	/**
	 * Gets the service currently registered for the current class,
	 * or {@link Optional#empty()} if none is available.
	 *
	 * @param service
	 *      The service's class.
	 * @return
	 *      An {@link Optional} containing the service.
	 *
	 * @since 0.2.1
	 */
	@Contract(value = "null -> fail; !null -> !null", pure = true)
	@Nonnull
	public Optional<ISkdService> get(@Nonnull final Class<?> service) {
		return Optional.ofNullable(this.services.get(service));
	}

	/**
	 * Provides a new service for the specified class.
	 *
	 * @param clazz
	 *      The class this service is for.
	 * @param service
	 *      The new service instance.
	 *
	 * @since 0.2.1
	 */
	public void provide(@Nonnull final Class<?> clazz, @Nonnull final ISkdService service) {
		Preconditions.checkNotNull(clazz, "Class must not be null");
		Preconditions.checkNotNull(service, "Service must not be null");
		final ISkdService old = this.services.get(clazz);
		// Micro-optimization: if they are the same, do not continue
		if (old == service) {
			// Do not use equals because we check for identity, not equality
			return;
		}

		if (old != null) {
			old.finalizeService();
		}

		service.init();

		if (old == null) {
			service.grabInfo(null, null);
		} else {
			service.grabInfo(old, old.getClass());
		}

		if (!service.isUsable() && old != null) {
			service.finalizeService();
			old.init();
			old.grabInfo(service, service.getClass());
			if (!old.isUsable()) {
				throw new RuntimeException("Unable to set service "
						+ "without freezing");
			}
			return;
		}

		if (!service.isUsable()) {
			throw new RuntimeException("Unable to set default service");
		}

		this.services.put(clazz, service);
	}
}
