package net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.service.acceptance;

import net.thesilkminer.skl.interpreter.api.skd.structure.IDatabase;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.service.AcceptanceService;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.database.AbstractDatabase;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.database.SingletonDatabase;

import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * Manages the acceptance of various databases.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public class DatabaseAcceptanceService extends AcceptanceService<IDatabase> {

	private Class<? extends AbstractDatabase> acceptDb;

	@Override
	public boolean canAccept(@Nonnull final IDatabase type) {
		this.acceptDb = null;
		AbstractDatabase.getPairs().entrySet().stream()
				.forEach(it -> {
					if (it.getValue().getKey().canAccept(type)) {
						this.acceptDb = it.getKey();
					}
				});
		return this.acceptDb != null;
	}

	@Nonnull
	@Override
	public IDatabase accept(@Nonnull final IDatabase type) {
		if (this.acceptDb == null) {
			throw new RuntimeException();
		}
		final IDatabase ret = Optional.ofNullable(
				AbstractDatabase.getPairs().get(this.acceptDb).getValue()
						.accept(type)
		).orElseThrow(RuntimeException::new);
		this.acceptDb = null;
		return ret;
	}

	@Override
	public void init() {
		AbstractDatabase.register(SingletonDatabase.class,
			db -> db.structure().nonNullSize() == 1,
			db -> {
				final SingletonDatabase database = new SingletonDatabase();
				database.mainTag(db.structure().getIndexTagNonNull(0)
						.orElseThrow(RuntimeException::new));
				database.version(db.version());
				database.docType(db.docType());
				return database;
			});
	}
}
