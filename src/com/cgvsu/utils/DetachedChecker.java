package com.cgvsu.utils;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class DetachedChecker {

    private TreeSet<Integer> canDeleteVertices;
    private TreeSet<Integer> canDeleteNormals;
    private TreeSet<Integer> canDeleteVT;

    private Model model;

    public DetachedChecker(TreeSet<Integer> canDeleteVertices, TreeSet<Integer> canDeleteNormals, TreeSet<Integer> canDeleteVT, Model model) {
        this.canDeleteVertices = canDeleteVertices;
        this.canDeleteVT = canDeleteVT;
        this.canDeleteNormals = canDeleteNormals;
        this.model = model;
    }

    public void deleteDetached() {
        deleteSmth(() -> model.vertices, canDeleteVertices, Polygon::getVertexIndices);
        deleteSmth(() -> model.normals, canDeleteNormals, Polygon::getNormalIndices);
        deleteSmth(() -> model.textureVertices,canDeleteVT, Polygon::getTextureVertexIndices);
    }

    private void deleteSmth(Supplier<ArrayList<?>> supplier, TreeSet<Integer> canDelete, Function<Polygon, ArrayList<Integer>> func) {
        int shift = 0;
        ArrayList<Integer> deleteInd = new ArrayList<>();

        ArrayList<?> arr = supplier.get();

        int[] replacementArr = IntStream.rangeClosed(0, arr.size() - 1).toArray();

        for (int i = 0; i < arr.size(); i++) {
            if (canDelete.contains(i)) {
                shift++;
                deleteInd.add(i);
            } else {
                replacementArr[i] = i - shift;
            }
        }

        for (int i = deleteInd.size() -1; i >= 0; i--) {
            int k = deleteInd.get(i);
            arr.remove(k);
        }

        for (Polygon poly : model.polygons) {
            if (poly != null) {
                ArrayList<Integer> updatedIndexes = func.apply(poly);
                for (int i = 0; i < updatedIndexes.size(); i++) {
                    updatedIndexes.set(i, replacementArr[updatedIndexes.get(i)]);
                }
            }
        }
    }

}
