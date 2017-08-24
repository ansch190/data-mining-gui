package sample.other.clustering;

import java.util.ArrayList;
import java.util.List;

import sample.other.clustering.enums.ClusteringAlgorithm;
import sample.other.database.enums.DataTag;

/**
 * Created by andreasschw on 07.08.2017.
 *
 * Result of Clustering
 */
public class ClusterResult {

  private ClusteringAlgorithm clusterAlgo = ClusteringAlgorithm.UNKNOWN;

  private List<Double> clusterAlgoParameter = null;

  private DataTag dataTag = null;

  private List<List<List<Double>>> data = null;

  public ClusterResult(ClusteringAlgorithm clusterAlgo, List<Double> clusterAlgoParameter, DataTag dataTag){
    this.clusterAlgo = clusterAlgo;
    this.clusterAlgoParameter = clusterAlgoParameter;
    this.dataTag = dataTag;
    init();
  }

  private void init(){
    data = new ArrayList<>();
  }

  //### Algorithm ###

  public ClusteringAlgorithm getClusterAlgorithm(){
    return this.clusterAlgo;
  }

  public List<Double> getClusterAlgorithmParameter(){
    return clusterAlgoParameter;
  }

  public DataTag getDataTag(){
    return this.dataTag;
  }

  //### CLUSTER ###

  public void addCluster(){
    data.add(new ArrayList<>());
  }

  public void addCluster(List<List<Double>> inputData){
    data.add(inputData);
  }

  public List<List<Double>> getCluster(int clusterIndex){
    return data.get(clusterIndex);
  }

  public void setCluster(int clusterIndex, List<List<Double>> inputData){
    data.set(clusterIndex, inputData);
  }

  public int getClusterCount(){
    return data.size();
  }

  public int getClusterSize(int clusterIndex){
    return data.get(clusterIndex).size();
  }

  //### DATA-SET ###

  //CellID;XPos;YPos;File0;File1;... other Files

  public void addDataSet(int clusterIndex){
    data.get(clusterIndex).add(new ArrayList<>());
  }

  public void addDataSet(int clusterIndex, List<Double> dataSet){
    data.get(clusterIndex).add(dataSet);
  }

  public List<Double> getDataSet(int clusterIndex, int dataSetIndex){
    return data.get(clusterIndex).get(dataSetIndex);
  }

  public void addDataSet(int clusterIndex, int dataSetIndex, List<Double> dataSet){
    data.get(clusterIndex).set(dataSetIndex, dataSet);
  }

  public int getDataSetSize(int clusterIndex, int dataSetIndex){
    return data.get(clusterIndex).get(dataSetIndex).size();
  }

  //### PARAMETER ###

  public int getClusterID(int clusterIndex, int dataSetIndex){
    return clusterIndex;
  }

  public int getCellID(int clusterIndex, int dataSetIndex){
    return getDataSet(clusterIndex, dataSetIndex).get(0).intValue();
  }

  public int getXPos(int clusterIndex, int dataSetIndex){
    return getDataSet(clusterIndex, dataSetIndex).get(1).intValue();
  }

  public int getYPos(int clusterIndex, int dataSetIndex){
    return getDataSet(clusterIndex, dataSetIndex).get(2).intValue();
  }

}
