package sample;

public class Quaternion {
    public double w, x, y, z;

    public Quaternion(double w, double x, double y, double z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Quaternion fromAxisAngle(double ax, double ay, double az, double angle) {
        double half = angle / 2.0;
        double sin = Math.sin(half);
        return new Quaternion(
                Math.cos(half),
                ax * sin,
                ay * sin,
                az * sin
        );
    }

    public Quaternion multiply(Quaternion q) {
        return new Quaternion(
                w*q.w - x*q.x - y*q.y - z*q.z,
                w*q.x + x*q.w + y*q.z - z*q.y,
                w*q.y - x*q.z + y*q.w + z*q.x,
                w*q.z + x*q.y - y*q.x + z*q.w
        );
    }

    public Matrix3 toMatrix3() {
        double[][] m = new double[3][3];

        m[0][0] = 1 - 2*(y*y + z*z);
        m[0][1] = 2*(x*y - z*w);
        m[0][2] = 2*(x*z + y*w);

        m[1][0] = 2*(x*y + z*w);
        m[1][1] = 1 - 2*(x*x + z*z);
        m[1][2] = 2*(y*z - x*w);

        m[2][0] = 2*(x*z - y*w);
        m[2][1] = 2*(y*z + x*w);
        m[2][2] = 1 - 2*(x*x + y*y);

        return new Matrix3(m);
    }
}
