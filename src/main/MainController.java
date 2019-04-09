package main;

import entity.*;
import tools.Tools;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {

    private boolean isFullEdition;
    public void setIsFullEdition(boolean isFullEdition) {
        this.isFullEdition = isFullEdition;
    }

    private Project project = new Project();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // print on console if we are using full version of cerberus
        Platform.runLater(() -> System.out.println("isFullEdition = "+isFullEdition));

        // initialize table columns for 'analyze hashes' tab
        tcAlgorithm.setCellValueFactory(new PropertyValueFactory<>("algorithm"));
        tcHash.setCellValueFactory(new PropertyValueFactory<>("hexString"));

        // initialize table columns for 'start crack' tab
        tcCrackedPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        tcCrackedHash.setCellValueFactory(new PropertyValueFactory<>("hexString"));

        // fill choice box on 'start crack' tab with values
        String[] algorithms = {"MD5","SHA","SHA-224","SHA-256","SHA-384","SHA-512"};
        cbAttackAlgorithm.getItems().addAll(algorithms);
        cbAttackAlgorithm.setValue(cbAttackAlgorithm.getItems().get(0));
    }

    ////////////////////////////////////////////MENU////////////////////////////////////////////////////////////////////

    @FXML private void btnOpenProject(){
        if(!isFullEdition){
            String title = "Using community version";
            String text = "You are using community version, you can't open another project";
            Tools.openInfoWindow(title, text);
            return;
        }

        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);

        if(file == null) return;

        Thread thread = new Thread(() -> {
            ObjectInputStream objectinputstream = null;
            try {
                FileInputStream streamIn = new FileInputStream(file);
                objectinputstream = new ObjectInputStream(streamIn);
                project = (Project) objectinputstream.readObject();
                objectinputstream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Platform.runLater(() -> {
                updateAnalyzedHashesTableView();
                updateLoadedHashesListView();
                updateCrackedHashesTableView();
            });
        });
        thread.start();

    }

    @FXML private void btnSaveProject(){
        if(!isFullEdition){
            String title = "Using community version";
            String text = "You are using community version, you can't save your project";
            Tools.openInfoWindow(title, text);
            return;
        }
        FileChooser saveAs = new FileChooser();
        File outputFile = saveAs.showSaveDialog(null);

        if(outputFile==null) return;

        ObjectOutputStream oos = null;
        FileOutputStream fout = null;
        try{
            fout = new FileOutputStream(outputFile+".cerb", true);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(project);
            oos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML private void btnExportToCSV(){

        FileChooser saveAs = new FileChooser();

        File outputFile = saveAs.showSaveDialog(null);

        if (outputFile != null) {
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile+".csv"));

                for (CrackedHash crackedHash : project.getCrackedHashes()) {
                    writer.write(crackedHash.getHexString()+";"+crackedHash.getPassword()+"\n");
                }

                writer.close();
                System.out.println("Saved");
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @FXML private void btnExit(){
        isCracking = false;
        System.exit(0);
    }

    @FXML private void btnOpenHasher() {
        openToolWindow("../tools/hasher/hasher.fxml","Hasher");
    }

    @FXML private void btnOpenDistinguisher() {
        openToolWindow("../tools/distinguisher/distinguisher.fxml","Hash Distinguisher");
    }

    @FXML private void btnOpenRainbowTableGenerator() {
        openToolWindow("../tools/rainbowTableGenerator/rainbowTableGenerator.fxml","Rainbow-Table Generator");
    }

    @FXML private void btnOpenBenchmark() {
        openToolWindow("../tools/benchmark/benchmark.fxml","Benchmark");
    }

    private void openToolWindow(String fxml, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));

            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();

            stage.setScene(scene);
            stage.setTitle(title);
            stage.setResizable(false);
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    ///////////////////////////////////////////LOAD HASHES TAB//////////////////////////////////////////////////////////

    @FXML private Label lHashfileSize;
    @FXML private ListView<LoadedHash> lvLoadedHashes = new ListView<>();
    @FXML private TextField tfAddHash;

    // stlacenie tlacitka 'select hash file'
    @FXML private void btnSelectHashFile() {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);

        if(file == null) return;

        Thread thread = new Thread(() -> {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));

                String hash;
                while ((hash = br.readLine()) != null) {
                    if(!isFullEdition) if(project.getLoadedHashes().size()>=10) {
                        Platform.runLater(() -> {
                            String title = "Using community version";
                            String text = "You are using community version, you can add maximum of 10 hashes";
                            Tools.openInfoWindow(title, text);
                        });
                        break;
                    }
                    project.addLoadedHash(new LoadedHash(hash));
                }

                Platform.runLater(this::updateLoadedHashesListView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    // stlacenie tlacitka 'clear hashes'
    @FXML private void btnClearHashes() {
        project.setLoadedHashes(new HashSet<>());
        updateLoadedHashesListView();
    }

    // stlacenie tlacitka 'add hashes'
    @FXML private void btnAddHash() {
        String s = tfAddHash.getText();
        tfAddHash.clear();

        if(s.length() == 0) {
            tfAddHash.setPromptText("Cannot insert empty value");
        }else if(s.length()%2!=0) {
            tfAddHash.setPromptText("Hash must be even number");
        }else if(!isFullEdition) {
            if(project.getLoadedHashes().size()>=10) {
                Platform.runLater(() -> {
                    String title = "Using community version";
                    String text = "You are using community version, you can add maximum of 10 hashes";
                    Tools.openInfoWindow(title, text);
                });
                tfAddHash.setPromptText("Community version supports maximum of 10 hashes");
            }
        }else{
            project.addLoadedHash(new LoadedHash(s));
            updateLoadedHashesListView();
        }
    }

    // function called if we want to update list view
    private void updateLoadedHashesListView(){
        lvLoadedHashes.getItems().clear();
        lvLoadedHashes.getItems().addAll(project.getLoadedHashes());

        String out = String.format("Hashes loaded: %,d", project.getLoadedHashes().size());
        lHashfileSize.setText(out);
    }

    ///////////////////////////////////////ANALYZE HASHES TAB///////////////////////////////////////////////////////////

    @FXML private Label lAnalyzedHashesSize;
    @FXML private TableView<AnalyzedHash> twAnalyzedHashes;
    @FXML private TableColumn<AnalyzedHash,String> tcAlgorithm;
    @FXML private TableColumn<AnalyzedHash,String> tcHash;

    @FXML private void btnAnalyzeLoadedHashes(){

        project.setAnalyzedHashes(new HashMap<>());
        twAnalyzedHashes.getItems().clear();

        for(LoadedHash lh: project.getLoadedHashes()){

            byte[] key = Tools.hexStringToByteArray(lh.getHexString());

            ByteArray byteArray = new ByteArray(key);

            String hexString = lh.getHexString();
            String algorithm = Tools.distinguishHash(hexString);

            AnalyzedHash ah = new AnalyzedHash(hexString,algorithm);

            project.addAnalyzedHash(byteArray,ah);
        }
        updateAnalyzedHashesTableView();
    }

    private void updateAnalyzedHashesTableView(){
        twAnalyzedHashes.getItems().clear();
        twAnalyzedHashes.getItems().addAll(project.getAnalyzedHashes().values());

        String out = String.format("Hashes analyzed: %,d", project.getAnalyzedHashes().size());
        lAnalyzedHashesSize.setText(out);
    }

    ///////////////////////////////////////////SETTINGS TAB/////////////////////////////////////////////////////////////

    @FXML private RadioButton radioButtonWordlist;
    @FXML private Button buttonSelectWordlist;
    @FXML private Label labelWordlistPath;
    @FXML private Label labelWordlistSize;

    @FXML private RadioButton radioButtonBrueforce;
    @FXML private Label labelCharset;
    @FXML private TextField textFieldCharset;
    @FXML private Label labelMinLength;
    @FXML private TextField textFieldMinLength;
    @FXML private Label labelMaxLength;
    @FXML private TextField textFieldMaxLength;

    @FXML private RadioButton radioButtonRainbowTable;
    @FXML private Button buttonSelectRainbowTable;
    @FXML private Label labelRainbowTablePath;
    @FXML private Label labelRainbowTableSize;

    private boolean isCracking = false;
    private int crackType = 0;

    private long counter = 0;
    private byte threadsCount = 4;

    //wordlist cracking variables
    private File wordlist = null;
    private long sizeWordlist = 0;

    //bruteforce cracking variables
    private long sizeBruteforcer;
    private char[] charset;
    private int minLength;
    private int maxLength;

    //rainbow table cracking variables
    private File rainbowTable = null;
    private long sizeRainbowtable = 0;

    @FXML private void radioButtonChange(){

        boolean wordlist = radioButtonWordlist.isSelected();
        boolean bruteforce = radioButtonBrueforce.isSelected();
        boolean rainbowtable = radioButtonRainbowTable.isSelected();

        if(wordlist){
            crackType = 0;
        }else if(bruteforce){
            crackType = 1;
        }else if(rainbowtable){
            crackType = 2;
        }

        buttonSelectWordlist.setDisable(!wordlist);
        labelWordlistPath.setDisable(!wordlist);
        labelWordlistSize.setDisable(!wordlist);

        labelCharset.setDisable(!bruteforce);
        textFieldCharset.setDisable(!bruteforce);
        labelMinLength.setDisable(!bruteforce);
        textFieldMaxLength.setDisable(!bruteforce);
        labelMaxLength.setDisable(!bruteforce);
        textFieldMinLength.setDisable(!bruteforce);

        buttonSelectRainbowTable.setDisable(!rainbowtable);
        labelRainbowTablePath.setDisable(!rainbowtable);
        labelRainbowTableSize.setDisable(!rainbowtable);
    }

    @FXML private void selectWordlist() {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);

        if(file == null) return;

        sizeWordlist = Tools.countLines(file);

        wordlist = file;

        labelWordlistPath.setText(file.getAbsolutePath());

        String out = String.format("Size: %,d", sizeWordlist);
        labelWordlistSize.setText(out);
    }

    @FXML private void chooseRainbowTable() {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);

        if(file == null) return;

        sizeRainbowtable = Tools.countLines(file);

        rainbowTable = file;

        labelRainbowTablePath.setText(file.getAbsolutePath());
        String out = String.format("Size: %,d", sizeRainbowtable);
        labelRainbowTableSize.setText(out);
    }

    /////////////////////////////////////////////RESULTS TAB////////////////////////////////////////////////////////////

    @FXML private Tab tabLoadHashes;
    @FXML private Tab tabAnalyzeHashes;
    @FXML private Tab tabSettings;
    @FXML private Button btnStartCracking;
    @FXML private Button btnStopCracking;
    @FXML private Label labelProgress;
    @FXML private ChoiceBox<String> cbAttackAlgorithm;
    @FXML private ProgressBar progressBar;

    @FXML private TableView<CrackedHash> tvCrackedHashes;
    @FXML private TableColumn<CrackedHash, String> tcCrackedPassword;
    @FXML private TableColumn<CrackedHash, String> tcCrackedHash;

    @FXML private void startCracking() {
        btnStopCracking.setDisable(false);

        btnStartCracking.setDisable(true);

        tabAnalyzeHashes.setDisable(true);
        tabLoadHashes.setDisable(true);
        tabSettings.setDisable(true);
        cbAttackAlgorithm.setDisable(true);

        isCracking = true;

        tvCrackedHashes.getItems().clear();

        switch (crackType){
            case 0:
                startCrackWithWordlist();
                break;
            case 1:
                startCrackWithBruteforce();
                break;
            case 2:
                startCrackWithRainbowTable();
                break;
        }
    }

    @FXML private void stopCracking() {
        btnStopCracking.setDisable(true);

        btnStartCracking.setDisable(false);

        tabAnalyzeHashes.setDisable(false);
        tabLoadHashes.setDisable(false);
        tabSettings.setDisable(false);
        cbAttackAlgorithm.setDisable(false);

        isCracking = false;
    }

    private void updateProgressBar(long current, long max) {

        String out = String.format("%,d / %,d", current,max);
        labelProgress.setText(out);

        float progress = (float) current / max;
        progressBar.setProgress( progress );
    }

    private void updateCrackedHashesTableView() {

        try {
            tvCrackedHashes.getItems().clear();
            tvCrackedHashes.getItems().addAll(project.getCrackedHashes());
        }catch (ConcurrentModificationException e){
            //ignorujeme :]
        }
    }

    ///////////////////////////////////////////////////WORDLIST/////////////////////////////////////////////////////////

    private void startCrackWithWordlist(){

        final String attackAlgorithm = cbAttackAlgorithm.getValue();

        counter = 0;
        Thread[] threads = new Thread[threadsCount];

        for (int i = 0; i < threads.length; i++) {

            int finalI = i;

            threads[i] = new Thread(() -> {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(wordlist));

                    for (int j = 0; j < finalI *(sizeWordlist/4); j++) br.readLine();

                    String password;
                    while ((password = br.readLine()) != null)
                    {
                        if(counter>sizeWordlist) stopCracking();
                        if(!isCracking) break;

                        ByteArray hashedBytes = new ByteArray(Tools.hash(password, attackAlgorithm));

                        if(project.getAnalyzedHashes().containsKey(hashedBytes)) {
                            String hexString = project.getAnalyzedHashes().get(hashedBytes).getHexString();
                            CrackedHash ch = new CrackedHash(hexString, password);
                            project.addCrackedHash(ch);
                        }

                        counter++;
                        if(counter%10000==0)
                            Platform.runLater(() -> {
                                updateProgressBar(counter, sizeWordlist);
                                updateCrackedHashesTableView();
                            });
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            });

            threads[i].start();
        }
    }

    //////////////////////////////////////////////////BRUTEFORCE////////////////////////////////////////////////////////

    private void startCrackWithBruteforce(){

        /*
        charset = textFieldCharset.getText().toCharArray();
        minLength = Integer.parseInt(textFieldMinLength.getText());
        maxLength = Integer.parseInt(textFieldMaxLength.getText());

        counter = 0;

        sizeBruteforcer = (int) Math.pow(charset.length, maxLength);

        for (int length = minLength;  length <= maxLength; length++) {
            generate("", 0, length);
        }
        */

    /*
    private void generate(String str, int pos, int length) {
        if (length == 0) {
            System.out.println(str);
            counter++;
            System.out.println(counter);
            if(counter%1000==0) updateProgressBar(counter, sizeBruteforcer);
        } else {
            if (pos != 0) {
                pos = 0;
            }
            for (int i = pos; i < charset.length; i++) {
                generate(str + charset[i], i, length - 1);
            }
        }
    }
    */

    }

    //////////////////////////////////////////////////RAINBOW TABLE/////////////////////////////////////////////////////

    private void startCrackWithRainbowTable(){
        /*
        counter = 0;
        Thread[] threads = new Thread[threadsCount];

        for (int i = 0; i < threads.length; i++) {

            int finalI = i;

            threads[i] = new Thread(() -> {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(rainbowTable));

                    for (int j = 0; j < finalI * (sizeRainbowtable / 4); j++) br.readLine();

                    String line;
                    while ((line = br.readLine()) != null) {
                        if (counter > sizeRainbowtable) stopCracking();
                        if (!isCracking) break;

                        String[] array = line.trim().split(":");

                        if (project.getAnalyzedHashes().containsKey(array[0])) {
                            CrackedHash ch = new CrackedHash(array[0], array[1]);

                            project.addCrackedHash(ch);

                            Platform.runLater(() -> tvCrackedHashes.getItems().add(ch));
                        }

                        counter++;
                        if (counter % 1000 == 0)
                            Platform.runLater(() -> updateProgressBar(counter,sizeRainbowtable));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            threads[i].start();
        }
        */
    }

    ///////////////////////////////////////////////END OF RAINBOW RABLE/////////////////////////////////////////////////

}
