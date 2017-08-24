package sample.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import sample.other.database.DataPicture;
import sample.other.database.enums.DataTag;

/**
 * Created by andreasschw on 03.08.2017.
 *
 * JavaFX Service Thread
 */
public class CsvCombineService extends Service<List<File>>{

  private DataTag dataTag = null;
  private List<File> splittedFilesSelected = null;
  private File tmpFolder = null;
  private String readSplitChar = ";";

  private int linearScaleRange = -1;

  private File result = null;

  public CsvCombineService(DataTag dataTag, List<File> splittedFilesSelected, File tmpFolder, String readSplitChar, int linearScaleRange){
    this.dataTag = dataTag;
    this.splittedFilesSelected = splittedFilesSelected;
    this.tmpFolder = tmpFolder;
    this.readSplitChar = readSplitChar;

    this.linearScaleRange = linearScaleRange;
  }

  @Override
  protected Task<List<File>> createTask() {
    Task<List<File>> task = new Task<List<File>>() {
      @Override
      protected List<File> call() throws Exception {
        List<File> files = new ArrayList<>();
        File f0 = recreateSplittedCSVFile(dataTag ,splittedFilesSelected, tmpFolder,readSplitChar, false);
        File f1 = recreateSplittedCSVFile(dataTag ,splittedFilesSelected, tmpFolder,readSplitChar, true);
        files.add(f0);
        files.add(f1);
        return files;
      }
    };
    return task;
  }

  //### WORK ###

  //recreate CSV-File from splitted Files
  private File recreateSplittedCSVFile(DataTag dataTag, List<File> files, File outputDirectory, String readSplitChar, boolean coords){
    String saveSplitChar = ",";
    File saveFile = null;

    //create new Header
    String header = "";
    for(File f : files){
      header += f.getName().replaceAll(".csv", "") + saveSplitChar;
    }

    List<String> results = null;

    int count = 0;

    //Files durchgehen
    for(File f : files){
      DataPicture dp = new DataPicture(f, readSplitChar);
      dp = scaleData(dp);

      //Listen init
      if(results == null){
        results = new ArrayList<>(dp.getDataSize() + 1);
        //Add Header
        if(coords){
          results.add(DataTag.X_POSITION_IN_IMAGE.getString() + saveSplitChar + DataTag.Y_POSITION_IN_IMAGE.getString() + saveSplitChar + header);
        }
        else{
          results.add(header);
        }

        for(int i=0; i<dp.getDataSize(); i++){
          if(coords){
            results.add(dp.getData(i, DataTag.X_POSITION_IN_IMAGE) + saveSplitChar + dp.getData(i, DataTag.Y_POSITION_IN_IMAGE) + saveSplitChar);
          }
          else{
            results.add("");
          }
        }
      }

      //Datensätze durchgehen
      for(int i=0; i<dp.getDataSize(); i++){
        results.set(i+1, results.get(i+1) + dp.getData(i, dataTag) + saveSplitChar);
      }
      count++;
    }

    try{
      String savePath = "";
      if(coords){
        savePath = outputDirectory.getPath() + "\\" + dataTag.getString() + "_" + files.size() + "_coords" + ".csv";
      }
      else{
        savePath = outputDirectory.getPath() + "\\" + dataTag.getString() + "_" + files.size() + "_data" + ".csv";
      }
      saveFile = new File(savePath);
      BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile));

      for(String s : results){
        if(s.contains("NaN")){
          System.out.println("Value contains invalid value -> NaN; " + s);
          //System.exit(0);
          s = s.replaceAll("NaN", "0.0");
        }
        bw.write(s.substring(0,s.length()-1) + "\n");
      }
      bw.close();
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return saveFile;
  }

  //###############
  //### SCALING ###
  //###############

  private DataPicture scaleData(DataPicture dp){
    //Scaling
    double min = getMinimum(dp, dataTag);
    double max = getMaximum(dp, dataTag);

    for(int i=0; i<dp.getDataSize(); i++){
      double d = Double.parseDouble(dp.getData(i, dataTag));
      //Scaling Calculation
      double result = ((d - min) / (max - min)) * linearScaleRange;
      dp.setData(i, dataTag, result);
    }

    return dp;
  }

  //Find Minimum for DataTag
  private double getMinimum(DataPicture dp, DataTag dataTag){
    double min = Double.MAX_VALUE;
    for(int i=0; i<dp.getDataSize(); i++){
      double d = Double.parseDouble(dp.getData(i, dataTag));
      if(d < min){
        min = d;
      }
    }
    return min;
  }

  //Find Maximum for DataTag
  private double getMaximum(DataPicture dp, DataTag dataTag){
    double max = Double.MIN_VALUE;
    for(int i=0; i<dp.getDataSize(); i++){
      double d = Double.parseDouble(dp.getData(i, dataTag));
      if(d > max){
        max = d;
      }
    }
    return max;
  }

}
