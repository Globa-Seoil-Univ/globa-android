package team.y2k2.globa.docs.detail.highlight;

public class DocsDetailHighlightItem {
    private final int startIndex;
    private final int endIndex;
    private final int highlightColor;

    public DocsDetailHighlightItem(int startIndex, int endIndex, int highlightColor) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.highlightColor = highlightColor;
    }


    public int getStartIndex() {
        return startIndex;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public int getHighlightColor() {
        return highlightColor;
    }

}
