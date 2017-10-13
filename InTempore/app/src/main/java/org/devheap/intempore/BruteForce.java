package org.devheap.intempore;

public class BruteForce extends PathAlgorithm {
    public BruteForce (PathGraph graph){
        _graph = graph;
    }

    @override
    protected void _calculate(){
        //TODO: bruteforce
        //for each permutation
        //calculate the path length
        //compare to current minima
        //construct a PathData instance
    }

    private ArrayList<ArrayList<Vertex>> _permute() {
        //TODO: permutations - they must have the same starting point
    }
}