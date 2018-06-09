package info.bliki.wiki.template;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.namespaces.INamespace;
import info.bliki.wiki.namespaces.INamespace.INamespaceValue;

/**
 * A template parser function for <code>{{ns: ... }}</code> <i>namespace/i> syntax
 *
 * From <a href="https://www.mediawiki.org/wiki/Help:Magic_words#Namespaces_2"> MediaWiki</a>:
 *
 * {{ns:}} returns the current localized name for the namespace with that index, canonical name, or local alias. Thus
 * {{ns:6}}, {{ns:File}}, and {{ns:Image}} (an old name for the File namespace) all return "File". On a wiki where the
 * content language was French, {{ns:Fichier}} would also be valid, but {{ns:Datei}} (the localisation of "File" into
 * German) would not.
 */
public class NS extends AbstractTemplateFunction {
	public final static ITemplateFunction CONST = new NS();

	@Override
	public String parseFunction(final List<String> list, final IWikiModel model, final char[] src, final int beginIndex,
			final int endIndex, final boolean isSubst) {
		if (list.size() > 0) {
			final String arg0 = isSubst ? list.get(0) : parseTrim(list.get(0), model);
			final INamespace namespace = model.getNamespace();
			try {
				final int numberCode = Integer.valueOf(arg0).intValue();
				try {
					return Optional.ofNullable(namespace).map(ns -> ns.getNamespaceByNumber(numberCode))
							.map(INamespaceValue::toString).orElse(null);
				} catch (final InvalidParameterException ipe) {
					// nothing to do
				}
			} catch (final NumberFormatException nfe) {
				// the given argument could not be parsed as integer number
				final INamespaceValue value = namespace.getNamespace(arg0);
				if (value != null) {
					return value.getPrimaryText();
				}
				return "[[:" + namespace.getTemplate().getPrimaryText() + ":Ns:" + arg0 + "]]";
			}
		}
		return null;
	}

}
