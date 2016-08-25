package net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import net.thesilkminer.skl.interpreter.api.skd.SkdApi;
import net.thesilkminer.skl.interpreter.api.skd.holder.IDatabaseHolder;
import net.thesilkminer.skl.interpreter.api.skd.logging.ISkdLogger;
import net.thesilkminer.skl.interpreter.api.skd.parser.ISkdParser;
import net.thesilkminer.skl.interpreter.api.skd.service.ISkdService;
import net.thesilkminer.skl.interpreter.api.skd.structure.IDatabase;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdProperty;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdTag;
import net.thesilkminer.skl.interpreter.api.skd.structure.IStructure;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeDeclaration;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.version.IDatabaseVersionDeclaration;
import net.thesilkminer.skl.interpreter.implementation.skd.SkdLogger;
import net.thesilkminer.skl.interpreter.implementation.skd.structure.declarations.DatabaseVersion;
import net.thesilkminer.skl.interpreter.implementation.skd.structure.declarations.DocType;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.NewSkdParser;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.database.AbstractDatabase;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.property.AbstractProperty;
import net.thesilkminer.skl.interpreterx.skdx.thesilkminer.parserex.v0_1.structure.tag.AbstractTag;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Service used by ParserEx to allow for custom
 * tags and parsers to be used.
 *
 * @author TheSilkMiner
 *
 * @since 0.1
 */
public class ParserExService implements ISkdService {

	private static final class BaseDatabase extends AbstractDatabase {
		private BaseDatabase() {
			super();
			this.removeFromList();
		}
	}

	private static final class BaseTag extends AbstractTag {
		private BaseTag(@Nonnull final String name) {
			super(name);
			this.removeFromList();
		}
	}

	private static final class BaseProperty extends AbstractProperty {
		private BaseProperty(@Nonnull final String name) {
			super(name);
			this.removeFromList();
		}
	}

	private static final class Structure implements IStructure {

		private final List<ISkdTag> tags;

		private Structure() {
			this.tags = Lists.newArrayList();
		}

		@Nonnull
		private static IStructure of(@Nonnull final List<ISkdTag> main) {
			final IStructure $this = new Structure();
			$this.mainTags(main);
			return $this;
		}

		@Nonnull
		@Override
		public List<ISkdTag> mainTags() {
			return this.tags;
		}

		@Override
		public void mainTags(@Nonnull final List<ISkdTag> tags) {
			this.tags.addAll(Preconditions.checkNotNull(tags));
		}

		@Override
		public boolean canApply(@Nonnull final IDocTypeDeclaration declaration) {
			return true;
		}

		@Override
		public void apply(@Nonnull final IDocTypeDeclaration declaration) {
			// TODO
		}

		@Nonnull
		@Override
		public Optional<ISkdTag> getIndexTagNonNull(final int index) {
			int idx = 0;
			for (final ISkdTag it : this.mainTags()) {
				if (it == null) {
					continue;
				}
				if (index == idx) {
					return Optional.of(it);
				}
				++idx;
			}
			return Optional.empty();
		}

		@Override
		public int nonNullSize() {
			return this.mainTags().stream().filter(Objects::nonNull).toArray().length;
		}

		@Nonnull
		@Override
		public String toString() {
			String toString = "";
			boolean first = true;

			for (final ISkdTag tag : this.mainTags()) {
				if (tag == null) {
					if (first) {
						first = false;
						continue;
					}
					toString += "\n";
					continue;
				}
				toString += tag.toString();
				toString += "\n";
			}

			return toString;
		}
	}

	private ISkdLogger logger;
	private boolean cached;

	@Override
	public void init() {
		this.logger = SkdLogger.get();
		this.cached = "true".equals(
				System.getProperty("net.thesilkminer.skl.interpreterx."
						+ "skdx.thesilkminer.parserex.v0_1.service."
						+ "cache", "false")
		);
	}

	@Override
	public void finalizeService() {

	}

	@Nonnull
	@Override
	public ISkdLogger logger() {
		return this.logger;
	}

	@Nonnull
	@Override
	public ISkdParser parser(@Nonnull final IDatabaseHolder databaseHolder) {
		return NewSkdParser.get(databaseHolder, this.cached);
	}

	@Nonnull
	@Override
	public IDatabaseHolder databaseHolder(@Nonnull final Object object) {
		return ((DatabaseHolderGetterService) SkdApi.get().serviceManager()
				.get(IDatabaseHolder.class).orElseThrow(RuntimeException::new))
				.get(object).orElseThrow(RuntimeException::new);
	}

	@Nonnull
	@Override
	public IDatabase database(@Nonnull final IDocTypeDeclaration type,
	                          @Nonnull final IDatabaseVersionDeclaration version,
	                          @Nonnull final IStructure structure) {
		final IDatabase db = new BaseDatabase();
		db.structure(structure);
		db.version(version);
		db.docType(type);
		return db;
	}

	@Nonnull
	@Override
	public IDocTypeDeclaration doctype(@Nonnull final String type) {
		return DocType.of(type);
	}

	@Nonnull
	@Override
	public IDatabaseVersionDeclaration version(@Nullable final String version) {
		if (version == null) {
			return DatabaseVersion.get();
		}
		return DatabaseVersion.get(version);
	}

	@Nonnull
	@Override
	public IStructure structure(@Nonnull final List<ISkdTag> main) {
		return Structure.of(main);
	}

	@Nonnull
	@Override
	public ISkdTag tag(@Nonnull final String name) {
		return new BaseTag(name);
	}

	@Nonnull
	@Override
	public ISkdProperty property(@Nonnull final String name, @Nonnull final Object value) {
		final ISkdProperty property = new BaseProperty(name);
		property.setValue(value.toString());
		return property;
	}

	@Override
	public void grabInfo(@Nullable final ISkdService service,
	                     @Nullable final Class<? extends ISkdService> serviceClass) {
		if (service == null) {
			return;
		}

		this.logger = service.logger();
	}

	@Override
	public boolean isUsable() {
		return true;
	}

	@Nonnull
	@Override
	public Multimap<Class<?>, Class<?>> additionalTypes() {
		//Why can't you infer the generic type????
		return ImmutableListMultimap.<Class<?>, Class<?>>builder()
				.putAll(ISkdTag.class, AbstractTag.getTags())
				.putAll(ISkdProperty.class, AbstractProperty.getProperties())
				.putAll(IDatabase.class, AbstractDatabase.getDatabases())
				// TODO
				.build();
	}
}
