package net.thesilkminer.skl.interpreter.implementation.skd;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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

// Implementation imports: bad practice, but oh well!
import net.thesilkminer.skl.interpreter.implementation.skd.structure.declarations.DatabaseVersion;
import net.thesilkminer.skl.interpreter.implementation.skd.structure.declarations.DocType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 * This class is the main SKD parser.
 *
 * <p>This class's scope is parse a specified database
 * and construct an object representation of it.</p>
 *
 * @author TheSilkMiner
 *
 * @since 0.2
 */
public class SkdParser implements ISkdParser {

	private static final Map<IDatabaseHolder, SkdParser> MAP = Maps.newHashMap();

	// Mainly done due to avoid hacks and direct registrations.
	private static final List<Class<? extends IDeclaration>> DECLARATIONS =
			      Lists.newArrayList();
	private static final Map<Class<? extends IDeclaration>, IDeclaration> DEF_OBJ =
			      Maps.newHashMap();

	private final IDatabaseHolder databaseFile;
	private final Map<Integer, ISkdTag> lastTagOnLevel;

	private boolean init;
	private boolean error;
	private BufferedReader reader;

	private IStructure struct;
	private List<ISkdTag> mainTags;
	private IDocTypeDeclaration docType;
	private IDatabaseVersionDeclaration version;

	private SkdParser(final IDatabaseHolder databaseFile) {

		this.databaseFile = databaseFile;
		this.lastTagOnLevel = Maps.newHashMap();

		this.init = false;
		this.error = false;
		this.reader = null;

		this.struct = SkdApi.get().api().structure(Lists.newArrayList());
		this.mainTags = Lists.newArrayList();
		this.docType = null;
		this.version = null;
	}

	static {

		declaration(IDocTypeDeclaration.class, DocType.dummy());
		declaration(IDatabaseVersionDeclaration.class, DatabaseVersion.dummy());
	}

	/**
	 * Creates a new Parser for the specified database file.
	 *
	 * <p>If a parser for the file has already been created,
	 * that one is returned instead.</p>
	 *
	 * @param file
	 * 		The IDatabaseHolder you need to create the parser for.
	 * @return
	 * 		A new SkdParser.
	 */
	public static SkdParser of(@Nonnull final IDatabaseHolder file) {

		Preconditions.checkNotNull(file, "IDatabaseHolder must not be null");

		if (MAP.containsKey(file) && MAP.get(file) != null) {

			return MAP.get(file);
		}

		if (MAP.containsKey(file)) {

			MAP.remove(file);
		}

		final SkdParser parser = new SkdParser(file);
		MAP.put(file, parser);
		return parser;
	}

	private static void declaration(final Class<? extends IDeclaration> declaration,
					             final IDeclaration defaultImplementation) {

		if (DECLARATIONS.contains(declaration)
				      || DEF_OBJ.containsValue(defaultImplementation)) {

			throw new RuntimeException("Declaration registered twice");
		}

		DECLARATIONS.add(declaration);
		DEF_OBJ.put(declaration, defaultImplementation);
	}

	@Override
	public void init(final boolean force) {

		if (this.init()) {

			throw new IllegalStateException("Parser has already been initialized");
		}

		SkdApi.get().api().logger().info("Initializing database parser");

		if (!(this.databaseFile instanceof DatabaseFile)) {

			SkdApi.get().api().logger().error("Supplied file must be an instance of "
					      + "DatabaseFile to be parsed");
			return;
		}

		try {

			this.checkFile(force);

			this.reader = new BufferedReader(
					new FileReader((DatabaseFile) this.databaseFile));
		} catch (final Throwable thr) {

			SkdApi.get().api().logger().stacktrace(thr);
			this.error = true;
		}

		if (this.errored()) {

			return;
		}

		this.init = true;
	}

	@Override
	public boolean init() {

		return this.init;
	}

	private void checkFile(final boolean force) {

		if (this.databaseFile == null) {

			throw new IllegalStateException("File was null");
		}

		SkdApi.get().api().logger().info("Checking file");

		if (!((DatabaseFile) this.databaseFile).getFileExtension()
				      .equalsIgnoreCase("skd")) {

			if (!force) {

				SkdApi.get().api().logger()
						.error("File specified does not end with .skd");
				SkdApi.get().api().logger().error("Aborting process");

				throw new IllegalStateException("File must end with .skd to be "
						+ "able to be parsed");
			}

			SkdApi.get().api().logger().warn("File specified does not end with .skd");
			SkdApi.get().api().logger().warn("Forced to accept it...");
		}
	}

	@Override
	public boolean errored() {

		return this.error;
	}

	@Override
	public IDatabase read() {

		if (!this.init() || this.errored()) {

			throw new IllegalStateException();
		}

		try {
			this.reader.lines().forEach(line -> {
				if (!this.parse(line)) {
					throw new IllegalDatabaseSyntaxException();
				}
			});
		} catch (final IllegalDatabaseSyntaxException ex) {
			throw ex;
		} catch (final Exception ex) {
			SkdApi.get().api().logger().stacktrace(ex);
		}

		if (!this.mainTags.isEmpty()) {

			this.struct.mainTags(mainTags);
		}

		if (this.docType == null || this.version == null) {

			throw new IllegalDatabaseSyntaxException();
		}

		return SkdApi.get().api().database(this.docType, this.version, this.struct);
	}

	private boolean parse(final String line) {

		final int indentationLevel = this.countIndentationLevel(line);

		if (indentationLevel != 0 && line.charAt(indentationLevel) != '<') {

			final ISkdTag tag = this.lastTagOnLevel.get(indentationLevel - 1);
			final Optional<String> content = tag.getContent();
			String newContent;

			if (content.isPresent()) {

				newContent = content.get()
						+ "\n"
						+ line.substring(indentationLevel);
			} else {

				newContent = line.substring(indentationLevel);
			}

			while (newContent.startsWith("\t")) {

				newContent = newContent.substring(1);
			}

			if (newContent.isEmpty()) {

				return true;
			}

			tag.setContent(Optional.of(newContent));

			return true;
		}

		final String ln = line.substring(indentationLevel);

		if (ln.startsWith("\t")) {

			throw new RuntimeException("Tab not stripped when it should. "
					+ "This is a serious bug! Report it to TheSilkMiner");
		}

		boolean closingTag = ln.startsWith("</");

		if (!closingTag && this.lastTagOnLevel.get(indentationLevel) != null) {

			// Tag not closed before declaration
			throw new IllegalDatabaseSyntaxException();
		}

		if (closingTag) {

			return this.handleClosingTag(ln, indentationLevel);
		}

		if (!ln.startsWith("<")
				      && indentationLevel != 0
				      && this.lastTagOnLevel.get(indentationLevel - 1) == null) {

			throw new IllegalDatabaseSyntaxException(); // Incorrect content indentation
		}

		return ln.isEmpty() || this.handleTag(ln, indentationLevel);
	}

	private int countIndentationLevel(final String line) {

		int ind = 0;

		for (final char c : line.toCharArray()) {

			if (c == '\t') {

				++ind;
				continue;
			}

			break;
		}

		return ind;
	}

	private boolean handleClosingTag(final String strippedLine, final int indentation) {

		final String tagName = strippedLine.substring(2, strippedLine.length() - 1);

		if (tagName.contains(" ")) {

			throw new IllegalDatabaseSyntaxException(); // Ending tags cannot have props
		}

		if (this.lastTagOnLevel.get(indentation) == null) {

			throw new IllegalDatabaseSyntaxException(); // Tag closed without being open
		}

		this.lastTagOnLevel.put(indentation,
				SkdApi.get().api().tagCallback(
						this.lastTagOnLevel.get(indentation)));

		this.lastTagOnLevel.put(indentation, null);

		if (this.lastTagOnLevel.get(indentation + 1) != null) {

			// Parent tag closed before children
			throw new IllegalDatabaseSyntaxException();
		}

		return true;
	}

	private boolean handleTag(final String strippedLine, final int currentIndentation) {

		if (!strippedLine.startsWith("<") && strippedLine.endsWith(">")) {

			throw new IllegalDatabaseSyntaxException(); // Wrong line
		}

		final boolean emptyTag = strippedLine.endsWith("/>");
		final String line = strippedLine.substring(1,
				      strippedLine.length() - (emptyTag ? 2 : 1));

		if (this.checkForDeclarations(line, currentIndentation)) {

			return true;
		}

		final List<String> parts = Lists.newArrayList();
		boolean inQuotes = false;
		String word = "";

		for (final char character : line.toCharArray()) {
			if (inQuotes) {
				if (character == '"') {
					inQuotes = false;
				}
				word += character;
				continue;
			}
			if (character == '"') {
				inQuotes = true;
				word += character;
				continue;
			}
			if (character == ' ') {
				parts.add(word);
				word = "";
				continue;
			}
			word += character;
		}

		for (final char c : word.toCharArray()) {
			if (c != ' ') {
				parts.add(word);
				break;
			}
		}

		final List<ISkdProperty> properties = Lists.newArrayList();
		//final ISkdTag tag = SkdApi.get().api().tag(parts.get(0));

		for (final String part : parts) {

			if (part.equals(parts.get(0))) {

				// Tag name
				continue;
			}

			final String[] pair = part.split("=");

			if (pair[0].endsWith("=")) {

				pair[0] = pair[0].substring(0, pair[0].length() - 1);
			}

			if (pair[1].startsWith("=")) {

				pair[1] = pair[1].substring(1);
			}

			// Check for 2 because if it is less than 2 it would have already failed
			if (pair.length != 2) {

				for (int i = 2; i < pair.length; ++i) {

					pair[1] += pair[i];
				}
			}

			pair[1] = pair[1].substring(1, pair[1].length() - 1); // Remove quotes

			properties.add(SkdApi.get().api().propertyCallback(
					SkdApi.get().api().property(pair[0], pair[1])));
		}

		final ISkdTag tag = SkdApi.get().api().tag(parts.get(0));
		properties.stream().forEach(tag::addProperty);

		if (currentIndentation == 0) {

			this.mainTags.add(tag);
		}

		if (currentIndentation > 0) {

			this.lastTagOnLevel.get(currentIndentation - 1).addChildTag(tag);
		}

		if (!emptyTag) {

			this.lastTagOnLevel.put(currentIndentation, tag);
		} else {

			tag.setVoidElement();
		}

		return true;
	}

	private boolean checkForDeclarations(final String strippedLine,
							         final int currentIndentation) {

		if (currentIndentation != 0) {

			return false; // Declaration must be at level 0
		}

		for (Class<? extends IDeclaration> declaration : DECLARATIONS) {

			final IDeclaration mapObject = DEF_OBJ.get(declaration);

			if (mapObject == null) {

				throw new RuntimeException("Illegal declaration registration");
			}

			if (!strippedLine.startsWith(mapObject.getDeclarationName())) {

				continue;
			}

			final String syntaxRaw = mapObject.getDeclarationSyntax()
					      .replaceAll("<([A-Za-z0-9_])+>", "%s");

			final String[] wordsLine = strippedLine.split(" ");
			final String[] wordsSyntax = syntaxRaw.split(" ");
			final String[] line = new String[wordsSyntax.length];

			if (wordsLine.length != wordsSyntax.length) {

				continue;
			}

			for (int i = 0; i < wordsSyntax.length; ++i) {

				if (wordsSyntax[i].equals("%s")) {

					line[i] = wordsLine[i];
					continue;
				}

				if (!wordsSyntax[i].equals(wordsLine[i])) {

					break; // or throw?
				}

				line[i] = wordsSyntax[i];
			}

			final StringBuilder buf = new StringBuilder();
			Arrays.stream(line).forEach(it -> {
				buf.append(it);
				buf.append(" ");
			});
			final String ln = buf.toString().substring(0, buf.toString().length() - 1);

			this.handleDeclaration(ln);

			return true;
		}

		return false;
	}

	private void handleDeclaration(final String line) {

		if (line.startsWith(DEF_OBJ.get(IDocTypeDeclaration.class).getDeclarationName())) {

			final String split = line.split(" ")[2];
			final IDocTypeDeclaration docType = SkdApi.get().api().doctype(split);

			if (!docType.validate()) {

				SkdApi.get().api().logger().warn("Invalid doctype declaration.");
				SkdApi.get().api().logger().warn("Accepting it anyway.");
				SkdApi.get().api().logger().warn("Warning! This may change in "
						      + "the future. Please fix the declaration!");
			}

			if (this.docType != null) {

				SkdApi.get().api().logger().severe("DocType already defined");
				throw new IllegalDatabaseSyntaxException(); // ?
			}

			this.docType = docType;

			return;
		}

		if (line.startsWith(DEF_OBJ.get(IDatabaseVersionDeclaration.class)
				      .getDeclarationName())) {

			final String split = line.split(" ")[2];
			final IDatabaseVersionDeclaration version = SkdApi.get().api().version(
					       CURRENT_VERSION.equals(split) ? null : split
			);

			if (this.version != null) {

				SkdApi.get().api().logger().severe("Version already defined");
				throw new IllegalDatabaseSyntaxException(); // ?
			}

			this.version = version;
		}
	}

	@Override
	public boolean write(final IDatabase database, final IDatabaseHolder holder) {

		Preconditions.checkArgument(holder.writable(), "IDatabaseHolder must be writable");
		Preconditions.checkArgument(holder instanceof DatabaseFile,
				"Only DatabaseFile is supported");

		final DatabaseFile file = (DatabaseFile) holder;

		try {

			this.write(database, file);
			return true;
		} catch (final IOException ex) {

			return false;
		}
	}

	private void write(final IDatabase database, final DatabaseFile file) throws IOException {

		// Yeah, well.
		// Just a toString() call.
		// LOL
		final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		out.println(database.toString());
		out.close();
	}

	@Override
	public Optional<String> getDatabaseName() {

		return this.databaseHolder().name();
	}

	@Override
	public IDatabaseHolder databaseHolder() {

		return this.databaseFile;
	}

	/**
	 * Test method.
	 *
	 * @param args
	 * 		The arguments.
	 *
	 * @since 0.2
	 */
	public static void main(final String... args) {

		final java.io.File exampleFile = new java.io.File(SkdParser.class.getResource(
				      "/assets/skd_interpreter/examples/DatabaseExample.skd"
			).getFile()
		);
		final IDatabaseHolder db = SkdApi.get().api().databaseHolder(exampleFile);
		final ISkdParser parser = SkdApi.get().api().parser(db);

		parser.init(false);

		final IDatabase database = parser.read();

		System.out.println(database.toString());

		final java.io.File outputFile = new java.io.File(System.getProperty("user.dir"),
						"temp.skd");
		try {

			if (!outputFile.createNewFile()) {

				throw new java.io.IOException();
			}
		} catch (final java.io.IOException ex) {

			ex.printStackTrace();
		}

		System.out.println(parser.write(database, SkdApi.get().api()
				.databaseHolder(outputFile)));

		try (final BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {

			reader.lines().forEach(System.out::println);

			if (!outputFile.delete()) {

				outputFile.deleteOnExit();
			}
		} catch (final IOException ex) {

			SkdApi.get().api().logger().stacktrace(ex);
		}
	}
}
