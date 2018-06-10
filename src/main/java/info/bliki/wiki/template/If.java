package info.bliki.wiki.template;

import java.util.List;

import info.bliki.util.Throwables;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.IWikiModel;
import lombok.extern.slf4j.Slf4j;

/**
 * A template parser function for <code>{{ #if: ... }}</code> syntax. See
 * <a href ="https://www.mediawiki.org/wiki/Help:Extension:ParserFunctions">Mediwiki's
 * Help:Extension:ParserFunctions</a>
 *
 */
@Slf4j
public class If extends AbstractTemplateFunction {

	public final static ITemplateFunction CONST = new If();

	@Override
	public String parseFunction(final List<String> list, final IWikiModel model, final char[] src, final int beginIndex,
			final int endIndex, final boolean isSubst) {
		if (list.size() > 1) {
			String ifCondition = "";
			try {
				ifCondition = isSubst ? list.get(0) : parseTrim(list.get(0), model);
				if (ifCondition.length() > 0) {
					// &lt;then text&gt;
					return isSubst ? list.get(1) : parseTrim(list.get(1), model);
				} else {
					if (list.size() >= 3) {
						// &lt;else text&gt;
						return isSubst ? list.get(2) : parseTrim(list.get(2), model);
					}
				}
			} catch (final Exception e) {
				if (Configuration.DEBUG) {
					log.debug("#if error: " + ifCondition);
				}
				if (Configuration.STACKTRACE) {
					Throwables.log(log, e);
				}
				return "<div class=\"error\">Expression error: " + e.getMessage() + "</div>";
			}
		}
		return null;
	}
}
