package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Renderer3D {
    private final Camera3D camera;
    private final Canvas canvas;

    public Renderer3D(Camera3D camera, Canvas canvas){
        this.camera = camera;
        this.canvas = canvas;
    }

    public void clearAndDrawGrid(){
        GraphicsContext g = canvas.getGraphicsContext2D();
        double w = canvas.getWidth(), h = canvas.getHeight();
        g.setFill(Color.web("#0b1220"));
        g.fillRect(0,0,w,h);

        g.setStroke(Color.web("#172229"));
        for (double x=0;x<=w;x+=40) g.strokeLine(x,0,x,h);
        for (double y=0;y<=h;y+=40) g.strokeLine(0,y,w,y);
    }

    // draw edges of object (project with camera rotation)
    public void drawObject(Object3D obj){
        GraphicsContext g = canvas.getGraphicsContext2D();
        Matrix3 camR = camera.getRotationMatrix();

        double cx = canvas.getWidth()/2.0;
        double cy = canvas.getHeight()/2.0;
        double scale = 1.0 * (Math.min(canvas.getWidth(), canvas.getHeight())/600.0) * camera.zoom;

        // project all vertices
        Vector3[] proj = new Vector3[obj.vertices.size()];
        for (int i=0;i<obj.vertices.size();i++){
            Vector3 v = obj.vertices.get(i);
            Vector3 rv = camR.transform(v); // rotated
            double px = cx + rv.x * scale;
            double py = cy - rv.y * scale; // Y-up to screen Y-down
            proj[i] = new Vector3(px, py, rv.z);
        }

        // draw edges
        g.setStroke(Color.web("#bfe8ff"));
        g.setLineWidth(2);
        for (int[] e : obj.edges){
            Vector3 a = proj[e[0]];
            Vector3 b = proj[e[1]];
            g.strokeLine(a.x, a.y, b.x, b.y);
        }

        // draw origin small
        g.setFill(Color.WHITE);
        g.fillOval(cx-3, cy-3, 6, 6);
    }
}
