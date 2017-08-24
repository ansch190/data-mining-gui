package sample.other.clustering;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import weka.core.Instances;
import weka.core.converters.CSVLoader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import sample.other.clustering.algorithms.KMeans;
import sample.other.clustering.algorithms.SNN;
import sample.other.clustering.enums.ClusteringAlgorithm;
import sample.other.database.enums.DataTag;
import sample.other.plotting.PlotEngine;

/**
 * Created by andreasschw on 04.08.2017.
 *
 * Calculating Cluster with different Algorithms
 */
public class ClusterEngine {

  //###########
  //### CSV ###
  //###########

  private static double[] loadCsvFile(File file){
    try {
      //Load Data
      CSVLoader csvLoader = new CSVLoader();
      csvLoader.setSource(file);
      csvLoader.setNoHeaderRowPresent(false);
      Instances inst = csvLoader.getDataSet();

      double[] data = new double[inst.numInstances()];
      for (int i=0; i<inst.numInstances(); i++) {
        data[i] = inst.instance(i).value(0);
      }

      return data;
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }

  private static double[][] loadCsvFile2(File file){
    try{
      //Load Data
      CSVLoader csvLoader = new CSVLoader();
      csvLoader.setSource(file);
      csvLoader.setNoHeaderRowPresent(false);
      Instances inst = csvLoader.getDataSet();

      double[][] data = new double[inst.numInstances()][inst.numAttributes()];
      for (int i=0; i<inst.numInstances(); i++){
        data[i] = inst.instance(i).toDoubleArray();
      }

      return data;
    }
    catch(Exception e){
      e.printStackTrace();
    }
    return null;
  }

  private static void saveResultCsvFile(ClusterResult result, List<File> inputFiles){
    //ClusterID, CellID, XPos, YPos, Feature, DAPIi-, CD103-PE, CD44-FITC,...

    int fileCount = result.getDataSetSize(0,0) - 3;  //CellID;XPos;YPos;

    List<Double> parameter = result.getClusterAlgorithmParameter();

    //Create Filename
    String resultClusterCSVName = "cluster_result_" + fileCount + "_" + result.getClusterAlgorithm();
    if(result.getClusterAlgorithm() == ClusteringAlgorithm.K_MEANS){
      resultClusterCSVName += "_" + "C" + parameter.get(0);
    }
    else if(result.getClusterAlgorithm() == ClusteringAlgorithm.SNN){
      resultClusterCSVName += "_" + "K" + parameter.get(0);
      resultClusterCSVName += "_" + "E" + parameter.get(1);
      resultClusterCSVName += "_" + "M" + parameter.get(2);
    }
    resultClusterCSVName += ".csv";

    try {
      //create File
      File resultClusterCSV = new File(inputFiles.get(0).getPath().replaceAll(inputFiles.get(0).getName(), resultClusterCSVName));
      BufferedWriter bwr = new BufferedWriter(new FileWriter(resultClusterCSV));

      //Load Data
      CSVLoader csvLoader = new CSVLoader();
      csvLoader.setSource(inputFiles.get(1));
      csvLoader.setNoHeaderRowPresent(true);
      Instances inst = csvLoader.getDataSet();

      //create Header
      String header = "ClusterID;CellID;XPos;YPos;Feature;";
      for(int i=2; i<inst.numAttributes(); i++){
        header += inst.attribute(i).enumerateValues().nextElement() + ";";
      }

      //System.out.println("Header: " + header);
      bwr.write(header + "\n");

      //create DataSets
      String line = "";

      for(int i=0; i<result.getClusterCount(); i++){
        for(int j=0; j<result.getClusterSize(i); j++){
          line = i + ";";  //ClusterID
          line += result.getCellID(i,j) + ";";  //CellID
          line += result.getXPos(i,j) + ";";  //XPos
          line += result.getYPos(i,j) + ";";  //YPos
          line += result.getDataTag().getString() + ";";  //Feature
          for(int k=3; k<result.getDataSetSize(i,j); k++){
            line += result.getDataSet(i,j).get(k) + ";";
          }
          //System.out.println(line);
          bwr.write(line + "\n");
        }
      }
      bwr.close();

    }
    catch(Exception e){
      e.printStackTrace();
    }

  }

  //###########
  //### SNN ###
  //###########

  public static void calculateSNN(List<File> inputFiles, int k, double eps, int minpts, int linearScaleRange, DataTag dataTag){
    //load CSV
    double[][] data = loadCsvFile2(inputFiles.get(0));
    //calculate SNN
    SNN snn = new SNN(data, k, eps, minpts);
    int[] clusterLabels = snn.calc();

    //Calculation for Results
    data = loadCsvFile2(inputFiles.get(1));
    List<Double> parameters = Arrays.asList((double)k, eps, (double)minpts);
    ClusterResult result = new ClusterResult(ClusteringAlgorithm.SNN, parameters, dataTag);
    result = resultCalculationSNN(data, clusterLabels, result);

    //Save Result
    saveResultCsvFile(result, inputFiles);

    //Calculation for Plot
    XYSeriesCollection plotResult = plotCalculation(result);

    //Plot Results
    String windowTitle = "SNN - Analysis";
    String chartTitle = "SNN-Analysis" + " - " + dataTag.getString() + " - " + "LinearScaleRange " + linearScaleRange + " - " + "K " + k + ", " + "Eps " + eps + ", " + "MinPts " + minpts;
    String xAxisName = "X-Axis";
    String yAxisName = "Y-Axis";

    PlotEngine pe = new PlotEngine();
    pe.paintResults(plotResult, windowTitle, chartTitle, xAxisName, yAxisName);
  }

  private static ClusterResult resultCalculationSNN(double[][] data, int[] dataLabels, ClusterResult result){
    int dataSetSize = data[0].length;

    //create  Clusters
    Set<Integer> clusters = new HashSet<>();
    for(int i : dataLabels){
      clusters.add(i);
    }

    for(int clusterValue : clusters){
      List<List<Double>> cluster = new ArrayList<>();
      for(int i=0; i<data.length; i++){
        if(clusterValue == dataLabels[i]){
          List<Double> dataSet = new ArrayList<>();
          dataSet.add((double) i);  //CellID
          dataSet.add(data[i][0]);  //XPos
          dataSet.add(data[i][1]);  //YPos für graphische Darstellung gespiegelt
          for(int j=3; j<dataSetSize+1; j++){
            dataSet.add(data[i][j-1]);  //File0-n mit Featurewerten
          }
          cluster.add(dataSet);
        }
      }
      result.addCluster(cluster);
    }

    return result;
  }

  //###############
  //### K-MEANS ###
  //###############

  public static void calculateKMeans(List<File> inputFiles, int clusterCount, int linearScaleRange, DataTag dataTag){
    //load CSV
    double[][] data = loadCsvFile2(inputFiles.get(0));
    //calculate KMeans
    KMeans kMeans = new KMeans(data, clusterCount);
    int[][] cluster = kMeans.calc();

//    //Print Results
//    for(int i=0; i<cluster.length; i++){
//      System.out.print("Cluster: " + i + " # " + cluster[i].length + " #");
//      for(int j=0; j<cluster[i].length; j++){
//        System.out.print(" " + cluster[i][j]);
//      }
//      System.out.println();
//    }

    //Calculation for Results
    double[][] dataCoords = loadCsvFile2(inputFiles.get(1));
    List<Double> parameters = Arrays.asList((double)clusterCount);
    ClusterResult result = new ClusterResult(ClusteringAlgorithm.K_MEANS, parameters, dataTag);
    result = resultCalculationKMeans(dataCoords, cluster, result);

    //Save Result
    saveResultCsvFile(result, inputFiles);

    //Calculation for Plot
    XYSeriesCollection plotResult = plotCalculation(result);

    //Plot Results
    String windowTitle = "K-Means - Analysis";
    String chartTitle = "K-Means-Analysis" + " - " + dataTag.getString() + " - " + "LinearScaleRange " + linearScaleRange + " - " + "ClusterCount " + clusterCount;
    String xAxisName = "X-Axis";
    String yAxisName = "Y-Axis";

    PlotEngine pe = new PlotEngine();
    pe.paintResults(plotResult, windowTitle, chartTitle, xAxisName, yAxisName);
  }

  private static ClusterResult resultCalculationKMeans(double[][] data, int[][] cluster, ClusterResult result){
    for(int i=0; i<cluster.length; i++){
      List<List<Double>> clusterList = new ArrayList<>();
      for(int j=0; j<cluster[i].length; j++){
        int x = cluster[i][j];
        if(cluster[i][j] >= 0){
          List<Double> dataSet = new ArrayList<>();
          dataSet.add((double) x);  //CellID
          dataSet.add(data[x][0]);  //XPos
          dataSet.add(data[x][1]);  //YPos
          for(int k=2; k<data[x].length; k++){
            dataSet.add(data[x][k]);  //File0-n mit Featurewerten
          }
          clusterList.add(dataSet);
        }
      }
      result.addCluster(clusterList);
    }

    return result;
  }

  //#############
  //### OTHER ###
  //#############

  private static XYSeriesCollection plotCalculation(ClusterResult data){
    XYSeriesCollection result = new XYSeriesCollection();
    for(int i=0; i<data.getClusterCount(); i++){
      XYSeries series = new XYSeries("Cluster" + " " + i);
      for(int j=0; j<data.getClusterSize(i); j++){
        List<Double> dataSet = data.getDataSet(i,j);
        double x = dataSet.get(1);
        double y = dataSet.get(2) * -1.0;
        series.add(x, y);
      }
      result.addSeries(series);
    }
    return result;
  }

}
