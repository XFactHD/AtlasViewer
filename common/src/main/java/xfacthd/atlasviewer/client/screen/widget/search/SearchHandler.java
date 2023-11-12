package xfacthd.atlasviewer.client.screen.widget.search;

public interface SearchHandler
{
    int getResultCount();

    void updateSearch(String text);

    void jumpToNextResult();

    int getFocusedResultIndex();
}
