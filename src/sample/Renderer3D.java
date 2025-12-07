package sample;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Renderer3D {

    private Camera3D camera;
    private Canvas canvas;
    private GraphicsContext gc;

    public Renderer3D(Camera3D camera, Canvas canvas){
        this.camera = camera;
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    public void clearAndDrawGrid(){
        gc.setFill(Color.web("#222"));
        gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());

        gc.setStroke(Color.web("#333"));
        gc.setLineWidth(1);

        for (int i=0;i<canvas.getWidth();i+=40){
            gc.strokeLine(i, 0, i, canvas.getHeight());
        }
        for (int i=0;i<canvas.getHeight();i+=40){
            gc.strokeLine(0, i, canvas.getWidth(), i);
        }
    }

    public void drawObject(Object3D obj){
        Matrix3 camR = camera.getRotationMatrix();

        double cx = canvas.getWidth()/2;
        double cy = canvas.getHeight()/2;

        Vector3[] projected = new Vector3[obj.vertices.size()];

        for (int i=0;i<obj.vertices.size();i++){
            Vector3 v = camR.transform(obj.vertices.get(i));

            double px = cx + v.x * camera.zoom;
            double py = cy - v.y * camera.zoom;

            projected[i] = new Vector3(px, py, v.z);
        }

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);

        for (int[] e : obj.edges){
            Vector3 a = projected[e[0]];
            Vector3 b = projected[e[1]];
            gc.strokeLine(a.x, a.y, b.x, b.y);
        }
    }
}
