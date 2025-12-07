package sample;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Controller {

    @FXML private Canvas canvas;

    // SCALE
    @FXML private CheckBox cbScale;
    @FXML private Slider sliderSx, sliderSy, sliderSz;
    @FXML private Label labelSx, labelSy, labelSz;

    // ROTATE
    @FXML private CheckBox cbRotate;
    @FXML private Slider sliderRx, sliderRy, sliderRz;
    @FXML private Label labelRx, labelRy, labelRz;

    // SHEAR
    @FXML private CheckBox cbShear;
    @FXML private Slider sliderShXY, sliderShXZ, sliderShYX, sliderShYZ, sliderShZX, sliderShZY;
    @FXML private Label labelShXY, labelShXZ, labelShYX, labelShYZ, labelShZX, labelShZY;

    // REFLECT
    @FXML private CheckBox cbReflect, cbRefX, cbRefY, cbRefZ;

    // BUTTONS - HAPUS atau COMMENT btnUndo dan btnRedo jika tidak ada di FXML
    @FXML private Button btnApply, btnReset;
    // @FXML private Button btnUndo, btnRedo; // HAPUS JIKA TIDAK ADA DI FXML

    private Camera3D camera;
    private Renderer3D renderer;

    private Object3D baseObject;      // initial model
    private Object3D currentObject;   // transformed model

    private final Stack<List<Vector3>> undoStack = new Stack<>();
    private final Stack<List<Vector3>> redoStack = new Stack<>();

    // ---------------- INIT -------------------
    @FXML
    public void initialize() {
        System.out.println("Initializing Controller...");

        camera = new Camera3D();
        renderer = new Renderer3D(camera, canvas);

        baseObject = Object3D.createAxisBox(100, 10);
        currentObject = baseObject.copy();

        // Setup sliders
        setupSlider(sliderSx, labelSx);
        setupSlider(sliderSy, labelSy);
        setupSlider(sliderSz, labelSz);

        setupSlider(sliderRx, labelRx, true);
        setupSlider(sliderRy, labelRy, true);
        setupSlider(sliderRz, labelRz, true);

        setupSlider(sliderShXY, labelShXY);
        setupSlider(sliderShXZ, labelShXZ);
        setupSlider(sliderShYX, labelShYX);
        setupSlider(sliderShYZ, labelShYZ);
        setupSlider(sliderShZX, labelShZX);
        setupSlider(sliderShZY, labelShZY);

        // Setup buttons - hanya yang ada di FXML
        if (btnApply != null) {
            btnApply.setOnAction(e -> applyTransform());
        } else {
            System.err.println("WARNING: btnApply is null!");
        }

        if (btnReset != null) {
            btnReset.setOnAction(e -> resetAll());
        } else {
            System.err.println("WARNING: btnReset is null!");
        }

        render();
    }

    // auto-update label
    private void setupSlider(Slider s, Label l) {
        if (s != null && l != null) {
            s.valueProperty().addListener((obs, oldV, newV) -> {
                l.setText(String.format("%.2f", newV.doubleValue()));
            });
        }
    }

    // for angles
    private void setupSlider(Slider s, Label l, boolean angle) {
        if (s != null && l != null) {
            s.valueProperty().addListener((obs, oldV, newV) -> {
                l.setText(String.format("%.1f°", newV.doubleValue()));
            });
        }
    }

    // ---------------- APPLY TRANSFORM -------------------
    private void applyTransform() {
        undoStack.push(copyVertices(currentObject.vertices));
        redoStack.clear();

        List<TransformOperation> ops = new ArrayList<>();

        double sx = sliderSx.getValue();
        double sy = sliderSy.getValue();
        double sz = sliderSz.getValue();

        double rx = sliderRx.getValue();
        double ry = sliderRy.getValue();
        double rz = sliderRz.getValue();

        double shXY = sliderShXY.getValue();
        double shXZ = sliderShXZ.getValue();
        double shYX = sliderShYX.getValue();
        double shYZ = sliderShYZ.getValue();
        double shZX = sliderShZX.getValue();
        double shZY = sliderShZY.getValue();

        // SCALE
        if (cbScale.isSelected())
            ops.add(TransformOperation.scale(sx, sy, sz));

        // ROTATE
        if (cbRotate.isSelected())
            ops.add(TransformOperation.rotate(rx, ry, rz));

        // SHEAR
        if (cbShear.isSelected())
            ops.add(TransformOperation.shear(shXY, shXZ, shYX, shYZ, shZX, shZY));

        // REFLECT
        if (cbReflect.isSelected())
            ops.add(TransformOperation.reflect(cbRefX.isSelected(), cbRefY.isSelected(), cbRefZ.isSelected()));

        Matrix3 M = Matrix3.identity();
        for (TransformOperation op : ops) {
            M = op.toMatrix().multiply(M);
        }

        for (int i = 0; i < currentObject.vertices.size(); i++) {
            currentObject.vertices.set(i, M.transform(currentObject.vertices.get(i)));
        }

        render();
    }

    // ---------------- RESET -------------------
    private void resetAll() {
        currentObject = baseObject.copy();
        render();
    }

    // ---------------- UNDO & REDO -------------------
    // HAPUS atau COMMENT jika button tidak ada di FXML
    /*
    private void undo() {
        if (undoStack.isEmpty()) return;
        redoStack.push(copyVertices(currentObject.vertices));
        restoreVertices(currentObject.vertices, undoStack.pop());
        render();
    }

    private void redo() {
        if (redoStack.isEmpty()) return;
        undoStack.push(copyVertices(currentObject.vertices));
        restoreVertices(currentObject.vertices, redoStack.pop());
        render();
    }
    */

    // ---------------- UTIL -------------------
    private List<Vector3> copyVertices(List<Vector3> src) {
        List<Vector3> r = new ArrayList<>();
        for (Vector3 v : src) r.add(v.cloneV());
        return r;
    }

    private void restoreVertices(List<Vector3> target, List<Vector3> src) {
        target.clear();
        for (Vector3 v : src) target.add(v.cloneV());
    }

    private void render() {
        renderer.clearAndDrawGrid();
        renderer.drawObject(currentObject);
    }
}