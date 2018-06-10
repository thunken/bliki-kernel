package info.bliki.wiki.tags.code;

import info.bliki.wiki.filter.FilterTestSupport;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CSharpTest extends FilterTestSupport {
	@Test
	public void testCSharp() throws Exception {
		String result = wikiModel.render("'''C# Example'''\n" + "<source lang=csharp>\n" + "public class Test {\n"
				+ "< > \" \' &" + "}\n" + "</source>", false);

		assertThat(result).isEqualTo("\n" + "<p><b>C# Example</b>\n" + "</p><pre class=\"csharp\">\n"
				+ "<span style=\"color:#7F0055; font-weight: bold; \">public</span> <span style=\"color:#7F0055; font-weight: bold; \">class</span> Test {\n"
				+ "&#60; &#62; <span style=\"color:#2A00FF; \">&#34; &#39; &#38;}\n" + "</span></pre>");
	}
}
