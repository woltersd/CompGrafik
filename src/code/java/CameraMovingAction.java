package code.java;


import javax.vecmath.Vector3f;
import java.util.LinkedList;
import java.util.List;

/**
 * @author peter
 */
public class CameraMovingAction {
    private class CameraPathPoint {
        int steps;
        Vector3f wayPoint;
        Vector3f viewDirection;
        float zAxisRotationAngle;
    }
    private List<CameraPathPoint> cameraPathPoints;

    private Camera camera;
    private int currentWayPointIndex;
    private boolean movingActionActive;

    private Vector3f currentDestinationVector;
    private float stepDistance;
    private int stepsLeft;
    private Vector3f currentRotationVector;
    private float currentRotation;
    private float rotationsStepAngle;




    public CameraMovingAction(Camera camera) {
        this.camera = camera;
        this.cameraPathPoints = new LinkedList<>();
        this.currentDestinationVector = new Vector3f();
        this.stepDistance = 0;
        this.currentWayPointIndex = 0;
        this.movingActionActive = false;
        this.stepsLeft = 0;
        this.currentRotationVector = new Vector3f();
        this.currentRotation = 0;
        this.rotationsStepAngle = 0;

    }

    public CameraMovingAction(Camera camera, List<Integer> stepsList, List<Vector3f> wayPoints, List<Vector3f> viewDirectionList, List<Float> zAxisRotationAngleList) {
        this(camera);
        int minIndex = Math.min(stepsList.size(), Math.min(wayPoints.size(), Math.min(viewDirectionList.size(), zAxisRotationAngleList.size())));
        for (int i = 0; i < minIndex; i++) {
            CameraPathPoint cameraPathPoint = new CameraPathPoint();
            cameraPathPoint.steps = stepsList.get(i);
            cameraPathPoint.wayPoint = wayPoints.get(i);
            cameraPathPoint.viewDirection = viewDirectionList.get(i);
            cameraPathPoint.zAxisRotationAngle = zAxisRotationAngleList.get(i);
            cameraPathPoints.add(cameraPathPoint);
        }
    }

    public void addWayPoint(int steps, Vector3f wayPoint, Vector3f viewDirection, float zRotationAngle) {
        CameraPathPoint cameraPathPoint = new CameraPathPoint();
        cameraPathPoint.steps =steps;
        cameraPathPoint.wayPoint = wayPoint;
        cameraPathPoint.viewDirection = viewDirection;
        cameraPathPoint.zAxisRotationAngle = zRotationAngle;
        cameraPathPoints.add(cameraPathPoint);
    }

    public void setupMovingAction() {
        currentWayPointIndex = 0;
        movingActionActive = true;
        setupWayPoint();
    }

    private void setupWayPoint() {
        if (!movingActionActive) {
            return;
        } else if (cameraPathPoints.size() <= currentWayPointIndex) {
            movingActionActive = false;
            camera.enableInputListeners();
            return;
        }
        CameraPathPoint cameraPathPoint = cameraPathPoints.get(currentWayPointIndex);
        if (cameraPathPoint.steps == 0) {
            camera.setPosition(cameraPathPoint.wayPoint);
            camera.lookAt(cameraPathPoint.viewDirection, cameraPathPoint.zAxisRotationAngle);
            currentWayPointIndex++;
            return;
        }
        currentDestinationVector.sub(cameraPathPoint.wayPoint, camera.getCurPosition());
        stepDistance =  currentDestinationVector.length() / (float) cameraPathPoint.steps;
        stepsLeft = cameraPathPoint.steps;
        currentRotationVector.cross(cameraPathPoint.viewDirection, camera.getCurrentLookVector());
        rotationsStepAngle = cameraPathPoint.zAxisRotationAngle / (float) cameraPathPoint.steps;
    }

    public void doStep() {
        if (!movingActionActive) {
            return;
        }
        if (stepsLeft == 0) {
            camera.setPosition(cameraPathPoints.get(currentWayPointIndex).wayPoint);
            currentWayPointIndex++;
            setupWayPoint();
            return;
        }
        if (camera.isInputListenersActive()) {
            camera.disableInputListeners();
        }
        camera.moveCamera(stepDistance, currentDestinationVector);
        if (rotationsStepAngle != 0) {
            Vector3f newLookAt = camera.getRotatedAxis(rotationsStepAngle, currentRotationVector, camera.getCurrentLookVector());
            camera.lookAt(newLookAt, currentRotation);
            currentRotation += rotationsStepAngle;
          //  currentDestinationVector.sub(cameraPathPoints.get(currentWayPointIndex).wayPoint, camera.getCurPosition());
        }
        stepsLeft--;
    }

    public boolean isMovingActionActive() {
        return movingActionActive;
    }
}
