package info.bliki.wiki.filter;

import static info.bliki.wiki.filter.AbstractWikipediaParser.getRedirectedTemplateContent;
import static info.bliki.wiki.filter.WikipediaParser.parseRedirect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import info.bliki.util.Throwables;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.tags.TemplateTag;
import info.bliki.wiki.tags.util.WikiTagNode;
import info.bliki.wiki.template.ITemplateFunction;
import info.bliki.wiki.template.Safesubst;
import info.bliki.wiki.template.Subst;
import lombok.extern.slf4j.Slf4j;

/**
 * A template parser for the first pass in the parsing of a Wikipedia text
 *
 * @see WikipediaParser for the second pass
 */
@Slf4j
public class TemplateParser extends AbstractParser {
	public static final String TEMPLATE_PARSER_ERROR = "TemplateParserError";

	private static final Pattern HTML_COMMENT_PATTERN = Pattern.compile("<!--(.*?)-->");
	private static final String SUBST = "subst:";
	private static final String SAFESUBST = "safesubst:";
	private static final int SUBST_LENGTH = SUBST.length();
	private static final int SAFESUBST_LENGTH = SAFESUBST.length();

	private final boolean fParseOnlySignature;
	private final boolean fRenderTemplate;
	private boolean fOnlyIncludeFlag;

	public TemplateParser(final String stringSource) {
		this(stringSource, false, false);
	}

	public TemplateParser(final String stringSource, final boolean parseOnlySignature, final boolean renderTemplate) {
		this(stringSource, parseOnlySignature, renderTemplate, false);
	}

	public TemplateParser(final String stringSource, final boolean parseOnlySignature, final boolean renderTemplate,
			final boolean onlyIncludeFlag) {
		super(stringSource);
		fParseOnlySignature = parseOnlySignature;
		fRenderTemplate = renderTemplate;
		fOnlyIncludeFlag = onlyIncludeFlag;
	}

	public static void parse(final String rawWikitext, final IWikiModel wikiModel, final Appendable writer,
			final boolean renderTemplate) throws IOException {
		parse(rawWikitext, wikiModel, writer, false, renderTemplate);
	}

	/**
	 * Parse the wiki texts templates, comments and signatures into the given <code>StringBuilder</code>.
	 *
	 * @param rawWikitext
	 * @param wikiModel
	 * @param writer
	 * @param parseOnlySignature
	 *            change only the signature string and ignore templates and comments parsing
	 * @param renderTemplate
	 */
	public static void parse(final String rawWikitext, final IWikiModel wikiModel, final Appendable writer,
			final boolean parseOnlySignature, final boolean renderTemplate) throws IOException {
		parseRecursive(rawWikitext, wikiModel, writer, parseOnlySignature, renderTemplate);
	}

	protected static void parseRecursive(final String rawWikitext, final IWikiModel wikiModel, final Appendable writer,
			final boolean parseOnlySignature, final boolean renderTemplate) throws IOException {
		parseRecursive(rawWikitext, wikiModel, writer, parseOnlySignature, renderTemplate, null);
	}

	/**
	 * Preprocess parsing of the <code>&lt;includeonly&gt;</code>, <code>&lt;onlyinclude&gt;</code> and
	 * <code>&lt;noinclude&gt;</code> tags
	 *
	 * @param writer
	 *            writer for output
	 * @param diff
	 * @return true if an onlyinclude section was parsed
	 * @throws IOException
	 */
	private boolean parsePreprocessRecursive(final StringBuilder writer, final int diff) throws IOException {
		final StringBuilder buf = new StringBuilder(fCurrentPosition - fWhiteStartPosition);
		appendContent(buf, fWhiteStart, fWhiteStartPosition, diff, true);
		final int startIndex = Util.indexOfTemplateParsing(buf);
		if (startIndex < 0) {
			writer.append(buf);
			return false;
		} else {
			return parsePreprocessRecursive(startIndex, buf.toString(), fWikiModel, writer, fRenderTemplate, false,
					null);
		}
	}

	/**
	 * Preprocess parsing of the <code>&lt;includeonly&gt;</code>, <code>&lt;onlyinclude&gt;</code> and
	 * <code>&lt;noinclude&gt;</code> tags. Also performs template parameter substitution.
	 */
	public static boolean parsePreprocessRecursive(final int startIndex, final String rawWikitext,
			final IWikiModel wikiModel, final StringBuilder writer, final boolean renderTemplate,
			final boolean onlyIncludeFlag, final Map<String, String> templateParameterMap) throws IOException {
		try {
			final int templateLevel = wikiModel.incrementTemplateRecursionLevel();
			if (templateLevel > Configuration.TEMPLATE_RECURSION_LIMIT) {
				writer.append("Error - template recursion limit exceeded parsing templates.");
				return false;
			}

			final StringBuilder sb = new StringBuilder(rawWikitext.length());
			final TemplateParser parser = new TemplateParser(rawWikitext, false, renderTemplate, onlyIncludeFlag);
			parser.setModel(wikiModel);
			parser.runPreprocessParser(0, startIndex, sb, /* ignoreTemplateTags */ false);
			writer.append(substituteParameters(templateParameterMap, wikiModel, sb));

			return parser.fOnlyIncludeFlag;
		} catch (Exception | Error e) {
			handleParserError(e, writer);
			return false;
		} finally {
			wikiModel.decrementTemplateRecursionLevel();
		}
	}

	private static StringBuilder substituteParameters(final Map<String, String> templateParameterMap,
			final IWikiModel wikiModel, final StringBuilder writer) {
		final boolean hasParamsToReplace = templateParameterMap != null && !templateParameterMap.isEmpty();
		final boolean hasEmptyDefaultParams = writer.indexOf("{{{|") != -1;

		if (hasParamsToReplace || hasEmptyDefaultParams) {
			final TemplateParser scanner = new TemplateParser(writer.toString());
			scanner.setModel(wikiModel);
			final StringBuilder result = scanner.replaceTemplateParameters(templateParameterMap, 0);
			if (result != null) {
				return result;
			}
		}
		return writer;
	}

	public static void parseRecursive(final String rawWikitext, final IWikiModel wikiModel, final Appendable writer,
			final boolean parseOnlySignature, final boolean renderTemplate,
			final Map<String, String> templateParameterMap) throws IOException {
		final int startIndex = Util.indexOfTemplateParsing(rawWikitext);
		if (startIndex < 0) {
			writer.append(rawWikitext);
			return;
		}
		StringBuilder sb = new StringBuilder(rawWikitext.length());

		parsePreprocessRecursive(startIndex, rawWikitext, wikiModel, sb, renderTemplate, false, templateParameterMap);

		if (parseOnlySignature) {
			writer.append(sb);
			return;
		}

		try {
			final int templateLevel = wikiModel.incrementTemplateRecursionLevel();
			if (templateLevel > Configuration.TEMPLATE_RECURSION_LIMIT) {
				writer.append("Error - template recursion limit exceeded parsing templates.");
				return;
			}

			TemplateParser parser = new TemplateParser(sb.toString(), false, renderTemplate);
			parser.setModel(wikiModel);
			sb = new StringBuilder(sb.length());
			// process <math>, <source>, <pre> tags
			parser.runPreprocessParser(sb, true);

			parser = new TemplateParser(sb.toString(), parseOnlySignature, renderTemplate);
			parser.setModel(wikiModel);
			sb = new StringBuilder(sb.length());
			parser.runParser(sb);

			// parse again, expansion inside parameter arguments
			if (!wikiModel.isParameterParsingMode()) {
				parser = new TemplateParser(sb.toString(), parseOnlySignature, renderTemplate);
				parser.setModel(wikiModel);
				sb = new StringBuilder(sb.length());
				parser.runParser(sb);
			}

			if (!renderTemplate) {
				final String redirectedLink = parseRedirect(sb.toString(), wikiModel);
				if (redirectedLink != null) {
					final String redirectedContent = getRedirectedTemplateContent(wikiModel, redirectedLink,
							templateParameterMap);
					if (redirectedContent != null) {
						parseRecursive(redirectedContent, wikiModel, writer, parseOnlySignature, renderTemplate,
								templateParameterMap);
						return;
					}
				}
			}
			writer.append(sb);
		} catch (Exception | Error e) {
			handleParserError(e, writer);
		} finally {
			wikiModel.decrementTemplateRecursionLevel();
		}
	}

	private static void handleParserError(final Throwable e, final Appendable writer) {
		Throwables.log(log, e);
		try {
			writer.append(TEMPLATE_PARSER_ERROR).append(':').append(e.getClass().getSimpleName());
		} catch (final IOException ignored) {
		}
	}

	/**
	 * Preprocess parsing of the <code>&lt;includeonly&gt;</code>, <code>&lt;onlyinclude&gt;</code> and
	 * <code>&lt;noinclude&gt;</code> tags, HTML comments and <code>&lt;nowiki&gt;</code>, <code>&lt;source&gt;</code>
	 * and <code>&lt;math&gt;</code> tags.
	 *
	 * @param writer
	 * @param ignoreTemplateTags
	 *            don't parse special template tags like &lt;includeonly&gt;, &lt;noinclude&gt;, &lt;onlyinclude&gt;
	 * @throws IOException
	 */
	protected void runPreprocessParser(final StringBuilder writer, final boolean ignoreTemplateTags)
			throws IOException {
		runPreprocessParser(fCurrentPosition, fCurrentPosition, writer, ignoreTemplateTags);
	}

	/**
	 * Preprocess parsing of the <code>&lt;includeonly&gt;</code>, <code>&lt;onlyinclude&gt;</code> and
	 * <code>&lt;noinclude&gt;</code> tags, HTML comments and <code>&lt;nowiki&gt;</code>, <code>&lt;source&gt;</code>
	 * and <code>&lt;math&gt;</code> tags.
	 *
	 * @param writer
	 * @param whiteStartPosition
	 *            the position to start the <i>normal content</i> before the first special template tag
	 * @param currentPosition
	 *            the position to continue with parsing special template tags
	 * @param ignoreTemplateTags
	 *            don't parse special template tags like &lt;includeonly&gt;, &lt;noinclude&gt;, &lt;onlyinclude&gt;
	 * @throws IOException
	 */
	protected void runPreprocessParser(final int whiteStartPosition, final int currentPosition,
			final StringBuilder writer, final boolean ignoreTemplateTags) throws IOException {
		fWhiteStart = true;
		fWhiteStartPosition = whiteStartPosition;
		int oldPosition;
		try {
			while (true) {
				fCurrentCharacter = fSource[fCurrentPosition++];
				switch (fCurrentCharacter) {
				case '{': // wikipedia subst: and safesubst: handling
					if (isSubsOrSafesubst()) {
						oldPosition = fCurrentPosition;
						if (parseSubstOrSafesubst(writer)) {
							fWhiteStart = true;
							fWhiteStartPosition = fCurrentPosition;
							continue;
						} else {
							fCurrentPosition = oldPosition;
						}
					}
					break;
				case '<':
					final int htmlStartPosition = fCurrentPosition;
					if (!fParseOnlySignature && parseIncludeWikiTags(writer, ignoreTemplateTags)) {
						continue;
					}
					fCurrentPosition = htmlStartPosition;
					break;
				case '~':
					int tildeCounter;
					if (fSource.length > fCurrentPosition + 1 && fSource[fCurrentPosition] == '~'
							&& fSource[fCurrentPosition + 1] == '~') {
						// parse signatures '~~~', '~~~~' or '~~~~~'
						tildeCounter = 3;
						try {
							if (fSource[fCurrentPosition + 2] == '~') {
								tildeCounter = 4;
								if (fSource[fCurrentPosition + 3] == '~') {
									tildeCounter = 5;
								}
							}
						} catch (final IndexOutOfBoundsException e1) {
							// end of scanner text
						}
						appendContent(writer, fWhiteStart, fWhiteStartPosition, 1, true);
						fWikiModel.appendSignature(writer, tildeCounter);
						fCurrentPosition += tildeCounter - 1;
						fWhiteStart = true;
						fWhiteStartPosition = fCurrentPosition;
					}
					break;
				}
				if (!fWhiteStart) {
					fWhiteStart = true;
					fWhiteStartPosition = fCurrentPosition - 1;
				}

			}
			// -----------------end switch while try--------------------
		} catch (final IndexOutOfBoundsException e) {
			// end of scanner text
		}
		try {
			if (!fOnlyIncludeFlag) {
				appendContent(writer, fWhiteStart, fWhiteStartPosition, 1, true);
			}
		} catch (final IndexOutOfBoundsException e) {
			// end of scanner text
		}
	}

	private boolean isSubsOrSafesubst() {
		if (fSource[fCurrentPosition] == '{' && fSource.length > fCurrentPosition + SUBST_LENGTH) {
			for (int pos = fCurrentPosition + 1; pos < fSource.length; pos++) {
				if (!Character.isWhitespace(fSource[pos])) {
					return fSource[pos] == 's';
				}
			}
			return false;
		} else {
			return false;
		}
	}

	private void runParser(final Appendable writer) throws IOException {
		fWhiteStart = true;
		fWhiteStartPosition = fCurrentPosition;
		try {
			while (true) {
				fCurrentCharacter = fSource[fCurrentPosition++];

				// ---------Identify the next token-------------
				switch (fCurrentCharacter) {
				case '{': // wikipedia template handling
					final int oldPosition = fCurrentPosition;
					if (!fParseOnlySignature && parseTemplateOrTemplateParameter(writer)) {
						fWhiteStart = true;
						fWhiteStartPosition = fCurrentPosition;
						continue;
					}
					fCurrentPosition = oldPosition;
					break;
				case '<':
					final int htmlStartPosition = fCurrentPosition;
					if (!fParseOnlySignature && parseSpecialWikiTags(writer)) {
						continue;
					}
					fCurrentPosition = htmlStartPosition;
					break;
				case '~':
					int tildeCounter = 0;
					if (fSource.length > fCurrentPosition + 1 && fSource[fCurrentPosition] == '~'
							&& fSource[fCurrentPosition + 1] == '~') {
						// parse signatures '~~~', '~~~~' or '~~~~~'
						tildeCounter = 3;
						try {
							if (fSource[fCurrentPosition + 2] == '~') {
								tildeCounter = 4;
								if (fSource[fCurrentPosition + 3] == '~') {
									tildeCounter = 5;
								}
							}
						} catch (final IndexOutOfBoundsException e1) {
							// end of scanner text
						}
						appendContent(writer, fWhiteStart, fWhiteStartPosition, 1, true);
						fWikiModel.appendSignature(writer, tildeCounter);
						fCurrentPosition += tildeCounter - 1;
						fWhiteStart = true;
						fWhiteStartPosition = fCurrentPosition;
					}
				}

				if (!fWhiteStart) {
					fWhiteStart = true;
					fWhiteStartPosition = fCurrentPosition - 1;
				}

			}
			// -----------------end switch while try--------------------
		} catch (final IndexOutOfBoundsException e) {
			// end of scanner text
		}
		try {
			appendContent(writer, fWhiteStart, fWhiteStartPosition, 1, true);
		} catch (final IndexOutOfBoundsException e) {
			// end of scanner text
		}
	}

	/**
	 * See
	 * <a href="https://en.wikipedia.org/wiki/Help:Template#Controlling_what_gets_transcluded">Help:Template#Controlling
	 * what gets transcluded</a>
	 *
	 * @param writer
	 *            the writer to receive the parsed content
	 * @param ignoreTemplateTags
	 *            don't parse special template tags like &lt;includeonly&gt;, &lt;noinclude&gt;, &lt;onlyinclude&gt;
	 * @return
	 * @throws IOException
	 */
	private boolean parseIncludeWikiTags(final StringBuilder writer, final boolean ignoreTemplateTags)
			throws IOException {
		try {
			switch (fSource[fCurrentPosition]) {
			case '!': // <!-- html comment -->
				if (parseHTMLCommentTags(writer)) {
					return true;
				}
				break;
			default:
				if (fSource[fCurrentPosition] == '/') {
					break;
				}
				// starting tag
				final int lessThanStart = fCurrentPosition - 1;
				final int startPosition = fCurrentPosition;
				int diff;
				final WikiTagNode tagNode = parseTag(fCurrentPosition);
				if (tagNode != null && (!tagNode.isEmptyXmlTag() || "noinclude".equals(tagNode.getTagName()))) {
					fCurrentPosition = tagNode.getEndPosition();
					final int tagStart = fCurrentPosition;
					final String tagName = tagNode.getTagName();

					switch (tagName) {
					case "nowiki":
						readUntilIgnoreCase("</", "nowiki>");
						return true;
					case "source":
						readUntilIgnoreCase("</", "source>");
						appendContentWithComment(writer, startPosition);
						return true;
					case "math":
						readUntilIgnoreCase("</", "math>");
						return true;
					case "pre":
						readUntilIgnoreCase("</", "pre>");
						return true;
					default:
						if (ignoreTemplateTags) {
							return false;
						}
					}

					if (fRenderTemplate) {
						switch (tagName) {
						case "noinclude":
							diff = readUntilNestedIgnoreCase(tagNode);
							appendContent(writer, fWhiteStart, fWhiteStartPosition, fCurrentPosition - lessThanStart,
									true);
							fWhiteStart = true;
							fWhiteStartPosition = tagStart;

							parsePreprocessRecursive(writer, diff);
							fWhiteStart = true;
							fWhiteStartPosition = fCurrentPosition;
							return true;
						case "includeonly":
							readUntilNestedIgnoreCase(tagNode);
							appendContent(writer, fWhiteStart, fWhiteStartPosition, fCurrentPosition - lessThanStart,
									true);
							fWhiteStart = true;
							fWhiteStartPosition = fCurrentPosition;
							return true;
						case "onlyinclude":
							diff = readUntilNestedIgnoreCase(tagNode);
							appendContent(writer, fWhiteStart, fWhiteStartPosition, fCurrentPosition - lessThanStart,
									true);
							fWhiteStart = true;
							fWhiteStartPosition = tagStart;
							parsePreprocessRecursive(writer, diff);
							fWhiteStart = true;
							fWhiteStartPosition = fCurrentPosition;
							return true;
						}
					} else { // not rendering a Template namespace directly
						switch (tagName) {
						case "includeonly":

							diff = readUntilNestedIgnoreCase(tagNode);
							if (!fOnlyIncludeFlag) {
								appendContent(writer, fWhiteStart, fWhiteStartPosition,
										fCurrentPosition - lessThanStart, true);
							}
							fWhiteStart = true;
							fWhiteStartPosition = tagStart;

							if (!fOnlyIncludeFlag) {
								final StringBuilder tempWriter = new StringBuilder();
								if (fOnlyIncludeFlag = parsePreprocessRecursive(tempWriter, diff)) {
									writer.delete(0, writer.length());
								}
								writer.append(tempWriter);
							}

							fWhiteStart = true;
							fWhiteStartPosition = fCurrentPosition;
							return true;

						case "noinclude":
							diff = readUntilNestedIgnoreCase(tagNode);
							if (!fOnlyIncludeFlag) {
								appendContent(writer, fWhiteStart, fWhiteStartPosition,
										fCurrentPosition - lessThanStart, true);
							}
							fWhiteStart = true;
							fWhiteStartPosition = fCurrentPosition;
							return true;
						case "onlyinclude":
							diff = readUntilNestedIgnoreCase(tagNode);
							if (!fOnlyIncludeFlag) {
								// delete the content, which is already added
								writer.delete(0, writer.length());
								fOnlyIncludeFlag = true;
							}

							fWhiteStart = true;
							fWhiteStartPosition = tagStart;

							parsePreprocessRecursive(writer, diff);

							fWhiteStart = true;
							fWhiteStartPosition = fCurrentPosition;
							return true;
						}
					}
					fCurrentPosition = startPosition;
				}
			} // switch
		} catch (final IndexOutOfBoundsException e) {
			// do nothing
		}
		return false;
	}

	private boolean parseSpecialWikiTags(final Appendable writer) throws IOException {
		final int startPosition = fCurrentPosition;
		try {
			switch (fSource[fCurrentPosition]) {
			case '!': // <!-- html comment -->
				if (parseHTMLCommentTags(writer)) {
					return true;
				}
				break;
			default:

				if (fSource[fCurrentPosition] != '/') {

					// starting tag
					final WikiTagNode tagNode = parseTag(fCurrentPosition);
					if (tagNode != null && !tagNode.isEmptyXmlTag()) {
						fCurrentPosition = tagNode.getEndPosition();
						final String tagName = tagNode.getTagName();
						switch (tagName) {
						case "nowiki":
							readUntilIgnoreCase("</", "nowiki>");
							return true;

						case "source":
							readUntilIgnoreCase("</", "source>");
							appendContentWithComment(writer, startPosition);
							return true;

						case "pre":
							readUntilIgnoreCase("</", "pre>");
							return true;
						// see issue#153
						// } else if (tagName.equals("math")) {
						// readUntilIgnoreCase("</", "math>");
						// return true;
						}
					}
				}
			}
		} catch (final IndexOutOfBoundsException e) {
			// do nothing
		}
		fCurrentPosition = startPosition;
		return false;
	}

	private void appendContent(final Appendable writer, final boolean whiteStart, final int whiteStartPosition,
			final int diff, final boolean stripHTMLComments) throws IOException {
		if (whiteStart) {
			try {
				final int whiteEndPosition = fCurrentPosition - diff;
				final int count = whiteEndPosition - whiteStartPosition;
				if (count > 0) {
					if (stripHTMLComments) {
						writer.append(HTML_COMMENT_PATTERN
								.matcher(fStringSource.substring(whiteStartPosition, whiteEndPosition)).replaceAll(""));
					} else {
						writer.append(fStringSource, whiteStartPosition, whiteEndPosition);
					}
				}
			} finally {
				fWhiteStart = false;
			}
		}
	}

	private void appendContentWithComment(final Appendable writer, final int startPosition) throws IOException {
		if (fWhiteStartPosition < startPosition - 1) {
			appendContent(writer, fWhiteStart, fWhiteStartPosition, fCurrentPosition - startPosition + 1, true);
		}
		appendContent(writer, true, startPosition - 1, 0, false);
		fWhiteStart = true;
		fWhiteStartPosition = fCurrentPosition;
	}

	private boolean parseSubstOrSafesubst(final Appendable writer) throws IOException {
		appendContent(writer, fWhiteStart, fWhiteStartPosition, 1, true);
		final int pos = fCurrentPosition;

		consumeWhitespace();
		final int startTemplatePosition = ++fCurrentPosition;

		final int templateEndPosition = findNestedTemplateEnd(fSource, fCurrentPosition);
		if (templateEndPosition < 0) {
			fCurrentPosition = pos;
			return false;
		} else {
			return parseSubst(writer, startTemplatePosition, templateEndPosition);
		}
	}

	private boolean parseTemplateOrTemplateParameter(final Appendable writer) throws IOException {
		if (fSource[fCurrentPosition] == '{') {
			appendContent(writer, fWhiteStart, fWhiteStartPosition, 1, true);
			final int startTemplatePosition = ++fCurrentPosition;
			if (fSource[fCurrentPosition] == '{' && fSource[fCurrentPosition + 1] != '{') {
				// parse template parameters
				final int[] templateEndPosition = findNestedParamEnd(fSource, fCurrentPosition + 1);
				if (templateEndPosition[0] < 0) {
					if (templateEndPosition[1] < 0) {
						--fCurrentPosition;
					} else {
						writer.append('{');
						++fCurrentPosition;
						return parseTemplate(writer, startTemplatePosition + 1, templateEndPosition[1]);
					}
				} else {
					parseTemplateParameter(writer, startTemplatePosition, templateEndPosition[0]);
					return true;
				}
			} else {
				final int templateEndPosition = findNestedTemplateEnd(fSource, fCurrentPosition);
				if (templateEndPosition < 0) {
					fCurrentPosition--;
				} else {
					return parseTemplate(writer, startTemplatePosition, templateEndPosition);
				}
			}
		}
		return false;
	}

	private boolean parseSubst(final Appendable writer, final int startTemplatePosition, final int templateEndPosition)
			throws IOException {
		fCurrentPosition = templateEndPosition;
		// insert template handling
		final int endPosition = fCurrentPosition;
		String plainContent;
		final int endOffset = fCurrentPosition - 2;
		final Object[] objs = createParameterMap(fSource, startTemplatePosition,
				fCurrentPosition - startTemplatePosition - 2);
		final String templateName = (String) objs[1];
		@SuppressWarnings("unchecked")
		final List<String> parts = (List<String>) objs[0];
		ITemplateFunction templateFunction;
		int currOffset;
		if (templateName.startsWith(SUBST)) {
			templateFunction = Subst.CONST;
			currOffset = SUBST_LENGTH;
		} else if (templateName.startsWith(SAFESUBST)) {
			templateFunction = Safesubst.CONST;
			currOffset = SAFESUBST_LENGTH;
		} else {
			return false;
		}

		parts.set(0, templateName.substring(currOffset));
		plainContent = templateFunction.parseFunction(parts, fWikiModel, fSource, startTemplatePosition + currOffset,
				endOffset, false);
		fCurrentPosition = endPosition;
		if (plainContent != null) {
			writer.append(plainContent);
			return true;
		}
		return false;

	}

	/**
	 * Parse a single template call {{...}}. There are 3 main steps:
	 * <ol>
	 * <li>Check if the call is a parser function in the <code>checkParserFunction()</code> method; if <code>true</code>
	 * execute the parser function and return.</li>
	 * <li>Split the template call in the <code>createParameterMap()</code method
	 * into a <code>templateName</code> and a parameter/value map.</li>
	 * <li>Substitute the raw template text into the existing text and replace all template parameters with their value
	 * in <code>TemplateParser.parseRecursive()</code method.</li>
	 * </ol>
	 *
	 * @param writer
	 * @param startTemplatePosition
	 * @param templateEndPosition
	 * @return
	 * @throws IOException
	 */
	private boolean parseTemplate(final Appendable writer, final int startTemplatePosition,
			final int templateEndPosition) throws IOException {
		fCurrentPosition = templateEndPosition;

		final int endPosition = fCurrentPosition;
		String plainContent;
		final int endOffset = fCurrentPosition - 2;
		final Object[] objs = createParameterMap(fSource, startTemplatePosition,
				fCurrentPosition - startTemplatePosition - 2);
		@SuppressWarnings("unchecked")
		final List<String> parts = (List<String>) objs[0];
		String templateName = (String) objs[1];
		final StringBuilder buf = new StringBuilder(templateName.length() + templateName.length() / 10);
		TemplateParser.parse(templateName, fWikiModel, buf, false);
		templateName = buf.toString();
		final int currOffset = TemplateParser.checkParserFunction(buf);
		if (currOffset > 0) {
			final String function = templateName.substring(0, currOffset - 1).trim();
			if (Configuration.PARSER_FUNCTIONS) {
				System.out.println(function);
			}
			final ITemplateFunction templateFunction = fWikiModel.getTemplateFunction(function);
			if (templateFunction != null) {
				// if (function.charAt(0) == '#') {
				// #if:, #ifeq:,...
				parts.set(0, templateName.substring(currOffset));
				// if (Configuration.PARSER_FUNCTIONS) {
				// System.out.println(function + ": " + parts);
				// }
				plainContent = templateFunction.parseFunction(parts, fWikiModel, fSource,
						startTemplatePosition + currOffset, endOffset, false);
				fCurrentPosition = endPosition;
				if (plainContent != null) {
					TemplateParser.parseRecursive(plainContent, fWikiModel, writer, false, false);
				}
				return true;
			}
			fCurrentPosition = endOffset + 2;
		}
		if (Util.isInvalidTemplateName(templateName)) {
			return false;
		}
		fCurrentPosition = endPosition;
		final LinkedHashMap<String, String> parameterMap = new LinkedHashMap<>();
		if (parts.size() > 1) {
			final List<String> unnamedParameters = new ArrayList<>();
			for (int i = 1; i < parts.size(); i++) {
				createSingleParameter(parts.get(i), fWikiModel, parameterMap, unnamedParameters);
			}
			mergeParameters(parameterMap, unnamedParameters);
		}

		fWikiModel.substituteTemplateCall(templateName, parameterMap, writer);
		return true;
	}

	/**
	 * If template calls have a mix between named and unnamed parameters, the collected <code>unnamedParameters</code>
	 * are merged into the <code>parameterMap</code>.
	 *
	 *
	 * See <a href="https://meta.wikimedia.org/wiki/Help:Template#Mix_of_named_and_unnamed_parameters"
	 * >Help:Template#Mix_of_named_and_unnamed_parameters</a>
	 */
	public static void mergeParameters(final LinkedHashMap<String, String> parameterMap,
			final List<String> unnamedParameters) {
		if (unnamedParameters.size() == 0) {
			return;
		}
		int unnamedParameterIndex = 1;
		for (final String param : unnamedParameters) {
			final String key = Integer.toString(unnamedParameterIndex++);
			if (!parameterMap.containsKey(key)) {
				parameterMap.put(key, param);
			}
		}
	}

	/**
	 * Parse a single template parameter {{{...}}}
	 *
	 * @throws IOException
	 */
	private void parseTemplateParameter(final Appendable writer, final int startTemplatePosition,
			final int templateEndPosition) throws IOException {
		final String plainContent = fStringSource.substring(startTemplatePosition - 2, templateEndPosition);
		fCurrentPosition = templateEndPosition;
		final int indx = plainContent.indexOf("{{{");
		if (indx >= 0) {
			final TemplateParser scanner = new TemplateParser(plainContent);
			scanner.setModel(fWikiModel);
			final StringBuilder plainBuffer = scanner.replaceTemplateParameters(null, 0);
			if (plainBuffer == null) {
				writer.append(plainContent);
				return;
			}
			TemplateParser.parseRecursive(plainBuffer.toString().trim(), fWikiModel, writer, false, false);
		}
	}

	/**
	 * Create a map from the parameters defined in a template call
	 *
	 * @return the templates parameters <code>java.util.List</code> at index [0] and the template name at index [1]
	 *
	 */
	public static Object[] createParameterMap(final char[] src, final int startOffset, final int len) {
		final Object[] objs = new Object[2];
		final int currOffset = startOffset;
		final int endOffset = startOffset + len;
		final List<String> resultList = new ArrayList<>();
		objs[0] = resultList;
		splitByPipe(src, currOffset, endOffset, resultList);
		if (resultList.size() <= 1) {
			// set the template name
			objs[1] = new String(src, startOffset, len).trim();
		} else {
			objs[1] = resultList.get(0).trim();
		}
		return objs;
	}

	/**
	 * Create a {@link TemplateTag} from the passed template call.
	 *
	 * @return the template tag
	 */
	public static TemplateTag createTemplateTag(final char[] src, final int startOffset, final int len,
			final IWikiModel model) {
		// Parse parameters
		final Object[] objs = TemplateParser.createParameterMap(src, startOffset, len);
		final String templateName = (String) objs[1];
		final Map<String, String> parameterMap = createParameterMap(objs, model);

		// Create the tag
		final TemplateTag tag = new TemplateTag(templateName);
		for (final Map.Entry<String, String> entry : parameterMap.entrySet()) {
			tag.addAttribute(entry.getKey(), entry.getValue(), false);
		}

		return tag;
	}

	public static Map<String, String> createParameterMap(final Object[] objs, final IWikiModel model) {
		@SuppressWarnings("unchecked")
		final List<String> parts = (List<String>) objs[0];

		final LinkedHashMap<String, String> parameterMap = new LinkedHashMap<>();
		if (parts.size() > 1) {
			final List<String> unnamedParameters = new ArrayList<>();
			for (int i = 1; i < parts.size(); ++i) {
				createSingleParameter(parts.get(i), model, parameterMap, unnamedParameters);
			}
			mergeParameters(parameterMap, unnamedParameters);
		}

		return parameterMap;
	}

	/**
	 * Create a single parameter, defined in a template call, and add it to the <b>named parameters map</b> or
	 * <b>unnamed parameter list</b>
	 *
	 * <p>
	 * See <a href="https://en.wikipedia.org/wiki/Help:Template">Help:Template</a>: <i>Remember that whitespace
	 * characters (spaces, tabs, carriage returns and line feeds) are not automatically stripped from the start and end
	 * of unnamed parameters (as they are from named parameters). Including such characters (or any other non-visible
	 * characters in any parameters) may in some cases affect the template's behaviour in unexpected ways. (Template
	 * designers can use {{StripWhitespace}} to remove unwanted whitespace in unnamed parameters.)</i>
	 * </p>
	 *
	 * @param srcString
	 * @param namedParameterMap
	 *            a key/value pairs for name and value of a template parameter
	 * @param unnamedParams
	 *            a list of unnamed parameter values
	 */
	public static void createSingleParameter(final String srcString, final IWikiModel wikiModel,
			final Map<String, String> namedParameterMap, final List<String> unnamedParams) {
		int currOffset = 0;
		final char[] src = srcString.toCharArray();
		final int endOffset = srcString.length();
		char ch;
		String parameter = null;
		String value;
		boolean equalCharParsed = false;

		int lastOffset = currOffset;
		int[] temp = new int[] { -1, -1 };
		try {
			while (currOffset < endOffset) {
				ch = src[currOffset++];
				if (ch == '[' && src[currOffset] == '[') {
					currOffset++;
					temp[0] = findNestedEnd(src, '[', ']', currOffset);
					if (temp[0] >= 0) {
						currOffset = temp[0];
					}
				} else if (ch == '{' && src[currOffset] == '{') {
					currOffset++;
					if (src[currOffset] == '{' && src[currOffset + 1] != '{') {
						currOffset++;
						temp = findNestedParamEnd(src, currOffset);
						if (temp[0] >= 0) {
							currOffset = temp[0];
						} else {
							if (temp[1] >= 0) {
								currOffset = temp[1];
							}
						}
					} else {
						temp[0] = findNestedTemplateEnd(src, currOffset);
						if (temp[0] >= 0) {
							currOffset = temp[0];
						}
					}

				} else if (ch == '=') {
					if (!equalCharParsed) {
						parameter = srcString.substring(lastOffset, currOffset - 1).trim();
						lastOffset = currOffset;
					}
					equalCharParsed = true;
				}
			}

		} catch (final IndexOutOfBoundsException e) {

		} finally {
			if (currOffset >= lastOffset) {
				try {
					value = srcString.substring(lastOffset, currOffset);
					final boolean parameterParsingMode = wikiModel.isParameterParsingMode();
					try {
						wikiModel.setParameterParsingMode(true);
						if (parameter != null) {
							final StringBuilder buf = new StringBuilder(value.length());
							TemplateParser.parseRecursive(value, wikiModel, buf, false, false);
							value = Util.trimNewlineRight(buf.toString());
							namedParameterMap.put(parameter, value);
						} else {
							// whitespace characters are not automatically stripped from the
							// start and end of unnamed parameters!
							final StringBuilder buf = new StringBuilder(value.length());
							TemplateParser.parseRecursive(value, wikiModel, buf, false, false);
							unnamedParams.add(buf.toString());
						}
					} finally {
						wikiModel.setParameterParsingMode(parameterParsingMode);
					}
				} catch (final IOException ignored) {
				}
			}
		}
	}

	/**
	 * Check if this template contains a template function
	 *
	 * Note: repositions this#fCurrentPosition behind the parser function string if possible
	 *
	 * @param plainContent
	 * @return the offset behind the &acute;:&acute; character at the end of the parser function name or <code>-1</code>
	 *         if no parser function can be found in this template.
	 */
	public static int checkParserFunction(final CharSequence plainContent) {
		int currOffset = 0;
		final int len = plainContent.length();
		char ch;
		while (currOffset < len) {
			ch = plainContent.charAt(currOffset++);
			if (Character.isLetter(ch) || ch == '#' || ch == '$') {
				while (currOffset < len) {
					ch = plainContent.charAt(currOffset++);
					if (ch == ':') {
						return currOffset;
					} else if (!Character.isLetterOrDigit(ch) && ch != '$') {
						return -1;
					}
				}
				break;
			} else if (!Character.isWhitespace(ch)) {
				return -1;
			}
		}
		return -1;
	}

	protected boolean parseHTMLCommentTags(final Appendable writer) throws IOException {
		if (fStringSource.startsWith("<!--", fCurrentPosition - 1)) {
			int temp = readWhitespaceUntilStartOfLine(2);
			if (!fOnlyIncludeFlag) {
				int diff = 1;
				if (temp >= 0) {
					diff = fCurrentPosition - temp - 1;
				}
				appendContent(writer, fWhiteStart, fWhiteStartPosition, diff, true);
			}
			fCurrentPosition += 3;
			readUntil("-->");
			// end of HTML comment
			if (temp >= 0) {
				temp = readWhitespaceUntilEndOfLine(0);
				if (temp >= 0) {
					fCurrentPosition++;
				}
			}
			fWhiteStart = true;
			fWhiteStartPosition = fCurrentPosition;
			return true;
		}
		return false;
	}

	/**
	 * Replace the wiki template parameters in the given template string
	 *
	 * @param templateParameters
	 * @param curlyBraceOffset
	 *            TODO
	 * @param template
	 *
	 * @return <code>null</code> if no replacement could be found
	 */
	@Nullable
	public StringBuilder replaceTemplateParameters(@Nullable final Map<String, String> templateParameters,
			final int curlyBraceOffset) {
		StringBuilder buffer = null;
		int bufferStart = 0;
		try {
			final int level = fWikiModel.incrementRecursionLevel();
			if (level > Configuration.PARSER_RECURSION_LIMIT) {
				return null; // no further processing
			}
			fScannerPosition += curlyBraceOffset;
			char ch;
			int parameterStart = -1;
			StringBuilder recursiveResult;
			boolean isDefaultValue;
			while (true) {
				ch = fSource[fScannerPosition++];
				if (ch == '{' && fSource[fScannerPosition] == '{' && fSource[fScannerPosition + 1] == '{'
						&& fSource[fScannerPosition + 2] != '{') {
					fScannerPosition += 2;
					parameterStart = fScannerPosition;

					final int temp[] = findNestedParamEnd(fSource, parameterStart);
					if (temp[0] >= 0) {
						fScannerPosition = temp[0];
						final List<String> list = splitByPipe(fSource, parameterStart, fScannerPosition - 3, null);
						if (list.size() > 0) {
							String parameterString = list.get(0).trim();

							final TemplateParser scanner1 = new TemplateParser(parameterString);
							scanner1.setModel(fWikiModel);
							recursiveResult = scanner1.replaceTemplateParameters(templateParameters, curlyBraceOffset);
							if (recursiveResult != null) {
								parameterString = recursiveResult.toString();
							}

							String value = null;
							isDefaultValue = false;
							if (templateParameters != null) {
								value = templateParameters.get(parameterString);
							}
							if (value == null && list.size() > 1) {
								// default value is available for the template
								value = list.get(1);
								isDefaultValue = true;
							}
							if (value != null) {
								if (value.length() <= Configuration.TEMPLATE_VALUE_LIMIT) {
									if (buffer == null) {
										buffer = new StringBuilder(fSource.length + 128);
									}
									if (bufferStart < fScannerPosition) {
										buffer.append(fSource, bufferStart, parameterStart - bufferStart - 3);
									}

									final TemplateParser scanner2 = new TemplateParser(value);
									scanner2.setModel(fWikiModel);
									if (isDefaultValue) {
										recursiveResult = scanner2.replaceTemplateParameters(templateParameters,
												curlyBraceOffset);
									} else {
										recursiveResult = scanner2.replaceTemplateParameters(null, curlyBraceOffset);
									}
									if (recursiveResult != null) {
										buffer.append(recursiveResult);
									} else {
										buffer.append(value);
									}
									bufferStart = fScannerPosition;
								}
							}
						}
						fScannerPosition = temp[0];
						parameterStart = -1;
					}
				}
				if (buffer != null && buffer.length() > Configuration.TEMPLATE_BUFFER_LIMIT) {
					// Controls the scanner, when infinite recursion occurs the
					// buffer grows out of control.
					return buffer;
				}
			}
		} catch (final IndexOutOfBoundsException e) {
			// ignore
		} finally {
			fWikiModel.decrementRecursionLevel();
		}
		if (buffer != null && bufferStart < fScannerPosition) {
			buffer.append(fSource, bufferStart, fScannerPosition - bufferStart - 1);
		}
		return buffer;
	}
}
