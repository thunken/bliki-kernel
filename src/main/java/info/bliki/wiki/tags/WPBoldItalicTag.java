package info.bliki.wiki.tags;

import info.bliki.wiki.filter.ITextConverter;
import info.bliki.wiki.model.IWikiModel;

import java.io.IOException;

/**
 * A special Wikipedia tag (i.e. ==, ===, ''', '', ...)
 *
 */
public class WPBoldItalicTag extends WPTag {
	String outerTag;

	String innerTag;

	public WPBoldItalicTag() {
		super("bi");
		outerTag = "b";
		innerTag = "i";
	}

	@Override
	public boolean isReduceTokenStack() {
		return false;
	}

	@Override
	public void renderHTML(ITextConverter converter, Appendable buf, IWikiModel model) throws IOException {
		if (outerTag != null) {
			buf.append("<").append(outerTag).append(">");
		}
		setName(innerTag);
		super.renderHTML(converter, buf, model);
		setName("bi");
		if (outerTag != null) {
			buf.append("</").append(outerTag).append(">");
		}
	}

	@Override
	public Object clone() {
		WPBoldItalicTag tag = (WPBoldItalicTag) super.clone();
		tag.outerTag = outerTag;
		tag.innerTag = innerTag;
		return tag;
	}

}
