package net.thesilkminer.skl.interpreter.skd.api

import net.thesilkminer.skl.interpreter.skd.multicatch
import java.util.Optional

/**
 * Main entry point for the API.
 *
 * All library users should refer to this class and the
 * provider exposed through this API. Refer to
 * [ApiProvider] for more information.
 *
 * @since 0.3
 */
object Skd {
    private const val CLASS_NAME = "net.thesilkminer.skl.interpreter.skd.ApiProvider"

    /**
     * Represents the API provider implementation currently
     * found and that is being used by the API.
     *
     * @since 0.3
     */
    val provider: ApiProvider by lazy {
        try {
            this.tryInit()
        } catch (e: Throwable) {
            e.multicatch(ReflectiveOperationException::class.java, InstantiationError::class.java) {
                this.report(e.message.toString())
                this.report("Falling back to no-op implementation")
                ApiProvider.NoOpImplementation()
            }
        }
    }

    private fun tryInit(): ApiProvider {
        val classLoader = this.javaClass.classLoader
        val cl = Class.forName(CLASS_NAME, false, classLoader)
        val validConstructors = cl.declaredConstructors.filter { it.parameterCount == 0 }
        if (validConstructors.count() == 0) {
            throw InstantiationError("Unable to instantiate API provider: missing parameter-less constructor")
        }
        val constructor = Optional.ofNullable(validConstructors.firstOrNull { it.isAccessible })
                .orElse(validConstructors[0]) ?: throw ReflectiveOperationException("No valid constructor found")
        val wasAccessible = constructor.isAccessible
        if (!wasAccessible) {
            constructor.isAccessible = true
        }
        val providerAny = constructor.newInstance()
        if (!wasAccessible && constructor.isAccessible) {
            constructor.isAccessible = false
        }
        if (!ApiProvider::class.java.isInstance(providerAny)) {
            throw ReflectiveOperationException("ApiProvider implementation is not an instance of ApiProvider")
        }
        return providerAny as ApiProvider
    }

    private fun report(message: String) {
        // We are too early and too high-level to attempt using SLF4J,
        // even trough reflection. Hell, reflection would be even
        // worse considering that this is not only a public facing
        // API, but also the main entry point. Thus, if we fail here,
        // the entire library becomes practically unusable (we crash,
        // basically). Henceforth why we use the OLD OLD OLD logging
        // way -- println.
        System.err.println("[SKD API Init/Error Reporting] $message")
    }
}

