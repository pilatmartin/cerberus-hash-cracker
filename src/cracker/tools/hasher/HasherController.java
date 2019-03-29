package cracker.tools.hasher;

import cracker.tools.Tools;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.security.MessageDigest;
import java.security.Security;
import java.util.ResourceBundle;
import java.util.Set;


public class HasherController implements Initializable {

    @FXML TextField tfPassword;
    @FXML TextField tfHash;
    @FXML ChoiceBox cbHashAlgorithm;

    @FXML private void hash(){
        String password = tfPassword.getText();

        String hexString = Tools.hash(password,cbHashAlgorithm.getValue().toString());

        tfHash.setText(hexString);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbHashAlgorithm.getItems().add("MD5");
        cbHashAlgorithm.getItems().add("SHA");
        cbHashAlgorithm.getItems().add("SHA-224");
        cbHashAlgorithm.getItems().add("SHA-256");
        cbHashAlgorithm.getItems().add("SHA-384");
        cbHashAlgorithm.getItems().add("SHA-512");
        cbHashAlgorithm.setValue(cbHashAlgorithm.getItems().get(0));
    }
}
