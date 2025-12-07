package sample;

import java.util.ArrayList;
import java.util.List;

public class Object3D {

    public List<Vector3> vertices = new ArrayList<>();
    public List<int[]> edges = new ArrayList<>();

    public Object3D copy(){
        Object3D o = new Object3D();
        for (Vector3 v : vertices) o.vertices.add(v.cloneV());
        for (int[] e : edges) o.edges.add(new int[]{e[0], e[1]});
        return o;
    }

    // contoh objek kotak + axis
    public static Object3D createAxisBox(double size, double thickness){

        Object3D o = new Object3D();

        // Simple cube
        o.vertices.add(new Vector3(-size, -size, -size));
        o.vertices.add(new Vector3(size, -size, -size));
        o.vertices.add(new Vector3(size, size, -size));
        o.vertices.add(new Vector3(-size, size, -size));

        o.vertices.add(new Vector3(-size, -size, size));
        o.vertices.add(new Vector3(size, -size, size));
        o.vertices.add(new Vector3(size, size, size));
        o.vertices.add(new Vector3(-size, size, size));

        int[][] e = {
                {0,1},{1,2},{2,3},{3,0},
                {4,5},{5,6},{6,7},{7,4},
                {0,4},{1,5},{2,6},{3,7}
        };

        for (int[] x : e) o.edges.add(x);

        return o;
    }
}
