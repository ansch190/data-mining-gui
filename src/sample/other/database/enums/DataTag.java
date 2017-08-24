package sample.other.database.enums;

/**
 * Created by andreasschw on 21.07.2017.
 *
 * Tags from Header of DataPictures
 */
public enum DataTag {

  FILENAME("Filename", DataType.STRING),
  MICS_POSITION("MICSPosition", DataType.STRING),
  MARKER_ID("MarkerID", DataType.STRING),
  ANTIBODY_TITER_1x("AntibodyTiter1:x", DataType.STRING),
  FLUOROCHROME("Fluorochrome", DataType.STRING),
  WELL_ID("WellID", DataType.STRING),
  GROUP("Group", DataType.STRING),
  SEGMENT("Segment", DataType.STRING),
  OBJECT_ID("ObjectID", DataType.INTEGER),
  X_POSITION_IN_IMAGE("XPositionInImage", DataType.INTEGER),
  Y_POSITION_IN_IMAGE("YPositionInImage", DataType.INTEGER),
  CATEGORY("Category", DataType.STRING),
  EXT("ExT", DataType.STRING),
  SAMPLE_ID("SampleID", DataType.STRING),
  FIELD("Field", DataType.STRING),
  CELL_SIZE("CellSize", DataType.INTEGER),
  TOTAL("Total", DataType.DOUBLE),
  MEDIAN("Median", DataType.INTEGER),
  MEDIAN_BG_CORRECTED("MedianBGCorrected", DataType.INTEGER),
  MEAN("Mean", DataType.DOUBLE),
  MEAN_BG_CORRECTED("MeanBGCorrected", DataType.DOUBLE),
  STD("Std", DataType.DOUBLE),
  BACKGROUND("Background", DataType.INTEGER);

  private String s;
  private DataType type;

  private DataTag(String s, DataType type){
    this.s = s;
    this.type = type;
  }

  public String getString(){
    return this.s;
  }

  public DataType getType(){
    return this.type;
  }

  public static DataTag getTag(String s){
    for(DataTag tag : DataTag.values()){
      if(tag.getString().equals(s)){
        return tag;
      }
    }
    return null;
  }

}
