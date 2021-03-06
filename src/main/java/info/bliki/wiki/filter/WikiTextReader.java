package info.bliki.wiki.filter;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import info.bliki.util.Throwables;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WikiTextReader {
	/**
	 * Constant for an empty char array
	 */
	public static final char[] NO_CHAR = new char[0];

	private static final int DEFAULT_READING_SIZE = 8192;

	private final String fTemplateBaseFilename;

	public WikiTextReader(final String templateBaseFileName) {
		fTemplateBaseFilename = templateBaseFileName;
	}

	public String getPlainContent(final String wikiTitle) {
		final String filename = fTemplateBaseFilename.replace("${title}", wikiTitle);
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filename);
			return new String(getInputStreamAsCharArray(fis, -1, UTF_8));
		} catch (final IOException e) {
			Throwables.log(log, e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (final IOException e) {
				}
			}
		}
		return null;
	}

	/**
	 * Returns the given input stream's contents as a character array. If a length is specified (ie. if length != -1),
	 * only length chars are returned. Otherwise all chars in the stream are returned. Note this doesn't close the
	 * stream.
	 *
	 * @throws IOException
	 *             if a problem occured reading the stream.
	 */
	public static char[] getInputStreamAsCharArray(final InputStream stream, final int length, final Charset charset)
			throws IOException {
		InputStreamReader reader;
		reader = charset == null ? new InputStreamReader(stream) : new InputStreamReader(stream, charset);
		char[] contents;
		if (length == -1) {
			contents = NO_CHAR;
			int contentsLength = 0;
			int amountRead = -1;
			do {
				final int amountRequested = Math.max(stream.available(), DEFAULT_READING_SIZE); // read at least 8K

				// resize contents if needed
				if (contentsLength + amountRequested > contents.length) {
					System.arraycopy(contents, 0, contents = new char[contentsLength + amountRequested], 0,
							contentsLength);
				}

				// read as many chars as possible
				amountRead = reader.read(contents, contentsLength, amountRequested);

				if (amountRead > 0) {
					// remember length of contents
					contentsLength += amountRead;
				}
			} while (amountRead != -1);

			// resize contents if necessary
			if (contentsLength < contents.length) {
				System.arraycopy(contents, 0, contents = new char[contentsLength], 0, contentsLength);
			}
		} else {
			contents = new char[length];
			int len = 0;
			int readSize = 0;
			while (readSize != -1 && len != length) {
				// See PR 1FMS89U
				// We record first the read size. In this case len is the actual
				// read size.
				len += readSize;
				readSize = reader.read(contents, len, length - len);
			}
			// See PR 1FMS89U
			// Now we need to resize in case the default encoding used more than
			// one byte for each
			// character
			if (len != length) {
				System.arraycopy(contents, 0, contents = new char[len], 0, len);
			}
		}

		return contents;
	}
}
