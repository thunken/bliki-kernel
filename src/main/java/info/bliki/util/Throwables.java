package info.bliki.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.slf4j.Logger;

public class Throwables {

	private final static Pattern NEWLINE = Pattern.compile("[\\r\\n]+"), WHITESPACE = Pattern.compile("[\\h\\v]+");

	private Throwables() {
		/* NO OP */
	}

	public static void log(@Nonnull final Logger logger, @Nonnull final Throwable throwable) {
		logger.warn(getStackTraceAsString(throwable));
	}

	private static String getStackTraceAsString(@Nonnull final Throwable throwable) {
		final StringWriter stringWriter = new StringWriter();
		throwable.printStackTrace(new PrintWriter(stringWriter));
		String stackTrace = stringWriter.toString();
		stackTrace = NEWLINE.matcher(stackTrace).replaceAll(", ");
		stackTrace = WHITESPACE.matcher(stackTrace).replaceAll(" ").trim();
		return stackTrace.endsWith(",") ? stackTrace.substring(0, stackTrace.length() - 1) : stackTrace;
	}

}
