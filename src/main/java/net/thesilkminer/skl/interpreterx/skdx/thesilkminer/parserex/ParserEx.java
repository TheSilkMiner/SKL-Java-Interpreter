package net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex;

// In Idea all imports are yellow apart from one
// LOL
import net.thesilkminer.skl.interpreterx.base.annotations.Extension;
import net.thesilkminer.skl.interpreterx.base.annotations.VersionCatalog;
import net.thesilkminer.skl.interpreterx.base.annotations.VersionMainType;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.ParserExV01;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Main class of ParserExV01.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
@Extension(id = "parserex",
		name = "ParserEx",
		author = "TheSilkMiner",
		version = "0.1",
		language = Extension.Language.SKD,
		versionCatalog = ParserEx.Version.class)
public class ParserEx {

	@SuppressWarnings("WeakerAccess")
	@VersionCatalog
	public enum Version {
		@VersionMainType(ParserExV01.class)	V0_1,
		;
	}

	@Contract(value = "-> !null", pure = true)
	@NotNull
	public static String currentVersion() {
		return Version.V0_1.toString().replace("V", "").replace('_', '.');
	}
}
