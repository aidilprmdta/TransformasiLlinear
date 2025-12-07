package sample;

public class Camera3D {
    public double yaw = 30;   // degrees (around Y)
    public double pitch = -20; // degrees (around X)
    public double zoom = 1.0;

    // compute world->camera rotation matrix (R = R_yaw * R_pitch)
    public Matrix3 getRotationMatrix() {
        double ry = Math.toRadians(yaw);
        double rx = Math.toRadians(pitch);

        Matrix3 Ry = new Matrix3(new double[][]{
                {Math.cos(ry), 0, Math.sin(ry)},
                {0, 1, 0},
                {-Math.sin(ry), 0, Math.cos(ry)}
        });

        Matrix3 Rx = new Matrix3(new double[][]{
                {1,0,0},
                {0, Math.cos(rx), -Math.sin(rx)},
                {0, Math.sin(rx),  Math.cos(rx)}
        });

        return Ry.multiply(Rx); // first pitch (X) then yaw (Y) in world->camera (approx)
    }
}
