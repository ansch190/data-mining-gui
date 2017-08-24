package sample.other.csv;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import sample.other.database.DataPicture;
import sample.other.database.enums.DataTag;
import sample.other.scaling.ScalingEngine;

/**
 * Created by andreasschw on 18.08.2017.
 *
 * Working with CSV-Files
 */
public class CsvEngine {

  //recreate CSV-File from splitted Files
  public static File recreateSplittedCSVFile(DataTag dataTag, List<File> files, File outputDirectory, String readSplitChar, boolean coords, int linearScaleRange){
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
      dp = ScalingEngine.scaleData(dp, dataTag, linearScaleRange);

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

}
