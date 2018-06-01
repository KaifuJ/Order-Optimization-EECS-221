package sample;

import com.sun.scenario.effect.impl.sw.java.JSWBlend_BLUEPeer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private BorderPane borderPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setAlignment(Pos.TOP_LEFT);
        Rectangle[][] cells = new Rectangle[21][39];

        for (int i = 0; i < cells.length; i++) { // 21
            for (int j = 0; j < cells[0].length; j++) { // 39
                cells[i][j] = new Rectangle();
                cells[i][j].setWidth(20);
                cells[i][j].setHeight(20);
                GridPane.setConstraints(cells[i][j], j, i);
                if ((j - 1) % 2 == 0 && (20 - i) % 2 == 0) {
                    cells[i][j].setFill(Color.RED);
                } else {
                    cells[i][j].setFill(Color.BLUE);
                }
                grid.getChildren().add(cells[i][j]);
            }
        }
        grid.setGridLinesVisible(true);
        borderPane.setCenter(grid);
    }
}
