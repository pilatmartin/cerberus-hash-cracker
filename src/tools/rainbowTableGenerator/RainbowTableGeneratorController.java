package tools.rainbowTableGenerator;

import tools.Tools;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class RainbowTableGeneratorController implements Initializable {

    @FXML Label lWordlistPath;
    @FXML Label lOutputPath;
    @FXML ChoiceBox<String> cbAlgorithm;
    @FXML ProgressBar pbProgress;

    private File passwordFile;
    private String passwordFilePath;
    private String outputFilePath;

    private static long counter = 0;
    private static long fileSize;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] algorithms = {"MD5","SHA","SHA-224","SHA-256","SHA-384","SHA-512"};
        cbAlgorithm.getItems().addAll(algorithms);
        cbAlgorithm.setValue(cbAlgorithm.getItems().get(0));
    }

    @FXML private void btnSelectPasswordFile() {
        FileChooser fc = new FileChooser();
        passwordFile = fc.showOpenDialog(null);

        fileSize = Tools.countLines(passwordFile);

        passwordFilePath = passwordFile.getAbsolutePath();
        outputFilePath = passwordFilePath + ".rt";

        lWordlistPath.setText(passwordFilePath);
        lOutputPath.setText(outputFilePath);
    }

    @FXML private void btnGenerate(){
        Thread thread = new Thread(() -> {
            try {
                BufferedReader br = new BufferedReader(new FileReader(passwordFilePath));
                BufferedWriter bw = new BufferedWriter(new FileWriter(outputFilePath));

                String algorithm = cbAlgorithm.getValue();

                counter = 0;
                String password;
                while ((password = br.readLine()) != null) {
                    byte[] byteArray = Tools.hash(password, algorithm);
                    String hexString = Tools.byteArrayToString(byteArray);
                    if(hexString.equals("d41d8cd98f00b204e9800998ecf8427e")) continue;
                    bw.write(hexString+":"+password+"\n");

                    counter++;
                    if(counter%1000==0)Platform.runLater(this::updateProgressBar);
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
