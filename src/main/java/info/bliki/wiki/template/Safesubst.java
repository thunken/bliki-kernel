package info.bliki.wiki.template;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import info.bliki.htmlcleaner.Utils;
import info.bliki.util.Throwables;
import info.bliki.wiki.filter.ParsedPageName;
import info.bliki.wiki.filter.TemplateParser;
import info.bliki.wiki.filter.Util;
import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.model.WikiModelContentException;
import info.bliki.wiki.namespaces.INamespace;
import lombok.extern.slf4j.Slf4j;

/**
 * A template parser function for <code>{{safesubst: ... }}</code>. See
 * <a href="https://en.wikipedia.org/wiki/en:Help:Substitution#safesubst:">Wikipedia-Help:Substitution</a>
 */
@Slf4j
public class Safesubst extends AbstractTemplateFunction {
	public final static ITemplateFunction CONST = new Safesubst();

	@Override
	public String parseFunction(final List<String> parts1, final IWikiModel model, final char[] src,
			final int beginIndex, final int endIndex, final boolean isSubst) {
		final String substArg = new String(src, beginIndex, endIndex - beginIndex);
		final String substituted = Safesubst.parsePreprocess(substArg, model, null);
		final char[] src2 = substituted.toCharArray();

		final Object[] objs = TemplateParser.createParameterMap(src2, 0, src2.length);
		@SuppressWarnings("unchecked")
		final List<String> parts = (List<String>) objs[0];
		final String templateName = (String) objs[1];

		final int currOffset = TemplateParser.checkParserFunction(substituted);
		if (currOffset > 0) {
			final String function = substituted.substring(0, currOffset - 1).trim();
			final ITemplateFunction templateFunction = model.getTemplateFunction(function);
			if (templateFunction != null) {
				parts.set(0, templateName.substring(currOffset));
				String plainContent;
				try {
					plainContent = templateFunction.parseFunction(parts, model, src2, currOffset, src2.length, isSubst);
					if (plainContent != null) {
						return plainContent;
					}
				} catch (final IOException ignored) {
				}
			}
			return "";
		}

		final Map<String, String> parameterMap = TemplateParser.createParameterMap(objs, model);

		final INamespace namespace = model.getNamespace();
		// TODO: remove trailing "#section"?!
		final ParsedPageName parsedPagename = ParsedPageName.parsePageName(model, templateName, namespace.getTemplate(),
				true, false);
		if (!parsedPagename.valid) {
			return "{{" + parsedPagename.pagename + "}}";
		}

		String plainContent = null;
		try {
			plainContent = model.getRawWikiContent(parsedPagename, parameterMap);
		} catch (final WikiModelContentException e) {
		}
		if (plainContent != null) {
			return Safesubst.parsePreprocess(plainContent, model, parameterMap);
		}
		return "";
	}

	/**
	 * Parse the preprocess step for the given content string with the template parser and
	 * <code>Utils#trimNewlineLeft()</code> the resulting string.
	 *
	 * @param content
	 * @param model
	 * @return parsed content
	 */
	public static String parsePreprocess(final String content, final IWikiModel model,
			final Map<String, String> templateParameterMap) {
		if (content == null || content.length() == 0) {
			return "";
		}
		final int startIndex = Util.indexOfTemplateParsing(content);
		if (startIndex < 0) {
			return Utils.trimNewlineLeft(content);
		}
		final StringBuilder buf = new StringBuilder(content.length());
		try {
			TemplateParser.parsePreprocessRecursive(startIndex, content, model, buf, false, false,
					templateParameterMap);
		} catch (final IOException e) {
			Throwables.log(log, e);
		}
		return Utils.trimNewlineLeft(buf.toString());
	}
}
