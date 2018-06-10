package info.bliki.wiki.dump;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import info.bliki.util.Throwables;
import lombok.extern.slf4j.Slf4j;

/**
 * A Wikipedia XML dump file parser
 *
 * Original version with permission from Marco Schmidt. See:
 * <a href="https://schmidt.devlib.org/software/lucene-wikipedia.html"
 * >https://schmidt.devlib.org/software/lucene-wikipedia.html</a>
 *
 * @author Marco Schmidt
 */
@Slf4j
public class WikiXMLParser extends DefaultHandler {

	private static final String WIKIPEDIA_SITEINFO = "siteinfo";
	private static final String WIKIPEDIA_TITLE = "title";
	private static final String WIKIPEDIA_TEXT = "text";
	private static final String WIKIPEDIA_PAGE = "page";
	private static final String WIKIPEDIA_REVISION = "revision";
	private static final String WIKIPEDIA_NAMESPACE = "namespace";
	private static final String WIKIPEDIA_TIMESTAMP = "timestamp";
	private static final String WIKIPEDIA_ID = "id";

	private Siteinfo fSiteinfo = null;
	private String fNamespaceKey = null;
	private WikiArticle fArticle;
	private boolean fRevision;
	private StringBuilder fData;
	private final XMLReader fXMLReader;
	private final Reader fReader;

	private final IArticleFilter fArticleFilter;

	public WikiXMLParser(final File filename, final IArticleFilter filter) throws IOException, SAXException {
		this(getReader(filename), filter);
	}

	public WikiXMLParser(final InputStream inputStream, final IArticleFilter filter) throws SAXException {
		fArticleFilter = filter;
		fXMLReader = XMLReaderFactory.createXMLReader();
		fXMLReader.setContentHandler(this);
		fXMLReader.setErrorHandler(this);
		fReader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));
	}

	public WikiXMLParser(final Reader reader, final IArticleFilter filter) throws SAXException {
		fArticleFilter = filter;
		fXMLReader = XMLReaderFactory.createXMLReader();
		fXMLReader.setContentHandler(this);
		fXMLReader.setErrorHandler(this);
		fReader = reader;
	}

	/**
	 * @return a Reader created from wikiDumpFilename
	 * @throws java.io.IOException
	 */
	public static Reader getReader(final File wikiDumpFilename) throws IOException {
		InputStream inputStream = new FileInputStream(wikiDumpFilename);
		if (wikiDumpFilename.getName().endsWith(".gz")) {
			inputStream = new GZIPInputStream(inputStream);
		} else if (wikiDumpFilename.getName().endsWith(".bz2")) {
			inputStream = new BZip2CompressorInputStream(inputStream, true);
		}
		return new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
	}

	private String getString() {
		if (fData == null) {
			return null;
		} else {
			final String s = fData.toString();
			fData = null;
			return s;
		}
	}

	@Override
	public void startDocument() {
		// System.out.println("START");
	}

	@Override
	public void endDocument() {
		// System.out.println("END");
	}

	@Override
	public void startElement(final String namespaceURI, final String localName, final String qName,
			final Attributes atts) {
		// fAttributes = atts;
		fData = null;
		if (WIKIPEDIA_SITEINFO.equals(qName)) {
			fSiteinfo = new Siteinfo();
			return;
		}
		if (fArticle == null) {
			fNamespaceKey = null;
			if (fSiteinfo != null) {
				if (WIKIPEDIA_NAMESPACE.equals(qName)) {
					fNamespaceKey = atts.getValue("key");
					return;
				}
			}
		}

		if (WIKIPEDIA_PAGE.equals(qName)) {
			fArticle = new WikiArticle();
			fRevision = false;
		}
		if (WIKIPEDIA_REVISION.equals(qName)) {
			fRevision = true;
		}
	}

	@Override
	public void endElement(final String uri, final String name, final String qName) throws SAXException {
		try {
			if (fArticle == null) {
				if (fSiteinfo != null) {
					if (WIKIPEDIA_NAMESPACE.equals(qName) && fNamespaceKey != null) {
						fSiteinfo.addNamespace(fNamespaceKey, getString());
					} else if ("sitename".equals(qName)) {
						fSiteinfo.setSitename(getString());
					} else if ("base".equals(qName)) {
						fSiteinfo.setBase(getString());
					} else if ("generator".equals(qName)) {
						fSiteinfo.setGenerator(getString());
					} else if ("case".equals(qName)) {
						fSiteinfo.setCharacterCase(getString());
					}
				}
			} else {
				if (WIKIPEDIA_PAGE.equals(qName)) {
					// ignore
				} else if (WIKIPEDIA_TEXT.equals(qName)) {
					fArticle.setText(getString());
					try {
						fArticleFilter.process(fArticle, fSiteinfo);
					} catch (final IOException e) {
						throw new SAXException(e);
					}
					// emit(wikiText);
				} else if (WIKIPEDIA_TITLE.equals(qName)) {
					fArticle.setTitle(getString(), fSiteinfo);
				} else if (WIKIPEDIA_TIMESTAMP.equals(qName)) {
					fArticle.setTimeStamp(getString());
				} else if (!fRevision && WIKIPEDIA_ID.equals(qName)) {
					// get the id from wiki page, not the id from the revision
					fArticle.setId(getString());
				} else if (fRevision && WIKIPEDIA_ID.equals(qName)) {
					// get the id from revision, not the id from the wiki PAGE
					fArticle.setRevisionId(getString());
				}
			}
			fData = null;
			// fAttributes = null;
		} catch (final RuntimeException re) {
			Throwables.log(log, re);
		}
	}

	/**
	 * parse an unlimited amount of characters between 2 enclosing XML-Tags
	 *
	 * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
	 */
	@Override
	public void characters(final char[] ch, final int start, final int length) throws SAXException {
		if (fData == null) {
			fData = new StringBuilder(length);
		}
		fData.append(ch, start, length);
	}

	public void parse() throws IOException, SAXException {
		fXMLReader.parse(new InputSource(fReader));
	}
}
