package sample;

public class TransformService {

    // identity
    public static Matrix3 identity() {
        return new Matrix3(new double[][]{
                {1,0,0},
                {0,1,0},
                {0,0,1}
        });
    }

    // scale 3D (3×3 version)
    public static Matrix3 scale(double sx, double sy, double sz) {
        return new Matrix3(new double[][]{
                {sx, 0 , 0},
                {0 , sy, 0},
                {0 , 0 , sz}
        });
    }

    // rotate X
    public static Matrix3 rotateX(double deg) {
        double rad = Math.toRadians(deg);
        double c = Math.cos(rad);
        double s = Math.sin(rad);

        return new Matrix3(new double[][]{
                {1, 0, 0},
                {0, c,-s},
                {0, s, c}
        });
    }

    // rotate Y
    public static Matrix3 rotateY(double deg) {
        double rad = Math.toRadians(deg);
        double c = Math.cos(rad);
        double s = Math.sin(rad);

        return new Matrix3(new double[][]{
                { c, 0, s},
                { 0, 1, 0},
                {-s, 0, c}
        });
    }

    // rotate Z
    public static Matrix3 rotateZ(double deg) {
        double rad = Math.toRadians(deg);
        double c = Math.cos(rad);
        double s = Math.sin(rad);

        return new Matrix3(new double[][]{
                {c,-s,0},
                {s, c,0},
                {0, 0,1}
        });
    }

    // shear (full 6-component)
    public static Matrix3 shear(double xy, double xz, double yx, double yz, double zx, double zy) {
        return new Matrix3(new double[][]{
                {1,  xy, xz},
                {yx, 1,  yz},
                {zx, zy, 1 }
        });
    }

    // reflect
    public static Matrix3 reflect(boolean x, boolean y, boolean z) {
        return new Matrix3(new double[][]{
                {x ? -1 : 1, 0, 0},
                {0, y ? -1 : 1, 0},
                {0, 0, z ? -1 : 1}
        });
    }

    // multiply
    public static Matrix3 multiply(Matrix3 A, Matrix3 B) {

        double[][] r = new double[3][3];

        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                r[i][j] = A.m[i][0]*B.m[0][j] +
                        A.m[i][1]*B.m[1][j] +
                        A.m[i][2]*B.m[2][j];
            }
        }
        return new Matrix3(r);
    }
}
