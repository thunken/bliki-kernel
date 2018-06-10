package info.bliki.wiki.model;

import static info.bliki.wiki.tags.WPATag.CLASS;
import static info.bliki.wiki.tags.WPATag.HREF;
import static info.bliki.wiki.tags.WPATag.WIKILINK;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.StringEscapeUtils;

import info.bliki.htmlcleaner.BaseToken;
import info.bliki.htmlcleaner.ContentToken;
import info.bliki.htmlcleaner.TagNode;
import info.bliki.htmlcleaner.TagToken;
import info.bliki.htmlcleaner.Utils;
import info.bliki.util.Throwables;
import info.bliki.wiki.filter.AbstractWikipediaParser;
import info.bliki.wiki.filter.Encoder;
import info.bliki.wiki.filter.HTMLConverter;
import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.filter.MagicWord;
import info.bliki.wiki.filter.MagicWord.MagicWordE;
import info.bliki.wiki.filter.PDFConverter;
import info.bliki.wiki.filter.ParsedPageName;
import info.bliki.wiki.filter.SectionHeader;
import info.bliki.wiki.filter.TemplateParser;
import info.bliki.wiki.filter.WikipediaParser;
import info.bliki.wiki.filter.WikipediaPreTagParser;
import info.bliki.wiki.namespaces.INamespace;
import info.bliki.wiki.namespaces.INamespace.INamespaceValue;
import info.bliki.wiki.namespaces.INamespace.NamespaceCode;
import info.bliki.wiki.namespaces.Namespace;
import info.bliki.wiki.tags.HTMLBlockTag;
import info.bliki.wiki.tags.HTMLTag;
import info.bliki.wiki.tags.TableOfContentTag;
import info.bliki.wiki.tags.WPATag;
import info.bliki.wiki.tags.code.SourceCodeFormatter;
import info.bliki.wiki.tags.util.TagStack;
import info.bliki.wiki.template.ITemplateFunction;
import info.bliki.wiki.template.extension.AttributeList;
import info.bliki.wiki.template.extension.AttributeRenderer;
import lombok.extern.slf4j.Slf4j;

/**
 * Standard model implementation for the Wikipedia syntax.
 */
@Slf4j
public abstract class AbstractWikiModel implements IWikiModel, IContext {

	private static int fNextNumberCounter = 0;
	protected ArrayList<Reference> fReferences;
	protected Map<String, Integer> fReferenceNames;
	protected int fRecursionLevel;
	protected int fTemplateRecursionCount;
	protected TagStack fTagStack;
	private boolean fInitialized;
	protected Locale fLocale;
	private final IConfiguration fConfiguration;
	private IEventListener fWikiListener;
	protected final INamespace fNamespace;
	protected String fRedirectLink;
	protected String fPageTitle = "PAGENAME";
	protected String fNamespaceName = "";

	protected int fSectionCounter;
	protected boolean fTemplateTopic;
	protected boolean fParameterParsingMode;
	protected boolean fNoToc;

	protected int fExternalLinksCounter;

	private TableOfContentTag fTableOfContentTag = null;
	private SimpleDateFormat fFormatter = null;
	private List<Object> fTableOfContent = null;

	/**
	 * Contains all anchor strings to create unique anchors
	 */
	protected HashSet<String> fToCSet;

	/**
	 * Map an attribute name to its value(s). These values are set by outside code via st.setAttribute(name, value).
	 * StringTemplate is like self in that a template is both the "class def" and "instance". When you create a
	 * StringTemplate or setTemplate, the text is broken up into chunks (i.e., compiled down into a series of chunks
	 * that can be evaluated later). You can have multiple
	 */
	protected Map<String, Object> attributes;

	protected Map<String, Counter> fTemplates;

	/**
	 * A Map<Class,Object> that allows people to register a renderer for a particular kind of object to be displayed in
	 * this template. This overrides any renderer set for this template's group.
	 *
	 * Most of the time this map is not used because the StringTemplateGroup has the general renderer map for all
	 * templates in that group. Sometimes though you want to override the group's renderers.
	 */
	private Map<Class<?>, Object> attributeRenderers;

	private final InterWikiMap fInterWikiMap;

	public AbstractWikiModel() {
		this(new Configuration());
	}

	public AbstractWikiModel(final Configuration configuration) {
		this(configuration, Locale.ENGLISH);
	}

	public AbstractWikiModel(final Configuration configuration, final Locale locale) {
		this(configuration, locale, new Namespace(locale));
	}

	public AbstractWikiModel(final Configuration configuration, final Locale locale, final INamespace namespace) {
		fLocale = locale;
		fInitialized = false;
		fConfiguration = configuration;
		fNamespace = namespace;
		fInterWikiMap = new InterWikiMap(configuration.getInterWikiMapping(), configuration.getWikiId());
		initialize();
	}

	@Override
	public void addCategory(final String categoryName, final String sortKey) {

	}

	@Override
	public SourceCodeFormatter addCodeFormatter(final String key, final SourceCodeFormatter value) {
		return fConfiguration.addCodeFormatter(key, value);
	}

	@Override
	public String addInterwikiLink(final String key, final String value) {
		return fConfiguration.addInterwikiLink(key, value);
	}

	@Override
	public void addLink(final String topicName) {

	}

	@Override
	public boolean addSemanticAttribute(final String attribute, final String attributeValue) {
		return false;
	}

	@Override
	public boolean addSemanticRelation(final String relation, final String relationValue) {
		return false;
	}

	@Override
	public void addTemplate(final String template) {
	}

	@Override
	public void addInclude(final String pageName) {
	}

	@Override
	public ITemplateFunction addTemplateFunction(final String key, final ITemplateFunction value) {
		return fConfiguration.addTemplateFunction(key, value);
	}

	@Override
	public TagToken addTokenTag(final String key, final TagToken value) {
		return fConfiguration.addTokenTag(key, value);
	}

	@Override
	public String[] addToReferences(final String reference, final String nameAttribute) {
		final String[] result = new String[2];
		result[1] = null;
		if (fReferences == null) {
			fReferences = new ArrayList<>();
			fReferenceNames = new HashMap<>();
		}
		if (nameAttribute != null) {
			final Integer index = fReferenceNames.get(nameAttribute);
			if (index != null) {
				result[0] = index.toString();
				final Reference ref = fReferences.get(index - 1);
				final int count = ref.incCounter();
				if (count >= Reference.CHARACTER_REFS.length()) {
					result[1] = nameAttribute + '_' + 'Z';
				} else {
					if (count == 0) {
						result[1] = nameAttribute;
					} else {
						result[1] = nameAttribute + '_' + Reference.CHARACTER_REFS.charAt(count);
					}
				}
				return result;
			}
		}

		if (nameAttribute != null) {
			fReferences.add(new Reference(reference, nameAttribute));
			final Integer index = fReferences.size();
			fReferenceNames.put(nameAttribute, index);
			result[1] = nameAttribute;
		} else {
			fReferences.add(new Reference(reference));
		}
		result[0] = Integer.toString(fReferences.size());
		return result;
	}

	/**
	 * Add a section header with the given <code>headLevel</code> to the &quot;table of content&quot;
	 *
	 * @param toc
	 *            the &quot;table of content list&quot;
	 * @param strPair
	 *            a new section header
	 * @param headLevel
	 *            the level of the new section header
	 */
	protected void addToTableOfContent(final List<Object> toc, final SectionHeader strPair, int headLevel) {
		if (headLevel == 1) {
			toc.add(strPair);
		} else {
			if (toc.size() > 0) {
				if (toc.get(toc.size() - 1) instanceof List) {
					@SuppressWarnings("unchecked")
					final List<Object> list = (List<Object>) toc.get(toc.size() - 1);
					addToTableOfContent(list, strPair, --headLevel);
					return;
				}
			}
			final ArrayList<Object> list = new ArrayList<>();
			toc.add(list);
			addToTableOfContent(list, strPair, --headLevel);
		}
	}

	@Override
	public void append(final BaseToken contentNode) {
		fTagStack.append(contentNode);
	}

	@Override
	public void appendExternalImageLink(final String imageSrc, final String imageAltText) {
		final TagNode spanTagNode = new TagNode("span");
		append(spanTagNode);
		spanTagNode.addAttribute("class", "image", true);
		final TagNode imgTagNode = new TagNode("img");
		spanTagNode.addChild(imgTagNode);
		imgTagNode.addAttribute("src", imageSrc, true);
		imgTagNode.addAttribute("alt", imageAltText, true);
		// "nofollow" keyword is not allowed for XHTML
		// imgTagNode.addAttribute("rel", "nofollow", true);
	}

	/**
	 * Append an external link (starting with http, https, ftp,...) as described in
	 * <a href="https://en.wikipedia.org/wiki/Help:Link#External_links">Help Links</a>
	 *
	 * @param uriSchemeName
	 *            the top level URI (Uniform Resource Identifier) scheme name (without the following colon character
	 *            ":"). Example "ftp", "http", "https". See <a href="https://en.wikipedia.org/wiki/URI_scheme">URI
	 *            scheme</a>
	 * @param link
	 *            the external link with <code>https://, https:// or ftp://</code> prefix
	 * @param linkName
	 *            the link name which is separated from the URL by a space
	 * @param withoutSquareBrackets
	 *            if <code>true</code> a link with no square brackets around the link was parsed
	 */
	@Override
	public void appendExternalLink(final String uriSchemeName, String link, final String linkName,
			final boolean withoutSquareBrackets) {
		link = Utils.escapeXml(link, true, false, false);

		final TagNode aTagNode = new TagNode("a");
		aTagNode.addAttribute("href", link, true);
		aTagNode.addAttribute("rel", "nofollow", true);
		if (withoutSquareBrackets) {
			aTagNode.addAttribute("class", "external free", true);
			append(aTagNode);
			aTagNode.addChild(new ContentToken(linkName));
		} else {
			final String trimmedText = linkName.trim();
			if (trimmedText.length() > 0) {
				pushNode(aTagNode);
				if (linkName.equals(link)
						// protocol-relative URLs also get auto-numbered if there is no
						// real
						// alias
						|| link.length() >= 2 && link.charAt(0) == '/' && link.charAt(1) == '/'
								&& link.substring(2).equals(linkName)) {
					aTagNode.addAttribute("class", "external autonumber", true);
					aTagNode.addChild(new ContentToken("[" + (++fExternalLinksCounter) + "]"));
				} else {
					aTagNode.addAttribute("class", "external text", true);
					WikipediaParser.parseRecursive(trimmedText, this, false, true);
				}
				popNode();
			}
		}
	}

	/**
	 * Append a new head to the table of content
	 */
	@Override
	public ITableOfContent appendHead(final String rawHead, final int headLevel, final boolean noToC,
			final int headCounter, final int startPosition, final int endPosition) {
		final TagStack localStack = WikipediaParser.parseRecursive(rawHead.trim(), this, true, true);

		final HTMLBlockTag headTagNode = new HTMLBlockTag("h" + headLevel, Configuration.SPECIAL_BLOCK_TAGS);
		final TagNode spanTagNode = new TagNode("span");
		// Example:
		// <h2><span class="mw-headline" id="Header_level_2">Header level
		// 2</span></h2>
		spanTagNode.addChildren(localStack.getNodeList());
		headTagNode.addChild(spanTagNode);
		final String tocHead = headTagNode.getBodyString();
		String anchor = Encoder.encodeDotUrl(tocHead);
		createTableOfContent(false);
		if (!noToC && headCounter > 3) {
			fTableOfContentTag.setShowToC(true);
		}
		if (fToCSet.contains(anchor)) {
			String newAnchor = anchor;
			for (int i = 2; i < Integer.MAX_VALUE; i++) {
				newAnchor = anchor + '_' + Integer.toString(i);
				if (!fToCSet.contains(newAnchor)) {
					break;
				}
			}
			anchor = newAnchor;
		}
		fToCSet.add(anchor);
		final SectionHeader strPair = new SectionHeader(headLevel, startPosition, endPosition, tocHead, anchor);
		addToTableOfContent(fTableOfContent, strPair, headLevel);
		if (getRecursionLevel() == 1) {
			buildEditLinkUrl(fSectionCounter++);
		}
		spanTagNode.addAttribute("class", "mw-headline", true);
		spanTagNode.addAttribute("id", anchor, true);

		append(headTagNode);
		return fTableOfContentTag;
	}

	@Override
	public void appendInternalImageLink(final String hrefImageLink, final String srcImageLink,
			final ImageFormat imageFormat) {
		final String caption = imageFormat.getCaption();
		final String imageType = imageFormat.getType();
		final TagNode divInnerTagNode = new TagNode("div");
		divInnerTagNode.addAttribute("id", "image", false);

		if (hrefImageLink.length() != 0) {
			divInnerTagNode.addAttribute("href", hrefImageLink, false);
		}

		divInnerTagNode.addAttribute("src", srcImageLink, false);
		setDefaultThumbWidth(imageFormat);
		divInnerTagNode.addObjectAttribute("wikiobject", imageFormat);
		pushNode(divInnerTagNode);
		try {
			// TODO: test all these cases
			if (caption != null && caption.length() > 0
					&& ("frame".equals(imageType) || "thumb".equals(imageType) || "thumbnail".equals(imageType))) {

				final TagNode captionTagNode = new TagNode("div");
				String clazzValue = "caption";
				final String type = imageFormat.getType();
				if (type != null) {
					clazzValue = type + clazzValue;
				}
				captionTagNode.addAttribute("class", clazzValue, false);
				//
				final TagStack localStack = WikipediaParser.parseRecursive(caption, this, true, true);
				captionTagNode.addChildren(localStack.getNodeList());
				String altAttribute = imageFormat.getAlt();
				if (altAttribute == null) {
					altAttribute = captionTagNode.getBodyString();
					imageFormat.setAlt(Encoder.encodeHtml(altAttribute));// see
																			// issue
																			// #25
				}
				pushNode(captionTagNode);
				popNode();
			}
		} finally {
			popNode(); // div
		}
	}

	/**
	 * Set the default thumb format width. This method sets a &quot;default width&quot; (220px) for images of type
	 * &quot;thumb&quot;, if no width is set in the image format string.
	 */
	protected void setDefaultThumbWidth(final ImageFormat imageFormat) {
		final int pxWidth = imageFormat.getWidth();
		final String imageType = imageFormat.getType();
		if (pxWidth == -1 && (imageType == null || "thumb".equals(imageType))) {
			// set the default thumb format width
			imageFormat.setWidth(220);
		}
	}

	@Override
	public void appendInternalLink(final String topic, final String hashSection, final String topicDescription,
			final String cssClass, final boolean parseRecursive) {
		final WPATag aTagNode = new WPATag();
		String href = encodeTitleToUrl(topic, true);
		if (hashSection != null) {
			href = href + '#' + encodeTitleDotUrl(hashSection, false);
		}
		aTagNode.addAttribute(HREF, href, true);
		if (cssClass != null) {
			aTagNode.addAttribute(CLASS, cssClass, true);
		}
		aTagNode.addObjectAttribute(WIKILINK, topic);

		pushNode(aTagNode);
		if (parseRecursive) {
			WikipediaPreTagParser.parseRecursive(topicDescription.trim(), this, false, true);
		} else {
			aTagNode.addChild(new ContentToken(topicDescription));
		}
		popNode();
	}

	@Override
	public void appendInterWikiLink(final String namespace, final String title, final String linkText) {
		final InterWiki interWiki = getInterwikiMap().getInterWiki(namespace);
		String hrefLink;
		if (interWiki != null) {
			String encodedTitle = encodeTitleToUrl(title, false);
			if (replaceColon()) {
				encodedTitle = encodedTitle.replace(':', '/');
			}
			hrefLink = interWiki.pattern.replace("$1", encodedTitle);
		} else {
			// shouldn't really happen
			hrefLink = "#";
		}
		final TagNode aTagNode = new TagNode("a");
		aTagNode.addAttribute("href", hrefLink, true);
		pushNode(aTagNode);
		WikipediaParser.parseRecursive(linkText.trim(), this, false, true);
		popNode();
	}

	@Override
	public void appendISBNLink(final String isbnPureText) {
		final StringBuilder isbnUrl = new StringBuilder(isbnPureText.length() + 100);
		isbnUrl.append("http://www.amazon.com/exec/obidos/ASIN/");

		for (int index = 0; index < isbnPureText.length(); index++) {
			if (isbnPureText.charAt(index) >= '0' && isbnPureText.charAt(index) <= '9') {
				isbnUrl.append(isbnPureText.charAt(index));
			}
		}

		final String isbnString = isbnUrl.toString();
		final TagNode aTagNode = new TagNode("a");
		append(aTagNode);
		aTagNode.addAttribute("href", isbnString, true);
		aTagNode.addAttribute("class", "external text", true);
		aTagNode.addAttribute("title", isbnString, true);
		aTagNode.addAttribute("rel", "nofollow", true);
		aTagNode.addChild(new ContentToken(isbnPureText));
	}

	@Override
	public void appendMailtoLink(final String link, final String linkName, final boolean withoutSquareBrackets) {
		final TagNode aTagNode = new TagNode("a");
		append(aTagNode);
		aTagNode.addAttribute("href", link, true);
		aTagNode.addAttribute("class", "external free", true);
		aTagNode.addAttribute("title", link, true);
		aTagNode.addAttribute("rel", "nofollow", true);
		aTagNode.addChild(new ContentToken(linkName));
	}

	@Override
	public boolean appendRawNamespaceLinks(final String rawNamespaceTopic, String viewableLinkDescription,
			final boolean containsNoPipe) {
		final int colonIndex = rawNamespaceTopic.indexOf(':');
		if (colonIndex == -1) {
			return false;
		}

		final String ns = rawNamespaceTopic.substring(0, colonIndex);

		if (isSemanticWebActive() && rawNamespaceTopic.length() > colonIndex + 1) {
			// See <a href="https://en.wikipedia.org/wiki/Semantic_MediaWiki">Semantic MediaWiki</a> for more
			// information.
			if (rawNamespaceTopic.charAt(colonIndex + 1) == ':') {
				// found an SMW relation
				final String relationValue = rawNamespaceTopic.substring(colonIndex + 2);

				if (addSemanticRelation(ns, relationValue)) {
					if (containsNoPipe) {
						viewableLinkDescription = relationValue;
					}
					if (viewableLinkDescription.trim().length() > 0) {
						appendInternalLink(relationValue, null, viewableLinkDescription, "interwiki", true);
					}
					return true;
				}
			} else if (rawNamespaceTopic.charAt(colonIndex + 1) == '=') {
				// found an SMW attribute
				final String attributeValue = rawNamespaceTopic.substring(colonIndex + 2);
				if (addSemanticAttribute(ns, attributeValue)) {
					append(new ContentToken(attributeValue));
					return true;
				}
			}
		}

		if (isInterWiki(ns)) {
			final String title = rawNamespaceTopic.substring(colonIndex + 1);
			appendInterWikiLink(ns, title, viewableLinkDescription);
			return true;
		} else if (rawNamespaceTopic.startsWith(":") && rawNamespaceTopic.lastIndexOf(":") > 0
				&& isInterWiki(rawNamespaceTopic.substring(1, rawNamespaceTopic.lastIndexOf(":")))) {
			return appendRawNamespaceLinks(rawNamespaceTopic.substring(1), viewableLinkDescription, containsNoPipe);
		}
		return false;
	}

	@Override
	public boolean appendRawWikipediaLink(final String rawLinkText, final String suffix) {
		String rawTopicName = StringEscapeUtils.unescapeHtml4(rawLinkText);
		if (rawTopicName != null) {
			// trim the name for whitespace characters on the left side
			int trimLeftIndex = 0;
			while (trimLeftIndex < rawTopicName.length() && rawTopicName.charAt(trimLeftIndex) <= ' ') {
				trimLeftIndex++;
			}
			if (trimLeftIndex > 0) {
				rawTopicName = rawTopicName.substring(trimLeftIndex);
			}
			// Is there an alias like [alias|link] ?
			final int pipeIndex = rawTopicName.indexOf('|');
			String alias = "";
			if (-1 != pipeIndex) {
				alias = rawTopicName.substring(pipeIndex + 1);
				rawTopicName = rawTopicName.substring(0, pipeIndex);
				if (alias.length() == 0) {
					// special cases like: [[Test:hello world|]] or [[Test(hello world)|]] or [[Test, hello world|]]
					alias = rawTopicName;
					int index = alias.indexOf(':');
					if (index != -1) {
						alias = alias.substring(index + 1).trim();
					} else {
						index = alias.indexOf('(');
						if (index != -1) {
							alias = alias.substring(0, index).trim();
						} else {
							index = alias.indexOf(',');
							if (index != -1) {
								alias = alias.substring(0, index).trim();
							}
						}
					}
				}
			}

			final int hashIndex = rawTopicName.lastIndexOf('#');

			String hash = null;
			if (-1 != hashIndex && hashIndex != rawTopicName.length() - 1) {
				hash = rawTopicName.substring(hashIndex + 1);
				rawTopicName = rawTopicName.substring(0, hashIndex);
			}

			// trim the name for whitespace characters on the right side
			int trimRightIndex = rawTopicName.length() - 1;
			while (trimRightIndex >= 0 && rawTopicName.charAt(trimRightIndex) <= ' ') {
				trimRightIndex--;
			}
			if (trimRightIndex != rawTopicName.length() - 1) {
				rawTopicName = rawTopicName.substring(0, trimRightIndex + 1);
			}

			// rawTopicName = Encoder.encodeHtml(rawTopicName); // see issue #25
			String viewableLinkDescriptionWithoutSuffix;
			String viewableLinkDescription;
			if (-1 != pipeIndex) {
				viewableLinkDescriptionWithoutSuffix = alias;
			} else {
				final String hashStr = hash != null ? "&#35;" + hash : ""; // #....
				if (rawTopicName.length() > 0 && rawTopicName.charAt(0) == ':') {
					viewableLinkDescriptionWithoutSuffix = rawTopicName.substring(1) + hashStr;
				} else {
					viewableLinkDescriptionWithoutSuffix = rawTopicName + hashStr;
				}
			}
			viewableLinkDescription = viewableLinkDescriptionWithoutSuffix + suffix;

			if (appendRawNamespaceLinks(rawTopicName, viewableLinkDescription, pipeIndex == -1)) {
				return true;
			}

			final int indx = rawTopicName.indexOf(':');
			String namespace = null;
			if (indx >= 0) {
				namespace = rawTopicName.substring(0, indx);
			}
			if (namespace != null && fNamespace.isNamespace(namespace, NamespaceCode.CATEGORY_NAMESPACE_KEY)) {
				// add the category to this texts metadata
				final String category = rawTopicName.substring(indx + 1).trim();
				if (category.length() > 0) {
					// TODO implement more sort-key behaviour
					// https://en.wikipedia.org/wiki/Wikipedia:Categorization#Category_sorting
					addCategory(category, viewableLinkDescriptionWithoutSuffix);
					return false;
				}
			} else if (namespace != null && fNamespace.isNamespace(namespace, NamespaceCode.FILE_NAMESPACE_KEY)) {
				parseInternalImageLink(namespace, rawLinkText);
				return false;
			} else {
				if (rawTopicName.length() > 0 && rawTopicName.charAt(0) == ':') {
					rawTopicName = rawTopicName.substring(1);
				}
				addLink(rawTopicName);
				appendInternalLink(rawTopicName, hash, viewableLinkDescription, null, true);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean appendRedirectLink(final String redirectLink) {
		fRedirectLink = redirectLink;
		return true;
	}

	@Override
	public void appendSignature(final Appendable writer, final int numberOfTildes) throws IOException {
		switch (numberOfTildes) {
		case 3:
			writer.append("~~~");
			break;
		case 4:
			writer.append("~~~~");
			break;
		case 5:
			writer.append("~~~~~");
			break;
		}
	}

	@Override
	public void appendStack(final TagStack stack) {
		if (stack != null) {
			fTagStack.append(stack);
		}
	}

	@Override
	public void buildEditLinkUrl(final int section) {
	}

	@Override
	public AbstractWikipediaParser createNewInstance(final String rawWikitext) {
		return new WikipediaParser(rawWikitext, isTemplateTopic(), getWikiListener());
	}

	/**
	 *
	 * @param isTOCIdentifier
	 *            <code>true</code> if the __TOC__ keyword was parsed
	 */
	@Override
	public ITableOfContent createTableOfContent(final boolean isTOCIdentifier) {
		if (fTableOfContentTag == null) {
			final TableOfContentTag tableOfContentTag = new TableOfContentTag("div");
			tableOfContentTag.addAttribute("id", "tableofcontent", true);
			tableOfContentTag.setShowToC(false);
			tableOfContentTag.setTOCIdentifier(isTOCIdentifier);
			fTableOfContentTag = tableOfContentTag;
			append(fTableOfContentTag);
		} else {
			if (isTOCIdentifier) {
				final TableOfContentTag tableOfContentTag = (TableOfContentTag) fTableOfContentTag.clone();
				fTableOfContentTag.setShowToC(false);
				tableOfContentTag.setShowToC(true);
				tableOfContentTag.setTOCIdentifier(true);
				fTableOfContentTag = tableOfContentTag;
				fTableOfContent = null;
				append(fTableOfContentTag);
			}
		}

		if (fTableOfContentTag != null) {
			if (fTableOfContent == null) {
				fTableOfContent = fTableOfContentTag.getTableOfContent();
			}
		}
		if (fToCSet == null) {
			fToCSet = new HashSet<>();
		}
		return fTableOfContentTag;
	}

	@Override
	public int decrementRecursionLevel() {
		return --fRecursionLevel;
	}

	@Override
	public int decrementTemplateRecursionLevel() {
		return --fTemplateRecursionCount;
	}

	@Override
	public String encodeTitleDotUrl(final String wikiTitle, final boolean firstCharacterAsUpperCase) {
		return Encoder.encodeTitleDotUrl(wikiTitle, firstCharacterAsUpperCase);
	}

	@Override
	public String encodeTitleToUrl(final String wikiTitle, final boolean firstCharacterAsUpperCase) {
		return Encoder.encodeTitleToUrl(wikiTitle, firstCharacterAsUpperCase);
	}

	/**
	 * Resolve an attribute reference. It can be in four possible places:
	 *
	 * 1. the attribute list for the current template 2. if self is an embedded template, somebody invoked us possibly
	 * with arguments--check the argument context 3. if self is an embedded template, the attribute list for the
	 * enclosing instance (recursively up the enclosing instance chain) 4. if nothing is found in the enclosing instance
	 * chain, then it might be a map defined in the group or the its supergroup etc...
	 *
	 * Attribute references are checked for validity. If an attribute has a value, its validity was checked before
	 * template rendering. If the attribute has no value, then we must check to ensure it is a valid reference. Somebody
	 * could reference any random value like $xyz$; formal arg checks before rendering cannot detect this--only the ref
	 * can initiate a validity check. So, if no value, walk up the enclosed template tree again, this time checking
	 * formal parameters not attributes Map. The formal definition must exist even if no value.
	 *
	 * To avoid infinite recursion in toString(), we have another condition to check regarding attribute values. If your
	 * template has a formal argument, foo, then foo will hide any value available from "above" in order to prevent
	 * infinite recursion.
	 *
	 * This method is not static so people can override functionality.
	 */
	@Override
	public Object getAttribute(final String attribute) { // StringTemplate self,
		// String
		// attribute) {
		// System.out.println("### get("+self.getEnclosingInstanceStackString()+", "+
		// attribute+")");
		// System.out.println("attributes="+(self.attributes!=null?self.attributes.
		// keySet().toString():"none"));
		// if ( self==null ) {
		// return null;
		// }
		//
		// if ( lintMode ) {
		// self.trackAttributeReference(attribute);
		// }

		// is it here?
		Object o = null;
		if (attributes != null) {
			o = attributes.get(attribute);
		}

		// // nope, check argument context in case embedded
		// if ( o==null ) {
		// Map argContext = self.getArgumentContext();
		// if ( argContext!=null ) {
		// o = argContext.get(attribute);
		// }
		// }
		//
		// if ( o==null &&
		// !self.passThroughAttributes &&
		// self.getFormalArgument(attribute)!=null )
		// {
		// // if you've defined attribute as formal arg for this
		// // template and it has no value, do not look up the
		// // enclosing dynamic scopes. This avoids potential infinite
		// // recursion.
		// return null;
		// }
		//
		// // not locally defined, check enclosingInstance if embedded
		// if ( o==null && self.enclosingInstance!=null ) {
		// /*
		// System.out.println("looking for "+getName()+"."+attribute+" in super="+
		// enclosingInstance.getName());
		// */
		// Object valueFromEnclosing = get(self.enclosingInstance, attribute);
		// if ( valueFromEnclosing==null ) {
		// checkNullAttributeAgainstFormalArguments(self, attribute);
		// }
		// o = valueFromEnclosing;
		// }
		//
		// // not found and no enclosing instance to look at
		// else if ( o==null && self.enclosingInstance==null ) {
		// // It might be a map in the group or supergroup...
		// o = self.group.getMap(attribute);
		// }

		return o;
	}

	/**
	 * What renderer is registered for this attributeClassType for this template. If not found, the template's group is
	 * queried.
	 */
	@Override
	public AttributeRenderer getAttributeRenderer(final Class<?> attributeClassType) {
		AttributeRenderer renderer = null;
		if (attributeRenderers != null) {
			renderer = (AttributeRenderer) attributeRenderers.get(attributeClassType);
		}
		if (renderer != null) {
			return renderer;
		}
		return null;
	}

	@Override
	public Map<String, SourceCodeFormatter> getCodeFormatterMap() {
		return fConfiguration.getCodeFormatterMap();
	}

	@Override
	public Date getCurrentTimeStamp() {
		return new Date(System.currentTimeMillis());
	}

	@Override
	public InterWikiMap getInterwikiMap() {
		return fInterWikiMap;
	}

	@Override
	public Locale getLocale() {
		return fLocale;
	}

	@Override
	public synchronized int getNextNumber() {
		return fNextNumberCounter++;
	}

	@Override
	public TagToken getNode(final int offset) {
		return fTagStack.get(offset);
	}

	@Override
	public String getPageName() {
		return fPageTitle;
	}

	@Override
	public MagicWordE getMagicWord(final String name) {
		return MagicWord.getMagicWord(name);
	}

	@Override
	public String getRawWikiContent(final ParsedPageName parsedPagename, final Map<String, String> templateParameters)
			throws WikiModelContentException {
		final INamespaceValue namespace = parsedPagename.namespace;
		final String templateName = parsedPagename.pagename;
		if (Configuration.RAW_CONTENT) {
			System.out.println("AbstractWikiModel raw: " + " " + namespace + " " + templateName);
		}
		if (parsedPagename.magicWord instanceof MagicWordE) {
			return MagicWord.processMagicWord((MagicWordE) parsedPagename.magicWord, parsedPagename.magicWordParameter,
					this);
		}
		return null;
	}

	@Override
	public int getRecursionLevel() {
		return fRecursionLevel;
	}

	@Override
	public String getRedirectLink() {
		return fRedirectLink;
	}

	@Override
	public List<Reference> getReferences() {
		return fReferences;
	}

	@Override
	public ResourceBundle getResourceBundle() {
		return fNamespace.getResourceBundle();
	}

	@Override
	public List<SemanticAttribute> getSemanticAttributes() {
		return null;
	}

	@Override
	public List<SemanticRelation> getSemanticRelations() {
		return null;
	}

	@Override
	public SimpleDateFormat getSimpleDateFormat() {
		if (fFormatter != null) {
			return fFormatter;
		}
		fFormatter = new SimpleDateFormat();
		final TimeZone utc = TimeZone.getTimeZone("GMT+00");
		fFormatter.setTimeZone(utc);
		return fFormatter;
	}

	@Override
	public ITableOfContent getTableOfContent() {
		return fTableOfContentTag;
	}

	@Override
	public Map<String, String> getTemplateCallsCache() {
		return fConfiguration.getTemplateCallsCache();
	}

	@Override
	public ITemplateFunction getTemplateFunction(final String name) {
		return getTemplateMap().get(name);
	}

	@Override
	public Map<String, ITemplateFunction> getTemplateMap() {
		return fConfiguration.getTemplateMap();
	}

	@Override
	public Map<String, TagToken> getTokenMap() {
		return fConfiguration.getTokenMap();
	}

	@Override
	public Set<String> getUriSchemeSet() {
		return fConfiguration.getUriSchemeSet();
	}

	@Override
	public Casing casing() {
		return fConfiguration.casing();
	}

	@Override
	public IEventListener getWikiListener() {
		return fWikiListener;
	}

	@Override
	public int incrementRecursionLevel() {
		return ++fRecursionLevel;
	}

	@Override
	public int incrementTemplateRecursionLevel() {
		return ++fTemplateRecursionCount;
	}

	protected void initialize() {
		if (!fInitialized) {
			fWikiListener = null;
			fFormatter = null;
			fToCSet = null;
			fTableOfContent = null;
			fTableOfContentTag = null;
			fTagStack = new TagStack();
			fReferences = null;
			fReferenceNames = null;
			fRecursionLevel = 0;
			fTemplateRecursionCount = 0;
			fSectionCounter = 0;
			fExternalLinksCounter = 0;
			fInitialized = true;
			fTemplates = new HashMap<>();
		}
	}

	@Override
	public boolean isCamelCaseEnabled() {
		return false;
	}

	@Override
	public boolean isEditorMode() {
		return false;
	}

	@Override
	public boolean isInterWiki(final String namespace) {
		return !isNamespace(namespace) && getInterwikiMap().getInterWiki(namespace) != null;
	}

	@Override
	public boolean isMathtranRenderer() {
		return false;
	}

	@Override
	public boolean isNamespace(final String namespace) {
		return fNamespace.getNamespace(namespace) != null;
	}

	@Override
	public boolean isNoToc() {
		return fNoToc;
	}

	@Override
	public boolean isParameterParsingMode() {
		return fParameterParsingMode;
	}

	@Override
	public boolean isPreviewMode() {
		return false;
	}

	@Override
	public boolean isSemanticWebActive() {
		return false;
	}

	@Override
	public boolean isTemplateTopic() {
		return fTemplateTopic;
	}

	@Override
	public boolean isValidUriScheme(final String uriScheme) {
		return getUriSchemeSet().contains(uriScheme);
	}

	@Override
	public boolean isValidUriSchemeSpecificPart(final String uriScheme, final String uriSchemeSpecificPart) {

		if (uriScheme.equals("ftp") || uriScheme.equals("http") || uriScheme.equals("https")) {
			return uriSchemeSpecificPart.length() >= 2 && uriSchemeSpecificPart.substring(0, 2).equals("//");
		} else {
			return true;
		}
	}

	@Override
	public boolean parseBehaviorSwitch(final String identifier) {
		for (int i = 0; i < WikipediaParser.TOC_IDENTIFIERS.length; i++) {
			if (WikipediaParser.TOC_IDENTIFIERS[i].equals(identifier)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void parseEvents(final IEventListener listener, final String rawWikiText) {
		initialize();
		if (rawWikiText == null) {
			return;
		}
		fWikiListener = listener;
		WikipediaParser.parse(rawWikiText, this, false, null);
		fInitialized = false;
	}

	@Override
	public String parseTemplates(final String rawWikiText) {
		return parseTemplates(rawWikiText, false);
	}

	@Override
	public String parseTemplates(final String rawWikiText, final boolean parseOnlySignature) {
		if (rawWikiText == null) {
			return "";
		}
		if (!parseOnlySignature) {
			initialize();
		}
		final StringBuilder buf = new StringBuilder(rawWikiText.length() + rawWikiText.length() / 10);
		try {
			// TemplateParser.parse(rawWikiText, this, buf, parseOnlySignature,
			// true);
			TemplateParser.parseRecursive(rawWikiText, this, buf, parseOnlySignature, true, null);

		} catch (final Exception e) {
			Throwables.log(log, e);
			buf.append("<span class=\"error\">TemplateParser exception: ").append(e.getClass().getSimpleName())
					.append("</span>");
		}
		return buf.toString();
	}

	@Override
	public TagToken peekNode() {
		return fTagStack.peek();
	}

	@Override
	public TagToken popNode() {
		return fTagStack.pop();
	}

	@Override
	public boolean pushNode(final TagToken node) {
		return fTagStack.push(node);
	}

	/**
	 * Map a value to a named attribute. Throw NoSuchElementException if the named attribute is not formally defined in
	 * self's specific template and a formal argument list exists.
	 */
	protected void rawSetAttribute(final Map<String, Object> attributes, final String name, final Object value) {
		if (value == null) {
			return;
		}
		attributes.put(name, value);
	}

	/**
	 * Register a renderer for all objects of a particular type. This overrides any renderer set in the group for this
	 * class type.
	 */
	@Override
	public void registerRenderer(final Class<?> attributeClassType, final AttributeRenderer renderer) {
		if (attributeRenderers == null) {
			attributeRenderers = new HashMap<>();
		}
		attributeRenderers.put(attributeClassType, renderer);
	}

	@Override
	public String render(final ITextConverter converter, final String rawWikiText) throws IOException {
		return render(converter, rawWikiText, false);
	}

	@Override
	public String render(final ITextConverter converter, final String rawWikiText, final boolean templateTopic)
			throws IOException {
		initialize();
		if (rawWikiText == null) {
			return "";
		}
		final StringBuilder buf = new StringBuilder(rawWikiText.length() + rawWikiText.length() / 10);

		render(converter, rawWikiText, buf, templateTopic, true);

		return buf.toString();
	}

	@Override
	public void render(final ITextConverter converter, final String rawWikiText, final Appendable buf,
			final boolean templateTopic, final boolean parseTemplates) throws IOException {
		initialize();
		if (rawWikiText == null) {
			return;
		}
		fTemplateTopic = templateTopic;
		WikipediaParser.parse(rawWikiText, this, parseTemplates, null);
		if (converter != null) {
			final List<BaseToken> list = fTagStack.getNodeList();
			try {
				converter.nodesToText(list, buf, this);
			} finally {
				fInitialized = false;
			}
			return;
		}
		fInitialized = false;
	}

	@Override
	public String render(final String rawWikiText) throws IOException {
		return render(rawWikiText, false);
	}

	@Override
	public String render(final String rawWikiText, final boolean templateTopic) throws IOException {
		return render(new HTMLConverter(), rawWikiText, templateTopic);
	}

	@Override
	public String renderPDF(final String rawWikiText) throws IOException {
		return render(new PDFConverter(), rawWikiText, false);
	}

	@Override
	public boolean replaceColon() {
		return true;
	}

	/**
	 * Set an attribute for this template. If you set the same attribute more than once, you get a multi-valued
	 * attribute. If you send in a StringTemplate object as a value, it's enclosing instance (where it will inherit
	 * values from) is set to 'this'. This would be the normal case, though you can set it back to null after this call
	 * if you want. If you send in a List plus other values to the same attribute, they all get flattened into one List
	 * of values. This will be a new list object so that incoming objects are not altered. If you send in an array, it
	 * is converted to an ArrayIterator.
	 */
	@Override
	public void setAttribute(final String name, final Object value) {
		if (value == null || name == null) {
			return;
		}
		if (name.indexOf('.') >= 0) {
			throw new IllegalArgumentException("cannot have '.' in attribute names");
		}
		if (attributes == null) {
			attributes = new HashMap<>();
		}

		AttributeList v;

		final Object o = attributes.get(name);
		if (o == null) { // new attribute
			if (value instanceof List) {
				@SuppressWarnings("unchecked")
				final List<Object> list = (List<Object>) value;
				v = new AttributeList(list.size());
				// flatten incoming list into existing
				v.addAll(list);
				rawSetAttribute(attributes, name, v);
				return;
			}
			rawSetAttribute(attributes, name, value);
			return;
		}

		if (o.getClass() == AttributeList.class) { // already a list made by ST
			v = (AttributeList) o;
		} else if (o instanceof List) { // existing attribute is non-ST List
			// must copy to an ST-managed list before adding new attribute
			@SuppressWarnings("unchecked")
			final List<Object> listAttr = (List<Object>) o;
			v = new AttributeList(listAttr.size());
			v.addAll(listAttr);
			rawSetAttribute(attributes, name, v); // replace attribute
													// w/list
		} else {
			// non-list second attribute, must convert existing to ArrayList
			v = new AttributeList(); // make list to hold multiple values
			// make it point to list now
			rawSetAttribute(attributes, name, v); // replace attribute
													// w/list
			v.add(o); // add previous single-valued attribute
		}
		if (value instanceof List) {
			// flatten incoming list into existing
			if (v != value) { // avoid weird cyclic add
				@SuppressWarnings("unchecked")
				final List<Object> list = (List<Object>) value;
				v.addAll(list);
			}
		} else {
			v.add(value);
		}
	}

	/**
	 * Specify a complete map of what object classes should map to which renderer objects.
	 */
	public void setAttributeRenderers(final Map<Class<?>, Object> renderers) {
		attributeRenderers = renderers;
	}

	@Override
	public void setPageName(final String pageTitle) {
		fPageTitle = pageTitle;
	}

	@Override
	public void setParameterParsingMode(final boolean parameterParsingMode) {
		fParameterParsingMode = parameterParsingMode;
	}

	@Override
	public void setSemanticWebActive(final boolean semanticWeb) {

	}

	@Override
	public void setTemplateCallsCache(final Map<String, String> map) {
		fConfiguration.setTemplateCallsCache(map);
	}

	@Override
	public void setUp() {
		fFormatter = null;
		fToCSet = null;
		fTableOfContent = null;
		fTableOfContentTag = null;
		fTagStack = new TagStack();
		fReferences = null;
		fReferenceNames = null;
		fRecursionLevel = 0;
		fTemplateRecursionCount = 0;
		fRedirectLink = null;
		fSectionCounter = 0;
		fExternalLinksCounter = 0;
		fTemplates = new HashMap<>();
		fParameterParsingMode = false;
	}

	@Override
	public boolean showSyntax(final String tagName) {
		return true;
	}

	@Override
	public int stackSize() {
		return fTagStack.size();
	}

	/**
	 * Substitute the template name by the template content and parameters and append the new content to the writer.
	 *
	 * @param templateName
	 *            the name of the template
	 * @param parameterMap
	 *            the templates parameter <code>java.util.SortedMap</code>
	 * @param writer
	 *            the buffer to append the substituted template content
	 * @throws IOException
	 */
	@Override
	public void substituteTemplateCall(final String templateName, final Map<String, String> parameterMap,
			final Appendable writer) throws IOException {
		Counter val = null;
		try {
			final ParsedPageName parsedPagename = ParsedPageName.parsePageName(this, templateName,
					fNamespace.getTemplate(), true, true);
			if (!parsedPagename.valid) {
				writer.append("{{");
				writer.append(templateName);
				writer.append("}}");
				return;
			}
			final String fullTemplateStr = parsedPagename.fullPagename();

			val = fTemplates.get(fullTemplateStr);
			if (val == null) {
				val = new Counter(0);
				fTemplates.put(fullTemplateStr, val);
			}
			if (val.inc() > 1) {
				writer.append("<span class=\"error\">Template loop detected: <strong class=\"selflink\">")
						.append(fullTemplateStr).append("</strong></span>");
				return;
			}

			Map<String, String> templateCallsCache;
			String cacheKey = null;
			int cacheKeyLength = 0;
			templateCallsCache = fConfiguration.getTemplateCallsCache();
			if (templateCallsCache != null) {
				cacheKeyLength += fullTemplateStr.length() + 1;
				for (final Entry<String, String> entry : parameterMap.entrySet()) {
					cacheKeyLength += entry.getKey().length() + entry.getValue().length() + 2;
				}
				if (cacheKeyLength < Configuration.MAX_CACHE_KEY_LENGTH) {
					final StringBuilder cacheKeyBuffer = new StringBuilder(cacheKeyLength);
					cacheKeyBuffer.append(fullTemplateStr);
					cacheKeyBuffer.append("|");
					for (final Entry<String, String> entry : parameterMap.entrySet()) {
						cacheKeyBuffer.append(entry.getKey());
						cacheKeyBuffer.append("=");
						cacheKeyBuffer.append(entry.getValue());
						cacheKeyBuffer.append("|");
					}
					cacheKey = cacheKeyBuffer.toString();

					final String value = templateCallsCache.get(cacheKey);
					if (value != null) {
						// System.out.println("Cache key: " + cacheKey);
						writer.append(value);
						if (Configuration.TEMPLATE_NAMES) {
							System.out.println("Cached: " + fullTemplateStr + "-" + cacheKey);
						}
						return;
					}
				}
				if (Configuration.TEMPLATE_NAMES) {
					System.out.println("Not Cached: " + fullTemplateStr + "-" + cacheKeyLength);
				}
			}

			if (parsedPagename.namespace.isType(NamespaceCode.TEMPLATE_NAMESPACE_KEY)) {
				if (isParameterParsingMode() && templateName.equals("!") && parameterMap.isEmpty()) {
					writer.append("{{").append(templateName).append("}}");
					return;
				}
				addTemplate(parsedPagename.pagename);
			} else {
				addInclude(fullTemplateStr);
				// invalidate cache:
				templateCallsCache = null;
			}

			String plainContent;
			try {
				plainContent = getRawWikiContent(parsedPagename, parameterMap);
			} catch (final WikiModelContentException wme) {
				writer.append(wme.getMessage());
				return;
			}
			if (plainContent == null) {
				// content of this transclusion is missing => render as link:
				plainContent = "[[:" + fullTemplateStr + "]]";
			}

			final StringBuilder templateBuffer = new StringBuilder(plainContent.length());

			TemplateParser.parseRecursive(plainContent.trim(), this, templateBuffer, false, false, parameterMap);

			if (templateCallsCache != null && cacheKey != null) {
				// save this template call in the cache
				final String cacheValue = templateBuffer.toString();
				templateCallsCache.put(cacheKey, cacheValue);
				writer.append(cacheValue);
			} else {
				writer.append(templateBuffer);
			}
		} finally {
			if (val != null) {
				val.dec();
			}
		}
	}

	@Override
	public TagStack swapStack(final TagStack stack) {
		final TagStack temp = fTagStack;
		fTagStack = stack;
		return temp;
	}

	@Override
	public void tearDown() {

	}

	/**
	 * Reduce the current token stack until an allowed parent is at the top of the stack
	 */
	@Override
	public void reduceTokenStack(final TagToken node) {
		final String allowedParents = node.getParents();
		if (allowedParents != null) {
			TagToken tag;
			int index;

			while (stackSize() > 0) {
				tag = peekNode();
				index = allowedParents.indexOf("|" + tag.getName() + "|");
				if (index < 0) {
					popNode();
					if (tag.getName().equals(node.getName())) {
						// for wrong nested HTML tags like <table>
						// <tr><td>number
						// 1<tr><td>number 2</table>
						break;
					}
				} else {
					break;
				}
			}
			if (stackSize() == 0 && node instanceof HTMLTag) {
				final HTMLTag defParent = ((HTMLTag) node).getDefaultParentTag();
				if (defParent != null) {
					pushNode((HTMLTag) defParent.clone());
				}
			}
		} else {
			while (stackSize() > 0) {
				popNode();
			}
		}
	}

	@Override
	public String getNamespaceName() {
		return fNamespaceName;
	}

	@Override
	public void setNamespaceName(final String namespaceLowercase) {
		if (namespaceLowercase == null) {
			fNamespaceName = "";
			return;
		}
		// TODO: we should only allow valid namespaces and probably set pagename and
		// namespace in one go
		final INamespaceValue nsVal = fNamespace.getNamespace(namespaceLowercase);
		if (nsVal != null) {
			fNamespaceName = nsVal.getPrimaryText();
		} else {
			fNamespaceName = namespaceLowercase;
		}
	}

	@Override
	public void setNoToc(final boolean disableToc) {
		fNoToc = disableToc;
	}

	@Override
	public String getImageBaseURL() {
		return "/wiki/${image}";
	}

	@Override
	public String getWikiBaseURL() {
		return "/wiki/${title}";
	}

	@Override
	public String getWikiBaseEditURL() {
		if (fLocale != null) {
			final String lang = fLocale.getLanguage();
			return "http://" + lang + ".wikipedia.org/w/index.php?title=${title}";
		}
		return "http://en.wikipedia.org/w/index.php?title=${title}";
	}

	@Override
	public String[] splitNsTitle(final String fullTitle) {
		return fNamespace.splitNsTitle(fullTitle, true, ' ', true);
	}

}
