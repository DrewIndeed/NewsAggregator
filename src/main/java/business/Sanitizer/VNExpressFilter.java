package business.Sanitizer;

import business.Helper.CSS;
import business.Helper.Scraper;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.safety.Safelist;
import org.jsoup.select.NodeFilter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VNExpressFilter implements NodeFilter {
    Element root;

    public VNExpressFilter(Element root) {
        this.root = root;
    }

    @Override
    public FilterResult head(Node node, int i) {
        if (!(node instanceof Element)) return FilterResult.SKIP_ENTIRELY;

        Element child = (Element) node;
        boolean validTag = false;

        // skip these tags immediately
        if (child.attr("style").contains("display: none"))
            return FilterResult.SKIP_ENTIRELY;

        // get paragraph
        if (child.tagName().equals("p")) {
            Element para = new Element("p");

            if (child.hasClass(CSS.VNEXPRESS_PARAGRAPH))
                para.addClass(CSS.PARAGRAPH);

//            if (child.hasClass(CSS.VNEXPRESS_AUTHOR))
//                para.addClass(CSS.AUTHOR);

            Safelist safelist = Safelist.basic();
            String cleanHtml = Jsoup.clean(child.html(), safelist);
            para.html(cleanHtml);

            root.append(para.outerHtml());
            validTag = true;
        }

        // get img tag that is in figure tag
        else if (child.tagName().equals("figure")) {
            Element figure = filterFigureTag(child);
            if (figure != null) {
                figure.addClass(CSS.FIGURE);
                root.append(figure.outerHtml());
            }
            validTag = true;
        }

        // get standalone img tag
        else if (child.tagName().equals("img")) {
            child = Scraper.createCleanImgTag(child);
            if (child != null) {
                child.addClass(CSS.FIGURE);
                root.append(child.outerHtml());
            }

            validTag = true;
        }

        // get video
        else if (child.tagName().equals("video")) {
            Element videoTag = filterVideoTag(child);
            if (videoTag != null) {
                videoTag.addClass(CSS.VIDEO);
                root.append(videoTag.outerHtml());
            }
            validTag = true;
        }

        // get relevant news at the end of the article
//        else if (child.hasClass("list-news")) {
//            Element relevantNews = filterRelevantNewsTag(child);
//            if (relevantNews != null) {
//                relevantNews.addClass(CSS.RELEVANT_NEWS);
//                root.append(relevantNews.outerHtml());
//            }
//            validTag = true;
//        }

        // stop traversing if a valid tag is found
        // regardless of if it has been appended
        if (validTag) return FilterResult.SKIP_ENTIRELY;
        else return FilterResult.CONTINUE;

    }

    @Override
    public FilterResult tail(Node node, int i) {

        return null;
    }

    private static Element filterRelevantNewsTag(Element tag) {
        Safelist safelist = Safelist.basic();
        safelist.removeTags("span");
        String cleanHtml = Jsoup.clean(tag.html(), safelist);
        Element relevantNews = new Element("ul");

        if (!StringUtils.isEmpty(cleanHtml)) {
            return relevantNews.html(cleanHtml);
        } else return null;
    }

    // create a figure tag with img AND figcaption tagS
    private static Element filterFigureTag(Element tag) {
        for (Element img : tag.getElementsByTag("img")) {
            String src = img.attr("data-src");
            if (!StringUtils.isEmpty(src)) {
                img.attr("src", src);
            }
        }
        Safelist safelist = Safelist.basicWithImages();
        safelist.addTags("figcaption");

        String cleanHtml = Jsoup.clean(tag.html(), safelist);


        if (StringUtils.isEmpty(cleanHtml))
            return null;

        Element figure = new Element("figure");
        return figure.html(cleanHtml);
    }

    private static Element filterVideoTag(Element tag) {
        URL src;
        try {
            src = new URL(tag.attr("src"));
        } catch (MalformedURLException e) {
            return null;
        }

        String protocol = src.getProtocol();
        String host = src.getHost();
        String file = src.getFile();

        host = host.replaceFirst(Pattern.quote("d1"), Matcher.quoteReplacement("v"));

        // remove an extra /video from file path
        file = file.replaceFirst(Pattern.quote("/video"), Matcher.quoteReplacement(""));

        // remove resolution from file path
        file = file.replaceFirst(Pattern.quote("/,240p,360p,480p,,"), Matcher.quoteReplacement(""));
        file = file.replaceFirst(Pattern.quote("/,240p,360p,480p,720p,"), Matcher.quoteReplacement(""));
        file = file.replaceFirst(Pattern.quote("/,240p,360p,,"), Matcher.quoteReplacement(""));
        file = file.replaceFirst(Pattern.quote("/,240p,,"), Matcher.quoteReplacement(""));

        // change extension to mp4
        file = file.replaceFirst(Pattern.quote("/vne/master.m3u8"), Matcher.quoteReplacement(".mp4"));

        try {
            src = new URL(protocol, host, file);
        } catch (MalformedURLException e) {
            return null;
        }

        Element newVideo = new Element("video");
        newVideo.attr("controls", true);

        Element newVideoSrc = new Element("source");
        newVideoSrc.attr("src", src.toString());

        newVideo.appendChild(newVideoSrc);
        return newVideo;
    }
}