package info.bliki.wiki.filter;

import info.bliki.wiki.model.IWikiModel;
import info.bliki.wiki.tags.util.TagStack;

/**
 * Entry for a Wikipedia list string (i.e. a line which starts with '*', '#',
 * ':', ';'). See <a href="https://meta.wikimedia.org/wiki/Help:List"
 * >https://meta.wikimedia.org/wiki/Help:List</a>
 *
 * @see info.bliki.wiki.filter.WPList
 */
public class WPListElement {
    int fStartPos;

    int fEndPos;

    final char[] fSequence;

    TagStack fStack;

    public WPListElement(int level, final char[] sequence, int start) {
        fSequence = sequence;
        fStartPos = start;
        fStack = null;
    }

    /**
     * @return Returns the endPos.
     */
    public int getEndPos() {
        return fEndPos;
    }

    /**
     * Create the internal TagNodes stack for a single list line
     *
     * @param endPos
     *          The endPos to set.
     */
    public void createTagStack(char[] src, IWikiModel wikiModel, int endPos) {
        fEndPos = endPos;
        if (fEndPos > fStartPos) {
            String rawWikiText = new String(src, fStartPos, fEndPos - fStartPos);
            AbstractWikipediaParser parser = wikiModel.createNewInstance(rawWikiText);
            fStack = parser.parseRecursiveInternal(wikiModel, true, true);
        }
    }

    /**
     * @return Returns the startPos.
     */
    public int getStartPos() {
        return fStartPos;
    }

    /**
     * @param startPos
     *          The startPos to set.
     */
    public void setStartPos(int startPos) {
        fStartPos = startPos;
    }

    /**
     * @return returns the character sequence of this list element.
     */
    public char[] getSequence() {
        return fSequence;
    }

    /**
     *
     * @return <code>null</code> or the internally created TagStack
     */
    public TagStack getTagStack() {
        return fStack;
    }

    @Override
    public String toString() {
        return fStartPos+"|"+fEndPos+"|"+String.valueOf(fSequence);
    }
}
