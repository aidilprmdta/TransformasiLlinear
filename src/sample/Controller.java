package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.util.Duration;

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

    // Buttons
    @FXML private Button btnApply, btnReset, btnUndo, btnRedo;

    // Core
    private Camera3D camera;
    private Renderer3D renderer;
    private Object3D baseObject;
    private Object3D currentObject;

    // Undo / Redo: store snapshots of vertices (deep copy)
    private final Stack<List<Vector3>> undoStack = new Stack<>();
    private final Stack<List<Vector3>> redoStack = new Stack<>();

    // Animation
    private Timeline currentAnimation = null;
    private final double ANIM_DURATION_MS = 600.0; // animation duration

    @FXML
    public void initialize() {
        camera = new Camera3D();
        renderer = new Renderer3D(camera, canvas);

        baseObject = Object3D.createAxisBox(100, 10);
        currentObject = baseObject.copy();

        // Bind sliders -> labels
        bindSlider(sliderSx, labelSx, "%.2f");
        bindSlider(sliderSy, labelSy, "%.2f");
        bindSlider(sliderSz, labelSz, "%.2f");

        bindSlider(sliderRx, labelRx, "%.1f°");
        bindSlider(sliderRy, labelRy, "%.1f°");
        bindSlider(sliderRz, labelRz, "%.1f°");

        bindSlider(sliderShXY, labelShXY, "%.2f");
        bindSlider(sliderShXZ, labelShXZ, "%.2f");
        bindSlider(sliderShYX, labelShYX, "%.2f");
        bindSlider(sliderShYZ, labelShYZ, "%.2f");
        bindSlider(sliderShZX, labelShZX, "%.2f");
        bindSlider(sliderShZY, labelShZY, "%.2f");

        // Buttons
        btnApply.setOnAction(e -> applyWithAnimation());
        btnReset.setOnAction(e -> resetAll());
        btnUndo.setOnAction(e -> performUndo());
        btnRedo.setOnAction(e -> performRedo());

        updateUndoRedoButtons();
        render();
    }

    private void bindSlider(Slider s, Label l, String fmt) {
        l.setText(String.format(fmt, s.getValue()));
        s.valueProperty().addListener((obs, oldV, newV) -> l.setText(String.format(fmt, newV.doubleValue())));
    }

    private void render() {
        renderer.clearAndDrawGrid();
        renderer.drawObject(currentObject);
    }

    // ---------------- Snapshot helpers ----------------
    private List<Vector3> copyVertices(List<Vector3> src) {
        List<Vector3> r = new ArrayList<>();
        for (Vector3 v : src) r.add(v.cloneV());
        return r;
    }

    private void restoreVertices(List<Vector3> target, List<Vector3> src) {
        target.clear();
        for (Vector3 v : src) target.add(v.cloneV());
    }

    private void pushUndoSnapshot() {
        undoStack.push(copyVertices(currentObject.vertices));
        // limit undo history optionally (e.g., 50)
        final int MAX = 100;
        while (undoStack.size() > MAX) undoStack.remove(0);
        // clear redo on new action
        redoStack.clear();
        updateUndoRedoButtons();
    }

    private void updateUndoRedoButtons() {
        btnUndo.setDisable(undoStack.isEmpty());
        btnRedo.setDisable(redoStack.isEmpty());
    }

    // ---------------- Build transform matrix ----------------
    private Matrix3 buildTransformMatrix() {
        Matrix3 M = Matrix3.identity();

        if (cbScale != null && cbScale.isSelected()) {
            double sx = sliderSx.getValue();
            double sy = sliderSy.getValue();
            double sz = sliderSz.getValue();
            M = TransformService.scale(sx, sy, sz).multiply(M);
        }

        if (cbRotate != null && cbRotate.isSelected()) {
            double rx = sliderRx.getValue();
            double ry = sliderRy.getValue();
            double rz = sliderRz.getValue();
            Matrix3 Rx = TransformService.rotateX(rx);
            Matrix3 Ry = TransformService.rotateY(ry);
            Matrix3 Rz = TransformService.rotateZ(rz);
            // apply Rz * Ry * Rx
            M = Rz.multiply(Ry).multiply(Rx).multiply(M);
        }

        if (cbShear != null && cbShear.isSelected()) {
            double shXY = sliderShXY.getValue();
            double shXZ = sliderShXZ.getValue();
            double shYX = sliderShYX.getValue();
            double shYZ = sliderShYZ.getValue();
            double shZX = sliderShZX.getValue();
            double shZY = sliderShZY.getValue();
            Matrix3 H = TransformService.shear(shXY, shXZ, shYX, shYZ, shZX, shZY);
            M = H.multiply(M);
        }

        if (cbReflect != null && cbReflect.isSelected()) {
            Matrix3 R = TransformService.reflect(cbRefX.isSelected(), cbRefY.isSelected(), cbRefZ.isSelected());
            M = R.multiply(M);
        }

        return M;
    }

    // ---------------- Apply (with animation) ----------------
    private void applyWithAnimation() {
        if (currentAnimation != null) {
            currentAnimation.stop();
            currentAnimation = null;
        }

        // push undo snapshot BEFORE changing
        pushUndoSnapshot();

        // compute target vertices by applying matrix to current vertices copy
        Matrix3 M = buildTransformMatrix();
        List<Vector3> target = new ArrayList<>();
        for (Vector3 v : currentObject.vertices) {
            Vector3 tv = M.transform(v);
            target.add(tv);
        }

        // animate from current -> target
        animateVerticesTo(target, ANIM_DURATION_MS);
    }

    // ---------------- Animation helper ----------------
    private void animateVerticesTo(List<Vector3> targetVertices, double durationMs) {
        final int steps = 30; // number of frames, more => smoother
        final double frameMs = durationMs / steps;

        // capture starting positions
        List<Vector3> start = copyVertices(currentObject.vertices);

        // If sizes mismatch, abort and set instantly
        if (start.size() != targetVertices.size()) {
            restoreVertices(currentObject.vertices, targetVertices);
            render();
            return;
        }

        // build per-vertex deltas
        final double[][] deltas = new double[start.size()][3];
        for (int i = 0; i < start.size(); i++) {
            deltas[i][0] = (targetVertices.get(i).x - start.get(i).x) / steps;
            deltas[i][1] = (targetVertices.get(i).y - start.get(i).y) / steps;
            deltas[i][2] = (targetVertices.get(i).z - start.get(i).z) / steps;
        }

        // timeline animation
        Timeline tl = new Timeline();
        for (int f = 1; f <= steps; f++) {
            final int frameIndex = f;
            KeyFrame kf = new KeyFrame(Duration.millis(frameMs * f), ev -> {
                for (int i = 0; i < currentObject.vertices.size(); i++) {
                    Vector3 s = start.get(i);
                    double nx = s.x + deltas[i][0] * frameIndex;
                    double ny = s.y + deltas[i][1] * frameIndex;
                    double nz = s.z + deltas[i][2] * frameIndex;
                    currentObject.vertices.set(i, new Vector3(nx, ny, nz));
                }
                render();
            });
            tl.getKeyFrames().add(kf);
        }

        // when finished, ensure exact target and update UI state
        tl.setOnFinished(ev -> {
            restoreVertices(currentObject.vertices, targetVertices);
            currentAnimation = null;
            updateUndoRedoButtons();
            render();
        });

        currentAnimation = tl;
        tl.play();
    }

    // ---------------- Undo / Redo actions ----------------
    private void performUndo() {
        if (currentAnimation != null) {
            currentAnimation.stop();
            currentAnimation = null;
        }
        if (undoStack.isEmpty()) return;

        // push current state to redo
        redoStack.push(copyVertices(currentObject.vertices));

        // pop state from undo and animate to it
        List<Vector3> prev = undoStack.pop();
        animateVerticesTo(prev, ANIM_DURATION_MS);

        updateUndoRedoButtons();
    }

    private void performRedo() {
        if (currentAnimation != null) {
            currentAnimation.stop();
            currentAnimation = null;
        }
        if (redoStack.isEmpty()) return;

        // push current state to undo
        undoStack.push(copyVertices(currentObject.vertices));

        // pop state from redo and animate to it
        List<Vector3> next = redoStack.pop();
        animateVerticesTo(next, ANIM_DURATION_MS);

        updateUndoRedoButtons();
    }

    // ---------------- Reset ----------------
    private void resetAll() {
        if (currentAnimation != null) {
            currentAnimation.stop();
            currentAnimation = null;
        }
        pushUndoSnapshot(); // allow user to undo reset
        currentObject = baseObject.copy();
        render();
        updateUndoRedoButtons();
    }
}
