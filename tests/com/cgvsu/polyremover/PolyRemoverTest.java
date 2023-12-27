package com.cgvsu.polyremover;

import com.cgvsu.math.Vector3f;
import com.cgvsu.model.Model;
import com.cgvsu.model.Polygon;
import com.cgvsu.objreader.ObjReader;
import com.cgvsu.utils.DeletingPolygons;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PolyRemoverTest {

    @Test
    public void testNotDeletingAdditionalVert() throws IOException {
        String fileContent = Files.readString(Path.of("tests/com/cgvsu/polyremover/testModels/cubeWithAdditionalV.obj"));
        Model model = ObjReader.read(fileContent);
        List<Integer> check = Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11);


        DeletingPolygons.deletePolyWithDet(model, check);

        Assertions.assertEquals(1, model.vertices.size());
    }

    @Test
    public void testDelAllPolys() throws IOException {
        String fileContent = Files.readString(Path.of("tests/com/cgvsu/polyremover/testModels/simpleCube.obj"));
        Model model = ObjReader.read(fileContent);
        List<Integer> check = Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11);
        DeletingPolygons.deletePolyWithDet(model, check);

        Assertions.assertEquals(0, model.polygons.size());
        Assertions.assertEquals(0, model.vertices.size());
        Assertions.assertEquals(0, model.textureVertices.size());
        Assertions.assertEquals(0, model.normals.size());
    }

    @Test
    public void testDelOneDetVert() throws IOException {
        String fileContent = Files.readString(Path.of("tests/com/cgvsu/polyremover/testModels/simpleCube.obj"));
        Model model = ObjReader.read(fileContent);

        List<Integer> check = Arrays.asList(0,3,4,9,10);
        int k = model.vertices.size();
        DeletingPolygons.deletePolyWithDet(model, check);

        Assertions.assertEquals(k - 1, model.vertices.size());

    }

    @Test
    public void testPolyDel() throws IOException {
        String fileContent = Files.readString(Path.of("tests/com/cgvsu/polyremover/testModels/simpleCube.obj"));
        Model model = ObjReader.read(fileContent);

        List<Integer> check = List.of(0);
        List<Vector3f> k = model.vertices;
        int colPoly = model.polygons.size();

        DeletingPolygons.deletePolyWithDet(model, check);

        Assertions.assertEquals(k, model.vertices);
        Assertions.assertEquals(colPoly - 1, model.polygons.size());
    }

    @Test
    public void testPlaneCornerPoly() throws IOException {
        String fileContent = Files.readString(Path.of("tests/com/cgvsu/polyremover/testModels/plane2.obj"));
        Model model = ObjReader.read(fileContent);

        List<Integer> check = List.of(0);

        DeletingPolygons.deletePolyWithDet(model, check);

        fileContent = Files.readString(Path.of("tests/com/cgvsu/polyremover/testModels/planeWithDel0Poly.obj"));
        Model goodModel = ObjReader.read(fileContent);

        boolean equalsFlag = true;

        ArrayList<Polygon> badArr = model.polygons;
        ArrayList<Polygon> goodArr = goodModel.polygons;

        if (badArr.size() == goodArr.size()) {
            for (int i = 0; i < goodArr.size(); i++) {
                if (goodArr.get(i).getVertexIndices().size() == badArr.get(i).getVertexIndices().size()) {
                    for (int j = 0; j < goodArr.get(i).getVertexIndices().size(); j++) {
                        if (!goodArr.get(i).getVertexIndices().get(j).equals(badArr.get(i).getVertexIndices().get(j))) {
                            equalsFlag = false;
                            break;
                        }
                    }
                }
            }
        } else {
            equalsFlag = false;
        }
        Assertions.assertTrue(equalsFlag);
    }

    @Test
    public void testPlaneInsidePoly() throws IOException {
        String fileContent = Files.readString(Path.of("tests/com/cgvsu/polyremover/testModels/plane2.obj"));
        Model model = ObjReader.read(fileContent);

        int k = model.vertices.size();

        List<Integer> check = List.of(12);

        DeletingPolygons.deletePolyWithDet(model, check);

        Assertions.assertEquals(k, model.vertices.size());
    }

}
