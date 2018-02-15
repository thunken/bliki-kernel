package info.bliki.htmlcleaner.util;

import info.bliki.htmlcleaner.BaseToken;
import info.bliki.htmlcleaner.HtmlCleaner;
import info.bliki.htmlcleaner.TagNode;

import java.io.IOException;
import java.util.List;


public abstract class AbstractHtmlExtractor<T> {
    final T fResultObject;

    public AbstractHtmlExtractor(T resultObject) {
        super();
        this.fResultObject = resultObject;
    }

    /**
     * Append the content of the nodes to the given result object.
     *
     * @param nodes
     */
    protected abstract void appendContent(List<Object> nodes);

    /**
     * Append the content of the given <code>TagNode</code> to the resultObject
     *
     * @param tagNode
     * @return <code>true</code> if <code>appendContent()</code> should be
     *         called.
     */
    protected abstract boolean isFound(TagNode tagNode);

    protected T getResultObject() {
        return fResultObject;
    }

    protected void visitTokenList(List<Object> nodes) {
        if (nodes != null && !nodes.isEmpty()) {
            for (Object item : nodes) {
                if (item != null) {
                    if (item instanceof List) {
                        @SuppressWarnings("unchecked")
                        final List<Object> list = (List<Object>) item;
                        visitTokenList(list);
                    } else if (item instanceof BaseToken) {
                        visitBaseToken((BaseToken) item);
                    }
                }
            }
        }
    }

    protected void visitBaseToken(BaseToken node) {
        if (node instanceof TagNode) {
            TagNode tagNode = (TagNode) node;
            if (isFound(tagNode)) {
                appendContent(tagNode.getChildren());
            } else {
                List<Object> children = tagNode.getChildren();
                if (children.size() != 0) {
                    visitTokenList(children);
                }
            }
        }
    }

    /**
     * Extract the information from the given html text.
     *
     * @param html
     */
    public void extractContent(String html) {
        HtmlCleaner cleaner = null;
        try {
            cleaner = new HtmlCleaner(html);
            cleaner.clean();
            TagNode body = cleaner.getBodyNode();
            visitBaseToken(body);
        } catch (IOException e) {
        }
    }
}
