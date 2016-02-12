package net.thesilkminer.skl.interpreter.sks.listeners.java;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import net.thesilkminer.skl.interpreter.sks.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
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
 * Created by TheSilkMiner on 14/09/2015.
 * Package: net.thesilkminer.skl.interpreter.sks.listeners.java.
 * Project: Java Interpreter.
 */
public class JavaMainListener implements IScriptListener {

	private static class SourceObject extends SimpleJavaFileObject {

		private String code;

		public SourceObject(String className, String contents) {

			super(URI.create(
					"string:///"
					+ className.replace('.', '/')
					+ Kind.SOURCE.extension), Kind.SOURCE);
			this.code = contents;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors)
				throws IOException {

			return this.code;
		}
	}

	private static class ClassObject extends SimpleJavaFileObject {

		private ByteArrayOutputStream byteArrayOutputStream;

		public ClassObject(String className) throws URISyntaxException {

			super(new URI(className), Kind.CLASS);
			this.byteArrayOutputStream = new ByteArrayOutputStream();
		}

		@Override
		public OutputStream openOutputStream() throws IOException {

			return this.byteArrayOutputStream;
		}

		public byte[] getByteCode() {

			return this.byteArrayOutputStream.toByteArray();
		}
	}

	private static class DynamicClassLoader extends ClassLoader {

		private Map<String, ClassObject> customClass = new HashMap<>();

		public DynamicClassLoader(ClassLoader parent) {

			super(parent);
		}

		public void setClass(ClassObject clazz) {

			this.customClass.put(clazz.getName(), clazz);
		}

		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {

			ClassObject clazz = this.customClass.get(name);

			if (clazz == null) {

				return super.findClass(name);
			}

			byte[] byteCode = clazz.getByteCode();

			return this.defineClass(name, byteCode, 0, byteCode.length);
		}
	}

	private static class ExtendedFileManager
			      extends ForwardingJavaFileManager<JavaFileManager> {

		private ClassObject clazz;
		private DynamicClassLoader classLoader;

		protected ExtendedFileManager(JavaFileManager manager,
									  ClassObject clazz,
									  DynamicClassLoader
									    classLoader) {

			super(manager);

			this.clazz = clazz;
			this.classLoader = classLoader;
			this.classLoader.setClass(this.clazz);
		}

		@Override
		@SuppressWarnings("all")
		public JavaFileObject getJavaFileForOutput(Location location,
												   String className,
												   JavaFileObject.Kind kind,
												   FileObject sibling)
				throws IOException {

			return this.clazz;
		}

		@Override
		public ClassLoader getClassLoader(Location location) {

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
	public void init(SksParser parser, IScriptHolder scriptFile) {

		this.logs = Lists.newArrayList();
		this.className = parser.getScriptName();
		this.result = Result.ERRORED;
	}

	@Override
	public void runScript(List<String> lines) {

		try {

			Class.forName("javax.tools.JavaFileObject");
			Class.forName("javax.tools.ForwardingJavaFileManager");
			Class.forName("javax.tools.JavaCompiler");
			Class.forName("javax.tools.JavaFileManager");
			Class.forName("javax.tools.JavaFileObject");
			Class.forName("javax.tools.SimpleJavaFileObject");
			Class.forName("javax.tools.ToolProvider");
		} catch (ClassNotFoundException e) {

			this.logs.add("The interpreter was not able to find the compiler class");
			this.logs.add("Make sure you are using the JDK, not only the JRE");
			this.logs.add("Then run again the Java interpreter");
			return;
		}

		String sourceCode = "";
		for (String line : lines) {

			sourceCode += line;
		}

		SourceObject source = new SourceObject(this.className, sourceCode);
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

		@SuppressWarnings("all")
		Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(source);

		DynamicClassLoader classLoader;
		classLoader = new DynamicClassLoader(ClassLoader.getSystemClassLoader());

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		ExtendedFileManager efm = new ExtendedFileManager(
				      compiler.getStandardFileManager(null, null, null),
				      clazz, classLoader);

		JavaCompiler.CompilationTask task = compiler.getTask(
				      null, efm, null, null, null, compilationUnits);

		boolean result = task.call();

		if (!result) {

			this.logs.add("The interpreter was not able to compile the class");
			this.logs.add("Error: Compilation has failed");
			return;
		}

		Class<?> compiledClass;

		try {

			compiledClass = classLoader.loadClass(className);
			Method main = compiledClass.getMethod("main", String[].class);
			main.setAccessible(true);
			main.invoke(null, (Object[]) new String[1]);

		} catch (ClassNotFoundException e) {

			this.logs.add("The interpreter was not able to compile the class");
			this.logs.add("Error: Class hasn't been found");
			return;
		} catch (NoSuchMethodException e) {

			this.logs.add("The interpreter was not able to compile the class");
			this.logs.add("Error: \"main\" method not found");
			return;
		} catch (IllegalAccessException | InvocationTargetException e) {

			this.logs.add("The interpreter was not able to compile the class");
			this.logs.add("Error: Unable to access \"main\" method");
			return;
		}

		this.result = Result.SUCCESSFUL;

		try {

			Thread.sleep(1000);
		} catch (InterruptedException e) {

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
