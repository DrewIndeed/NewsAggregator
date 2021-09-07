package Business.Scraper.ArticleCrawler;

import Business.News.Article;
import Business.News.ArticleFactory;
import Business.Scraper.Helper.LocalDateTimeParser;
import Business.Scraper.Helper.ScrapingUtils;
import Business.Scraper.LinksCrawler.Category;
import Business.Scraper.Sanitizer.Sanitizer;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static Business.Scraper.Helper.ScrapingUtils.getFirstElementByClass;
import static Business.Scraper.Helper.ScrapingUtils.scrapeFirstImgUrl;

public class Scraper {
    private final Sanitizer sanitizer;
    private final String DEFAULT_THUMBNAIL;
    private final String SOURCE;

    // css class to target and pull out need elements
    private final String TITLE;
    private final String AUTHOR;
    private final String DESCRIPTION;
    private final String MAIN_CONTENT;
    private final String CATEGORY;
    private final String PICTURE;
    private final String THUMBNAIL;
    private final String PUBLISHED_TIME;

    /** Scraper Constructor
     * @param source article's url domain
     * @param sanitizer article's sanitizer
     * @param defaultThumbnail thumbnail default url
     * @param title article title class
     * @param author article author class
     * @param category article category class
     * @param description article description class
     * @param mainContent article mainContent class
     * @param picture article picture class
     * @param thumbnail article thumbnail class
     * @param publishedTime article publishedTime class
     */
    public Scraper( String source,
                    Sanitizer sanitizer,
                    String defaultThumbnail,
                    String title,
                    String author,
                    String category,
                    String description,
                    String mainContent,
                    String picture,
                    String thumbnail,
                    String publishedTime) {
        this.SOURCE = source;
        this.sanitizer = sanitizer;
        this.DEFAULT_THUMBNAIL = defaultThumbnail;
        this.TITLE = title;
        this.AUTHOR = author;
        this.CATEGORY = category;
        this.DESCRIPTION = description;
        this.MAIN_CONTENT = mainContent;
        this.PICTURE = picture;
        this.THUMBNAIL = thumbnail;
        this.PUBLISHED_TIME = publishedTime;
    }

    /** Get Article all info (title, description, mainContent, thumbnail, categories, ...)
     * @param url article url
     * @return article all related information
     */
    public Article getArticle(String url){
        Document doc = ScrapingUtils.getDocumentAndDeleteCookies(url);
        if (doc == null){
            return null;
        }

        Element title = scrapeTitle(doc);
        Element description = scrapeDescription(doc);
        Element mainContent = scrapeMainContent(doc);
        String thumbNail = scrapeThumbnail(doc);
        Set<String> categories = scrapeCategoryNames(doc);
        LocalDateTime publishedTime = scrapePublishedTime(doc);

        if (title == null || description == null || mainContent == null || publishedTime == null){
            return null;
        }

        return ArticleFactory.createArticle(SOURCE,
                url,
                title,
                description,
                mainContent,
                thumbNail,
                categories, publishedTime);

    }

    /** Get author of an article
     * @param doc article document
     * @return author element
     */
    private Element scrapeAuthor(Document doc){
        Element author = getFirstElementByClass(doc, this.AUTHOR);
        if (author != null){
            for (Element a: author.getElementsByTag("a")){
                if (!StringUtils.isEmpty(a.ownText())){
                    return new Element("p")
                            .attr("style", "text-align:right;")
                            .appendChild(new Element("strong").text(a.ownText()));
                }
            }
            return null;
        }
        else{
            return null;
        }
    }

    /** Scrape Article Title Element that matches provided css
     * @param doc article document
     * @return sanitized title element
     */
    public Element scrapeTitle(Document doc) {
        Element title = getFirstElementByClass(doc, TITLE);
        return sanitizer.sanitizeTitle(title);
    }

    /** Scrape Article Description Element that matches provided css
     * @param doc article document
     * @return sanitized title element
     */    public Element scrapeDescription(Document doc) {
        Element description = getFirstElementByClass(doc, DESCRIPTION);
        return sanitizer.sanitizeMainContent(description);
    }

    /** Scrape Article Main content Element that matches provided css
     * @param doc article document
     * @return sanitized title element
     */    public Element scrapeMainContent(Document doc) {
        Element content = getFirstElementByClass(doc, MAIN_CONTENT);
        if (content == null) {
            return null;
        }

        Element authorTag = scrapeAuthor(doc);
        if (authorTag != null){
            content.append(authorTag.outerHtml());
        }
        return sanitizer.sanitizeMainContent(content);
    }

    /** Scrape Article Thumbnail url
     * @param doc article document
     * @return thumbnail url string
     */    public String scrapeThumbnail(Document doc) {
        String thumb = scrapeFirstImgUrl(doc, THUMBNAIL);
        String pic = scrapeFirstImgUrl(doc, PICTURE);

        if (!StringUtils.isEmpty(thumb)){
            return thumb;
        }
        else if (!StringUtils.isEmpty(pic)){
            return pic;
        }
        else {
            return DEFAULT_THUMBNAIL;
        }
    }

    /** Scrape Article Published Time in meta tag
     * @param doc article document
     * @param lookupAttribute itemprop attribute in meta tag
     * @return Article Published Time
     */
    private LocalDateTime scrapePublishedTimeFromMeta(Document doc, String lookupAttribute){
        Element time = ScrapingUtils.getFirstElementByMatchingValue(doc, "meta", lookupAttribute);
        if (time == null) {
            return null;
        }

        for (Attribute a: time.attributes()){
            if(a.getValue().contains("+07")){
                return LocalDateTimeParser.parse(a.getValue());
            }
        }

        return null;
    }

    /** Scrape Article Published Time in body
     * @param doc article document
     * @param css css class
     * @return Article Published Time
     */
    private LocalDateTime scrapePublishedTimeInBody(Document doc, String css){
        Elements dateTimeTags = doc.getElementsByClass(css);
        for (Element dateTimeTag :dateTimeTags){
            LocalDateTime time = LocalDateTimeParser.parse(dateTimeTag);
            if (time != null){
                return time;
            }
        }
        return null;
    }

    /** Scrape Article Published Time
     * @param doc article document
     * @return article Published Time
     */
    public LocalDateTime scrapePublishedTime(Document doc){
        // first look for published time in meta tag
        LocalDateTime time = scrapePublishedTimeFromMeta(doc, PUBLISHED_TIME);

        // look for published time in body if no time tag is found in meta
        if (time == null){
            time = scrapePublishedTimeInBody(doc, PUBLISHED_TIME);
        }
        return time;
    }

    public Set<String> scrapeCategoryNames(Document doc) {
        Set<String> categoryList = new HashSet<>();
        Element breadcrumb = getFirstElementByClass(doc, CATEGORY);
        if (breadcrumb != null){
            // breadcrumb contains links to categories
            Elements linkTags = breadcrumb.getElementsByTag("a");
            for(Element link: linkTags) {
                // name of category is the text of its link
                String category = link.text();

                // convert from Vietnamese to English name
                category = Category.translateToEnglish(category);
                categoryList.add(category);
            }
        }
        return categoryList;
    }
}

