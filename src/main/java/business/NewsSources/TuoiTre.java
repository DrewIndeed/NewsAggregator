package business.NewsSources;

import business.Helper.CATEGORY;
import business.Helper.CSS;
import business.Helper.LocalDateTimeParser;
import business.Sanitizer.HtmlSanitizer;
import business.Sanitizer.TuoiTreSanitizer;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TuoiTre extends NewsOutlet {
    private static final Category COVID = new Category(CATEGORY.COVID, "https://tuoitre.vn/covid-19.html", CSS.TUOITRE_TITLE_LINK);
    private static final Category POLITICS = new Category(CATEGORY.POLITICS, "https://tuoitre.vn/thoi-su.htm", CSS.TUOITRE_TITLE_LINK);
    static {
        POLITICS.addSub("https://tuoitre.vn/thoi-su/but-bi.htm");
        POLITICS.addSub("https://tuoitre.vn/thoi-su/xa-hoi.htm");
        POLITICS.addSub("https://tuoitre.vn/thoi-su/phong-su.htm");
        POLITICS.addSub("https://tuoitre.vn/thoi-su/binh-luan.htm");
    }


    private static final Category BUSINESS = new Category(CATEGORY.BUSINESS, "https://tuoitre.vn/kinh-doanh.htm", CSS.TUOITRE_TITLE_LINK);
    static {
        BUSINESS.addSub("https://tuoitre.vn/kinh-doanh/tai-chinh.htm");
        BUSINESS.addSub("https://tuoitre.vn/kinh-doanh/doanh-nghiep.htm");
        BUSINESS.addSub("https://tuoitre.vn/kinh-doanh/mua-sam.htm");
        BUSINESS.addSub("https://tuoitre.vn/kinh-doanh/dau-tu.htm");
    }

    private static final Category TECHNOLOGY = new Category(CATEGORY.TECHNOLOGY, "https://tuoitre.vn/khoa-hoc.htm", CSS.TUOITRE_TITLE_LINK);
    static {
        TECHNOLOGY.addSub("https://tuoitre.vn/khoa-hoc/thuong-thuc.htm");
        TECHNOLOGY.addSub("https://tuoitre.vn/khoa-hoc/phat-minh.htm");
    }

    private static final Category HEALTH = new Category(CATEGORY.HEALTH, "https://tuoitre.vn/suc-khoe.htm", CSS.TUOITRE_TITLE_LINK);
    static {
        HEALTH.addSub("https://tuoitre.vn/suc-khoe/dinh-duong.htm");
        HEALTH.addSub("https://tuoitre.vn/suc-khoe/me-va-be.htm");
        HEALTH.addSub("https://tuoitre.vn/suc-khoe/gioi-tinh.htm");
        HEALTH.addSub("https://tuoitre.vn/suc-khoe/phong-mach.htm");
        HEALTH.addSub("https://tuoitre.vn/suc-khoe/biet-de-khoe.htm");
    }

    private static final Category SPORTS = new Category(CATEGORY.SPORTS, "https://tuoitre.vn/the-thao.htm", CSS.TUOITRE_TITLE_LINK);
    static {
        SPORTS.addSub("https://tuoitre.vn/the-thao/bong-da.htm");
        SPORTS.addSub("https://tuoitre.vn/the-thao/bong-ro.htm");
        SPORTS.addSub("https://tuoitre.vn/the-thao/vo-thuat.htm");
        SPORTS.addSub("https://tuoitre.vn/the-thao/cac-mon-khac.htm");
        SPORTS.addSub("https://tuoitre.vn/the-thao/khoe-360.htm");
        SPORTS.addSub("https://tuoitre.vn/the-thao/nguoi-ham-mo.htm");
    }

    private static final Category ENTERTAINMENT = new Category(CATEGORY.ENTERTAINMENT, "https://tuoitre.vn/giai-tri.htm", CSS.TUOITRE_TITLE_LINK);
    static {
        ENTERTAINMENT.addSub("https://tuoitre.vn/giai-tri/nghe-gi-hom-nay.htm");
        ENTERTAINMENT.addSub("https://tuoitre.vn/giai-tri/am-nhac.htm");
        ENTERTAINMENT.addSub("https://tuoitre.vn/giai-tri/dien-anh.htm");
        ENTERTAINMENT.addSub("https://tuoitre.vn/giai-tri/tv-show.htm");
        ENTERTAINMENT.addSub("https://tuoitre.vn/giai-tri/thoi-trang.htm");
        ENTERTAINMENT.addSub("https://tuoitre.vn/giai-tri/hau-truong.htm");
    }

    private static final Category WORLD = new Category(CATEGORY.WORLD, "https://tuoitre.vn/the-gioi.htm", CSS.TUOITRE_TITLE_LINK);
    static {
        WORLD.addSub("https://tuoitre.vn/the-gioi/binh-luan.htm");
        WORLD.addSub("https://tuoitre.vn/the-gioi/kieu-bao.htm");
        WORLD.addSub("https://tuoitre.vn/the-gioi/muon-mau.htm");
        WORLD.addSub("https://tuoitre.vn/the-gioi/ho-so.htm");
    }

    private static final Category OTHERS = new Category(CATEGORY.OTHERS, "", CSS.TUOITRE_TITLE_LINK);
    static {
        OTHERS.addSub("https://tuoitre.vn/phap-luat.htm");
        OTHERS.addSub("https://tuoitre.vn/xe.htm");
        OTHERS.addSub("https://dulich.tuoitre.vn/");
        OTHERS.addSub("https://tuoitre.vn/nhip-song-tre.htm");
        OTHERS.addSub("https://tuoitre.vn/van-hoa.htm");
        OTHERS.addSub("https://tuoitre.vn/giao-duc.htm");
        OTHERS.addSub("https://tuoitre.vn/gia-that.htm");
        OTHERS.addSub("https://tuoitre.vn/ban-doc-lam-bao.htm");
    }


    public static NewsOutlet init() {
        HashMap<String, Category> categories = new HashMap<>();
        categories.put(CATEGORY.COVID, COVID);
        categories.put(CATEGORY.POLITICS, POLITICS);
        categories.put(CATEGORY.BUSINESS, BUSINESS);
        categories.put(CATEGORY.TECHNOLOGY, TECHNOLOGY);
        categories.put(CATEGORY.HEALTH, HEALTH);
        categories.put(CATEGORY.SPORTS, SPORTS);
        categories.put(CATEGORY.ENTERTAINMENT, ENTERTAINMENT);
        categories.put(CATEGORY.WORLD, WORLD);
        categories.put(CATEGORY.OTHERS, OTHERS);

        CssConfiguration TuoiTreCssConfig = new CssConfiguration(
                "https://tuoitre.vn/",
                CSS.TUOITRE_TITLE,
                CSS.TUOITRE_DESCRIPTION,
                CSS.TUOITRE_BODY,
                CSS.TUOITRE_TIME,
                CSS.TUOITRE_PIC);
        return new TuoiTre("Tuoi Tre",
                "https://dangkyxettuyennghe.tuoitre.vn/img/logo-tt.png",
                categories,
                TuoiTreCssConfig,
                new TuoiTreSanitizer());
    }

    public TuoiTre(String name, String defaultThumbnail, HashMap<String, Category> categories, CssConfiguration cssConfiguration, HtmlSanitizer sanitizer) {
        super(name, defaultThumbnail, categories, cssConfiguration, sanitizer);
    }

    @Override
    public LocalDateTime getPublishedTime(Document doc) {
        Elements dateTimeTag = doc.getElementsByAttributeValue("property", cssConfiguration.publishedTime);

        String dateTimeStr = dateTimeTag.attr("content");
        if (StringUtils.isEmpty(dateTimeStr)) {
            return LocalDateTime.now();
        }

        return LocalDateTimeParser.parse(dateTimeStr);
    }

    @Override
    public List<String> getCategoryNames(Document doc) {
        // get parent category
        Element tag = doc.getElementsByAttributeValue("property", "article:section").first();
        String parentCategory;
        if (tag == null) {
            parentCategory = CATEGORY.OTHERS;
        } else {
            parentCategory = tag.attr("content");
            parentCategory = CATEGORY.convert(parentCategory);
        }

        List<String> categoryList = new ArrayList<>();
        categoryList.add(parentCategory);

        // get child category
        Element childrenCategoryTag = doc.selectFirst(".breadcrumbs");
        if (childrenCategoryTag != null) {
            Elements children = childrenCategoryTag.getElementsByTag("a");
            for (Element e : children) {
                String category = e.attr("title");
                System.out.println(category);
                category = CATEGORY.convert(category);
                if (!categoryList.contains(category)) {
                    categoryList.add(category);
                }
            }
        }
        return categoryList;
    }
}
