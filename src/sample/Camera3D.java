package sample;

public class Camera3D {

    public double rotX = 0;
    public double rotY = 0;
    public double zoom = 1;

    public Matrix3 getRotationMatrix(){
        double rx = Math.toRadians(rotX);
        double ry = Math.toRadians(rotY);

        Matrix3 Rx = new Matrix3(new double[][]{
                {1,0,0},
                {0,Math.cos(rx), -Math.sin(rx)},
                {0,Math.sin(rx), Math.cos(rx)}
        });

        Matrix3 Ry = new Matrix3(new double[][]{
                {Math.cos(ry),0,Math.sin(ry)},
                {0,1,0},
                {-Math.sin(ry),0,Math.cos(ry)}
        });

        return Ry.multiply(Rx);
    }
}
