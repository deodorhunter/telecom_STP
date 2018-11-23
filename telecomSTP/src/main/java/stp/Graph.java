//package stp;
//
//import java.util.*;
//
//public class Graph {
//    //Map of adjacency lists for each node
//    Map<Integer, LinkedList<Integer>> adj;
//
//    public Graph(ArrayList<Integer> nodes) {
//        //your node labels are consecutive integers starting with one.
//        //to make the indexing easier we will allocate an array of adjacency one element larger than necessary
//        adj = new HashMap<Integer, LinkedList<Integer>>();
//        for (int i = 0; i < nodes.size(); ++i) {
//            adj.put(i, new LinkedList<Integer>());
//        }
//    }
//
//    public void addNeighbor(int v1, int v2) {
//        adj.get(v1).add(v2);
//    }
//
//    public List<Integer> getNeighbors(int v) {
//        return adj.get(v);
//    }
//
//    @Override
//    public String toString(){
////        String out="Vertici: "+this.
//    }
//
//}
//
