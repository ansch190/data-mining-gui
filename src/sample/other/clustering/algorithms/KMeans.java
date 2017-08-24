package sample.other.clustering.algorithms;

import java.util.Arrays;

import sample.other.clustering.algorithms.basic.ClusterAlgo;
import sample.other.clustering.enums.ClusteringAlgorithm;

/**
 * Created by andreasschw on 03.08.2017.
 *
 * K-Means Algorithm
 */
public class KMeans extends ClusterAlgo {

  private double[][] inputData2;
  private int dimension = -1;

  private double[] inputData;
  private int clusterCount = -1;
  private int dataCount = -1;

  public KMeans(double[][] inputData2, int clusterCount){
    this.algoName = ClusteringAlgorithm.K_MEANS;

    this.inputData2 = inputData2;
    this.dataCount = inputData2.length;
    this.clusterCount = clusterCount;
    this.dimension = inputData2[0].length;
  }

  public KMeans(double[] inputData, int clusterCount){
    this.algoName = ClusteringAlgorithm.K_MEANS;

    this.inputData = inputData;
    this.dataCount = inputData.length;
    this.clusterCount = clusterCount;
  }

  public int[][] calc(){
    return calculateKMeans2();
  }

  public int[][] calc(int clusterCount){
    this.clusterCount = clusterCount;
    return calc();
  }

  public int[][] calc(double[] inputData, int clusterCount){
    this.inputData = inputData;
    this.dataCount = inputData.length;
    this.clusterCount = clusterCount;
    return calc();
  }

  //###################
  //### CALCULATION ###
  //###################

  private double[][] initClusterCenter2(double[][] clusterCenter, double[][] inputData){
    for (int i=0; i<clusterCenter.length; i++){
      clusterCenter[i] = inputData[i];
    }
    return clusterCenter;
  }

//  private double[] initClusterCenter(double[] clusterCenter, double[] inputData){
//    for (int i=0; i<clusterCenter.length; i++){
//      clusterCenter[i] = inputData[i];
//    }
//    return clusterCenter;
//  }

  private int[][] initArrayInt(int[][] array, int value){
    for(int i=0; i<array.length; i++){
      for(int j=0; j<array[i].length; j++){
        array[i][j] = value;
      }
    }
    return array;
  }

  private int[][] calculateKMeans2(){
    /* Initialising arrays */
    int[][] clusterData0 = new int[clusterCount][dataCount];  //Zuordnung der Datenzeilen zu den Clustern
    int[][] clusterData1 = new int[clusterCount][dataCount];  //Zuordnung der Datenzeilen zu den Clustern
    double[][] clusterCenter = new double[clusterCount][dimension];  //Werte der Clusterzentren
    int[] clusterFill;  //Wie viele Werte jeder Cluster enthält

    int clusterID;  //ClusterID
    boolean check;  //Prüft ob Arrays gleich und Algorithmus endet

    //Init
    clusterCenter = initClusterCenter2(clusterCenter, inputData2);

    do {
      //Init
      clusterData0 = initArrayInt(clusterData0, -1);
      clusterFill = new int[clusterCount];

      //Calculate Cluster for every Element
      for (int i=0; i<dataCount; i++){
        clusterID = calcMinCluster2(clusterCenter, inputData2[i]);
        //ElementIndex to clusterData0[][]
        clusterData0[clusterID][clusterFill[clusterID]] = i;
        //ClusterFill + 1
        clusterFill[clusterID] = clusterFill[clusterID] + 1;
      }

      //Calculate new ClusterCenters
      clusterCenter = calcClusterCenter2(clusterData0, inputData2);

      //Check End of Algorithm
      check = checkArraysEqual2(clusterData0, clusterData1);

      //Copy Array for next Round
      if (!check){
        clusterData1 = clusterData0.clone();
      }
    } while (!check);

    return clusterData0;
  }

//  private int[][] calculateKMeans(){
//    /* Initialising arrays */
//    int[][] clusterData0 = new int[clusterCount][dataCount];  //Zuordnung der Datenzeilen zu den Clustern
//    int[][] clusterData1 = new int[clusterCount][dataCount];  //Zuordnung der Datenzeilen zu den Clustern
//    double[] clusterCenter = new double[clusterCount];  //Werte der Clusterzentren
//    int[] clusterFill;  //Wie viele Werte jeder Cluster enthält
//
//    int clusterID;  //ClusterID
//    boolean check;  //Prüft ob Arrays gleich und Algorithmus endet
//
//    //Init
//    clusterCenter = initClusterCenter(clusterCenter, inputData);
//
//    do {
//      //Init
//      clusterData0 = initArrayInt(clusterData0, -1);
//      clusterFill = new int[clusterCount];
//
//      //Calculate Cluster for every Element
//      for (int i=0; i<dataCount; i++){
//        clusterID = calcMinCluster(clusterCenter, inputData[i]);
//        //ElementIndex to clusterData0[][]
//        clusterData0[clusterID][clusterFill[clusterID]] = i;
//        //ClusterFill + 1
//        clusterFill[clusterID] = clusterFill[clusterID] + 1;
//      }
//
//      //Calculate new ClusterCenters
//      clusterCenter = calcClusterCenter(clusterData0, inputData);
//
//      //Check End of Algorithm
//      check = checkArraysEqual(clusterData0, clusterData1);
//
//      //Copy Array for next Round
//      if (!check){
//        clusterData1 = clusterData0.clone();
//      }
//    } while (!check);
//
//    return clusterData0;
//  }

  //######################
  //### HELP-FUNCTIONS ###
  //######################

  //Vergleiche Arrays
  private boolean checkArraysEqual2(int[][] array0, int[][]array1) {
    for (int i=0; i<array0.length; i++){
      for (int j=0; j<array0[0].length; j++){
        if(((array0[i][j] == -1) && (array1[i][j] != -1)) || ((array0[i][j] != -1) && (array1[i][j] == -1))){
          return false;
        }
        else if((array0[i][j] == -1) && (array1[i][j] == -1)){
          //...
        }
        else{
          //System.out.println(i + " " + j);
          if (!Arrays.equals(inputData2[array0[i][j]], inputData2[array1[i][j]])){
            return false;
          }
        }
      }
    }
    return true;
  }

//  private boolean checkArraysEqual(int[][] array0, int[][]array1) {
//    for (int i=0; i<array0.length; i++){
//      for (int j=0; j<array0[0].length; j++){
//        if(((array0[i][j] == -1) && (array1[i][j] != -1)) || ((array0[i][j] != -1) && (array1[i][j] == -1))){
//          return false;
//        }
//        else if((array0[i][j] == -1) && (array1[i][j] == -1)){
//          //...
//        }
//        else{
//          //System.out.println(i + " " + j);
//          if (inputData[array0[i][j]] != inputData[array1[i][j]]){
//            return false;
//          }
//        }
//      }
//    }
//    return true;
//  }

  //Berechne ClusterZentren neu
  private double[][] calcClusterCenter2(int[][] data, double[][] inputData){
    double[][] cluster = new double[data.length][dimension];

    for(int i=0; i<data.length; i++){
      int count = 0;
      for(int j=0; j<data[i].length; j++){
        if(data[i][j] != -1){
          cluster[i] = calcAddition(cluster[i], inputData[data[i][j]]);
          count++;
        }
      }
      cluster[i] = calcDivision(cluster[i], count);
    }
    return cluster;
  }

//  private double[] calcClusterCenter(int[][] data, double[] inputData){
//    double[] cluster = new double[data.length];
//
//    for(int i=0; i<data.length; i++){
//      int count = 0;
//      for(int j=0; j<data[i].length; j++){
//        if(data[i][j] != -1){
//          cluster[i] += inputData[data[i][j]];
//          count++;
//        }
//      }
//      cluster[i] = cluster[i] / count;
//    }
//    return cluster;
//  }

  //Berechnet ClusterID mit minimaler Differenz
  private int calcMinCluster2(double[][] cluster, double[] value){
    double[][] diffs = calcDiff2(cluster, value);
    return findMinimumIndex2(diffs);
  }

//  private int calcMinCluster(double[] cluster, double value){
//    double[] diffs = calcDiff(cluster, value);
//    return findMinimumIndex(diffs);
//  }

  //Berechnet die Differenz zwischen einem Clusterwert und einem Datenwert
  private double[][] calcDiff2(double[][] cluster, double[] value){
    double[][] result = new double[cluster.length][cluster[0].length];
    double a,b;
    a = calcModulus(value);
    for(int i=0; i<cluster.length; i++){
      b = calcModulus(cluster[i]);
      if (a > b){
        result[i] = calcDifference(value, cluster[i]);
      }
      else{
        result[i] = calcDifference(cluster[i], value);
      }
    }
    return result;
  }

//  private double[] calcDiff(double[] cluster, double value){
//    double[] result = new double[cluster.length];
//    for(int i=0; i<cluster.length; i++){
//      if (value > cluster[i]){
//        result[i] = value - cluster[i];
//      }
//      else{
//        result[i] = cluster[i] - value;
//      }
//    }
//    return result;
//  }

  //Finde Index des Minimums
  private int findMinimumIndex2(double[][] array){
    int index = 0;
    double a,b;
    for(int i=0; i<array.length; i++){
      a = calcModulus(array[i]);
      b = calcModulus(array[index]);
      if(a < b){
        index = i;
      }
    }
    return index;
  }

//  private int findMinimumIndex(double[] array){
//    int index = 0;
//    for(int i=0; i<array.length; i++){
//      if(array[i] < array[index]){
//        index = i;
//      }
//    }
//    return index;
//  }

  //### MATH ###

  //Betrag berechnen (n-Dimensional)
  private double calcModulus(double[] array){
    double result;
    double x = 0.0;
    //Addition
    for(double d : array){
      x = x + (d * d);
    }
    //Wurzel
    result = Math.sqrt(x);
    return result;
  }

  //Differenz berechnen (n-Dimensional)
  private double[] calcDifference(double[] a, double[] b){
    double[] result = new double[a.length];
    for(int i=0; i<a.length; i++){
      result[i] = a[i] - b[i];
    }
    return result;
  }

  //Addition berechnen (n-Dimensional)
  private double[] calcAddition(double[] a, double[] b){
    double[] result = new double[a.length];
    for(int i=0; i<a.length; i++){
      result[i] = a[i] + b[i];
    }
    return result;
  }

  //Addition berechnen (n-Dimensional)
  private double[] calcDivision(double[] a, double b){
    double[] result = new double[a.length];
    for(int i=0; i<a.length; i++){
      result[i] = a[i] / b;
    }
    return result;
  }

}
