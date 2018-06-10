package info.bliki.wiki.tags.code;

import info.bliki.wiki.filter.FilterTestSupport;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaScriptTest extends FilterTestSupport {
	@Test
	public void testJavaScript() throws Exception {
		String result = wikiModel.render("'''JavaScript Example'''\n" + "<source lang=javascript>\n"
				+ "public class Test {\n" + "< > \" \' &" + "}\n" + "</source>", false);

		assertThat(result).isEqualTo("\n" + "<p><b>JavaScript Example</b>\n" + "</p><pre class=\"javascript\">\n"
				+ "<span style=\"color:#7F0055; font-weight: bold; \">public</span> <span style=\"color:#7F0055; font-weight: bold; \">class</span> Test {\n"
				+ "&#60; &#62; <span style=\"color:#2A00FF; \">&#34; &#39; &#38;}\n" + "</span></pre>");
	}
}
