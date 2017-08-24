package sample.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
public class CsvSplitService extends Service<List<File>>{

  private File inputFile = null;
  private File outputDirectory = null;
  private String splitChar = null;

  //private List<File> result = null;

  public CsvSplitService(File inputFile, File outputDirectory, String splitChar){
    this.inputFile = inputFile;
    this.outputDirectory = outputDirectory;
    this.splitChar = splitChar;
  }

  @Override
  protected Task<List<File>> createTask() {
    Task<List<File>> task = new Task<List<File>>() {
      @Override
      protected List<File> call() throws Exception {
        return splitFile(inputFile, outputDirectory, splitChar);
      }
    };
    return task;
  }

  //### WORK ###

  // split File into Segments (ObjectID = 1-n)
  private List<File> splitFile(File inputFile, File outputDirectory, String splitChar){
    String header = "";

    List<File> files = new ArrayList<>();

    DataPicture dp = null;

    int count = 0;

    try{
      BufferedReader br = new BufferedReader(new FileReader(inputFile));
      String line;
      String[] tags;

      while((line = br.readLine())!= null){

        //Check Header
        if(!header.isEmpty()){
          //Check Invalid Input
          if(line.contains("bleach") || line.contains("Average") || line.contains("NaN")){
            continue;
          }
          if(!line.contains("Other Tissue;Cell")){
            continue;
          }
          //Select Data
          tags = line.split(splitChar);
          if (tags[8].equals("1")) {
            if (dp != null) {
              String filePath = outputDirectory.getPath() + "\\" + dp.getData(0, DataTag.MICS_POSITION) + "_" + dp.getData(0,DataTag.MARKER_ID) + "_" + dp.getData(0,DataTag.FLUOROCHROME) + ".csv";
              File saveFile = new File(filePath);
              //Prüfen ob Datei schon existiert
              count = 0;
              while(saveFile.exists()){
                saveFile = new File(filePath.replace(".csv","#" + count + ".csv"));
                count++;
              }
              dp.saveToFile(saveFile);
              files.add(saveFile);
            }
            dp = new DataPicture(header, splitChar);
          }
          dp.addData(line);
        }
        else{
          //Extract Header
          tags = line.split(splitChar);
          if(tags[0].equals(DataTag.FILENAME.getString())){
            //System.out.println("Header found!");
            header = line;
          }
        }

      }

      br.close();
    }
    catch(Exception e){
      e.printStackTrace();
    }

    //System.out.println("Files: " + files.size());

    return files;
  }

}
