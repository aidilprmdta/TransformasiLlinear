package sample;

public class Matrix3 {
    public final double[][] m;

    // identity
    public Matrix3() {
        this.m = new double[][] {
                {1,0,0},
                {0,1,0},
                {0,0,1}
        };
    }

    public Matrix3(double[][] arr) {
        if (arr.length != 3 || arr[0].length != 3) throw new IllegalArgumentException("Matrix3 must be 3x3");
        this.m = new double[3][3];
        for (int i=0;i<3;i++) for (int j=0;j<3;j++) this.m[i][j] = arr[i][j];
    }

    public static Matrix3 identity() { return new Matrix3(); }

    // M * N
    public Matrix3 multiply(Matrix3 other) {
        double[][] r = new double[3][3];
        for (int i=0;i<3;i++){
            for (int j=0;j<3;j++){
                double sum = 0;
                for (int k=0;k<3;k++) sum += this.m[i][k] * other.m[k][j];
                r[i][j] = sum;
            }
        }
        return new Matrix3(r);
    }

    // Apply matrix to vector (v' = M * v)
    public Vector3 transform(Vector3 v) {
        double nx = m[0][0]*v.x + m[0][1]*v.y + m[0][2]*v.z;
        double ny = m[1][0]*v.x + m[1][1]*v.y + m[1][2]*v.z;
        double nz = m[2][0]*v.x + m[2][1]*v.y + m[2][2]*v.z;
        return new Vector3(nx, ny, nz);
    }
}
