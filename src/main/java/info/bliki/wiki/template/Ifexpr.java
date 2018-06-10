package info.bliki.wiki.template;

import java.util.List;

import info.bliki.util.Throwables;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.template.expr.eval.DoubleEvaluator;
import lombok.extern.slf4j.Slf4j;

/**
 * A template parser function for <code>{{ #ifexpr: ... }}</code> syntax
 *
 * See <a href="https://www.mediawiki.org/wiki/Help:Extension:ParserFunctions"> Mediwiki's
 * Help:Extension:ParserFunctions</a> See:
 * <a href="https://svn.wikimedia.org/viewvc/mediawiki/trunk/extensions/ParserFunctions/Expr.php?view=markup" >Expr.php
 * in MediaWiki SVN</a>
 */
@Slf4j
public class Ifexpr extends AbstractTemplateFunction {
	public final static ITemplateFunction CONST = new Ifexpr();

	@Override
	public String parseFunction(final List<String> list, final IWikiModel model, final char[] src, final int beginIndex,
			final int endIndex, final boolean isSubst) {
		if (list.size() > 1) {
			final String condition = isSubst ? list.get(0) : parseTrim(list.get(0), model);
			if (condition.length() > 0) {
				try {
					final DoubleEvaluator engine = new DoubleEvaluator();
					final double d = engine.evaluate(condition);
					// if (d == 0.0) {
					if (Math.abs(d - 0.0) < DoubleEvaluator.EPSILON) {
						if (list.size() >= 3) {
							// &lt;else text&gt;
							return isSubst ? list.get(2) : parseTrim(list.get(2), model);
						}
						return null;
					}
					return isSubst ? list.get(1) : parseTrim(list.get(1), model);
				} catch (final Exception e) {
					if (Configuration.DEBUG) {
						log.debug("#ifexpr error: " + condition);
					}
					if (Configuration.STACKTRACE) {
						Throwables.log(log, e);
					}
					return "<div class=\"error\">Expression error: " + e.getMessage() + "</div>";
				}
			} else {
				if (list.size() >= 3) {
					// &lt;else text&gt;
					return isSubst ? list.get(2) : parseTrim(list.get(2), model);
				}
			}
		}
		return null;
	}
}
