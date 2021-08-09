package Application;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/test.fxml")));
        ScrollPane mainScroll = (ScrollPane) root.getChildrenUnmodifiable().get(2);
        VBox mainStack = (VBox) mainScroll.getContent();
        HBox baseArticleSlot = (HBox) mainStack.getChildren().get(0);
        ImageView baseArticleImg = (ImageView) baseArticleSlot.getChildren().get(0);
        GridPane baseTextContent = (GridPane) baseArticleSlot.getChildren().get(1);
        Label baseTitle = (Label) baseTextContent.getChildren().get(0);
        Label basePreview = (Label) baseTextContent.getChildren().get(1);

        for (int i = 1; i < 10; i++) {
            if (i > 5) mainStack.setPrefHeight(mainStack.getPrefHeight() + baseArticleSlot.getPrefHeight());
            // new article slot
            HBox newArticleSlot = new HBox();
            newArticleSlot.setPrefWidth(baseArticleSlot.getPrefWidth());
            newArticleSlot.setPrefHeight(baseArticleSlot.getPrefHeight());
            mainStack.getChildren().add(newArticleSlot);
            VBox.setMargin(mainStack.getChildren().get(i), new Insets(10,0,10,0));

            // new article image
            ImageView newArticleImg = new ImageView();
            newArticleImg.setPickOnBounds(true);
            newArticleImg.setPreserveRatio(true);
            newArticleImg.setFitWidth(baseArticleImg.getFitWidth());
            newArticleImg.setFitHeight(baseArticleImg.getFitHeight());
            newArticleSlot.getChildren().add(newArticleImg);

            // new grid pane to contain text display area
            GridPane newTextContent = new GridPane();
            newTextContent.setPrefWidth(baseTextContent.getPrefWidth());
            newTextContent.setPrefHeight(baseTextContent.getPrefHeight());
            ColumnConstraints onlyCol = baseTextContent.getColumnConstraints().get(0);
            RowConstraints titleRow = baseTextContent.getRowConstraints().get(0);
            RowConstraints prevRow = baseTextContent.getRowConstraints().get(1);
            newTextContent.getColumnConstraints().addAll(onlyCol);
            newTextContent.getRowConstraints().addAll(titleRow, prevRow);
            newArticleSlot.getChildren().add(newTextContent);

            // add title to text content area
            Label newArticleTitle = new Label();
            newArticleTitle.setAlignment(baseTitle.getAlignment());
            newArticleTitle.setPrefHeight(baseTitle.getPrefHeight());
            newArticleTitle.setPrefWidth(baseTitle.getPrefWidth());
            newArticleTitle.setText(baseTitle.getText());
            newArticleTitle.setFont(baseTitle.getFont());
            newArticleTitle.setStyle("-fx-opacity: 0");
            newTextContent.add(newArticleTitle, 0, 0);

            // add preview to text content area
            Label newArticlePreviews = new Label();
            newArticlePreviews.setAlignment(basePreview.getAlignment());
            newArticlePreviews.setPrefHeight(basePreview.getPrefHeight());
            newArticlePreviews.setPrefWidth(basePreview.getPrefWidth());
            newArticlePreviews.setText(basePreview.getText());
            newArticlePreviews.setFont(basePreview.getFont());
            newArticlePreviews.setStyle("-fx-opacity: 0");
            newTextContent.add(newArticlePreviews, 0, 1);
        }
        primaryStage.setTitle("Show HTML");
        primaryStage.setScene(new Scene(root, 1200, 1200));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
