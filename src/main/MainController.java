package main;

import entity.AnalyzedHash;
import entity.CrackedHash;
import entity.LoadedHash;
import javafx.collections.ObservableList;
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

    private Set<LoadedHash> loadedHashes = new HashSet<>();
    private Map<String,AnalyzedHash> analyzedHashes = new HashMap<>();
    private Set<CrackedHash> crackedHashes = new HashSet<>();

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
    // menu buttons on top of window

    @FXML private void btnSaveProject(){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("lastproject.cerb"));

            for (LoadedHash loadedHash : loadedHashes) {
                writer.write(loadedHash.getHexString()+"\n");
            }

            writer.write("---END OF LOADED HASHES---\n");
            writer.write("--------------------------\n");

            for (AnalyzedHash analyzedHash : analyzedHashes.values()) {
                writer.write(analyzedHash.getHexString()+"\n"+analyzedHash.getAlgorithm()+"\n");
            }

            writer.write("---END OF ANALYZED HASHES---\n");
            writer.write("----------------------------\n");

            for (CrackedHash crackedHash : crackedHashes) {
                writer.write(crackedHash.getHexString()+"\n"+crackedHash.getPassword()+"\n");
            }

            writer.write("---END OF CRACKED HASHES---\n");
            writer.write("---------------------------\n");

            writer.close();
            System.out.println("Saved");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML private void btnOpenProject(){
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);

        if(file == null) return;

        Thread thread = new Thread(() -> {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));

                int stage = 0;
                boolean check = true;
                String hash;
                while (check) {

                    switch (stage){
                        case 0:
                            hash = br.readLine();
                            if (hash.equals("---END OF LOADED HASHES---")){
                                hash = br.readLine();
                                stage++;
                                continue;
                            }
                            loadedHashes.add(new LoadedHash(hash));
                            break;
                        case 1:
                            hash = br.readLine();
                            String algorithm = br.readLine();
                            if (hash.equals("---END OF ANALYZED HASHES---")){
                                stage++;
                                continue;
                            }
                            analyzedHashes.put(hash, new AnalyzedHash(hash,algorithm));
                            break;
                        case 2:
                            hash = br.readLine();
                            String password = br.readLine();
                            if (hash.equals("---END OF CRACKED HASHES---")){
                                stage++;
                                continue;
                            }
                            crackedHashes.add(new CrackedHash(hash,password));
                            break;
                        case 3:
                            check = false;
                            break;
                    }
                }

                br.close();

                Platform.runLater(() -> {
                    updateAnalyzedHashesTableView();
                    updateLoadedHashesListView();
                    updateCrackedHashesTableView();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();

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
    // first tab

    @FXML private Label lHashfileSize;
    @FXML private ListView lvLoadedHashes = new ListView();
    @FXML private TextField tfAddHash;

    // stlacenie tlacitka 'select hash file'
    @FXML private void selectHashFile() {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);

        if(file == null) return;

        Thread thread = new Thread(() -> {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));

                String hash;
                while ((hash = br.readLine()) != null)
                    loadedHashes.add(new LoadedHash(hash));

                Platform.runLater(this::updateLoadedHashesListView);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    // stlacenie tlacitka 'clear hashes'
    @FXML private void clearHashes() {
        loadedHashes = new HashSet<>();
        updateLoadedHashesListView();
    }

    // stlacenie tlacitka 'add hashes'
    @FXML private void addHash() {
        String s = tfAddHash.getText();
        tfAddHash.clear();

        if(s.length() == 0) {
            tfAddHash.setPromptText("cannot insert empty value");
        }else if(s.length()%2!=0) {
            tfAddHash.setPromptText("hash must be even number");
        }else{
            loadedHashes.add(new LoadedHash(s));
            updateLoadedHashesListView();
        }
    }

    // function called if we want to update list view
    public void updateLoadedHashesListView(){
        lvLoadedHashes.getItems().clear();
        lvLoadedHashes.getItems().addAll(loadedHashes);
        lHashfileSize.setText("Hashes loaded: " + loadedHashes.size());
    }

    ///////////////////////////////////////ANALYZE HASHES TAB///////////////////////////////////////////////////////////

    @FXML private Label lAnalyzedHashesSize;
    @FXML private TableView<AnalyzedHash> twAnalyzedHashes;
    @FXML private TableColumn<AnalyzedHash,String> tcAlgorithm;
    @FXML private TableColumn<AnalyzedHash,String> tcHash;

    private static long counterOfAnalyzedHashes = 0;

    @FXML private  void analyzeLoadedHashes(){

        counterOfAnalyzedHashes = 0;

        analyzedHashes.clear();
        twAnalyzedHashes.getItems().clear();

        for(LoadedHash lh: loadedHashes){

            String hexString = lh.getHexString();
            String algorithm = Tools.distinguishHash(hexString);

            AnalyzedHash ah = new AnalyzedHash(hexString,algorithm);

            analyzedHashes.put(hexString,ah);

            counterOfAnalyzedHashes++;
        }
        updateAnalyzedHashesTableView();
    }

    public void updateAnalyzedHashesTableView(){
        twAnalyzedHashes.getItems().clear();
        Set<AnalyzedHash> tmp = new HashSet<>();
        for (AnalyzedHash analyzedHash : analyzedHashes.values()) {
            tmp.add(analyzedHash);
        }
        twAnalyzedHashes.getItems().addAll(tmp);
        lAnalyzedHashesSize.setText("Analyzed hashes: "+counterOfAnalyzedHashes);
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
        labelWordlistSize.setText("Size: "+sizeWordlist);
    }

    @FXML private void chooseRainbowTable() {
        FileChooser fc = new FileChooser();
        File file = fc.showOpenDialog(null);

        if(file == null) return;

        sizeRainbowtable = Tools.countLines(file);

        rainbowTable = file;

        labelRainbowTablePath.setText(file.getAbsolutePath());
        labelRainbowTableSize.setText("Size: "+sizeRainbowtable);
    }

    /////////////////////////////////////////////RESULTS TAB////////////////////////////////////////////////////////////

    @FXML private Button btnStartCracking;
    @FXML private Button btnStopCracking;
    @FXML private Label labelProgress;
    @FXML private ChoiceBox cbAttackAlgorithm;
    @FXML private ProgressBar progressBar;

    @FXML private TableView<CrackedHash> tvCrackedHashes;
    @FXML private TableColumn<CrackedHash, String> tcCrackedPassword;
    @FXML private TableColumn<CrackedHash, String> tcCrackedHash;

    @FXML private void startCracking() {
        btnStartCracking.setDisable(true);
        btnStopCracking.setDisable(false);
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
        btnStartCracking.setDisable(false);
        btnStopCracking.setDisable(true);
        isCracking = false;
    }

    public void updateProgressBar(long current, long max) {
        labelProgress.setText(current + " / " + max);
        float progress = (float) current / max;
        progressBar.setProgress( progress );
    }

    public void updateCrackedHashesTableView() {

        tvCrackedHashes.getItems().clear();
        tvCrackedHashes.getItems().addAll(crackedHashes);
    }

    ///////////////////////////////////////////////////WORDLIST/////////////////////////////////////////////////////////

    public void startCrackWithWordlist(){

        final String attackAlgorithm = cbAttackAlgorithm.getValue().toString();

        counter = 0;
        Thread threads[] = new Thread[threadsCount];

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

                        String hexString = Tools.hash(password, attackAlgorithm);

                        if(analyzedHashes.containsKey(hexString))
                            crackedHashes.add(new CrackedHash(hexString,password));

                        counter++;
                        if(counter%1000==0) Platform.runLater(() -> {
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

    public void startCrackWithBruteforce(){

        charset = textFieldCharset.getText().toCharArray();
        minLength = Integer.parseInt(textFieldMinLength.getText());
        maxLength = Integer.parseInt(textFieldMaxLength.getText());

        counter = 0;

        sizeBruteforcer = (int) Math.pow(charset.length, maxLength);

        for (int length = minLength;  length <= maxLength; length++) {
            generate("", 0, length);
        }
    }

    public void generate(String str, int pos, int length) {
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

    //////////////////////////////////////////////////RAINBOW TABLE/////////////////////////////////////////////////////

    public void startCrackWithRainbowTable(){

        counter = 0;
        Thread threads[] = new Thread[threadsCount];

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

                        if (analyzedHashes.containsKey(array[0])) {
                            CrackedHash ch = new CrackedHash(array[0], array[1]);

                            crackedHashes.add(ch);

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
    }
}
