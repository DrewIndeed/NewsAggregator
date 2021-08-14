package Application;
import business.GetArticleListService;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import business.News.Article;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.util.*;

import static Application.Helper.createPreviewPane;

public class Controller {
    @FXML
    private Pane previewBox;
    @FXML
    private ScrollPane mainArea;

    List<Article> articles; // keep reference to change pages
    GetArticleListService service;
    private static final int PREVIEWS_PER_PAGE = 10;
    public void initialize(){
        System.out.println("system initialized!");
    }
    public void displayNews(ActionEvent e){
        Object o = e.getSource();
        if (o instanceof Button){
            Button b = (Button) o;
            if(service != null){
                service.cancel();
            }
            String category = b.getText();
            displayPreviews(category);
        }
    }

    private void displayPreviews(String category){
        loadArticles(category);
    }

    private void changePage(int pageNum){
        // TODO: implement change page behavior
//        generatePreviews();
    }

    private void generatePreviews(){
        previewBox.getChildren().clear();
        for (int i = 0; i < PREVIEWS_PER_PAGE; i++){
            previewBox.getChildren().add(createPreviewPane(articles.get(i)));
        }
        mainArea.setContent(previewBox);
    }

    private void loadArticles(String category){
        service = new GetArticleListService(category);
        service.setOnSucceeded(e -> {
            articles = (List<Article>) e.getSource().getValue();
            generatePreviews();
        });

        StackPane stackPane = new StackPane();

//        Region veil = new Region();
//        veil.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4)");
//        veil.setPrefSize(500, 500);

        ProgressIndicator p = new ProgressIndicator();
        p.setMaxSize(140, 140);
        p.setStyle(" -fx-progress-color: orange;");

        p.progressProperty().bind(service.progressProperty());
//        veil.visibleProperty().bind(service.runningProperty());
        p.visibleProperty().bind(service.runningProperty());

        stackPane.getChildren().addAll(p);
        mainArea.setContent(stackPane);

        service.start();
    }
}



