package News;

import org.jsoup.nodes.Element;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;

public class Article {
    static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EE, dd/MMMM/yyyy, kk:mm ");
    static boolean validateTag(Element e, String type) throws Exception {
        if (e == null)
            throw new Exception("Element For " + type + " Not Found");

        if (e.text().isEmpty())
            throw new Exception("Empty Title");

        return true;
    }
    // attributes
    URL url;
    Element title;
    Element description;
    Element mainContent;
    Element thumbNail;
    LocalDateTime dateTime;
    HashSet<String> categories = new HashSet<>();

    String newsSource;

    public Article(){

    }

    public boolean belongsToCategory(String category){
        return this.categories.contains(category);
    }

    public String getHtml(){
        Element article = new Element("article");

        // create header div
        Element header = new Element("div");
        header.addClass(CSS.ARTICLE_HEADER);

        // category div
        Element categories = new Element("div");
        categories.addClass(CSS.ARTICLE_CATEGORY);
        StringBuilder categoriesStrBuilder = new StringBuilder();
        for (String category: this.categories){
            categoriesStrBuilder.append(category).append(" - ");
        }
        String categoriesStr = categoriesStrBuilder.substring(0, categoriesStrBuilder.length() - 2);
        categories.text(categoriesStr);

        // published time div
        Element publishedTime = new Element("div");
        publishedTime.addClass(CSS.PUBLISHED_TIME);
        publishedTime.text(getAbsoluteTime());

        header.appendChild(categories);
        header.appendChild(publishedTime);

        // create article content div which contains title, desp, and main content
        Element content = new Element("div");
        content.addClass(CSS.ARTICLE_CONTENT);
        content.appendChild(title);
        content.appendChild(description);
        content.appendChild(mainContent);

        article.appendChild(header);
        article.appendChild(content);

        return article.outerHtml();
    }

    public String getAbsoluteTime(){
        return dtf.format(this.dateTime);
    }

    public String getRelativeTime(){
        long minutes = ChronoUnit.MINUTES.between(dateTime, LocalDateTime.now());

        if (minutes == 0){
            long seconds = ChronoUnit.SECONDS.between(dateTime, LocalDateTime.now());
            if (seconds < 2)
                return "Just now.";
            return seconds + " seconds" + " ago.";
        }
        else if (minutes < 60){
            return minutes + (minutes == 1 ? " minute" : " minutes") + " ago";
        }
        else if (minutes < 1440){ // 1440 minutes = 1 day
            long hours = minutes/60;
            return hours + (hours == 1 ? " hour " : " hours ") + minutes%60 + (minutes == 1 ? " minute" : " minutes") + " ago.";
        }
        else {
            long days = minutes/1440;
            return days + (days == 1 ? " day" : " days") + " ago.";
        }
    }

    public long getMinutesSincePublished() {
        return ChronoUnit.MINUTES.between(dateTime, LocalDateTime.now());
    }


    // setters
    public void setUrl(URL url) throws Exception {
        if (url == null)
            throw new Exception("No Url Found For Article");

        this.url = url;
    }

    public void setTitle(Element title) throws Exception {
       if (validateTag(title, "Title"))
           this.title = title;
    }

    public void setDescription(Element description) throws Exception {
        if (validateTag(description, "Description"))
            this.description = description;
    }

    public void setMainContent(Element mainContent) throws Exception {
        if (validateTag(mainContent, "Main Content"))
            this.mainContent = mainContent;
    }

    public void setThumbNailUrl(Element thumbNail) throws Exception {
        if (validateTag(mainContent, "Thumbnail"))
            this.thumbNail = thumbNail;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void addCategory(String category){
        // check valid category
        this.categories.add(category);
    }

    public void setNewsSource(String newsSource) {
        this.newsSource = newsSource;
    }

    public Preview getPreview(){


        Element source = new Element("div");
        source.addClass(CSS.SOURCE);
        source.text(newsSource);

        Element relativeTime = new Element("div");
        relativeTime.addClass(CSS.PUBLISHED_TIME);
        relativeTime.text(getRelativeTime());

        Element thumbHeader = new Element("div");
        thumbHeader.addClass(CSS.THUMBNAIL_HEADER);
        thumbHeader.appendChild(source);
        thumbHeader.appendChild(relativeTime);


        Element thumbTitle = new Element("div");
        thumbTitle.addClass(CSS.THUMBNAIL_TITLE);
        thumbTitle.text(title.text());

        Element thumbDesp = new Element("div");
        thumbDesp.addClass(CSS.THUMBNAIL_DESCRIPTION);
        thumbDesp.text(description.text());


        Element preview = new Element("div");
        preview.addClass(CSS.PREVIEW);

        preview.appendChild(thumbNail);
        preview.appendChild(thumbHeader);
        preview.appendChild(thumbTitle);
        preview.appendChild(thumbDesp);

        return new Preview(preview, this);

    }


}
