package net.thesilkminer.skl.interpreter.implementation.sks.listeners.java;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.thesilkminer.skl.interpreter.api.sks.holder.IScriptHolder;
import net.thesilkminer.skl.interpreter.api.sks.listener.IScriptListener;
import net.thesilkminer.skl.interpreter.api.sks.listener.Result;
import net.thesilkminer.skl.interpreter.api.sks.parser.ISksParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.ToolProvider;

/**
 * Represents the main Java listener, provided by default from the
 * SKS parser.
 *
 * @author TheSilkMIner
 *
 * @since 0.1
 */
public class JavaMainListener implements IScriptListener {

	private static class SourceObject extends SimpleJavaFileObject {

		private final String code;

		private SourceObject(final String className, final String contents) {

			super(URI.create(
					"string:///"
					+ className.replace('.', '/')
					+ Kind.SOURCE.extension), Kind.SOURCE);
			this.code = contents;
		}

		@Override
		public CharSequence getCharContent(final boolean ignoreEncodingErrors)
				throws IOException {

			return this.code;
		}
	}

	private static class ClassObject extends SimpleJavaFileObject {

		private final ByteArrayOutputStream byteArrayOutputStream;

		private ClassObject(final String className) throws URISyntaxException {

			super(new URI(className), Kind.CLASS);
			this.byteArrayOutputStream = new ByteArrayOutputStream();
		}

		@Override
		public OutputStream openOutputStream() throws IOException {

			return this.byteArrayOutputStream;
		}

		private byte[] getByteCode() {

			return this.byteArrayOutputStream.toByteArray();
		}
	}

	private static class DynamicClassLoader extends ClassLoader {

		private final Map<String, ClassObject> customClass = Maps.newHashMap();

		private DynamicClassLoader(final ClassLoader parent) {

			super(parent);
		}

		public void setClass(final ClassObject clazz) {

			this.customClass.put(clazz.getName(), clazz);
		}

		@Override
		protected Class<?> findClass(final String name) throws ClassNotFoundException {

			final ClassObject clazz = this.customClass.get(name);

			if (clazz == null) {

				return super.findClass(name);
			}

			final byte[] byteCode = clazz.getByteCode();

			return this.defineClass(name, byteCode, 0, byteCode.length);
		}
	}

	private static class ExtendedFileManager
			      extends ForwardingJavaFileManager<JavaFileManager> {

		private final ClassObject clazz;
		private final DynamicClassLoader classLoader;

		private ExtendedFileManager(final JavaFileManager manager,
									  final ClassObject clazz,
									  final DynamicClassLoader
									    classLoader) {

			super(manager);

			this.clazz = clazz;
			this.classLoader = classLoader;
			this.classLoader.setClass(this.clazz);
		}

		@Override
		@SuppressWarnings("all")
		public JavaFileObject getJavaFileForOutput(final Location location,
												   final String className,
												   final JavaFileObject.Kind kind,
												   final FileObject sibling)
				throws IOException {

			return this.clazz;
		}

		@Override
		public ClassLoader getClassLoader(final Location location) {

			return this.classLoader;
		}
	}

	private List<String> logs;
	private String className;
	private Result result;

	@Override
	public String listenerFor() {

		return "java";
	}

	@Override
	public boolean needsInit() {

		return true;
	}

	@Override
	public boolean hasAlreadyInit() {

		return false;
	}

	@Override
	public void init(final ISksParser parser, final IScriptHolder scriptFile) {

		this.logs = Lists.newArrayList();
		this.className = parser.getScriptName();
		this.result = Result.ERRORED;
	}

	@Override
	public void runScript(final List<String> lines) {

		try {

			Class.forName("javax.tools.JavaFileObject");
			Class.forName("javax.tools.ForwardingJavaFileManager");
			Class.forName("javax.tools.JavaCompiler");
			Class.forName("javax.tools.JavaFileManager");
			Class.forName("javax.tools.JavaFileObject");
			Class.forName("javax.tools.SimpleJavaFileObject");
			Class.forName("javax.tools.ToolProvider");
		} catch (final ClassNotFoundException e) {

			this.logs.add("The interpreter was not able to find the compiler class");
			this.logs.add("Make sure you are using the JDK, not only the JRE");
			this.logs.add("Then run again the Java interpreter");
			return;
		}

		String sourceCode = "";
		for (final String line : lines) {

			sourceCode += line;
		}

		final SourceObject source = new SourceObject(this.className, sourceCode);
		ClassObject clazz;

		try {

			clazz = new ClassObject(this.className);
		} catch (URISyntaxException e) {

			clazz = null;
			Throwables.propagate(e);
		}

		if (clazz == null) {

			this.logs.add("The interpreter was not able to compile the class");
			this.logs.add("Error: URI not valid");
			return;
		}

		@SuppressWarnings("ArraysAsListWithZeroOrOneArgument")
		final Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(source);

		final DynamicClassLoader classLoader;
		classLoader = new DynamicClassLoader(ClassLoader.getSystemClassLoader());

		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		final ExtendedFileManager efm = new ExtendedFileManager(
				      compiler.getStandardFileManager(null, null, null),
				      clazz, classLoader);

		final JavaCompiler.CompilationTask task = compiler.getTask(
				      null, efm, null, null, null, compilationUnits);

		final boolean result = task.call();

		if (!result) {

			this.logs.add("The interpreter was not able to compile the class");
			this.logs.add("Error: Compilation has failed");
			return;
		}

		final Class<?> compiledClass;

		try {

			compiledClass = classLoader.loadClass(className);
			final Method main = compiledClass.getMethod("main", String[].class);
			main.setAccessible(true);
			main.invoke(null, (Object[]) new String[1]);

		} catch (final ClassNotFoundException e) {

			this.logs.add("The interpreter was not able to compile the class");
			this.logs.add("Error: Class hasn't been found");
			return;
		} catch (final NoSuchMethodException e) {

			this.logs.add("The interpreter was not able to compile the class");
			this.logs.add("Error: \"main\" method not found");
			return;
		} catch (final IllegalAccessException | InvocationTargetException e) {

			this.logs.add("The interpreter was not able to compile the class");
			this.logs.add("Error: Unable to access \"main\" method");
			return;
		}

		this.result = Result.SUCCESSFUL;

		try {

			Thread.sleep(1000);
		} catch (final InterruptedException e) {

			this.logs.add("Sleep interrupted. Ah well.");
		}
	}

	@Override
	public Result result() {

		return this.result;
	}

	@Override
	public Optional<List<String>> toLog() {

		if (this.logs != null && !this.logs.isEmpty()) {

			return Optional.of(this.logs);
		}

		return Optional.empty();
	}
}
