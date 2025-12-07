//package sample;
//
//public class EigenSolver {
//
//    private static final int MAX_ITERS = 50;
//    private static final double EPS = 1e-10;
//
//    public static class Result {
//        public double[] eigenvalues = new double[3];
//        public Vector3[] eigenvectors = new Vector3[3];
//    }
//
//    public static Result solve(Matrix3 M) {
//
//        double[][] A = new double[3][3];
//        // copy matrix
//        for (int i=0;i<3;i++)
//            for (int j=0;j<3;j++)
//                A[i][j] = M.m[i][j];
//
//        double[][] V = new double[][] {
//                {1,0,0},
//                {0,1,0},
//                {0,0,1}
//        };
//
//        for (int iter = 0; iter < MAX_ITERS; iter++) {
//            // find largest off-diagonal value
//            int p = 0, q = 1;
//            double max = Math.abs(A[p][q]);
//
//            for (int i=0;i<3;i++){
//                for (int j=i+1;j<3;j++){
//                    if (Math.abs(A[i][j]) > max){
//                        max = Math.abs(A[i][j]);
//                        p = i;
//                        q = j;
//                    }
//                }
//            }
//
//            if (max < EPS) break;
//
//            double theta = 0.5 * Math.atan2(2 * A[p][q], A[q][q] - A[p][p]);
//            double c = Math.cos(theta);
//            double s = Math.sin(theta);
//
//            // rotate A
//            double app = c*c * A[p][p] - 2*s*c*A[p][q] + s*s*A[q][q];
//            double aqq = s*s * A[p][p] + 2*s*c*A[p][q] + c*c*A[q][q];
//            A[p][p] = app;
//            A[q][q] = aqq;
//            A[p][q] = 0;
//            A[q][p] = 0;
//
//            for (int k=0;k<3;k++){
//                if (k != p && k != q){
//                    double aik = c*A[p][k] - s*A[q][k];
//                    double aqk = s*A[p][k] + c*A[q][k];
//                    A[p][k] = A[k][p] = aik;
//                    A[q][k] = A[k][q] = aqk;
//                }
//            }
//
//            // rotate eigenvectors
//            for (int k=0;k<3;k++){
//                double vip = c*V[k][p] - s*V[k][q];
//                double viq = s*V[k][p] + c*V[k][q];
//                V[k][p] = vip;
//                V[k][q] = viq;
//            }
//        }
//
//        Result result = new Result();
//        result.eigenvalues[0] = A[0][0];
//        result.eigenvalues[1] = A[1][1];
//        result.eigenvalues[2] = A[2][2];
//
//        result.eigenvectors[0] = new Vector3(V[0][0], V[1][0], V[2][0]);
//        result.eigenvectors[1] = new Vector3(V[0][1], V[1][1], V[2][1]);
//        result.eigenvectors[2] = new Vector3(V[0][2], V[1][2], V[2][2]);
//
//        return result;
//    }
//}
