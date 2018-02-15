package info.bliki.wiki.dump;

import java.io.File;

/**
 * Demo application which reads a compressed or uncompressed Wikipedia XML dump
 * file (depending on the given file extension <i>.gz</i>, <i>.bz2</i> or
 * <i>.xml</i>) and prints the title and wiki text.
 *
 */
public class DumpExample {
    /**
     * Print title an content of all the wiki pages in the dump.
     *
     */
    static class DemoArticleFilter implements IArticleFilter {

        @Override
        public void process(WikiArticle page, Siteinfo siteinfo) {
            System.out.println("----------------------------------------");
            System.out.println(page.getId());
            System.out.println(page.getRevisionId());
            System.out.println(page.getTitle());
            System.out.println("----------------------------------------");
            System.out.println(page.getText());
        }
    }

    /**
     * Print all titles of the wiki pages which have &quot;Real&quot; content
     * (i.e. the title has no namespace prefix) (key == 0).
     */
    static class DemoMainArticleFilter implements IArticleFilter {

        @Override
        public void process(WikiArticle page, Siteinfo siteinfo)  {
            if (page.isMain()) {
                System.out.println(page.getTitle());
            }
        }
    }

    /**
     * Print all titles of the wiki pages which are templates (key == 10).
     */
    static class DemoTemplateArticleFilter implements IArticleFilter {

        @Override
        public void process(WikiArticle page, Siteinfo siteinfo) {
            if (page.isTemplate()) {
                System.out.println(page.getTitle());
            }
        }
    }

    /**
     * Print all titles of the wiki pages which are categories (key == 14).
     */
    static class DemoCategoryArticleFilter implements IArticleFilter {

        @Override
        public void process(WikiArticle page, Siteinfo siteinfo)  {
            if (page.isCategory()) {
                System.out.println(page.getTitle());
            }
        }

    }

    /**
     * @param args filename, e.g. dewikiversity-20100401-pages-articles.xml.bz2
     */
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: Parser <xml-file.[bz2|gz]>");
            System.exit(-1);
        } else {
            new WikiXMLParser(new File(args[0]), new DemoArticleFilter()).parse();
        }
    }
}
