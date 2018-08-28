package in.tts.model;

public class BookmarkModel {

    private String pageTitle;
    private String pageUrl;
    private String pageFavicon;

    public BookmarkModel(String title, String url, String favicon) {
        pageTitle = title;
        pageUrl = url;
        pageFavicon = favicon;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getPageFavicon() {
        return pageFavicon;
    }

    public void setPageFavicon(String pageFavicon) {
        this.pageFavicon = pageFavicon;
    }

}