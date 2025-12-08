package sample;

import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ScrollPane;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.KeyValue;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.util.Duration;
import javafx.animation.Interpolator;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Slider;
import javafx.scene.control.CheckBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Controller {

    @FXML private Canvas canvas;

    @FXML private VBox sidebarContent;
    @FXML private StackPane sidebarContainer;
    @FXML private Button btnToggleSidebar;

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

    // Matrix display and trace
    @FXML private TextArea matrixArea;
    @FXML private TextArea traceArea;

    // Position label
    @FXML private Label labelPosition;


    private boolean sidebarVisible = true;

    // Core objects
    private Camera3D camera;
    private Renderer3D renderer;
    private Object3D baseObject;
    private Object3D currentObject;
    private final ExecutionTrace trace = new ExecutionTrace();

    // Undo / Redo: snapshots of vertices
    private final Stack<List<Vector3>> undoStack = new Stack<>();
    private final Stack<List<Vector3>> redoStack = new Stack<>();

    // Animation
    private Timeline currentAnimation = null;
    private final double ANIM_DURATION_MS = 600.0; // ms

    // Arcball camera rotation (quaternion -> matrix)
    private Quaternion cameraRotation = new Quaternion(1,0,0,0);
    private Matrix3 cameraMatrix = new Matrix3(); // identity

    // Arcball helpers
    private double startX, startY;
    private boolean dragging = false;

    @FXML
    public void initialize() {
        // initialize core
        camera = new Camera3D();
        renderer = new Renderer3D(camera, canvas);

        baseObject = Object3D.createAxisBox(120, 10); // default object
        currentObject = baseObject.copy();

        // bind sliders -> labels
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

        // buttons
        btnApply.setOnAction(e -> applyWithAnimation());
        btnReset.setOnAction(e -> resetAll());
        btnUndo.setOnAction(e -> performUndo());
        btnRedo.setOnAction(e -> performRedo());

        // install live preview for matrix & trace
        installLivePreview();

        // arcball mouse handlers
        installArcballHandlers();

        updateUndoRedoButtons();
        render();

        btnToggleSidebar.setOnAction(e -> toggleSidebar());

        // initial matrix & trace display
        displayMatrix(buildTransformMatrix());
        updateTracePanel();
    }

    private void toggleSidebar() {
        double targetWidth = sidebarVisible ? 0 : 350;

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(sidebarContainer.prefWidthProperty(), targetWidth, Interpolator.EASE_BOTH);
        KeyFrame kf = new KeyFrame(Duration.millis(280), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();

        // Fade
        Timeline fade = new Timeline();
        KeyValue fval = new KeyValue(sidebarContent.opacityProperty(), sidebarVisible ? 0 : 1, Interpolator.EASE_IN);
        fade.getKeyFrames().add(new KeyFrame(Duration.millis(240), fval));
        fade.play();

        sidebarVisible = !sidebarVisible;

        // Update icon
        btnToggleSidebar.setText(sidebarVisible ? "≡" : "▶");
    }

    // ---------- Bind helpers ----------
    private void bindSlider(Slider s, Label l, String fmt) {
        if (s == null || l == null) return;
        l.setText(String.format(fmt, s.getValue()));
        s.valueProperty().addListener((obs, oldV, newV) -> l.setText(String.format(fmt, newV.doubleValue())));
    }

    // ---------- Arcball mapping ----------
    private double[] mapToSphere(double x, double y) {
        double w = canvas.getWidth();
        double h = canvas.getHeight();

        double nx = (2.0 * x - w) / w;
        double ny = (h - 2.0 * y) / h;

        double length = nx*nx + ny*ny;
        double z;

        if (length > 1.0) {
            double norm = Math.sqrt(length);
            nx /= norm;
            ny /= norm;
            z = 0;
        } else {
            z = Math.sqrt(Math.max(0.0, 1.0 - length));
        }

        return new double[]{nx, ny, z};
    }

    private void installArcballHandlers() {
        canvas.setOnMousePressed(e -> {
            if (e.isPrimaryButtonDown()) {
                dragging = true;
                startX = e.getX();
                startY = e.getY();
            }
        });

        canvas.setOnMouseReleased(e -> {
            dragging = false;
        });

        canvas.setOnMouseDragged(e -> {
            if (!dragging) return;

            double curX = e.getX();
            double curY = e.getY();

            double[] p1 = mapToSphere(startX, startY);
            double[] p2 = mapToSphere(curX, curY);

            // cross product = rotation axis
            double ax = p1[1]*p2[2] - p1[2]*p2[1];
            double ay = p1[2]*p2[0] - p1[0]*p2[2];
            double az = p1[0]*p2[1] - p1[1]*p2[0];

            double dot = p1[0]*p2[0] + p1[1]*p2[1] + p1[2]*p2[2];
            dot = Math.max(-1.0, Math.min(1.0, dot));
            double angle = Math.acos(dot);

            // ignore extremely small motions
            if (Math.abs(angle) > 1e-4) {
                Quaternion q = Quaternion.fromAxisAngle(ax, ay, az, angle);
                cameraRotation = q.multiply(cameraRotation);
                cameraMatrix = cameraRotation.toMatrix3();
            }

            // update start for incremental rotation
            startX = curX;
            startY = curY;

            render();
        });
    }

    // ---------- Render ----------
// ---------- Render ----------
    private void render() {
        // update camera rotation & zoom (jika renderer punya setter)
        renderer.setCameraRotation(cameraMatrix);
        renderer.setZoom(camera.zoom);

        // draw
        renderer.clearAndDrawGrid();
        renderer.drawObject(currentObject);

        // update UI
        updatePositionLabel();
    }

    // ---------- Snapshot helpers ----------
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
        final int MAX = 100;
        while (undoStack.size() > MAX) undoStack.remove(0);
        redoStack.clear();
        updateUndoRedoButtons();
    }

    private void updateUndoRedoButtons() {
        if (btnUndo != null) btnUndo.setDisable(undoStack.isEmpty());
        if (btnRedo != null) btnRedo.setDisable(redoStack.isEmpty());
    }

    // ---------- Build transform matrix (3x3 linear, no translation) ----------
    private Matrix3 buildTransformMatrix() {
        Matrix3 M = new Matrix3(); // identity

        if (cbScale != null && cbScale.isSelected()) {
            double sx = sliderSx.getValue();
            double sy = sliderSy.getValue();
            double sz = sliderSz.getValue();
            Matrix3 S = TransformService.scale(sx, sy, sz);
            M = S.multiply(M);
        }

        if (cbRotate != null && cbRotate.isSelected()) {
            double rx = sliderRx.getValue();
            double ry = sliderRy.getValue();
            double rz = sliderRz.getValue();
            Matrix3 Rx = TransformService.rotateX(rx);
            Matrix3 Ry = TransformService.rotateY(ry);
            Matrix3 Rz = TransformService.rotateZ(rz);
            // Combined rotation (Rz * Ry * Rx) applied before current M
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

    // ---------- Display matrix prettily ----------
    private void displayMatrix(Matrix3 M) {
        if (matrixArea == null) return;
        double[][] a = M.get();
        StringBuilder sb = new StringBuilder();
        sb.append("Transform Matrix (row-major):\n\n");
        for (int i=0;i<3;i++) {
            sb.append(String.format("[ %8.4f  %8.4f  %8.4f ]\n", a[i][0], a[i][1], a[i][2]));
        }
        sb.append("\nNotes:\n");
        sb.append("- Matrix is 3x3 linear transform (no translation).\n");
        sb.append("- Order: Scale -> Rotate (X,Y,Z) -> Shear -> Reflect.\n");
        matrixArea.setText(sb.toString());
    }

    // ---------- Live preview listeners ----------
    private void installLivePreview() {
        Runnable upd = () -> {
            Matrix3 M = buildTransformMatrix();
            displayMatrix(M);
            // update trace preview (not final until Apply)
            trace.clear();
            // Build trace preview (same order as buildTransformMatrix)
            if (cbScale != null && cbScale.isSelected()) {
                trace.addStep(String.format("Scale(sx=%.2f, sy=%.2f, sz=%.2f)",
                                sliderSx.getValue(), sliderSy.getValue(), sliderSz.getValue()),
                        TransformService.scale(sliderSx.getValue(), sliderSy.getValue(), sliderSz.getValue()));
            }
            if (cbRotate != null && cbRotate.isSelected()) {
                if (Math.abs(sliderRx.getValue()) > 1e-6)
                    trace.addStep(String.format("RotateX(%.1f°)", sliderRx.getValue()),
                            TransformService.rotateX(sliderRx.getValue()));
                if (Math.abs(sliderRy.getValue()) > 1e-6)
                    trace.addStep(String.format("RotateY(%.1f°)", sliderRy.getValue()),
                            TransformService.rotateY(sliderRy.getValue()));
                if (Math.abs(sliderRz.getValue()) > 1e-6)
                    trace.addStep(String.format("RotateZ(%.1f°)", sliderRz.getValue()),
                            TransformService.rotateZ(sliderRz.getValue()));
            }
            if (cbShear != null && cbShear.isSelected()) {
                trace.addStep(String.format("Shear(%.2f,%.2f,%.2f,%.2f,%.2f,%.2f)",
                                sliderShXY.getValue(), sliderShXZ.getValue(), sliderShYX.getValue(),
                                sliderShYZ.getValue(), sliderShZX.getValue(), sliderShZY.getValue()),
                        TransformService.shear(sliderShXY.getValue(), sliderShXZ.getValue(),
                                sliderShYX.getValue(), sliderShYZ.getValue(), sliderShZX.getValue(), sliderShZY.getValue()));
            }
            if (cbReflect != null && cbReflect.isSelected()) {
                trace.addStep(String.format("Reflect(X=%b, Y=%b, Z=%b)",
                                cbRefX.isSelected(), cbRefY.isSelected(), cbRefZ.isSelected()),
                        TransformService.reflect(cbRefX.isSelected(), cbRefY.isSelected(), cbRefZ.isSelected()));
            }

            updateTracePanel();
        };

        // attach to sliders
        Slider[] sliders = {
                sliderSx, sliderSy, sliderSz,
                sliderRx, sliderRy, sliderRz,
                sliderShXY, sliderShXZ, sliderShYX, sliderShYZ, sliderShZX, sliderShZY
        };
        for (Slider s : sliders) if (s != null) s.valueProperty().addListener((o,oldV,newV) -> upd.run());

        // checkboxes
        CheckBox[] boxes = { cbScale, cbRotate, cbShear, cbReflect, cbRefX, cbRefY, cbRefZ };
        for (CheckBox cb : boxes) if (cb != null) cb.selectedProperty().addListener((o,oldV,newV) -> upd.run());
    }

    // ---------- Apply transform with animation ----------
    private void applyWithAnimation() {
        if (currentAnimation != null) {
            currentAnimation.stop();
            currentAnimation = null;
        }

        pushUndoSnapshot();

        Matrix3 M = buildTransformMatrix();
        displayMatrix(M); // explicit final display

        // build target vertices by applying M to current object's vertices
        List<Vector3> target = new ArrayList<>();
        for (Vector3 v : currentObject.vertices) {
            Vector3 tv = M.transform(v);
            target.add(tv);
        }

        // update ExecutionTrace (final)
        trace.clear();
        // replicate same trace steps as in live preview (important: same order)
        if (cbScale.isSelected())
            trace.addStep(String.format("Scale(sx=%.2f, sy=%.2f, sz=%.2f)",
                            sliderSx.getValue(), sliderSy.getValue(), sliderSz.getValue()),
                    TransformService.scale(sliderSx.getValue(), sliderSy.getValue(), sliderSz.getValue()));

        if (cbRotate.isSelected()) {
            if (Math.abs(sliderRx.getValue()) > 1e-6)
                trace.addStep(String.format("RotateX(%.1f°)", sliderRx.getValue()),
                        TransformService.rotateX(sliderRx.getValue()));
            if (Math.abs(sliderRy.getValue()) > 1e-6)
                trace.addStep(String.format("RotateY(%.1f°)", sliderRy.getValue()),
                        TransformService.rotateY(sliderRy.getValue()));
            if (Math.abs(sliderRz.getValue()) > 1e-6)
                trace.addStep(String.format("RotateZ(%.1f°)", sliderRz.getValue()),
                        TransformService.rotateZ(sliderRz.getValue()));
        }

        if (cbShear.isSelected())
            trace.addStep(String.format("Shear(%.2f,%.2f,%.2f,%.2f,%.2f,%.2f)",
                            sliderShXY.getValue(), sliderShXZ.getValue(), sliderShYX.getValue(),
                            sliderShYZ.getValue(), sliderShZX.getValue(), sliderShZY.getValue()),
                    TransformService.shear(sliderShXY.getValue(), sliderShXZ.getValue(),
                            sliderShYX.getValue(), sliderShYZ.getValue(), sliderShZX.getValue(), sliderShZY.getValue()));

        if (cbReflect.isSelected())
            trace.addStep(String.format("Reflect(X=%b, Y=%b, Z=%b)", cbRefX.isSelected(), cbRefY.isSelected(), cbRefZ.isSelected()),
                    TransformService.reflect(cbRefX.isSelected(), cbRefY.isSelected(), cbRefZ.isSelected()));

        updateTracePanel();

        // animate
        animateVerticesTo(target, ANIM_DURATION_MS);
    }

    // ---------- Animation helper ----------
    private void animateVerticesTo(List<Vector3> targetVertices, double durationMs) {
        final int steps = 40;
        final double frameMs = durationMs / steps;

        List<Vector3> start = copyVertices(currentObject.vertices);

        if (start.size() != targetVertices.size()) {
            restoreVertices(currentObject.vertices, targetVertices);
            render();
            return;
        }

        final double[][] deltas = new double[start.size()][3];
        for (int i = 0; i < start.size(); i++) {
            deltas[i][0] = (targetVertices.get(i).x - start.get(i).x) / steps;
            deltas[i][1] = (targetVertices.get(i).y - start.get(i).y) / steps;
            deltas[i][2] = (targetVertices.get(i).z - start.get(i).z) / steps;
        }

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

        tl.setOnFinished(ev -> {
            restoreVertices(currentObject.vertices, targetVertices);
            currentAnimation = null;
            updateUndoRedoButtons();
            render();
        });

        currentAnimation = tl;
        tl.play();
    }

    // ---------- Undo / Redo ----------
    private void performUndo() {
        if (currentAnimation != null) {
            currentAnimation.stop();
            currentAnimation = null;
        }
        if (undoStack.isEmpty()) return;

        redoStack.push(copyVertices(currentObject.vertices));
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

        undoStack.push(copyVertices(currentObject.vertices));
        List<Vector3> next = redoStack.pop();
        animateVerticesTo(next, ANIM_DURATION_MS);
        updateUndoRedoButtons();
    }

    // ---------- Reset ----------
    private void resetAll() {
        if (currentAnimation != null) {
            currentAnimation.stop();
            currentAnimation = null;
        }
        pushUndoSnapshot();
        currentObject = baseObject.copy();
        render();
        updateUndoRedoButtons();
    }

    // ---------- Trace / Matrix UI ----------
    private void updateTracePanel() {
        if (traceArea == null) return;
        traceArea.setText(trace.buildText());
    }

    // ---------- Position display ----------
    private void updatePositionLabel() {
        if (labelPosition == null) return;
        double cx = 0, cy = 0, cz = 0;
        for (Vector3 v : currentObject.vertices) {
            cx += v.x; cy += v.y; cz += v.z;
        }
        int n = currentObject.vertices.size();
        if (n == 0) return;
        cx /= n; cy /= n; cz /= n;
        labelPosition.setText(String.format("Center: (%.2f, %.2f, %.2f)", cx, cy, cz));
    }

}