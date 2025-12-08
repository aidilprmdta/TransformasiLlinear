package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Renderer3D {

    private final Canvas canvas;
    private Matrix3 cameraRotation = new Matrix3();
    private double zoom = 1.0;

    public Renderer3D(Camera3D camera, Canvas canvas){
        this.canvas = canvas;
    }

    public void setCameraRotation(Matrix3 m){
        this.cameraRotation = m;
    }

    public void setZoom(double z){
        this.zoom = z;
    }

    public void clearAndDrawGrid(){
        GraphicsContext g = canvas.getGraphicsContext2D();
        double w = canvas.getWidth(), h = canvas.getHeight();
        g.setFill(Color.web("#0b1220"));
        g.fillRect(0,0,w,h);
        g.setStroke(Color.web("#172229"));
        for (double x = 0; x <= w; x+=40) g.strokeLine(x,0,x,h);
        for (double y = 0; y <= h; y+=40) g.strokeLine(0,y,w,y);
    }

    public void drawObject(Object3D obj){
        GraphicsContext g = canvas.getGraphicsContext2D();

        double cx = canvas.getWidth()/2.0;
        double cy = canvas.getHeight()/2.0;
        double scale = zoom * (Math.min(canvas.getWidth(), canvas.getHeight())/600.0);

        Vector3[] p = new Vector3[obj.vertices.size()];

        for (int i = 0; i < obj.vertices.size(); i++){
            Vector3 v = obj.vertices.get(i);
            Vector3 vv = cameraRotation.transform(v);
            p[i] = new Vector3(
                    cx + vv.x * scale,
                    cy - vv.y * scale,
                    vv.z
            );
        }

        g.setStroke(Color.web("#bfe8ff"));
        g.setLineWidth(2);

        for (int[] e : obj.edges){
            Vector3 a = p[e[0]];
            Vector3 b = p[e[1]];
            g.strokeLine(a.x, a.y, b.x, b.y);
        }

        g.setFill(Color.WHITE);
        g.fillOval(cx-3, cy-3, 6, 6);
    }
}
