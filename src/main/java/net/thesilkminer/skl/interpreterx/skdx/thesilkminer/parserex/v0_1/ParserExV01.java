package net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1;

import net.thesilkminer.skl.interpreter.api.skd.SkdApi;
import net.thesilkminer.skl.interpreter.api.skd.holder.IDatabaseHolder;
import net.thesilkminer.skl.interpreter.api.skd.service.ISkdService;
import net.thesilkminer.skl.interpreter.api.skd.structure.IDatabase;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdProperty;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdTag;
import net.thesilkminer.skl.interpreterx.base.interfaces.INeedsInit;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.ParserEx;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.service.DatabaseHolderGetterService;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.service.ParserExService;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.service.acceptance.DatabaseAcceptanceService;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.service.acceptance.PropertiesAcceptanceService;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.service.acceptance.TagAcceptanceService;

/**
 * Main class of version 0.1 of ParserExV01.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public class ParserExV01 extends ParserEx implements INeedsInit {

	@Override
	public void init() {
		this.provide(SkdApi.class, new ParserExService());
		this.provide(IDatabaseHolder.class, new DatabaseHolderGetterService());
		this.provide(ISkdTag.class, new TagAcceptanceService());
		this.provide(ISkdProperty.class, new PropertiesAcceptanceService());
		this.provide(IDatabase.class, new DatabaseAcceptanceService());
	}

	private void provide(final Class<?> clazz, final ISkdService service) {
		SkdApi.get().serviceManager().provide(clazz, service);

		if (!SkdApi.get().serviceManager()
				.get(clazz)
				.orElseThrow(RuntimeException::new)
				.getClass().equals(service.getClass())) {
			throw new RuntimeException("Unable to set needed service ("
					+ service.getClass() + ")"
			);
		}
	}
}
