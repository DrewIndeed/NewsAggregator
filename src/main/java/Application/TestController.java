package Application;

import business.ArticleCollection;
import business.Helper.CATEGORY;
import business.News.Preview;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

public class TestController {
    List<Preview> previews = ArticleCollection.getPreviewsByCategory(CATEGORY.POLITICS);
    Document doc2 = Jsoup.parse(previews.get(0).getArticleHtml());

    @FXML
    public Label cate;
    public VBox scrolling;

    public void change (ActionEvent e) {
        // set title as category
        cate.setText(doc2.select(".article-category").text().replaceAll("[^A-Za-z]+", ""));

        for (int i = 0; i < 10; i++) {
            Document doc1 = Jsoup.parse(previews.get(i).getHtml());
            // 0 can be looped, get each article slots
            HBox articleSlot = (HBox) scrolling.getChildren().get(i);

            // dig into img
            ImageView articlePicture = (ImageView) articleSlot.getChildren().get(0);
            Image tempImgContainer = new Image(doc1.select("img.thumbnail").attr("src"), articlePicture.getFitWidth(), articlePicture.getFitHeight(),false, false);
            articlePicture.setImage(tempImgContainer);

            // get the text area of the article slot
            GridPane textArea = (GridPane) articleSlot.getChildren().get(1);
            Label title = (Label) textArea.getChildren().get(0);
            Label previews = (Label) textArea.getChildren().get(1);

            title.setText(doc1.select(".thumb-title").text());
            previews.setText(doc1.select(".thumb-desp").text());

            title.setStyle("-fx-opacity: 1");
            previews.setStyle("-fx-opacity: 1");

            title.setWrapText(true);
            previews.setWrapText(true);

            title.setTextAlignment(TextAlignment.JUSTIFY);
            previews.setTextAlignment(TextAlignment.JUSTIFY);
        }
    }
}












