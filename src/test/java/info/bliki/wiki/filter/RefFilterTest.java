package info.bliki.wiki.filter;

import info.bliki.wiki.model.Reference;
import org.junit.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RefFilterTest extends FilterTestSupport {

    @Test public void testRef01() throws Exception {
        assertThat(wikiModel
                .render("A <ref>Reference</ref> Test\n\n<references/>", false))
            .isEqualTo("\n"
                + "<p>A <sup id=\"_ref-1\" class=\"reference\"><a href=\"#_note-1\" title=\"\">[1]</a></sup> Test</p><ol class=\"references\">\n"
                + "<li id=\"_note-1\"><b><a href=\"#_ref-1\" title=\"\">&#8593;</a></b> Reference</li>\n</ol>");
        List<Reference> list = wikiModel.getReferences();
        Reference ref = list.get(0);
        assertThat(ref.getRefString()).isEqualTo("Reference");
    }

    @Test public void testRef02() throws Exception {
        assertThat(wikiModel.render("A <ref>first reference</ref> and a <ref>second reference</ref> Test\n\n<references/>", false))
            .isEqualTo("\n"
                + "<p>A <sup id=\"_ref-1\" class=\"reference\"><a href=\"#_note-1\" title=\"\">[1]</a></sup> and a <sup id=\"_ref-2\" class=\"reference\"><a href=\"#_note-2\" title=\"\">[2]</a></sup> Test</p><ol class=\"references\">\n"
                + "<li id=\"_note-1\"><b><a href=\"#_ref-1\" title=\"\">&#8593;</a></b> first reference</li><li id=\"_note-2\"><b><a href=\"#_ref-2\" title=\"\">&#8593;</a></b> second reference</li>\n</ol>");
        List<Reference> list = wikiModel.getReferences();
        Reference ref = list.get(0);
        assertThat(ref.getRefString()).isEqualTo("first reference");
        ref = list.get(1);
        assertThat(ref.getRefString()).isEqualTo("second reference");
    }

    @Test public void testRef03() throws Exception {
        assertThat(wikiModel.render("aaa <ref name=\"Freitag\"/> bbb<ref name=\"Arndt\"/> <ref>ccc</ref> <references/>", false))
            .isEqualTo("\n" +
                "<p>aaa <sup id=\"_ref-Freitag\" class=\"reference\"><a href=\"#_note-Freitag\" title=\"\">[1]</a></sup> bbb<sup id=\"_ref-Arndt\" class=\"reference\"><a href=\"#_note-Arndt\" title=\"\">[2]</a></sup> <sup id=\"_ref-3\" class=\"reference\"><a href=\"#_note-3\" title=\"\">[3]</a></sup> </p><ol class=\"references\">\n" +
                "<li id=\"_note-Freitag\"><b><a href=\"#_ref-Freitag\" title=\"\">&#8593;</a></b> </li><li id=\"_note-Arndt\"><b><a href=\"#_ref-Arndt\" title=\"\">&#8593;</a></b> </li><li id=\"_note-3\"><b><a href=\"#_ref-3\" title=\"\">&#8593;</a></b> ccc</li>\n</ol>");
        List<Reference> list = wikiModel.getReferences();
        Reference ref = list.get(0);
        assertThat(ref.getRefString()).isEqualTo("");
        ref = list.get(1);
        assertThat(ref.getRefString()).isEqualTo("");
        ref = list.get(2);
        assertThat(ref.getRefString()).isEqualTo("ccc");
    }

    @Test public void testRef04() throws Exception {
        assertThat(wikiModel.render("aaa <ref name=Freitag></ref> bbb<ref    name=Arndt>arn</ref> <ref>ccc</ref> <references/>", false))
            .isEqualTo("\n" +
                "<p>aaa <sup id=\"_ref-Freitag\" class=\"reference\"><a href=\"#_note-Freitag\" title=\"\">[1]</a></sup> bbb<sup id=\"_ref-Arndt\" class=\"reference\"><a href=\"#_note-Arndt\" title=\"\">[2]</a></sup> <sup id=\"_ref-3\" class=\"reference\"><a href=\"#_note-3\" title=\"\">[3]</a></sup> </p><ol class=\"references\">\n" +
                "<li id=\"_note-Freitag\"><b><a href=\"#_ref-Freitag\" title=\"\">&#8593;</a></b> </li><li id=\"_note-Arndt\"><b><a href=\"#_ref-Arndt\" title=\"\">&#8593;</a></b> arn</li><li id=\"_note-3\"><b><a href=\"#_ref-3\" title=\"\">&#8593;</a></b> ccc</li>\n</ol>");
        List<Reference> list = wikiModel.getReferences();
        Reference ref = list.get(0);
        assertThat(ref.getRefString()).isEqualTo("");
        ref = list.get(1);
        assertThat(ref.getRefString()).isEqualTo("arn");
        ref = list.get(2);
        assertThat(ref.getRefString()).isEqualTo("ccc");
    }

    @Test public void testRef05() throws Exception {
        assertThat(wikiModel.render("aaa <ref name=Freitag/> bbb<ref name=Arndt /> <ref>ccc</ref> <references/>", false))
            .isEqualTo("\n" +
                "<p>aaa <sup id=\"_ref-Freitag\" class=\"reference\"><a href=\"#_note-Freitag\" title=\"\">[1]</a></sup> bbb<sup id=\"_ref-Arndt\" class=\"reference\"><a href=\"#_note-Arndt\" title=\"\">[2]</a></sup> <sup id=\"_ref-3\" class=\"reference\"><a href=\"#_note-3\" title=\"\">[3]</a></sup> </p><ol class=\"references\">\n" +
                "<li id=\"_note-Freitag\"><b><a href=\"#_ref-Freitag\" title=\"\">&#8593;</a></b> </li><li id=\"_note-Arndt\"><b><a href=\"#_ref-Arndt\" title=\"\">&#8593;</a></b> </li><li id=\"_note-3\"><b><a href=\"#_ref-3\" title=\"\">&#8593;</a></b> ccc</li>\n</ol>");
        List<Reference> list = wikiModel.getReferences();
        Reference ref = list.get(0);
        assertThat(ref.getRefString()).isEqualTo("");
        ref = list.get(1);
        assertThat(ref.getRefString()).isEqualTo("");
        ref = list.get(2);
        assertThat(ref.getRefString()).isEqualTo("ccc");
    }

    @Test public void testRef06() throws Exception  {
        assertThat(wikiModel.render("aaa <ref name=Freitag/> bbb<ref name=A&B /> <ref>ccc</ref> <references/>", false))
            .isEqualTo("\n" +
                "<p>aaa <sup id=\"_ref-Freitag\" class=\"reference\"><a href=\"#_note-Freitag\" title=\"\">[1]</a></sup> bbb<sup id=\"_ref-A.26B\" class=\"reference\"><a href=\"#_note-A.26B\" title=\"\">[2]</a></sup> <sup id=\"_ref-3\" class=\"reference\"><a href=\"#_note-3\" title=\"\">[3]</a></sup> </p><ol class=\"references\">\n" +
                "<li id=\"_note-Freitag\"><b><a href=\"#_ref-Freitag\" title=\"\">&#8593;</a></b> </li><li id=\"_note-A.26B\"><b><a href=\"#_ref-A.26B\" title=\"\">&#8593;</a></b> </li><li id=\"_note-3\"><b><a href=\"#_ref-3\" title=\"\">&#8593;</a></b> ccc</li>\n</ol>");
        List<Reference> list = wikiModel.getReferences();
        Reference ref = list.get(0);
        assertThat(ref.getRefString()).isEqualTo("");
        ref = list.get(1);
        assertThat(ref.getRefString()).isEqualTo("");
        ref = list.get(2);
        assertThat(ref.getRefString()).isEqualTo("ccc");
    }

    @Test public void testRef07() throws Exception {
        assertThat(wikiModel
                .render("<ref>{{cite book |last=Malins|first=Steve|title=Depeche Mode: A Biography|year=2001|publisher=Andre Deutsch|chapter=|pages=82|isbn=978-0233994307}}</ref>\n\n\n<references/>", false))
            .isEqualTo("\n" +
                "<p><sup id=\"_ref-1\" class=\"reference\"><a href=\"#_note-1\" title=\"\">[1]</a></sup></p>\n" +
                "<ol class=\"references\">\n" +
                "<li id=\"_note-1\"><b><a href=\"#_ref-1\" title=\"\">&#8593;</a></b> <span class=\"citation book\">Malins, Steve (2001). <a href=\"http://www.bliki.info/wiki/Template:Citation/make_link\" title=\"Template:Citation/make link\">Template:Citation/make link</a>. Andre Deutsch. pp. 82. <a class=\"external text\" href=\"http://www.amazon.com/exec/obidos/ASIN/9780233994307\" rel=\"nofollow\" title=\"http://www.amazon.com/exec/obidos/ASIN/9780233994307\">ISBN 978-0233994307</a>.</span><span class=\"Z3988\" title=\"ctx_ver=Z39.88-2004&amp;rft_val_fmt=info%3Aofi%2Ffmt%3Akev%3Amtx%3Abook&amp;rft.genre=book&amp;rft.btitle=Depeche+Mode%3A+A+Biography&amp;rft.aulast=Malins&amp;rft.aufirst=Steve&amp;rft.au=Malins%2C%26%2332%3BSteve&amp;rft.date=2001&amp;rft.pages=pp.%26nbsp%3B82&amp;rft.pub=Andre+Deutsch&amp;rft.isbn=978-0233994307&amp;rfr_id=info:sid/en.wikipedia.org:PAGENAME\"><span style=\"display: none;\"> </span></span></li>\n</ol>");

    }

    @Test public void testRef08() throws Exception {
        assertThat(wikiModel.render("; 506 Variant Also Negotiates<ref name=\"RFC 2295\">RFC 2295</ref>\n" + "\n"
                + "== Einzelnachweise ==\n" + "\n" + "<references />", false))
            .isEqualTo("\n" +
                "<dl>\n" +
                "<dt>506 Variant Also Negotiates<sup id=\"_ref-RFC_2295\" class=\"reference\"><a href=\"#_note-RFC_2295\" title=\"\">[1]</a></sup></dt>\n</dl>\n" +
                "\n" +
                "<h2><span class=\"mw-headline\" id=\"Einzelnachweise\">Einzelnachweise</span></h2>\n" +
                "<ol class=\"references\">\n" +
                "<li id=\"_note-RFC_2295\"><b><a href=\"#_ref-RFC_2295\" title=\"\">&#8593;</a></b> RFC 2295</li>\n</ol>");
    }
}
