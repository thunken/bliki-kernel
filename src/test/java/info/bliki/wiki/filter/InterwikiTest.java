package info.bliki.wiki.filter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class InterwikiTest extends FilterTestSupport {

	@Test
	public void test001() throws Exception {
		assertThat(wikiModel.render("Interwiki [[disinfopedia:link]] link test", false)).isEqualTo("\n"
				+ "<p>Interwiki <a href=\"https://sourcewatch.org/index.php/link\">disinfopedia:link</a> link test</p>");
	}

	@Test
	public void test002() throws Exception {
		assertThat(wikiModel.render("Interwiki [[freebsdman:link]] link test", false)).isEqualTo("\n"
				+ "<p>Interwiki <a href=\"https://www.FreeBSD.org/cgi/man.cgi?apropos=1&query=link\">freebsdman:link</a> link test</p>");
	}
}
