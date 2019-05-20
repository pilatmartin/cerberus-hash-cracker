package activation;

import javafx.application.Platform;
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

    @FXML private TextField tfKeyField;
    @FXML private Label lKeyStatus;

    /*
        after entering good product key program will save key into file "product.key"
        this function run every time when user starts program and checks if this file already
        exists and if yes, then check key found in this file
     */
    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // read product key from file
            BufferedReader reader = new BufferedReader(new FileReader("product.key"));
            String productKey = reader.readLine();
            reader.close();

            // enter found key into text field
            tfKeyField.setText(productKey);

            // validate key
            validateKey(productKey);


        }catch (FileNotFoundException e){
            // when file doesn't exist
            System.out.println("Couldn't found key file");
        }catch (NullPointerException e){
            // when file exists but there is nothing in it
            System.out.println("Empty key file");
        }catch (Exception e) {
            // every other exception
            e.printStackTrace();
        }
    }

    /*
        function called when someone click on 'Activate' button
     */
    @FXML private void btnActivate() {

        // read text field
        String productKey = tfKeyField.getText();

        // check product key
        validateKey(productKey);
    }

    /*
        function called when someone click on 'Community edition' button
     */
    @FXML private void btnCommunityEdition() {

        // open main program with false parameter, which means community version
        openMainWindow(false);
    }

    /*
        function caller to open main window
        sends boolean value to main controller:
        true -> full version
        false -> community version
     */
    private void openMainWindow(boolean isFullEdition) {
        try {

            // close activation window (must be in platform run later ??)
            Platform.runLater(() -> {

                // we need to find out which stage we need to close
                Stage oldStage = (Stage) tfKeyField.getScene().getWindow();

                // close found stage
                oldStage.close();
            });

            // copy&paste code
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../main/main.fxml"));

            // copy&paste code
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            // send boolean about version to newly created main controller
            MainController controller = loader.<MainController>getController();
            controller.setIsFullEdition(isFullEdition);

            // set title, min height and width
            stage.setScene(scene);
            stage.setTitle("Cerberus");
            stage.setMinHeight(650);
            stage.setMinWidth(900);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /*
        checks input string with product key
        if the sum of all ascii values in key equals 1875, key is good
        dashes are ignored, they are there just for convenience for users
        ex:
            ABCD-EFGH-...
            A    B    C    D  - E    F    G    H  - ...
            65 + 66 + 67 + 68 + 69 + 70 + 71 + 72 + ...
            sum must equals 1875
     */
    private void validateKey(String key){

        int sum = 0;

        // for every char in string
        for (int i = 0; i < key.length(); i++) {

            // read char one by one
            char c = key.charAt(i);

            // only if char is letter or digit
            if((c >= 'a' && c <= 'z')||(c >= 'A' && c <= 'Z')||(c >= '0' && c <= '9')){

                // add ascii value to sum
                sum += c;
            }
        }

        //if sum equals to our secret value key was good
        if(sum == 1875){

            // save key to file, so user don't need to type product key every time
            writeKeyToFile(key);

            // true parameter means full version
            openMainWindow(true);

        }else {

            // when key is wrong
            lKeyStatus.setText("Bad key");

        }
    }

    /*
        function will save file with product ket into project root
     */
    private void writeKeyToFile(String key){
        try {

            // create or open file
            BufferedWriter writer = new BufferedWriter(new FileWriter("product.key"));

            // write key with newline at the end
            writer.write(key + "\n");

            // close file
            writer.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
