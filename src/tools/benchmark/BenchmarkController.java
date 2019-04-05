package tools.benchmark;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import tools.Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.net.URL;
import java.util.ResourceBundle;

public class BenchmarkController implements Initializable {

    @FXML private ChoiceBox<String> cbAlgorithm;
    @FXML private Button bBenchmark;
    @FXML private Text tOneThread;
    @FXML private Text tFourThread;
    @FXML private ProgressBar pbProgress;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cbAlgorithm.getItems().add("MD5");
        cbAlgorithm.getItems().add("SHA");
        cbAlgorithm.getItems().add("SHA-224");
        cbAlgorithm.getItems().add("SHA-256");
        cbAlgorithm.getItems().add("SHA-384");
        cbAlgorithm.getItems().add("SHA-512");
        cbAlgorithm.setValue(cbAlgorithm.getItems().get(0));
    }


    @FXML private void btnGenerate(){
        Thread thread = new Thread(() -> {
            try {

                String algorithm = cbAlgorithm.getValue().toString();
                //bw.write(algorithm+"\n");

                counter = 0;
                String password;
                while ((password = br.readLine()) != null) {
                    String hexString = Tools.hash(password, algorithm);
                    if(hexString.equals("d41d8cd98f00b204e9800998ecf8427e")) continue;
                    bw.write(hexString+":"+password+"\n");

                    counter++;
                    Platform.runLater(() -> updateProgressBar());
                }
                br.close();
                bw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    private void updateProgressBar(){
        double progress = (double) counter / fileSize;
        pbProgress.setProgress(progress);
    }

}