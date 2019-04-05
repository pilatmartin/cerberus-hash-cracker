package tools.benchmark;

import entity.CrackedHash;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.text.Text;
import tools.Tools;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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

    static private long oneThreadCounter = 0;
    static private boolean isBenchmarking = false;


    @FXML private void btnGenerate(){

        final String attackAlgorithm = cbAlgorithm.getValue().toString();

        isBenchmarking = true;

        Map<String,CrackedHash> testMap = new HashMap<>();

        oneThreadCounter = 0;
        Thread thread = new Thread(() -> {

            long startTime = System.currentTimeMillis();

            while(true) {

                long currentTimeMillis = System.currentTimeMillis();

                if (startTime + 10000 < currentTimeMillis) stopBenchmark();
                if (!isBenchmarking) break;

                String hexString = Tools.hash("benchmark", attackAlgorithm);

                if (testMap.containsKey(hexString));


                oneThreadCounter++;
                if (oneThreadCounter % 1000 == 0) Platform.runLater(() -> {
                    updateProgressBar(currentTimeMillis-startTime, 10000);
                });
            }

            String out = String.format("One thread %,d H/s", oneThreadCounter/10);
            tOneThread.setText(out);
            out = String.format("Four threads: %,d H/s", (oneThreadCounter/10)*4);
            tFourThread.setText(out);
        });
        thread.start();
    }

    private void stopBenchmark(){
        isBenchmarking = false;
    }

    private void updateProgressBar(long current, long full){
        double progress = (double) current / full;
        pbProgress.setProgress(progress);
    }

}