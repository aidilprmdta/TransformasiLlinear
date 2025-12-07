package sample;

public class TransformOperation {

    public enum Type { SCALE, ROTATE, SHEAR, REFLECT }

    public Type type;
    public double a,b,c,d,e,f; // parameters

    private TransformOperation(Type type, double... params){
        this.type = type;

        if (params.length > 0) a = params[0];
        if (params.length > 1) b = params[1];
        if (params.length > 2) c = params[2];
        if (params.length > 3) d = params[3];
        if (params.length > 4) e = params[4];
        if (params.length > 5) f = params[5];
    }

    public static TransformOperation scale(double sx, double sy, double sz){
        return new TransformOperation(Type.SCALE, sx, sy, sz);
    }

    public static TransformOperation rotate(double rx, double ry, double rz){
        return new TransformOperation(Type.ROTATE, rx, ry, rz);
    }

    public static TransformOperation shear(
            double shXY, double shXZ,
            double shYX, double shYZ,
            double shZX, double shZY
    ){
        return new TransformOperation(Type.SHEAR,
                shXY, shXZ, shYX, shYZ, shZX, shZY
        );
    }

    public static TransformOperation reflect(boolean x, boolean y, boolean z){
        return new TransformOperation(Type.REFLECT,
                x?1:0, y?1:0, z?1:0
        );
    }

    public Matrix3 toMatrix(){

        switch(type){

            case SCALE:
                return new Matrix3(new double[][]{
                        {a, 0, 0},
                        {0, b, 0},
                        {0, 0, c}
                });

            case ROTATE:
                double rx = Math.toRadians(a);
                double ry = Math.toRadians(b);
                double rz = Math.toRadians(c);

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

                Matrix3 Rz = new Matrix3(new double[][]{
                        {Math.cos(rz), -Math.sin(rz), 0},
                        {Math.sin(rz), Math.cos(rz), 0},
                        {0,0,1}
                });

                return Rz.multiply(Ry).multiply(Rx);

            case SHEAR:
                return new Matrix3(new double[][]{
                        {1,   a,   b},
                        {c,   1,   d},
                        {e,   f,   1}
                });

            case REFLECT:
                return new Matrix3(new double[][]{
                        { (a==1 ? -1 : 1), 0, 0 },
                        { 0, (b==1 ? -1 : 1), 0 },
                        { 0, 0, (c==1 ? -1 : 1) }
                });

            default:
                return Matrix3.identity();
        }
    }
}
