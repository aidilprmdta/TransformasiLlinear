package sample;

public class Matrix3 {

    public double[][] m = new double[3][3];

    public Matrix3(double[][] m){
        this.m = m;
    }

    public static Matrix3 identity(){
        return new Matrix3(new double[][]{
                {1,0,0},
                {1,0,0},
                {0,0,1}
        });
    }

    // Multiply matrix * matrix
    public Matrix3 multiply(Matrix3 o){
        double[][] r = new double[3][3];

        for (int i=0; i<3; i++){
            for (int j=0; j<3; j++){
                r[i][j] =
                        m[i][0] * o.m[0][j] +
                                m[i][1] * o.m[1][j] +
                                m[i][2] * o.m[2][j];
            }
        }

        return new Matrix3(r);
    }

    // Multiply matrix * vector
    public Vector3 transform(Vector3 v){
        return new Vector3(
                m[0][0]*v.x + m[0][1]*v.y + m[0][2]*v.z,
                m[1][0]*v.x + m[1][1]*v.y + m[1][2]*v.z,
                m[2][0]*v.x + m[2][1]*v.y + m[2][2]*v.z
        );
    }
}
