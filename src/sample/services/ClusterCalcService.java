package sample.services;

import java.io.File;
import java.util.List;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import sample.other.clustering.ClusterEngine;
import sample.other.clustering.enums.ClusteringAlgorithm;
import sample.other.database.enums.DataTag;

/**
 * Created by andreasschw on 18.08.2017.
 *
 * Cluster Calculation
 */
public class ClusterCalcService extends Service<Void>{

  private ClusteringAlgorithm clusteringAlgorithm = ClusteringAlgorithm.UNKNOWN;

  private DataTag dataTag;
  private int linearScaleRange;
  private List<File> files;

  //SNN
  private int k, minpts;
  private double eps;

  //K_MEANS
  private int clusterCount;

  public ClusterCalcService(List<File> inputFiles, int k, double eps, int minpts, int linearScaleRange, DataTag dataTag){
    this.clusteringAlgorithm = ClusteringAlgorithm.SNN;

    this.dataTag = dataTag;
    this.linearScaleRange = linearScaleRange;
    this.files = inputFiles;
    this.k = k;
    this.eps = eps;
    this.minpts = minpts;
  }

  public ClusterCalcService(List<File> inputFiles, int clusterCount, int linearScaleRange, DataTag dataTag){
    this.clusteringAlgorithm = ClusteringAlgorithm.K_MEANS;

    this.dataTag = dataTag;
    this.linearScaleRange = linearScaleRange;
    this.files = inputFiles;
    this.clusterCount = clusterCount;
    this.k = k;
    this.eps = eps;
    this.minpts = minpts;
  }

  @Override
  protected Task<Void> createTask() {
    Task<Void> task = new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        //Choose Algorithm
        if(clusteringAlgorithm == ClusteringAlgorithm.SNN){
          //SNN berechnen
          try {
            ClusterEngine.calculateSNN(files, k, eps, minpts, linearScaleRange, dataTag);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        else if(clusteringAlgorithm == ClusteringAlgorithm.K_MEANS){
          //K-Means berechnen
          try {
            ClusterEngine.calculateKMeans(files, clusterCount, linearScaleRange, dataTag);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        else{
          System.out.println("no Algorithm choosed, nothing calculated!");
        }
        return null;
      }
    };
    return task;
  }

}
