package main.help;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class howToController implements Initializable {

    @FXML
    public Button mainBtn, toolsBtn;
    @FXML
    private BorderPane borderPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        center mainCenter = new center("main/help/HowToMain.fxml");
        center toolsCenter = new center("main/help/HowToTools.fxml");
        borderPane.setCenter(mainCenter);

        mainBtn.setOnAction(mouseEvent->{
            borderPane.setCenter(mainCenter);
        });
        toolsBtn.setOnAction(mouseEvent->{
            borderPane.setCenter(toolsCenter);
        });

    }
}
