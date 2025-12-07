package sample;

public class TransformService {

    public static Matrix3 scale(double sx, double sy, double sz) {
        return new Matrix3(new double[][]{
                {sx, 0, 0},
                {0, sy, 0},
                {0, 0, sz}
        });
    }

    public static Matrix3 rotateX(double deg) {
        double r = Math.toRadians(deg);
        double c = Math.cos(r), s = Math.sin(r);
        return new Matrix3(new double[][]{
                {1, 0, 0},
                {0, c, -s},
                {0, s, c}
        });
    }

    public static Matrix3 rotateY(double deg) {
        double r = Math.toRadians(deg);
        double c = Math.cos(r), s = Math.sin(r);
        return new Matrix3(new double[][]{
                {c, 0, s},
                {0, 1, 0},
                {-s, 0, c}
        });
    }

    public static Matrix3 rotateZ(double deg) {
        double r = Math.toRadians(deg);
        double c = Math.cos(r), s = Math.sin(r);
        return new Matrix3(new double[][]{
                {c, -s, 0},
                {s,  c, 0},
                {0,  0, 1}
        });
    }

    // shear: matrix with 6 free off-diagonal shear coefficients
    // ordering: shXY, shXZ, shYX, shYZ, shZX, shZY
    public static Matrix3 shear(double shXY, double shXZ, double shYX, double shYZ, double shZX, double shZY) {
        return new Matrix3(new double[][]{
                {1,   shXY, shXZ},
                {shYX, 1,   shYZ},
                {shZX, shZY, 1}
        });
    }

    public static Matrix3 reflect(boolean rx, boolean ry, boolean rz) {
        return new Matrix3(new double[][]{
                { rx ? -1 : 1, 0, 0},
                { 0, ry ? -1 : 1, 0},
                { 0, 0, rz ? -1 : 1}
        });
    }
}
