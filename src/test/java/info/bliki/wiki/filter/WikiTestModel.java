package info.bliki.wiki.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import info.bliki.htmlcleaner.ContentToken;
import info.bliki.htmlcleaner.TagNode;
import info.bliki.htmlcleaner.Utils;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.WikiModel;
import info.bliki.wiki.model.WikiModelContentException;
import info.bliki.wiki.tags.IgnoreTag;
import info.bliki.wiki.tags.extension.ChartTag;
import lombok.extern.slf4j.Slf4j;

/**
 * Wiki model implementation which allows some special JUnit tests with namespaces and predefined templates
 */
@Slf4j
public class WikiTestModel extends WikiModel {
	private static final String MAIN_FOO = "BAR";
	private static final String FOODATE = "FOODATE";
	private boolean semanticWebActive;
	private final String resourceBase;
	private boolean debug;

	static {
		TagNode.addAllowedAttribute("style");
	}

	private static Configuration getTestConfiguration() {
		final Configuration configuration = new Configuration();
		configuration.addUriScheme("tel");
		configuration.addInterwikiLink("intra", "/$1");
		configuration.addTokenTag("chart", new ChartTag());
		configuration.addTokenTag("inputbox", new IgnoreTag("inputbox"));
		configuration.addTokenTag("imagemap", new IgnoreTag("imagemap"));
		configuration.setTemplateCallsCache(new HashMap<String, String>());
		return configuration;
	}

	public WikiTestModel(final Locale locale, final String imageBaseURL, final String linkBaseURL,
			final String resourceBase) {
		super(getTestConfiguration(), locale, imageBaseURL, linkBaseURL);
		semanticWebActive = false;
		this.resourceBase = resourceBase;
		fNamespace.getImage().addAlias("Bild");
	}

	@Override
	public String getRawWikiContent(final ParsedPageName page, final Map<String, String> map)
			throws WikiModelContentException {
		if (debug) {
			log.error("getRawWikiContent(" + page + ")");
		}
		final String result = super.getRawWikiContent(page, map);
		if (result != null) {
			return result;
		}
		final String name = encodeTitleToUrl(page.pagename, false);
		switch (page.namespace.getCode()) {
		case TEMPLATE_NAMESPACE_KEY:
			switch (name) {
			case FOODATE:
				return "FOO" + System.currentTimeMillis();
			default:
				return loadTemplateResource(name);
			}
		case MODULE_NAMESPACE_KEY:
			return loadModuleResource(name);
		case MAIN_NAMESPACE_KEY:
			switch (name) {
			case "Include_Page":
				return "an include page";
			case "FOO":
				return MAIN_FOO;
			}
		default: {
			return null;
		}
		}
	}

	@Override
	public boolean isSemanticWebActive() {
		return semanticWebActive;
	}

	@Override
	public void setSemanticWebActive(final boolean semanticWeb) {
		semanticWebActive = semanticWeb;
	}

	@Override
	public boolean showSyntax(final String tagName) {
		return true;
	}

	@Override
	public void appendExternalLink(final String uriSchemeName, String link, final String linkName,
			final boolean withoutSquareBrackets) {
		if (uriSchemeName.equalsIgnoreCase("tel")) {
			// example for a telephone link
			link = Utils.escapeXml(link, true, false, false);
			final TagNode aTagNode = new TagNode("a");
			aTagNode.addAttribute("href", link, true);
			aTagNode.addAttribute("class", "telephonelink", true);
			aTagNode.addAttribute("title", link, true);
			if (withoutSquareBrackets) {
				append(aTagNode);
				aTagNode.addChild(new ContentToken(linkName));
			} else {
				final String trimmedText = linkName.trim();
				if (trimmedText.length() > 0) {
					pushNode(aTagNode);
					WikipediaParser.parseRecursive(trimmedText, this, false, true);
					popNode();
				}
			}
			return;
		}
		super.appendExternalLink(uriSchemeName, link, linkName, withoutSquareBrackets);
	}

	private String loadTemplateResource(final String name) {
		return loadResource(resourceNameFromTemplateName(name));
	}

	private String loadModuleResource(final String name) {
		return loadResource(resourceNameFromModuleName(name));
	}

	private String loadResource(final String name) {
		if (name == null) {
			return null;
		}
		if (debug) {
			log.error("loading " + name);
		}
		try (InputStream is = getClass().getResourceAsStream(name)) {
			return is == null ? null : IOUtils.toString(is);
		} catch (final IOException e) {
			log.error("error loading " + name, e);
			throw new RuntimeException(e);
		}
	}

	private String resourceNameFromTemplateName(final String name) {
		return getResource("templates", name, null);
	}

	private String resourceNameFromModuleName(final String name) {
		return getResource("modules", name, "lua");
	}

	private String getResource(final String type, final String name, final String ext) {
		if (name.trim().length() == 0) {
			return null;
		} else {
			return String.format("/%s/%s/%s%s", resourceBase, type, name.replaceAll("[ /]", "_"),
					ext != null ? "." + ext : "");
		}
	}

	public void setDebug(final boolean debug) {
		this.debug = debug;
	}
}
