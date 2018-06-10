package info.bliki.wiki.template;

import java.io.IOException;
import java.util.List;

import info.bliki.util.Throwables;
import info.bliki.wiki.filter.TemplateParser;
import info.bliki.wiki.model.IWikiModel;
import lombok.extern.slf4j.Slf4j;

/**
 * An abstract template parser function.
 *
 */
@Slf4j
public abstract class AbstractTemplateFunction implements ITemplateFunction {

	@Override
	public String getFunctionDoc() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract String parseFunction(List<String> parts, IWikiModel model, char[] src, int beginIndex, int endIndex,
			boolean isSubst) throws IOException;

	/**
	 * Parse the given content string with the template parser.
	 *
	 * @param content
	 *            the raw content string
	 * @param model
	 *            the wiki model
	 * @return
	 */
	public static String parse(final String content, final IWikiModel model) {
		if (content == null || content.length() == 0) {
			return "";
		}
		final StringBuilder buf = new StringBuilder(content.length());
		try {
			TemplateParser.parse(content, model, buf, false);
		} catch (final IOException e) {
			Throwables.log(log, e);
		}
		return buf.toString();
	}

	/**
	 * Parse the given content string with the template parser and <code>trim()</code> the resulting string.
	 *
	 * @param content
	 *            the raw content string
	 * @param model
	 *            the wiki model
	 * @return
	 */
	public static String parseTrim(final String content, final IWikiModel model) {
		return parse(content, model).trim();
	}
}
