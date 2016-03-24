public static void printf(final String msg, final String... arguments) {

	if (arguments != null && arguments.length != 0) {

		System.out.println(String.format(msg, arguments));
		return;
	}
	System.out.println(msg);
}

public static void scanf(final String types, final Object... variables) {

	// Too bad we can't rely on external libraries: it would have been so much easier!
	// Also, Java is pass by value, not pass by reference
	// Probably I need fields names
	java.util.Scanner scanner = new java.util.Scanner(System.in);
	// TODO
}