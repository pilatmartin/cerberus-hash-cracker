package tools.hasher;

import tools.Tools;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;


public class HasherController implements Initializable {

    @FXML TextField tfPassword;
    @FXML TextField tfHash;
    @FXML ChoiceBox<String> cbHashAlgorithm;

    @FXML private void hash(){
        String password = tfPassword.getText();

        byte[] byteArray = Tools.hash(password,cbHashAlgorithm.getValue().toString());

        String hexString = Tools.byteArrayToString(byteArray);

        tfHash.setText(hexString);
    }

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] algorithms = {"MD5","SHA","SHA-224","SHA-256","SHA-384","SHA-512"};
        cbHashAlgorithm.getItems().addAll(algorithms);
        cbHashAlgorithm.setValue(cbHashAlgorithm.getItems().get(0));
    }
}
