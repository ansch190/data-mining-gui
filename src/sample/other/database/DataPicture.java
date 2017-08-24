package sample.other.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sample.other.database.enums.DataTag;

/**
 * Created by andreasschw on 21.07.2017.
 *
 * Data from a Picture
 */
public class DataPicture {

  private List<DataTag> header = null;

  private List<Map<DataTag, String>> dataLines = null;

  private String splitChar = ";";

  public DataPicture(String splitChar){
    this.splitChar = splitChar;
    this.header = new ArrayList<>();
    this.dataLines = new ArrayList<>();
  }

  public DataPicture(File file, String splitChar){
    this.splitChar = splitChar;
    parse(file);
  }

  public DataPicture(String header, String splitChar){
    this.splitChar = splitChar;
    init(header);
  }

  private void init(String header){
    createHeader(header);
    dataLines = new ArrayList<>();
  }

  private void createHeader(String headerString){
    header = new ArrayList<>();
    String[] tags = headerString.split(splitChar);
    for(String s : tags){
      DataTag dataTag = DataTag.getTag(s);
      if(dataTag != null){
        header.add(dataTag);
      }
    }
  }

  //### HEADER ###

  public void setHeader(List<DataTag> header){
    this.header = header;
  }

  public List<DataTag> getHeader(){
    return this.header;
  }

  public int getHeaderSize(){
    return header.size();
  }

  //### DATA ###

  public void addData(String line){
    String[] dataParts = line.split(splitChar);
    Map<DataTag, String> map = new HashMap<>();
    for(int i=0; i<dataParts.length; i++){
      map.put(header.get(i), dataParts[i]);
    }
    dataLines.add(map);
  }

  public String getData(int dataRow, DataTag dataTag){
    return dataLines.get(dataRow).get(dataTag);
  }

  public void setData(int dataRow, DataTag dataTag, String data){
    Map<DataTag, String> map = dataLines.get(dataRow);
    map.put(dataTag, data);
    dataLines.set(dataRow, map);
  }

  public void setData(int dataRow, DataTag dataTag, double data){
    setData(dataRow, dataTag, String.valueOf(data));
  }

  public int getDataSize(){
    return dataLines.size();
  }

  //### OTHER ###

  public void saveToFile(File file){
    saveToFile(file, splitChar);
  }

  //Save to CSV-File
  public void saveToFile(File file, String splitChar){

    try{
      BufferedWriter bw = new BufferedWriter(new FileWriter(file));

      String line = "";

      //Write Header
      for(DataTag t : header){
        line += t.getString() + splitChar;
      }
      bw.write(line + "\n");

      //Write Data
      for(Map<DataTag, String> map : dataLines){
        line = "";
        for(DataTag t : header){
          line += map.get(t) + splitChar;
        }
        bw.write(line + "\n");
      }

      bw.close();
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

  private void parse(File file){
    dataLines = new ArrayList<>();
    try{
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line;

      while((line = br.readLine())!= null){

        //Select Lines
        String[] tags = line.split(";");
        if(tags[8].equals(DataTag.OBJECT_ID.getString())){
          //System.out.println("Header saved! - ObjectID found!");
          createHeader(line);
        }
        else{
          addData(line);
        }
      }
      br.close();
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

}
