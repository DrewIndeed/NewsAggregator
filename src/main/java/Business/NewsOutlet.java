package Business;

import Business.News.Article;
import Business.Scraper.ArticleCrawler.Scraper;
import Business.Scraper.LinksCrawler.LinksCrawler;

import java.net.URL;
import java.util.List;
import java.util.Set;

import static Business.GetNewsOutlets.getNewsOutletsSize;
import static Business.Scraper.Helper.ScrapingUtils.MAX_ARTICLES_DISPLAYED;


public class NewsOutlet {
    // TODO: Khang comments this
    public final Scraper scraper;
    public final LinksCrawler linksCrawler;

    public NewsOutlet(Scraper scraper, LinksCrawler linksCrawler){
        this.scraper = scraper;
        this.linksCrawler = linksCrawler;
    }

    public void populateArticleList(List<Article> articleList, String category) {
        Set<URL> urls = linksCrawler.getArticleLinks(category);
//        int articlesScraped = 0;
        for (URL url : urls) {
            /* get the number of articles each news outlet need to scrape by dividing total articles displayed with
            total news outlets
            * */
//            if (articlesScraped ==
//                    MAX_ARTICLES_DISPLAYED/getNewsOutletsSize() +
//                            (MAX_ARTICLES_DISPLAYED % getNewsOutletsSize() == 0 ? 0 : 1)){
//                break;
//            }

            if (articleList.size() >= MAX_ARTICLES_DISPLAYED){
                break;
            }

            Article a = scraper.getArticle(url.toString());
            if (a != null){
                articleList.add(a);
//                articlesScraped++;
            }

        }
    }
}