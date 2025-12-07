package sample;

public class Vector3 {
    public double x, y, z;

    public Vector3(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 cloneV(){
        return new Vector3(x, y, z);
    }

    public String toString(){
        return String.format("(%.2f, %.2f, %.2f)", x, y, z);
    }
}
