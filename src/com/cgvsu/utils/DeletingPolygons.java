package com.cgvsu.utils;

import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Function;

public class DeletingPolygons {

    public static void deletePoly(Model model, List<Integer> arr) {
        deletePolygon(model, arr);

        clearPoly(model);
    }

    public static void deletePolyWithDet(Model model, List<Integer> list) {
        TreeSet<Integer> canDeleteVertices = findVertices(model, list, Polygon::getVertexIndices);
        TreeSet<Integer> canDeleteNormals = findVertices(model, list, Polygon::getNormalIndices);
        TreeSet<Integer> canDeleteVT = findVertices(model, list, Polygon::getTextureVertexIndices);

        deletePolygon(model, list);

        checkDetached(model, canDeleteVertices, canDeleteVT, canDeleteNormals);

        clearPoly(model);
    }
    private static TreeSet<Integer> findVertices(Model model, Iterable<Integer> list, Function<Polygon, ArrayList<Integer>> func) {
        TreeSet<Integer> canDeleteSmth = new TreeSet<>();

        for (Integer num : list) {
            try {
                ArrayList<Integer> arr = func.apply(model.polygons.get(num));
                if (arr != null) {
                    canDeleteSmth.addAll(arr);
                }
            } catch (IndexOutOfBoundsException e) {
                    throw new DeletingPolysException(String.valueOf(num));
            }
        }

        return canDeleteSmth;
    }

    private static void deletePolygon(Model model, Iterable<Integer> iterable) {
        for (Integer num : iterable) {
                model.polygons.set(num, null);
        }
    }

    public static void clearPoly(Model model) {
        for (int i = model.polygons.size() - 1; i >= 0; i--) {
            if (model.polygons.get(i) == null) {
                model.polygons.remove(i);
            }
        }
    }


    public static void checkDetached(Model model, TreeSet<Integer> canDeleteVertices, TreeSet<Integer> canDeleteVT, TreeSet<Integer> canDeleteNormals) {
        for (Polygon poly : model.polygons) {
            if (poly != null) {
                for (Integer vertex : poly.getVertexIndices()) {
                    canDeleteVertices.remove(vertex);
                }
                for (Integer normal : poly.getNormalIndices()) {
                    canDeleteNormals.remove(normal);
                }
                for (Integer vt : poly.getTextureVertexIndices()) {
                    canDeleteVT.remove(vt);
                }
            }
        }

        new DetachedChecker(canDeleteVertices, canDeleteNormals, canDeleteVT, model).deleteDetached();
    }


}
