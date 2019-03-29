package cracker.tools.distinguisher;

import cracker.tools.Tools;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class DistinguisherController {

    @FXML Label labelResult;
    @FXML TextField textFieldHash;

    @FXML
    private void distinguish(){
        String hash = textFieldHash.getText();
        String type = Tools.distinguishHash(hash);
        if(!type.equals(""))
            labelResult.setText("LoadedHash type: "+type);
        else
            labelResult.setText("Unknown hash type");
    }
}
