package tools.benchmark;

import entity.ByteArray;
import entity.LoadedHash;
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

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] algorithms = {"MD5","SHA","SHA-224","SHA-256","SHA-384","SHA-512"};
        cbAlgorithm.getItems().addAll(algorithms);
        cbAlgorithm.setValue(cbAlgorithm.getItems().get(0));
    }

    static private long threadCounter = 0;
    static private boolean isBenchmarking = false;

    private byte threadsCount = 4;

    @FXML private void btnGenerate(){

        final String attackAlgorithm = cbAlgorithm.getValue();


        Long[] threadArray = new Long[4];
        Thread[] threads = new Thread[threadsCount];


        isBenchmarking =  !isBenchmarking;

        Map<ByteArray, LoadedHash> testMap = new HashMap<>();
        testMap.put(new ByteArray(new byte[]{100,100}), new LoadedHash("00"));


        for (int i = 0; i < threads.length; i++) {

            int finalI = i;

            threads[i] = new Thread(() -> {

                long startTime = System.currentTimeMillis();

                while(true) {

                    long currentTimeMillis = System.currentTimeMillis();

                    if (startTime + 5000 < currentTimeMillis) stopBenchmark();
                    if (!isBenchmarking) break;

                    ByteArray hexString = new ByteArray(Tools.hash("benchmark", attackAlgorithm));

                    if (testMap.containsKey(hexString));

                    threadCounter++;
                    if (threadCounter % 1000 == 0) Platform.runLater(() -> {
                        updateProgressBar(currentTimeMillis-startTime, 5000);
                    });
                }
                threadArray[finalI] = threadCounter/5;

                String out = String.format("One thread %,d H/s", threadArray[0]);
                tOneThread.setText(out);
                out = String.format("Four threads: %,d H/s", threadArray[0] + threadArray[1] + threadArray[2] + threadArray[3]);
                tFourThread.setText(out);
            });
            threads[i].start();
        }
        threadCounter=0;

    }


    private void stopBenchmark(){
        isBenchmarking = false;
    }

    private void updateProgressBar(long current, long full){
        double progress = (double) current / full;
        pbProgress.setProgress(progress);
    }

}