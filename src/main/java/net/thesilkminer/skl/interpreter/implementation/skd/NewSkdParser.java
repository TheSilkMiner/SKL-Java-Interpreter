package net.thesilkminer.skl.interpreter.implementation.skd;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import net.thesilkminer.skl.interpreter.api.skd.SkdApi;
import net.thesilkminer.skl.interpreter.api.skd.exceptions.IllegalDatabaseSyntaxException;
import net.thesilkminer.skl.interpreter.api.skd.holder.IDatabaseHolder;
import net.thesilkminer.skl.interpreter.api.skd.parser.ISkdParser;
import net.thesilkminer.skl.interpreter.api.skd.structure.IDatabase;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdProperty;
import net.thesilkminer.skl.interpreter.api.skd.structure.ISkdTag;
import net.thesilkminer.skl.interpreter.api.skd.structure.IStructure;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.IDeclaration;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.doctype.IDocTypeDeclaration;
import net.thesilkminer.skl.interpreter.api.skd.structure.declarations.version.IDatabaseVersionDeclaration;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Contract;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * An improved version of the {@link SkdParser original version}.
 *
 * <p>This parser can work with multiple
 * {@link net.thesilkminer.skl.interpreter.api.skd.holder.IDatabaseHolder database holders}
 * and allow for a more fine-grained control over the currently
 * registered tags (e.g. better callback calls and an improved
 * general look).</p>
 *
 * <p>New implementations and/or services should refer to this
 * class by default.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2.1
 */
public class NewSkdParser implements ISkdParser {

	private static final Map<IDatabaseHolder, ISkdParser> CACHE = Maps.newHashMap();
	private static final String TAG_START = "<";
	private static final String TAG_END = ">";
	private static final String END_MARKER = "/";
	private static final String DECLARATION_MARKER = "!";
	private static final String DECLARATION_START = TAG_START + DECLARATION_MARKER;
	private static final String DECLARATION_END = TAG_END;
	private static final String VOID_TAG_END = END_MARKER + TAG_END;
	private static final String CLOSING_TAG_START = TAG_START + END_MARKER;

	private final Map<Integer, ISkdTag> lastTagOnLevel;
	private final IDatabaseHolder currentDatabaseHolder;
	private final Multimap<Class<?>, Class<?>> types;
	private final Map<String, IDeclaration> declarations;
	private boolean init;
	private BufferedReader in;
	private int indentCount;
	private IDatabase db;
	private IDocTypeDeclaration docType;
	private IDatabaseVersionDeclaration version;
	private IStructure structure;

	private NewSkdParser(@Nonnull final IDatabaseHolder databaseHolder) {
		this.lastTagOnLevel = Maps.newHashMap();
		this.currentDatabaseHolder = databaseHolder;
		this.types = SkdApi.get().api().additionalTypes();
		this.declarations = Maps.newHashMap();
		this.init = false;
		this.in = null;
		this.indentCount = 0;
		this.db = null;
		this.docType = null;
		this.version = null;
		this.structure = null;
	}

	/**
	 * Gets a new SKD parser for the specified {@code databaseHolder}.
	 *
	 * <p>This method, differently from {@link #get(IDatabaseHolder, boolean)},
	 * does not cache the value nor consults the parser cache.</p>
	 *
	 * @param databaseHolder
	 *      The database holder the parser is for.
	 * @return
	 *      A new instance of an {@link ISkdParser}.
	 *
	 * @since 0.2.1
	 */
	@Contract(pure = true)
	@Nonnull
	public static ISkdParser get(@Nonnull final IDatabaseHolder databaseHolder) {
		return get(databaseHolder, false);
	}

	/**
	 * Gets a new SKD parser for the specified {@code databaseHolder}.
	 *
	 * @param databaseHolder
	 *      The database holder the parser is for.
	 * @param cached
	 *      If the method should consult the cache before attempting
	 *      parser's creation.
	 * @return
	 *      A new instance of an {@link ISkdParser} or an already
	 *      created one, if available and {@code cached} is {@link true}.
	 *
	 * @since 0.2.1
	 */
	@Contract(pure = true)
	@Nonnull
	public static ISkdParser get(@Nonnull final IDatabaseHolder databaseHolder,
	                             final boolean cached) {
		if (cached && CACHE.containsKey(databaseHolder)) {
			return CACHE.get(databaseHolder);
		}

		final ISkdParser $this = new NewSkdParser(databaseHolder);

		if (cached) {
			CACHE.put(databaseHolder, $this);
		}

		return $this;
	}

	@Override
	public void init(final boolean force) {
		if (this.init()) {
			throw new RuntimeException("Parser already initialized");
		}

		if (!this.databaseHolder().canBeAcceptedByDefault() && !force) {
			throw new RuntimeException("Invalid database holder type");
		}

		SkdApi.get().api().logger().info("Initializing parser for database holder: "
				+ this.databaseHolder().toString());

		if (force && !this.databaseHolder().canBeAcceptedByDefault()) {
			SkdApi.get().api().logger().warn("This initialization has been forced");
			SkdApi.get().api().logger().warn("This may not go well...");
		}

		this.in = this.databaseHolder().readerStream();
		this.structure = SkdApi.get().api().structure(Lists.newArrayList());
		this.init = true;
	}

	@Override
	public boolean init() {
		return this.init;
	}

	@Override
	public boolean errored() {
		return false;
	}

	@Nonnull
	@Override
	public IDatabase read() {
		if (!this.init()) {
			this.init(false); // Better than manual initialization
		}

		try {
			this.in.lines().forEach(this::parse);

			this.docType = (IDocTypeDeclaration) this.declarations.get("DOCTYPE");
			this.version = (IDatabaseVersionDeclaration) this.declarations.get("SKD");

			this.db = SkdApi.get().api().database(this.docType,
					this.version,
					this.structure);
			this.db = SkdApi.get().api().databaseCallback(this.db);

			this.db = this.tryAccept(IDatabase.class, this.db).orElse(this.db);

			SkdApi.get().api().logger().info("Parse completed");

			return this.db;
		} catch (final RuntimeException exception) {
			final IllegalDatabaseSyntaxException toThrow
					= new IllegalDatabaseSyntaxException();
			toThrow.initCause(exception);
			try {
				final Class<?> clazzIdse = toThrow.getClass();
				Class<?> clazzT;
				do {
					clazzT = clazzIdse.getSuperclass();
				}
				while (clazzT.equals(Throwable.class));
				final Field detailMessage = clazzT.getField("detailMessage");
				detailMessage.setAccessible(true);
				detailMessage.set(toThrow,
						"Unable to parse the database: syntax error");
				detailMessage.setAccessible(false);
			} catch (final ReflectiveOperationException ignored) {
				//Ignore
			}
			throw toThrow;
		}
	}

	@Nonnull
	private <T> Optional<T> tryAccept(@Nonnull final Class<T> clazz, @Nonnull T toAccept) {
		final Collection<Class<?>> types = this.types.get(clazz);

		if (types != null && !types.isEmpty()) {
			for (final Class<?> typeClass : types) {
				try {
					final T type = (T) typeClass.newInstance();
					final boolean canAccept = (boolean) type.getClass()
							.getMethod("canAccept", Object.class)
							.invoke(type, toAccept);
					if (!canAccept) {
						continue;
					}
					type.getClass().getMethod("accept", Object.class)
							.invoke(type, toAccept);
					return Optional.of(type);
				} catch (final InstantiationException
						| IllegalAccessException
						| NoSuchMethodException
						| InvocationTargetException ex) {
					// Ignored
				}
			}
		}

		return Optional.empty();
	}

	private void parse(final String line) {
		SkdApi.get().api().logger().info("Currently parsing line " + line);

		this.indentCount = 0;
		this.countIndent(line);

		String realLine = line;
		while (realLine.startsWith("\t")) {
			realLine = realLine.substring("\t".length());
		}

		if (realLine.startsWith(DECLARATION_START)) {
			SkdApi.get().api().logger().debug("Found declaration " + realLine);
			this.parseDeclaration(realLine);
			return;
		}

		if (realLine.startsWith("<SKD")) {
			// Legacy support
			// TODO Remove in 0.3
			SkdApi.get().api().logger().debug("Found declaration " + realLine);
			this.parseVersionBridge(realLine);
			return;
		}

		if (realLine.startsWith(TAG_START)) {
			SkdApi.get().api().logger().debug("Found tag " + realLine);
			this.parseTag(realLine);
			return;
		}

		if (realLine.isEmpty()) {
			return;
		}

		SkdApi.get().api().logger().debug("Found tag content " + realLine);
		this.parseContent(realLine);
	}

	private void countIndent(final String line) {
		for (final char c : line.toCharArray()) {
			if (c == '\t') {
				++this.indentCount;
				continue;
			}
			break;
		}
	}

	private void parseDeclaration(final String line) {
		final String declaration = line.substring(DECLARATION_START.length(),
				line.length() - DECLARATION_END.length());
		final String name = declaration.contains(" ")
				? declaration.substring(0, declaration.indexOf(' '))
				: declaration;

		switch (name.toUpperCase(Locale.ENGLISH)) {
			case "DOCTYPE":
				SkdApi.get().api().logger().debug("Found doctype " + line);
				this.parseDocType(declaration);
				break;
			case "SKD":
				SkdApi.get().api().logger().debug("Found version " + line);
				this.parseVersion(declaration);
				break;
			default:
				throw new RuntimeException("Unrecognized declaration "
						+ declaration);
		}
	}

	private void parseDocType(final String declarationLine) {
		final String[] array = declarationLine.split(" ");
		if (array.length != 3) {
			throw new RuntimeException("Invalid doctype declaration");
		}

		final String style = Arrays.stream(array)
				.filter(it -> !it.equalsIgnoreCase("skd"))
				.filter(it -> !it.equalsIgnoreCase("DOCTYPE"))
				.findFirst()
				.orElseThrow(RuntimeException::new);

		this.declarations.put("DOCTYPE", SkdApi.get().api().doctype(style));
	}

	private void parseVersion(final String declarationLine) {
		final String[] array = declarationLine.split(" ");
		if (array.length != 3) {
			throw new RuntimeException("Invalid version declaration");
		}

		final String version = Arrays.stream(array)
				.filter(it -> !it.equalsIgnoreCase("version"))
				.filter(it -> !it.equalsIgnoreCase("SKD"))
				.findFirst()
				.orElseThrow(RuntimeException::new);

		this.declarations.put("SKD", SkdApi.get().api().version(version));
	}

	// LEGACY SUPPORT!
	// REMOVE IN LATER VERSIONS!
	private void parseVersionBridge(final String line) {
		this.parseDeclaration(line.replace("<SKD", "<!SKD"));
	}

	private void parseTag(final String line) {
		if (line.endsWith(VOID_TAG_END)) {
			SkdApi.get().api().logger().debug("Found void tag " + line);
			this.parseVoidTag(line);
			return;
		}

		if (line.startsWith(CLOSING_TAG_START)) {
			SkdApi.get().api().logger().debug("Found closing tag " + line);
			this.parseClosingTag(line);
			return;
		}

		SkdApi.get().api().logger().debug("Found opening tag " + line);
		this.parseOpeningTag(line);
	}

	private void parseContent(final String line) {
		final ISkdTag tag = this.lastTagOnLevel.get(this.indentCount - 1);
		final String content = tag.getContent().orElse("");
		String newCont = content + (content.isEmpty() ? "" : "\n") + line;

		while (newCont.startsWith("\t")) {
			newCont = newCont.substring(1);
		}

		if (newCont.isEmpty()) {
			return;
		}

		tag.setContent(newCont);
		SkdApi.get().api().tagCallback(tag);
		this.tryAccept(ISkdTag.class, tag).orElse(tag);
	}

	private void parseVoidTag(final String line) {
		final String tagLine = line.substring(TAG_START.length(),
				line.length() - VOID_TAG_END.length());
		final String tagName = tagLine.substring(0, tagLine.indexOf(" "));
		String propLine = line.substring(tagName.length() + TAG_START.length());

		while (propLine.startsWith(" ")) {
			propLine = propLine.substring(1);
		}

		if (propLine.endsWith(VOID_TAG_END)) {
			propLine = propLine.substring(0, propLine.length() - VOID_TAG_END.length());
		}

		if (propLine.endsWith(TAG_END)) {
			propLine = propLine.substring(0, propLine.length() - TAG_END.length());
		}

		ISkdTag tag = SkdApi.get().api().tag(tagName);

		this.parseProperties(propLine).stream().forEach(tag::addProperty);

		tag.setVoidElement();
		tag.close();

		SkdApi.get().api().tagCallback(tag);
		tag = this.tryAccept(ISkdTag.class, tag).orElse(tag);

		if (this.indentCount == 0) {
			this.structure.mainTags().add(tag);
			return;
		}

		final int indent = this.indentCount - 1;
		ISkdTag parent = this.lastTagOnLevel.get(indent);

		if (parent == null) {
			throw new RuntimeException("No parent tag found: invalid indentation");
		}

		if (parent.closed()) {
			throw new RuntimeException("Parent tag already closed");
		}

		parent.addChildTag(tag);

		SkdApi.get().api().tagCallback(parent);
		parent = this.tryAccept(ISkdTag.class, parent).orElse(parent);

		this.lastTagOnLevel.put(indent, parent);
	}

	private void parseClosingTag(final String line) {
		if (line.contains(" ")) {
			throw new RuntimeException("Spaces not allowed in closing tags");
		}

		if (this.lastTagOnLevel.get(this.indentCount) == null) {
			throw new RuntimeException("Tag closed without being opened");
		}

		final String name = line.substring(CLOSING_TAG_START.length(),
				line.length() - TAG_END.length());
		final ISkdTag original = this.lastTagOnLevel.get(this.indentCount);

		if (!name.equals(original.getName())) {
			throw new RuntimeException("Tag closed without being opened");
		}

		original.close();
		this.tryAccept(ISkdTag.class, original).orElse(original);

		if (this.indentCount == 0) {
			this.structure.mainTags().add(original);
		}
	}

	private void parseOpeningTag(final String line) {
		final String tagLine = line.substring(TAG_START.length(),
				line.length() - TAG_END.length());
		final String tagName = tagLine.contains(" ")
				? tagLine.substring(0, tagLine.indexOf(' '))
				: tagLine;
		String propLine = line.substring(tagName.length()
				+ TAG_START.length() + TAG_END.length());

		while (propLine.startsWith(" ")) {
			propLine = propLine.substring(1);
		}

		if (propLine.endsWith(TAG_END)) {
			propLine = propLine.substring(0, propLine.length() - TAG_END.length());
		}

		ISkdTag tag = SkdApi.get().api().tag(tagName);

		this.parseProperties(propLine).stream().forEach(tag::addProperty);

		SkdApi.get().api().tagCallback(tag);
		tag = this.tryAccept(ISkdTag.class, tag).orElse(tag);

		this.lastTagOnLevel.put(this.indentCount, tag);

		if (this.indentCount == 0) {
			return;
		}

		final int indent = this.indentCount - 1;
		ISkdTag parent = this.lastTagOnLevel.get(indent);

		if (parent == null) {
			throw new RuntimeException("No parent tag found: invalid indentation");
		}

		if (parent.closed()) {
			throw new RuntimeException("Parent tag already closed");
		}

		parent.addChildTag(tag);

		SkdApi.get().api().tagCallback(parent);
		parent = this.tryAccept(ISkdTag.class, parent).orElse(parent);

		this.lastTagOnLevel.put(indent, parent);
	}

	private Collection<ISkdProperty> parseProperties(final String propLine) {
		final List<String> strings = this.getAsList(propLine);

		if (strings.isEmpty()) {
			return Lists.newArrayList();
		}

		final List<Pair<String, String>> pairs = Lists.newArrayList();

		if (strings.size() % 2 == 1) { //Odd size
			throw new RuntimeException("Invalid property specifications");
		}

		for (int i = 0; i < strings.size(); ++i) {
			final String key = strings.get(i);
			++i;
			final String value = strings.get(i);

			pairs.add(Pair.of(key, value));
		}

		final List<ISkdProperty> props = Lists.newArrayList();
		pairs.stream()
				.filter(this::isValidPair)
				.map(this::parseProperty)
				.forEach(props::add);
		return props;
	}

	private List<String> getAsList(final String propLine) {
		final List<String> propertiesRaw = Lists.newArrayList();
		String word = "";
		boolean inQuotes = false;

		for (final char c : propLine.toCharArray()) {
			if (inQuotes) {
				if (c == '"') {
					inQuotes = false;
				}
				word += c;
				continue;
			}
			if (c == '"') {
				inQuotes = true;
				continue;
			}
			if (c == ' ' || c == '=') {
				propertiesRaw.add(word);
				word = "";
				continue;
			}
			word += c;
		}

		if (!word.isEmpty()) {
			propertiesRaw.add(word);
		}

		final List<String> properties = Lists.newArrayList();

		propertiesRaw.stream()
				.forEach(element -> {
					if (element.startsWith("\"")) {
						element = element.substring(1);
					}
					if (element.endsWith("\"")) {
						element = element.substring(0,
								element.length() - 1);
					}
					properties.add(element);
				});

		return properties;
	}

	@Contract(value = "null -> false; !null -> _")
	private boolean isValidPair(final Pair<String, String> pair) {
		return pair != null
				&& pair.getLeft() != null
				&& pair.getRight() != null
				&& !pair.getLeft().isEmpty();
	}

	private ISkdProperty parseProperty(final Pair<String, String> property) {
		SkdApi.get().api().logger().debug("Found property " + property);
		final String key = property.getLeft();
		final String value = property.getRight();
		if (key == null) {
			throw new RuntimeException("Impossible to set property with \"null\" key");
		}
		if (value == null) {
			// Just for 1 CHARACTER!!! #RageQuit
			throw new RuntimeException(
					"Impossible to set property with \"null\" value");
		}
		if (key.isEmpty()) {
			throw new RuntimeException("Impossible to set property with empty key");
		}

		ISkdProperty prop = SkdApi.get().api().property(key, value);
		SkdApi.get().api().propertyCallback(prop);

		prop = this.tryAccept(ISkdProperty.class, prop).orElse(prop);

		return prop;
	}

	@Override
	public boolean write(@Nonnull final IDatabase database,
	                     @Nonnull final IDatabaseHolder holder) {
		if (!holder.writable()) {
			return false;
		}

		SkdApi.get().api().logger().info("Writing database to database holder");

		final Optional<BufferedWriter> optionallyOut = holder.writerStream();

		if (!optionallyOut.isPresent()) {
			throw new RuntimeException("Implementation says it is writable "
					+ "but doesn't provide a valid writer stream.");
		}

		final BufferedWriter unwrappedOut = optionallyOut.get();
		// Let's wrap it into a PrintWriter for ease of use
		final PrintWriter out = new PrintWriter(unwrappedOut);
		//out.println(database.toString()); //TODO Which one?
		out.print(database.toString()); //TODO Which one?
		out.flush();
		out.close();

		SkdApi.get().api().logger().info("Write completed");

		return true;
	}

	@Nonnull
	@Override
	public Optional<String> getDatabaseName() {
		return this.databaseHolder().name();
	}

	@Nonnull
	@Override
	public IDatabaseHolder databaseHolder() {
		return this.currentDatabaseHolder;
	}

	/**
	 * Test method.
	 *
	 * @param args
	 *      The arguments.
	 *
	 * @since 0.2.1
	 */
	@SuppressWarnings("MagicNumber")
	public static void main(final String... args) { //Move to JUnit
		final java.io.File exampleFile = new java.io.File(SkdParser.class.getResource(
				"/assets/skd_interpreter/examples/DatabaseExample.skd"
		).getFile());
		final IDatabaseHolder db = SkdApi.get().api().databaseHolder(exampleFile);
		//final ISkdParser parser = SkdApi.get().api().parser(db);
		final ISkdParser parser = NewSkdParser.get(db);
		final IDatabase database = parser.read();

		System.out.println(database.toString());

		try {
			Thread.sleep(200);
		} catch (final Exception exc) {
			//NO-OP
		}

		final java.io.File outputFile = new java.io.File(System.getProperty("user.dir"),
				"temp.skd");

		System.out.println(outputFile.getPath());

		try {
			if (!outputFile.createNewFile()) {
				throw new java.io.IOException();
			}
		} catch (final java.io.IOException ex) {
			ex.printStackTrace();
		}

		System.out.println(parser.write(database, SkdApi.get().api()
				.databaseHolder(outputFile)));

		try {
			System.out.println("Waiting...");
			Thread.sleep(1000);
		} catch (final Exception ex) {
			//NO-OP
		}

		try (final BufferedReader reader = new BufferedReader(
				new java.io.FileReader(outputFile))) {
			reader.lines().forEach(System.out::println);

			if (!outputFile.delete()) {
				outputFile.deleteOnExit();
			}
		} catch (final java.io.IOException ex) {
			SkdApi.get().api().logger().stacktrace(ex);
		}
	}
}
