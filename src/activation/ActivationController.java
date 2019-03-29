package activation;

import main.MainController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ActivationController implements Initializable {

    @FXML private TextField keyField;
    @FXML private Label keyStatus;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {

            BufferedReader reader = new BufferedReader(new FileReader("product.key"));
            String key = reader.readLine();
            reader.close();

            keyField.setText(key);

        }catch (FileNotFoundException e){

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void btnActivate() {
        String productKey = keyField.getText();
        boolean isValidKey = validateKey(productKey);

        if(isValidKey == true){
            openMainWindow(true);
        }else {
            keyStatus.setText("Bad key");
        }
    }

    @FXML
    private void btnCommunityEdition() {
        openMainWindow(false);
    }

    private void openMainWindow(boolean isFullEdition) {
        try {
            Stage oldStage = (Stage) keyField.getScene().getWindow();
            oldStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("../main/main.fxml"));

            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            MainController controller = loader.<MainController>getController();
            controller.setIsFullEdition(isFullEdition);

            stage.setScene(scene);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private boolean validateKey(String key){
        int sum = 0;
        for (int i = 0; i < key.length(); i++) {
            char c = key.charAt(i);
            if((c >= 'a' && c <= 'z')||(c >= 'A' && c <= 'Z')||(c >= '0' && c <= '9')){
                sum += c;
            }
        }

        switch (sum){
            case 1280:
                writeKeyToFile(key);
                return true;
            default:
                return false;
        }
    }

    private void writeKeyToFile(String key){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("product.key"));
            writer.write(key + "\n");
            writer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
