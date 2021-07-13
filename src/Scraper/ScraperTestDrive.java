package Scraper;

import News.Article;
import News.Content.ContentFactory;
import News.NewsOutlet;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;

public class ScraperTestDrive {
    public static void main(String[] args) {
        HashMap<String, NewsOutlet> newsOutlets = NewsOutlet.initializeNewsOutlets();

        Scraper VNExpressScrapper = new Scraper(newsOutlets.get("vnexpress"));
        Scraper ZingNewsScrapper = new Scraper(newsOutlets.get("zingnews"));
        Scraper TuoiTreScrapper = new Scraper(newsOutlets.get("tuoitre"));
        Scraper ThanhNienScrapper = new Scraper(newsOutlets.get("thanhnien"));
        Scraper NhanDanScrapper = new Scraper(newsOutlets.get("nhandan"));

        final long startTime = System.currentTimeMillis();

        HashSet<Article> allArticles = new HashSet<>();
        allArticles.addAll(ZingNewsScrapper.getArticlesFromCategories());
        allArticles.addAll(VNExpressScrapper.getArticlesFromCategories());
        allArticles.addAll(TuoiTreScrapper.getArticlesFromCategories());
        allArticles.addAll(ThanhNienScrapper.getArticlesFromCategories());
        allArticles.addAll(NhanDanScrapper.getArticlesFromCategories());

        System.out.println(allArticles.size());

        for (Article article: allArticles){
            article.displayTitleAndCategory();
            System.out.println("---- time: " + article.getDateTime());
        }

        final long endTime = System.currentTimeMillis();
        System.out.println("Total execution time: " + (double) (endTime - startTime)/1000);

    }
}