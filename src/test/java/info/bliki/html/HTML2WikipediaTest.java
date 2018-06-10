package info.bliki.html;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import info.bliki.html.wikipedia.ToWikipedia;

public class HTML2WikipediaTest {

	@Test
	public void test0() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		conv.setInputHTML("<b>hello<em>world</em></b>");
		final String result = conv.toWiki(new ToWikipedia());
		assertThat("'''hello''world'''''").isEqualTo(result);
	}

	@Test
	public void test1() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		conv.setInputHTML("<ul><li>hello world<ol><li>hello subworld</li></ol></li><li>second line</li></ul>");
		final String result = conv.toWiki(new ToWikipedia());
		assertThat("\n* hello world\n" + "*# hello subworld\n" + "* second line\n").isEqualTo(result);
	}

	@Test
	public void test2() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		// invalid nested html test
		conv.setInputHTML("<ul><li>hello world<ol><li>hello subworld</ol><li>second line</ul>");
		final String result = conv.toWiki(new ToWikipedia());
		assertThat("\n* hello world\n" + "*# hello subworld\n" + "* second line\n").isEqualTo(result);
	}

	@Test
	public void test3() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		// invalid nested html test
		conv.setInputHTML("<h2>the good</h2><h3>the bad</h3><h2>and the ugly</h2>");
		final String result = conv.toWiki(new ToWikipedia());
		assertThat("\n" + "== the good ==\n" + "\n" + "=== the bad ===\n" + "\n" + "== and the ugly ==\n")
				.isEqualTo(result);
	}

	@Test
	public void test4() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		// invalid nested html test
		conv.setInputHTML("<table>\n" + "<tr>\n"
				+ "<td><i><a href=\"/wiki/Klasse_%28Biologie%29\" title=\"Klasse (Biologie)\">Klasse</a>:</i></td>\n"
				+ "<td><a href=\"/wiki/V%C3%B6gel\" title=\"Vögel\">Vögel</a> (Aves)</td>\n" + "</tr>\n" + "<tr>\n"
				+ "<td><i><a href=\"/wiki/Klasse_%28Biologie%29\" title=\"Klasse (Biologie)\">Unterklasse</a>:</i></td>\n"
				+ "<td><a href=\"/wiki/Urkieferv%C3%B6gel\" title=\"Urkiefervögel\">Urkiefervögel</a> (Palaeognathae)</td>\n"
				+ "\n" + "</tr>\n" + "<tr>\n"
				+ "<td><i><a href=\"/wiki/Ordnung_%28Biologie%29\" title=\"Ordnung (Biologie)\">Ordnung</a>:</i></td>\n"
				+ "<td><a href=\"/wiki/Laufv%C3%B6gel\" title=\"Laufvögel\">Laufvögel</a> (Struthioniformes)</td>\n"
				+ "</tr>\n" + "<tr>\n"
				+ "<td><i><a href=\"/wiki/Familie_%28Biologie%29\" title=\"Familie (Biologie)\">Familie</a>:</i></td>\n"
				+ "<td>Strauße (Struthionidae)</td>\n" + "</tr>\n" + "\n" + "<tr>\n"
				+ "<td><i><a href=\"/wiki/Gattung_%28Biologie%29\" title=\"Gattung (Biologie)\">Gattung</a>:</i></td>\n"
				+ "<td>Strauße (<i>Struthio</i>)</td>\n" + "</tr>\n" + "<tr>\n"
				+ "<td><i><a href=\"/wiki/Art_%28Biologie%29\" title=\"Art (Biologie)\">Art</a>:</i></td>\n"
				+ "<td>Afrikanischer Strauß</td>\n" + "</tr>\n" + "</table>");
		final String result = conv.toWiki(new ToWikipedia());
		assertThat("                         \n" + "{| border=\"1\"\n" + "|-\n" + "|\'\'[[Klasse]]:\'\'\n"
				+ "|[[Vögel]] (Aves)\n" + "|-\n" + "|\'\'[[Unterklasse]]:\'\'\n"
				+ "|[[Urkiefervögel]] (Palaeognathae)\n" + "|-\n" + "|\'\'[[Ordnung]]:\'\'\n"
				+ "|[[Laufvögel]] (Struthioniformes)\n" + "|-\n" + "|\'\'[[Familie]]:\'\'\n"
				+ "|Strauße (Struthionidae)\n" + "|-\n" + "|\'\'[[Gattung]]:\'\'\n" + "|Strauße (\'\'Struthio\'\')\n"
				+ "|-\n" + "|\'\'[[Art]]:\'\'\n" + "|Afrikanischer Strauß\n" + "|}").isEqualTo(result);
	}

	@Test
	public void test6() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		conv.setInputHTML("<b>hello</b> <em>world</em>");
		final String result = conv.toWiki(new ToWikipedia());
		assertThat("'''hello''' ''world''").isEqualTo(result);
	}

	@Test
	public void test7() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		conv.setInputHTML("<html><body><table>\n<tr>\n<td>hello world</td>\n</tr></table></body></html>");
		final String result = conv.toWiki(new ToWikipedia());
		assertThat("   \n" + "{| border=\"1\"\n" + "|-\n" + "|hello world\n" + "|}").isEqualTo(result);
	}

	@Test
	public void test8() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		conv.setInputHTML("<font size=\"1\">hello</font> <em>world</em>");
		final String result = conv.toWiki(new ToWikipedia(true, true));
		assertThat("hello ''world''").isEqualTo(result);
	}

	@Test
	public void test9() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		conv.setInputHTML("<h2>hello \n world\n</h2>");
		final String result = conv.toWiki(new ToWikipedia(true, true));
		assertThat("\n" + "== hello   world ==\n").isEqualTo(result);
	}

	@Test
	public void test10() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		conv.setInputHTML("<b>hello&nbsp;<em>world</em></b>");
		final String result = conv.toWiki(new ToWikipedia());
		assertThat("'''hello ''world'''''").isEqualTo(result);
	}

	@Test
	public void test11() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		conv.setInputHTML("<h2> \n \n</h2>");
		final String result = conv.toWiki(new ToWikipedia(true, true));
		assertThat("").isEqualTo(result);
	}

	@Test
	public void test12() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		conv.setInputHTML("<b> </b> <em> </em>");
		final String result = conv.toWiki(new ToWikipedia());
		assertThat("   ").isEqualTo(result);
	}

	@Test
	public void test13() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		conv.setInputHTML("<div> </div>");
		final String result = conv.toWiki(new ToWikipedia());
		assertThat(" ").isEqualTo(result);
	}

	@Test
	public void test14() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		// invalid nested html test
		conv.setInputHTML(
				"<h2>the <a href=\"http://good\">good</a></h2><h3>the <div>bad</div></h3><h2>and <b><i>the</i> ugly</b></h2>");
		final String result = conv.toWiki(new ToWikipedia());
		assertThat("\n" + "== the good ==\n" + "\n" + "=== the bad ===\n" + "\n" + "== and the ugly ==\n")
				.isEqualTo(result);
	}

	@Test
	public void test15() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		// invalid nested html test
		conv.setInputHTML("The <a href=\"http://good\">good</a> the <div>bad</div> and <b><i>the</i> ugly</b>");
		final String result = conv.toWiki(new ToWikipedia());
		assertThat("The [[good]] the \n" + "<div>bad\n" + "</div> and '''''the'' ugly'''").isEqualTo(result);
	}

	@Test
	public void test16() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		conv.setInputHTML(" <table>\n<tr>\n" + "                    <td align=\"left\" valign=\"top\">accesskey</td>\n"
				+ "                    <td align=\"left\" valign=\"top\">false</td>\n"
				+ "                    <td align=\"left\" valign=\"top\"></td>              <!----  empty cell -->\n"
				+ "                    <td align=\"left\" valign=\"top\">false</td>\n"
				+ "                    <td align=\"left\" valign=\"top\">String</td>\n"
				+ "                    <td align=\"left\" valign=\"top\">Set the html accesskey attribute on rendered html element</td>\n"
				+ "   </tr></table>");
		final String result = conv.toWiki(new ToWikipedia());
		assertThat("          \n" + "{| border=\"1\"\n" + "|-\n" + "|accesskey\n" + "|false\n" + "| \n" + "|false\n"
				+ "|String\n" + "|Set the html accesskey attribute on rendered html element\n" + "|}")
						.isEqualTo(result);
	}

	@Test
	public void test17() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		conv.setInputHTML(" <table>\n<tr>\n"
				+ "                    <td align=\"left\" valign=\"top\"></td>             \n" + "   </tr></table>");
		final String result = conv.toWiki(new ToWikipedia());
		assertThat("    \n" + "{| border=\"1\"\n" + "|-\n" + "| \n" + "|}").isEqualTo(result);
	}

	@Test
	public void test18() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		conv.setInputHTML("<a href=\"http://www.example.com\"><b><font color=red>A tag text</font></b></a>");
		final String result = conv.toWiki(new ToWikipedia());
		assertThat("[[A tag text]]").isEqualTo(result);
	}

	@Test
	public void test19() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		conv.setInputHTML("<br>fsafds\n<br>");
		final String result = conv.toWiki(new ToWikipedia());
		assertThat("<br>fsafds\n" + "<br>").isEqualTo(result);
	}

	@Test
	public void test20() {
		final HTML2WikiConverter conv = new HTML2WikiConverter();
		conv.setInputHTML("<a id=\"TST\" name=\"TST\"></a><h2>TST</h2>");
		final String result = conv.toWiki(new ToWikipedia());
		assertThat("\n" + "== TST ==\n" + "").isEqualTo(result);
	}

}
