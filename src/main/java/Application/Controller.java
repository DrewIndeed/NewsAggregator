package Application;

import business.ArticleCollection;
import business.News.Preview;
import business.Helper.CATEGORY;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;


public class Controller {
    List<Preview> previews = ArticleCollection.getPreviewsByCategory(CATEGORY.POLITICS);
    Document doc2 = Jsoup.parse(previews.get(0).getArticleHtml());

    @FXML
    public Label articleCate;
    public VBox subVbox;

    public void change (ActionEvent e) {
        articleCate.setText(doc2.select(".article-category").text());
        for (int i = 1; i <= 10; i++) {
            GridPane eachArticleMainPane = (GridPane) subVbox.getChildren().get(i - 1);
            ImageView eachArticleImg = (ImageView) eachArticleMainPane.getChildren().get(1);

            Document doc = Jsoup.parse(previews.get(i - 1).getHtml());

            Image tempImgContainer = new Image(doc.select(".thumbnail").attr("src"));
            eachArticleImg.setImage(tempImgContainer);

            GridPane eachArticleSubGridPane = (GridPane) eachArticleMainPane.getChildren().get(0);
            Label eachArticleTitle = (Label) eachArticleSubGridPane.getChildren().get(0);
            Label eachArticlePreview = (Label) eachArticleSubGridPane.getChildren().get(1);
            eachArticleTitle.setText(doc.select(".thumb-title").text());
            eachArticlePreview.setText(doc.select(".thumb-desp").text());

            //wrapping the label
            eachArticleTitle.setWrapText(true);
            //Setting the alignment to the label
            eachArticleTitle.setTextAlignment(TextAlignment.JUSTIFY);

            //wrapping the label
            eachArticlePreview.setWrapText(true);
            //Setting the alignment to the label
            eachArticlePreview.setTextAlignment(TextAlignment.JUSTIFY);
        }
    }
}