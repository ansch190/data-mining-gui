package sample.other.clustering.algorithms;

import net.sf.javaml.core.kdtree.KDTree;

import org.la4j.iterator.VectorIterator;
import org.la4j.matrix.sparse.CRSMatrix;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import sample.other.clustering.algorithms.basic.ClusterAlgo;
import sample.other.clustering.enums.ClusteringAlgorithm;

/**
 * Created by andreasschw on 04.08.2017.
 *
 * SNN Cluster Algorithm
 */
public class SNN extends ClusterAlgo {

  private double[][] data = null;

  private int k = -1;
  private double eps = -1.0;
  private int minpts = -1;

  public SNN(double[][] data, int k, double eps, int minpts){
    this.algoName = ClusteringAlgorithm.SNN;

    this.data = data;
    this.k = k;
    this.eps = eps;
    this.minpts = minpts;
  }

  public int[] calc(){
    return calculateSNN(data, k, eps, minpts);
  }

  public int[] calc(int k, double eps, int minpts){
    this.k = k;
    this.eps = eps;
    this.minpts = minpts;
    return calc();
  }

  public int[] calc(double[][] data, int k, double eps, int minpts){
    this.data = data;
    this.k = k;
    this.eps = eps;
    this.minpts = minpts;
    return calc();
  }

  //###################
  //### CALCULATION ###
  //###################

  /**
   * Run the SNN-clustering algorithm
   *
   * @param X      the data matrix, one example per row
   * @param K      number of neighbors to form the sparse similarity matrix
   * @param Eps    used for determining the SNN density of a point
   * @param MinPts used for determining core points
   * @return cluster labels for the points
   */
  private int[] calculateSNN(double[][] X, int K, double Eps, int MinPts){
    int N = X.length; // number of points
    int d = X[0].length; // dimensionality

    if (MinPts >= K) {
      throw new RuntimeException(
              "MinPts has to be smaller than K. No sense in a point having more than K neighbors.");
    }

    int[] labels = new int[N];

    // STEP 1 - get a similarity matrix

    // construct the kd-tree for knn queries
    KDTree kdtree = new KDTree(d);

    for (int i = 0; i < N; i++){
      kdtree.insert(X[i], i);
    }

    // STEP 2 - sparsify the matrix by keeping only the k most similar
    // neighbors

    // find the K-neighbors of each point
    HashMap<Integer, HashSet<Integer>> kns = new HashMap<>();
    HashSet<Integer> hs;

    for (int i = 0; i < N; i++) {
      // we will query for K + 1 nns because the
      // first nn is always the point itself
      Object[] nns = kdtree.nearest(X[i], K + 1);

      hs = new HashSet<Integer>();

      for (int j = 1; j < nns.length; j++){ // start from the 2nd nn
        hs.add((Integer) nns[j]);
      }

      kns.put(i, hs);
    }

    // STEP 3 - construct the shared nearest neighbor graph from the
    // sparsified matrix

    // The sparse matrix S holds in element (i,j) the SNN-similarity between
    // points i and j.
    CRSMatrix S = new CRSMatrix(N, N);
    int count;

    for (int i = 0; i < (N - 1); i++) {
      for (int j = i + 1; j < N; j++) {
        // create a link between i-j only if i is in j's kNN
        // neighborhood
        // and j is in i's kNN neighborhood
        if (kns.get(i).contains(j) && kns.get(j).contains(i)) {
          count = countIntersect(kns.get(i), kns.get(j));
          S.set(i, j, count);
          S.set(j, i, count);
        }
      }
    }

    // System.out.println(S.toCSV());

    // STEP 4 - find the SNN density of each point
    double[] snnDens = new double[N]; // should only contain ints though
    VectorIterator vi;
    double snnSim;

    for (int i = 0; i < N; i++) {
      vi = S.nonZeroIteratorOfRow(i);
      while (vi.hasNext()) {
        snnSim = vi.next();
        if (snnSim >= Eps)
          snnDens[i]++;
      }
    }

    // STEP 5 - find the core points
    // using MinPts, find all points that have SNN density greater than
    // MinPts
    ArrayList<Integer> corePts = new ArrayList<Integer>(N);
    boolean[] cores = new boolean[N]; // initialized to false by default

    for (int i = 0; i < N; i++) {
      if (snnDens[i] >= MinPts) {
        corePts.add(i);
        cores[i] = true;
      }
    }

    //System.out.println("Core pts list:");
    //System.out.println(corePts.toString());

    // System.out.println("similarities for point 0:");
    // vi = S.nonZeroIteratorOfRow(0);
    // while(vi.hasNext()) {
    // System.out.println("sim to 0: " + vi.next());
    // }

    // STEP 6 - form clusters from the core points. If two core pts are
    // within
    // Eps of each other, then place them in the same cluster
    int C = 0;
    HashSet<Integer> visited = new HashSet<Integer>(corePts.size());

    for (int i = 0; i < corePts.size(); i++) {
      int p = corePts.get(i);
      if (visited.contains(p)){
        continue;
      }
      visited.add(p);
      C++;
      labels[p] = C;
      ArrayDeque<Integer> neighCore = findCoreNeighbors(p, corePts, S, Eps);
      expandCluster(labels, neighCore, corePts, C, S, Eps, visited);
    }

    //System.out.println("labels after corepts merges:");
    //System.out.println(Arrays.toString(labels));

    // STEP 7 & STEP 8
    //
    // All points that are not within a radius of Eps of a core point are
    // discarded (noise);
    //
    // Assign all non-noise, non-core points to their nearest
    // core point

    for (int i = 0; i < N; i++) {
      boolean notNoise = false;
      double maxSim = Double.MIN_VALUE;
      int bestCore = -1;
      double sim;

      if (cores[i]){ // this is a core point
        continue;
      }

      for (int j = 0; j < corePts.size(); j++) {
        int p = corePts.get(j);
        sim = S.get(i, p);
        if (sim >= Eps){
          notNoise = true;
        }
        if (sim > maxSim) {
          maxSim = sim;
          bestCore = p;
        }
      }

      if (notNoise){
        labels[i] = labels[bestCore];
      }
    }

    return labels;
  }

  //######################
  //### HELP-FUNCTIONS ###
  //######################

  private int countIntersect(HashSet<Integer> h1, HashSet<Integer> h2) {
    int count = 0;
    for (Integer i : h1){
      if (h2.contains(i)){
        count++;
      }
    }
    return count;
  }

  private ArrayDeque<Integer> findCoreNeighbors(final int p, ArrayList<Integer> corePts, CRSMatrix S, final double Eps) {
    ArrayDeque<Integer> neighbors = new ArrayDeque<Integer>(corePts.size() / 2);
    int p2;
    for (int i = 0; i < corePts.size(); i++) {
      p2 = corePts.get(i);
      if (p != p2 && S.get(p, p2) >= Eps){
        neighbors.add(p2);
      }
    }
    return neighbors;
  }

  private void expandCluster(int[] labels, ArrayDeque<Integer> neighbors, ArrayList<Integer> corePts, int C, CRSMatrix S, double Eps, HashSet<Integer> visited) {
    while (neighbors.size() > 0) {
      int p = neighbors.poll();

      if (visited.contains(p)){
        continue;
      }

      labels[p] = C;
      visited.add(p);

      ArrayDeque<Integer> neigh = findCoreNeighbors(p, corePts, S, Eps);
      neighbors.addAll(neigh);
    }
  }

}
