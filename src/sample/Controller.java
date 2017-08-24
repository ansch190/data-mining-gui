package sample;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import sample.other.clustering.enums.ClusteringAlgorithm;
import sample.other.csv.CsvEngine;
import sample.other.database.enums.DataTag;
import sample.other.plotting.PlotEngine;
import sample.services.ClusterCalcService;
import sample.services.CsvSplitService;

public class Controller {

  //####################
  //### GUI-CONTROLS ###
  //####################
  @FXML private VBox rootbox;
  @FXML private Button button0;
  @FXML private Button button1;
  @FXML private Button button2;
  @FXML private Button button3;
  @FXML private TextField textfield0;
  @FXML private TextField textfield1;
  @FXML private TextField textfield2;
  @FXML private TextField textfield3;
  @FXML private TextField textfield4;
  @FXML private TextField textfield5;
  @FXML private TextField textfield6;
  @FXML private ProgressBar progress0;
  @FXML private ChoiceBox<String> choicebox0;
  @FXML private ChoiceBox<String> choicebox1;
  @FXML private ListView<File> listview0;
  @FXML private TabPane tabpane0;
  @FXML private Tab tab0;
  @FXML private Tab tab1;

  //Global Variables
  private File csvFile = null;
  private File tmpFolder = null;
  private String selectedFeature = "";
  private String selectedSize = "";
  private List<File> splittedFiles = null;
  private List<File> splittedFilesSelected = null;
  //SNN Parameter
  private int k = 20;
  private double eps = 11.0;
  private int minpts = 8;
  //K-Means Parameter
  private int clusterCount = 5;
  //Linear Scaling Parameter
  private int linearScaleRange = 100;

  //Threading Parameter
  private ExecutorService ex = null;

  @FXML
  //initialize all JavaFXElements
  public void initialize(){
    //System.out.println("FX -> initialize!");
    initChoiceBox0();
    initChoiceBox1();
    initListView();
    initTextField0();
    initTextField1();
    initTextField2();
    initTextField3();
    initTextField4();
    initTextField5();
    initTextField6();
    initTabs();
    initThreading();
  }

  //initialize Threading
  private void initThreading(){
    ex = Executors.newCachedThreadPool();
  }

  //initialize TextField0
  private void initTextField0(){
    textfield0.textProperty().addListener((observable, oldValue, newValue)
            -> {
      if(tmpFolder != null){
        clearFolder(tmpFolder);
        Platform.runLater(this::splitFile);  //bringt nix splitFile(); reicht auch.
      }
    });
  }

  //initialize TextField1
  private void initTextField1(){
    textfield1.textProperty().addListener((observable, oldValue, newValue)
            -> {
      if(csvFile != null){
        if(!oldValue.isEmpty()){
          clearFolder(new File(oldValue));
        }
        clearFolder(tmpFolder);

        Platform.runLater(this::splitFile);  //bringt nix splitFile(); reicht auch.
      }
    });
  }

  //initialize TextField2
  private void initTextField2(){
    textfield2.setText(String.valueOf(k));
    textfield2.textProperty().addListener((observable, oldValue, newValue)
            -> {
      //Check Value
      if(isInteger(newValue)){
        //System.out.println("K: " + k);
        k = Integer.parseInt(newValue);
        //System.out.println("K2: " + k);
        textfield2.setText(String.valueOf(k));
      }
      else{
        textfield2.setText(String.valueOf(k));
      }
    });
  }

  //initialize TextField3
  private void initTextField3(){
    textfield3.setText(String.valueOf(eps));
    textfield3.textProperty().addListener((observable, oldValue, newValue)
            -> {
      //Check Value
      if(isDouble(newValue)){
        eps = Double.parseDouble(newValue);
        //textfield3.setText(String.valueOf(eps));
        //System.out.println("Eps2: " + eps);
      }
      else{
        textfield3.setText(String.valueOf(eps));
      }
    });
  }

  //initialize TextField4
  private void initTextField4(){
    textfield4.setText(String.valueOf(minpts));
    textfield4.textProperty().addListener((observable, oldValue, newValue)
            -> {
      //Check Value
      if(isInteger(newValue)){
        minpts = Integer.parseInt(newValue);
        textfield4.setText(String.valueOf(minpts));
        //System.out.println("MinPts2: " + minpts);
      }
      else{
        textfield4.setText(String.valueOf(minpts));
      }
    });
  }

  //initialize TextField5
  private void initTextField5(){
    textfield5.setText(String.valueOf(clusterCount));
    textfield5.textProperty().addListener((observable, oldValue, newValue)
            -> {
      //Check Value
      if(isInteger(newValue)){
        int i = Integer.parseInt(newValue);
        if(i > 0){
          clusterCount = i;
        }
        textfield5.setText(String.valueOf(clusterCount));
        //System.out.println("ClusterCount: " + clusterCount);
      }
      else{
        textfield5.setText(String.valueOf(clusterCount));
      }
    });
  }

  //initialize TextField6
  private void initTextField6(){
    textfield6.setText(String.valueOf(linearScaleRange));
    textfield6.textProperty().addListener((observable, oldValue, newValue)
            -> {
      //Check Value
      if(isInteger(newValue)){
        int i = Integer.parseInt(newValue);
        if(i > 0){
          linearScaleRange = i;
        }
        textfield6.setText(String.valueOf(linearScaleRange));
        //System.out.println("Linear ScaleRange: " + linearScaleRange);
      }
      else{
        textfield6.setText(String.valueOf(linearScaleRange));
      }
    });
  }

  private boolean isInteger(String s) {
    try {
      Integer.parseInt(s);
    } catch(NumberFormatException e) {
      return false;
    } catch(NullPointerException e) {
      return false;
    }
    // only got here if we didn't return false
    return true;
  }

  private boolean isDouble(String s) {
    try {
      Double.parseDouble(s);
    } catch(NumberFormatException e) {
      return false;
    } catch(NullPointerException e) {
      return false;
    }
    // only got here if we didn't return false
    return true;
  }

  //initialize ListView
  private void initListView(){
    listview0.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
  }

  //fill ChoiceBox0 with Items
  private void initChoiceBox0(){
    choicebox0.getSelectionModel()
            .selectedItemProperty()
            .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue)
                    -> selectedFeature = choicebox0.getValue());

    ObservableList<String> items = FXCollections.observableArrayList(
            "Mean",
            "MeanBGCorrected",
            "Median",
            "MedianBGCorrected"
    );
    choicebox0.setItems(items);
    choicebox0.setValue(items.get(0));
  }

  //fill ChoiceBox1 with Items
  private void initChoiceBox1(){
    choicebox1.getSelectionModel()
            .selectedItemProperty()
            .addListener( (ObservableValue<? extends String> observable, String oldValue, String newValue)
                    -> selectedSize = choicebox1.getValue());

    ObservableList<String> items = FXCollections.observableArrayList(
            "2048x2048",
            "4096x4096"
    );
    choicebox1.setItems(items);
    choicebox1.setValue(items.get(0));
  }

  private ClusteringAlgorithm clusterAlgo = ClusteringAlgorithm.UNKNOWN;

  //initialize Tabs
  private void initTabs(){
    //Tab0 selected
    tab0.setOnSelectionChanged(new EventHandler<Event>() {
      @Override
      public void handle(Event event) {
        if(tab0.isSelected()){
          clusterAlgo = ClusteringAlgorithm.SNN;
          listview0.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
      }
    });

    //Tab1 selected
    tab1.setOnSelectionChanged(new EventHandler<Event>() {
      @Override
      public void handle(Event event) {
        if(tab1.isSelected()){
          clusterAlgo = ClusteringAlgorithm.K_MEANS;
          listview0.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        }
      }
    });

    clusterAlgo = ClusteringAlgorithm.SNN;
  }

  @FXML
  //open CSV-File
  private void openCsvFile(){
    //System.out.println("FX -> openCsvFile!");

    FileChooser fc = new FileChooser();
    fc.setTitle("Please choose CSV-File with Data");
    fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("CSV-File","*.csv")
    );
    File f = fc.showOpenDialog(null);
    if(f != null){
      //System.out.println(f.getAbsolutePath());
      csvFile = f;
      textfield0.setText(f.getName());
      textfield0.setTooltip(new Tooltip(f.getAbsolutePath()));
    }
  }

  //split CSV-File
  private void splitFile(){
    //System.out.println("FX-Java -> splitFile!");

    Service<List<File>> service = new CsvSplitService(csvFile, tmpFolder, ";");

    service.setOnRunning(new EventHandler<WorkerStateEvent>() {
      @Override
      public void handle(WorkerStateEvent event) {
        //ProgressBar
        progress0.setProgress(service.getProgress());
      }
    });

    service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
      @Override
      public void handle(WorkerStateEvent event) {
        //ListView update
        ObservableList<File> fileList = FXCollections.observableList(service.getValue());

        //Save Files
        splittedFiles = new ArrayList<>(fileList);
        //create Alias List for ListView
        fileList.clear();
        for(File f : splittedFiles){
          fileList.add(new File(f.getName()));
        }

        listview0.setItems(fileList);
        listview0.getSelectionModel().clearSelection();

        //ProgressBar
        progress0.setProgress(1.0);
      }
    });

    service.start();
  }

  //clear Folder
  private void clearFolder(File folder){
    //System.out.println("Clear Folder: " + folder.getPath());
    File[] files = folder.listFiles();
    for(File f : files){
      if((f.getName().endsWith(".csv") || f.getName().endsWith(".png")) && !f.equals(csvFile)){
        f.delete();
        //System.out.println("Delete -> " + f.getPath());
      }
    }
  }

  @FXML
  //open Folder for Tmp-Files
  private void openTmpFolder(){
    //System.out.println("FX -> openTmpFolder!");

    DirectoryChooser dc = new DirectoryChooser();
    dc.setTitle("Please choose Folder for temporary Files");
    File f = dc.showDialog(null);
    if(f != null){
      //System.out.println(f.getAbsolutePath());
      tmpFolder = f;
      textfield1.setText(f.getAbsolutePath());
      textfield1.setTooltip(new Tooltip(textfield1.getText()));
    }
  }

  @FXML
  //export transparent Cluster Picture
  private void exportClusterPicture(){
    //System.out.println("FX -> exportClusterPicture!");

    int dimension;

    if(choicebox1.getValue().equals("2048x2048")){
      dimension = 2048;
    }
    else if(choicebox1.getValue().equals("4096x4096")){
      dimension = 4096;
    }
    else{
      dimension = 0;
    }

    double scaling = dimension / 2048.0;

    BufferedImage image = new BufferedImage(dimension,dimension, BufferedImage.TYPE_INT_ARGB);

    //Load File
    String fileName;

    if(splittedFilesSelected == null){
      return;
    }

    fileName = "cluster_result_" + splittedFilesSelected.size() + "_";

    if(clusterAlgo == ClusteringAlgorithm.SNN){
      fileName += "SNN_K" + k + ".0_E" + eps + "_M" + minpts + ".0" + ".csv";
    }
    else if(clusterAlgo == ClusteringAlgorithm.K_MEANS){
      fileName += "K_MEANS_C" + clusterCount + ".0" + ".csv";
    }

    //Load Cluster Colors
    List<Color> clusterColors = PlotEngine.colors;

    //Create Graphics
    Graphics2D g = image.createGraphics();
    int radius = (int) (10.0 * scaling);

    //Load File
    try{
      File f = new File(tmpFolder + "\\" + fileName);

      if(!f.exists()){
        return;
      }

      BufferedReader br = new BufferedReader(new FileReader(f));

      String line;
      String[] parts;
      String splitChar = ";";
      while((line = br.readLine()) != null){
        parts = line.split(splitChar);

        if(parts[0].equals("ClusterID")){
          continue;
        }

        int clusterID = Integer.parseInt(parts[0]);
        int coordX = (int) (Integer.parseInt(parts[2]) * scaling);
        int coordY = (int) (Integer.parseInt(parts[3]) * scaling);

        //Paint Circle;
        g.setColor(clusterColors.get(clusterID));
        g.fillOval(coordX, coordY, radius, radius);
      }
      br.close();

      //Save Dialog
      FileChooser fc = new FileChooser();
      fc.setTitle("Save Overlay-Image " + choicebox1.getValue());
      fc.setInitialDirectory(tmpFolder);
      fc.setInitialFileName(fileName.replaceAll(".csv","_" + dimension + "_overlay.png"));
      fc.getExtensionFilters().addAll(
              new FileChooser.ExtensionFilter("PNG-File","*.png")
      );
      File file = fc.showSaveDialog(null);
      if(file != null){
        System.out.println(file.getAbsolutePath());
        ImageIO.write(image, "png", file);
      }

    }
    catch(Exception e){
      e.printStackTrace();
    }

  }

  @FXML
  private void calculateCluster(){
    //System.out.println("FX -> calculateCluster!");

    ObservableList<Integer> selectedIndices = listview0.getSelectionModel().getSelectedIndices();

    if(selectedIndices.size() == 0){
      return;
    }
    else if(k <= minpts){
      k = minpts + 1;
      textfield2.setText(String.valueOf(k));
      return;
    }

    splittedFilesSelected = new ArrayList<>();
    for(int i : selectedIndices){
      splittedFilesSelected.add(splittedFiles.get(i));
    }

    //Daten neu zusammenstellen
    DataTag dataTag = DataTag.getTag(selectedFeature);

    //### CSV-COMBINE ###

    List<File> combinedFiles = new ArrayList<>();
    String readSplitChar = ";";
    File f0 = CsvEngine.recreateSplittedCSVFile(dataTag ,splittedFilesSelected, tmpFolder, readSplitChar, false, linearScaleRange);
    File f1 = CsvEngine.recreateSplittedCSVFile(dataTag ,splittedFilesSelected, tmpFolder, readSplitChar, true, linearScaleRange);
    combinedFiles.add(f0);
    combinedFiles.add(f1);

//    Service<List<File>> service0 = new CsvCombineService(dataTag, splittedFilesSelected, tmpFolder, ";", linearScaleRange);
//
//    service0.setOnRunning(new EventHandler<WorkerStateEvent>() {
//      @Override
//      public void handle(WorkerStateEvent event) {
//        //ProgressBar
//        progress0.setProgress(service0.getProgress());
//      }
//    });
//
//    service0.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
//      @Override
//      public void handle(WorkerStateEvent event) {
//        combinedFiles.addAll(service0.getValue());
//        //ProgressBar
//        progress0.setProgress(0.5);
//      }
//    });
//
//    service0.start();

    //### CALCULATION ###

    Service<Void> service1 = null;

    if(clusterAlgo == ClusteringAlgorithm.SNN){
      service1 = new ClusterCalcService(combinedFiles, k, eps, minpts, linearScaleRange, dataTag);
    }
    else if(clusterAlgo == ClusteringAlgorithm.K_MEANS){
      service1 = new ClusterCalcService(combinedFiles, clusterCount, linearScaleRange, dataTag);
    }

    Service<Void> finalService = service1;
    service1.setOnRunning(new EventHandler<WorkerStateEvent>() {
      @Override
      public void handle(WorkerStateEvent event) {
        //ProgressBar
        progress0.setProgress(finalService.getProgress());
      }
    });

    service1.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
      @Override
      public void handle(WorkerStateEvent event) {
        //ProgressBar
        progress0.setProgress(1.0);
      }
    });

    service1.start();
  }

}
