package main.help;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class center extends AnchorPane {

    center(String path){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(path));

        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
            System.out.println(fxmlLoader);
        } catch (IOException e) {
            System.out.println("to nic heh");
        }
    }

}
