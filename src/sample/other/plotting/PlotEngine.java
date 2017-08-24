package sample.other.plotting;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by andreasschw on 04.08.2017.
 *
 * Plot Results with Diagrams
 */
public class PlotEngine {

  public static java.util.List<Color> colors = null;

  public PlotEngine(){
    //...
  }

  //nur Zeichnen (daten[][], clusternamen, clusterfarben, titel, x-name, y-name)
  public void paintResults(XYSeriesCollection data, String windowTitle, String chartTitle, String X_Axis_Name, String Y_Axis_Name){
    java.util.List<Color> clusterColors = null;

    File colorFile = new File("C:\\Users\\andreasschw\\IdeaProjects\\data_mining_gui\\resources\\data\\colors.csv");
    if(colorFile.exists()){
      clusterColors = loadColorsFromFile(colorFile, data.getSeriesCount());
    }
    else{
      //load Cluster Colors
      String colorFilePath = "/res/colors.csv";
      InputStream is = getClass().getResourceAsStream(colorFilePath);
      clusterColors = loadColorsFromStream(is, data.getSeriesCount());
    }
    colors = clusterColors;

    // create chart
    boolean legend = false;
    boolean tooltips = true;
    boolean urls = false;
    JFreeChart chart = ChartFactory.createScatterPlot(chartTitle, X_Axis_Name, Y_Axis_Name, data, PlotOrientation.VERTICAL, legend, tooltips, urls);
    XYPlot xypl = chart.getXYPlot();

    for(int i=0; i<data.getSeriesCount(); i++){
      xypl.getRenderer().setSeriesPaint(i, clusterColors.get(i));
    }

    int circleSize = 2;
    for (int i=0; i<data.getSeriesCount(); i++){
      xypl.getRenderer().setSeriesShape(i, ShapeUtilities.createDiamond(circleSize));
    }

    //Fenster zeichnen
    ChartFrame frame = new ChartFrame(windowTitle, chart);
    int frameHeight = 800;
    int frameWidth = (int) (frameHeight * 0.975);
    frame.setSize(frameWidth, frameHeight);
    frame.setLocationRelativeTo(null);
//    frame.pack();
    frame.setVisible(true);
  }

  //##############
  //### COLORS ###
  //##############

  //load Colors from File
  private java.util.List<Color> loadColorsFromFile(File file, int count){
    java.util.List<Color> colors = new ArrayList<>();
    try{
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line = "";
      String[] split;
      String splitChar = ",";
      while(((line = br.readLine()) != null) && count!=0){
        split = line.split(splitChar);
        if(split[0].equals("red")){
          continue;
        }
        else{
          Color c = new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
          colors.add(c);
          count--;
        }
      }
      br.close();
    }
    catch(Exception e){
      e.printStackTrace();
    }
    if(count!=0){
      System.out.println("Not enough Colors available! Lack of " + count + " Colors!");
    }
    return colors;
  }

  private java.util.List<Color> loadColorsFromStream(InputStream is, int count){
    java.util.List<Color> colors = new ArrayList<>();
    try{
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String line = "";
      String[] split;
      String splitChar = ",";
      while(((line = br.readLine()) != null) && count!=0){
        split = line.split(splitChar);
        if(split[0].equals("red")){
          continue;
        }
        else{
          Color c = new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]));
          colors.add(c);
          count--;
        }
      }
      br.close();
    }
    catch(Exception e){
      e.printStackTrace();
    }
    if(count!=0){
      System.out.println("Not enough Colors available! Lack of " + count + " Colors!");
    }
    return colors;
  }

  //generate Random Colors
  private java.util.List<Color> generateRndColors(int count){
    java.util.List<Color> colors = new ArrayList<>(count);
    Color c;

    Random rnd = new Random();
    int rndBound = 256;
    rnd.nextInt(rndBound);

    //generate different Colors
    while(colors.size() != count){
      c = new Color(rnd.nextInt(rndBound),rnd.nextInt(rndBound),rnd.nextInt(rndBound));
      if(!colors.contains(c)){
        colors.add(c);
      }
    }
    return colors;
  }

  //save Colors to File
  private void saveColorsToFile(File file, java.util.List<Color> colors){
    try{
      FileWriter fw = new FileWriter(file);
      String splitChar = ",";
      String s = "red" + splitChar + "green" + splitChar + "blue\n";
      fw.write(s);
      for(Color c : colors){
        s = c.getRed() + splitChar + c.getGreen() + splitChar + c.getBlue() + "\n";
        fw.write(s);
      }
      fw.close();
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

}
