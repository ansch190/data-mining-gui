package sample.other.scaling;

import sample.other.database.DataPicture;
import sample.other.database.enums.DataTag;

/**
 * Created by andreasschw on 18.08.2017.
 *
 * Scaling Data
 */
public class ScalingEngine {

  public static DataPicture scaleData(DataPicture dp, DataTag dataTag, int linearScaleRange){
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
  private static double getMinimum(DataPicture dp, DataTag dataTag){
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
  private static double getMaximum(DataPicture dp, DataTag dataTag){
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
