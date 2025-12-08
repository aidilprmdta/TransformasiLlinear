package sample;

import java.util.ArrayList;
import java.util.List;

public class ExecutionTrace {

    private final List<Step> steps = new ArrayList<>();
    private Matrix3 finalMatrix = Matrix3.identity();

    public void clear() {
        steps.clear();
        finalMatrix = Matrix3.identity();
    }

    public void addStep(String label, Matrix3 M) {
        steps.add(new Step(label, M));
        finalMatrix = M.multiply(finalMatrix); // komposisi
    }

    public Matrix3 getFinalMatrix() {
        return finalMatrix;
    }

    public String buildText() {
        StringBuilder sb = new StringBuilder();

        sb.append("Execution Trace:\n\n");

        for (int i = 0; i < steps.size(); i++) {
            Step s = steps.get(i);

            sb.append(String.format("%d. %s\n", i + 1, s.description));

            if (s.matrix != null) {
                double[][] m = s.matrix.get();
                for (int r = 0; r < 3; r++) {
                    sb.append(String.format(
                            "   [%.4f %.4f %.4f]\n",
                            m[r][0], m[r][1], m[r][2]
                    ));
                }
            }
            sb.append("\n");
        }

        sb.append("Final Combined Matrix:\n");
        if (finalMatrix != null) {
            double[][] f = finalMatrix.get();
            for (int r = 0; r < 3; r++) {
                sb.append(String.format(
                        "[%.4f %.4f %.4f]\n",
                        f[r][0], f[r][1], f[r][2]
                ));
            }
        } else {
            sb.append("(No final matrix yet)\n");
        }

        return sb.toString();
    }
}
