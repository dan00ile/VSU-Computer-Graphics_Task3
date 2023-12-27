package com.cgvsu;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;

import java.util.ArrayList;
import java.util.List;

public class PlaneGenerator {
    public static Model generateModel() {
        Model model = new Model();

        // Генерация вершин
        for (int i = 0; i < 20; i++) {
            double theta = i * 2 * Math.PI / 20;
            double x = Math.cos(theta);
            double y = Math.sin(theta);
            double z = Math.sin(theta * 2); // Просто для разнообразия
            model.vertices.add(new Vector3f((float) x, (float) y, (float) z));
        }

        // Генерация грани
        for (int i = 0; i < 18; i++) {
            Polygon polygon = new Polygon();
            polygon.setVertexIndices(new ArrayList<>(List.of(i, i+ 1, i+ 2)));
            model.polygons.add(polygon);
        }

        return model;
    }



}
