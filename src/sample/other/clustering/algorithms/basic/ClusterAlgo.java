package sample.other.clustering.algorithms.basic;

import sample.other.clustering.enums.ClusteringAlgorithm;

/**
 * Created by andreasschw on 03.08.2017.
 *
 * Cluster Algorithm
 */
public class ClusterAlgo {

  protected ClusteringAlgorithm algoName = ClusteringAlgorithm.UNKNOWN;

  public ClusterAlgo(){
    //...
  }

  //return Algorithm Name
  public ClusteringAlgorithm getAlgoName() {
    return algoName;
  }

}
