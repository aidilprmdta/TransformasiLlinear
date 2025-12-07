package sample;

import java.util.ArrayList;
import java.util.List;

public class Object3D {
    public final List<Vector3> vertices = new ArrayList<>();
    public final List<int[]> edges = new ArrayList<>();

    public Object3D copy(){
        Object3D n = new Object3D();
        for (Vector3 v : vertices) n.vertices.add(v.cloneV());
        for (int[] e : edges) n.edges.add(new int[]{e[0], e[1]});
        return n;
    }

    // create a centered cube and axis lines
    public static Object3D createAxisBox(double size, int gridLines) {
        Object3D o = new Object3D();

        double s = size;
        // cube vertices (8)
        o.vertices.add(new Vector3(-s, -s, -s)); //0
        o.vertices.add(new Vector3(s, -s, -s));  //1
        o.vertices.add(new Vector3(s, s, -s));   //2
        o.vertices.add(new Vector3(-s, s, -s));  //3
        o.vertices.add(new Vector3(-s, -s, s));  //4
        o.vertices.add(new Vector3(s, -s, s));   //5
        o.vertices.add(new Vector3(s, s, s));    //6
        o.vertices.add(new Vector3(-s, s, s));   //7

        int[][] cubeEdges = {
                {0,1},{1,2},{2,3},{3,0},
                {4,5},{5,6},{6,7},{7,4},
                {0,4},{1,5},{2,6},{3,7}
        };
        for (int[] e : cubeEdges) o.edges.add(e);

        // simple axis endpoints as extra vertices (will be appended after cube)
        int base = o.vertices.size();
        o.vertices.add(new Vector3(0,0,0));      // base origin index = base
        o.vertices.add(new Vector3(size*2,0,0)); // x axis
        o.vertices.add(new Vector3(0,size*2,0)); // y axis
        o.vertices.add(new Vector3(0,0,size*2)); // z axis
        o.edges.add(new int[]{base, base+1});
        o.edges.add(new int[]{base, base+2});
        o.edges.add(new int[]{base, base+3});

        return o;
    }
}
