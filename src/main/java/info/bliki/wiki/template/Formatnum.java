package info.bliki.wiki.template;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import info.bliki.util.Throwables;
import info.bliki.wiki.model.Configuration;
import info.bliki.wiki.model.IWikiModel;
import lombok.extern.slf4j.Slf4j;

/**
 * A template parser function for <code>{{formatnum: ... }}</code> <i>lower case</i> syntax. See
 * <a href="https://en.wikipedia.org/wiki/Help:Variable#Formatting">Wikipedia - Help:Variable#Formatting</a>
 *
 */
@Slf4j
public class Formatnum extends AbstractTemplateFunction {
	public final static ITemplateFunction CONST = new Formatnum();

	@Override
	public String parseFunction(final List<String> list, final IWikiModel model, final char[] src, final int beginIndex,
			final int endIndex, final boolean isSubst) {
		if (list.size() > 0) {
			String result = isSubst ? list.get(0) : parseTrim(list.get(0), model);
			if (result.length() > 0) {
				try {
					final NumberFormat nf = NumberFormat.getNumberInstance(model.getLocale());
					if (list.size() > 1 && list.get(1).equalsIgnoreCase("r")) {
						final Number num = nf.parse(result);
						if (num instanceof Double) {
							result = Expr.getWikiNumberFormat(num.doubleValue(), model);
						} else {
							result = num.toString();
						}
					} else {
						final Double dbl = new Double(result);
						// decimal number that will be rounded down by NumberFormat#format()?
						if (result.endsWith(".")) {
							final DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(model.getLocale());
							result = nf.format(dbl) + df.getDecimalFormatSymbols().getDecimalSeparator();
						} else if (dbl == dbl.intValue()) {
							final int idx = result.indexOf('.');
							if (idx != -1) {
								nf.setMinimumFractionDigits(result.length() - 1 - idx);
							}
							result = nf.format(dbl);
						} else {
							result = nf.format(dbl);
						}
					}
				} catch (final Exception ex) {
					if (Configuration.DEBUG) {
						log.debug("formatnum error: " + list.toString());
					}
					if (Configuration.STACKTRACE) {
						Throwables.log(log, ex);
					}
				}
			}
			return result;
		}
		return null;
	}
}
