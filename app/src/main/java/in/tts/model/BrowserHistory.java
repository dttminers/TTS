package in.tts.model;

public class BrowserHistory {

    private String pageTitle;
    private String pageUrl;
    private String pageFavicon;
    private String pageVisited;

    public BrowserHistory(/*String title,*/ String url,/* String favicon, */String visited) {
//        pageTitle = title;
        pageUrl = url;
//        pageFavicon = favicon;
        pageVisited = visited;
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

    public String getPageVisited() {
        return pageVisited;
    }

    public void setPageVisited(String pageVisited) {
        this.pageVisited = pageVisited;
    }


}
