package code.java;

import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.math.Matrix4;

import javax.vecmath.Vector3f;
import java.awt.event.*;

/**
 * @author peter
 * */
public class Camera implements  InputWaiter{
    private Matrix4 cameraMatrix;
    private Vector3f xAxis;
    private Vector3f yAxis;
    private Vector3f zAxis;
    private GLCanvas canvas;
    private Vector3f curPosition;
    private Integer oldMouseX;
    private Integer oldYMouseY;

    private boolean inputListenersActive;
    private InputListener inputListener;



    public Camera(GLCanvas canvas, InputListener inputListener) {

        this.canvas = canvas;
        xAxis = new Vector3f(1,0,0);
        yAxis = new Vector3f(0,1,0);
        zAxis = new Vector3f(0,0,1);

        oldMouseX = null;
        oldYMouseY = null;
        curPosition = new Vector3f(0,4,-60);

        cameraMatrix = new Matrix4();

        cameraMatrix.makePerspective(50, (float) canvas.getWidth() / (float) canvas.getHeight(), 0.1f, 200f);
        cameraMatrix.translate(curPosition.x, curPosition.y, curPosition.z);
        cameraMatrix.rotate(3.14159265359f, 0,0,1);

        this.inputListener = inputListener;
        enableInputListeners();

    }

    public Matrix4 getCameraMatrix() {
        return cameraMatrix;
    }

    public void moveCamera(float distance, Vector3f axis) {
        if (distance == 0) {
            return;
        }
        Vector3f changeVector = new Vector3f(axis);
        changeVector.normalize();
        changeVector.scale(distance);
        cameraMatrix.translate(-changeVector.x, -changeVector.y, changeVector.z);
        curPosition.add(changeVector);
    }

  /*  public void moveCameraOnGlobalAxis() {
        cameraMatrix
    }*/

    public Vector3f getRotatedAxis(float angle, Vector3f rotationAxis, Vector3f axis) {
        float cosAngle = (float) Math.cos(angle);
        float sinAngle = (float) Math.sin(angle);
        Vector3f newAxis = new Vector3f();
        newAxis.x = (rotationAxis.x *rotationAxis.x * (1 - cosAngle) + cosAngle) * axis.x +
                (rotationAxis.x * rotationAxis.y * (1 - cosAngle) - rotationAxis.z * sinAngle) * axis.y +
                (rotationAxis.x * rotationAxis.z * (1 - cosAngle) + rotationAxis.y * sinAngle) * axis.z;

        newAxis.y = (rotationAxis.x * rotationAxis.y * (1 - cosAngle) + rotationAxis.z * sinAngle) * axis.x +
                (rotationAxis.y * rotationAxis.y * (1 - cosAngle) + cosAngle) * axis.y +
                (rotationAxis.y * rotationAxis.z * (1 - cosAngle) - rotationAxis.x * sinAngle) * axis.z;

        newAxis.z = (rotationAxis.x * rotationAxis.z * (1 - cosAngle) - rotationAxis.y * sinAngle) * axis.x +
                (rotationAxis.y * rotationAxis.z * (1 - cosAngle) + rotationAxis.x * sinAngle) * axis.y +
                (rotationAxis.z * rotationAxis.z * (1 - cosAngle) + cosAngle) * axis.z;

        return newAxis;
    }

    private void rotateAroundXAxis(float angle) {
        selfRotation(angle,xAxis);
        yAxis = getRotatedAxis(-angle,xAxis, yAxis);
        zAxis = getRotatedAxis(-angle, xAxis, zAxis);
    }

    private void rotateAroundYAxis(float angle) {
        selfRotation(angle, yAxis);
        xAxis = getRotatedAxis(-angle, yAxis, xAxis);
        zAxis = getRotatedAxis(-angle, yAxis, zAxis);
    }

    public void rotateAroundZAxis(float angle) {
        selfRotation(angle, zAxis);
        xAxis = getRotatedAxis(-angle, zAxis, xAxis);
        yAxis = getRotatedAxis(-angle, zAxis, yAxis);
    }

    private void selfRotation(float angle,Vector3f axis) {
        cameraMatrix.translate(curPosition.x, curPosition.y, -curPosition.z);
        cameraMatrix.rotate(angle, axis.x, axis.y, axis.z);
        cameraMatrix.translate(-curPosition.x, -curPosition.y, curPosition.z);
    }

    public void lookAt(Vector3f point, float rotation) {

        Vector3f crossProduct = new Vector3f();
        point.normalize();
        crossProduct.cross(zAxis, point);
       // roundVector(crossProduct);
        // no lookAxisChange needed
        if (crossProduct.lengthSquared() == 0) {
            if (rotation != 0) {
                rotateAroundZAxis(rotation - xAxis.angle(new Vector3f(xAxis.x, 0, xAxis.z)));
            }
            return;
        }
        Float angle = zAxis.angle(point);
        crossProduct.normalize();
        Vector3f computedAxis = getRotatedAxis(angle, crossProduct, zAxis);
      //  roundVector(computedAxis);
        Vector3f test = new Vector3f();
        test.sub(computedAxis, point);
        if (test.length() > 1E-4) {
            angle *= 1f;
            computedAxis = getRotatedAxis(angle, crossProduct, new Vector3f(0,0,1));
          //  roundVector(computedAxis);
        }

        zAxis = computedAxis;
        selfRotation(angle, crossProduct);
        if (zAxis.z == 0) {
            xAxis.x = zAxis.z;
            xAxis.z = 1;
        } else if (zAxis.z < 0) {
            xAxis.x = -zAxis.z;
            xAxis.z = zAxis.x;
        } else {
            xAxis.x = zAxis.z;
            xAxis.z = -zAxis.x;
        }
        xAxis.y = 0;
        yAxis.cross(xAxis, zAxis);
        rotateAroundZAxis(rotation);

        /*computedAxis.y = 0;
        computedAxis.z = (float) Math.sqrt(1 / (- zAxis.z * zAxis.z / (zAxis.x * zAxis.x) + 1));
        computedAxis.x = (float) Math.sqrt(1 - computedAxis.z);
        xAxis = computedAxis;
        roundVector(xAxis);
        yAxis.cross(xAxis, zAxis);
        roundVector(yAxis);*/
    }

    private float[] transformVector(float x, float y, float z) {
        float[] floatMatrix = cameraMatrix.getMatrix();
        float[] transformedVector = new float[3];
        transformedVector[0] = x * floatMatrix[0] + x * floatMatrix[1] + x * floatMatrix[2] + x * floatMatrix[3];
        transformedVector[1] = y * floatMatrix[4] + y * floatMatrix[5] + y * floatMatrix[6] + y * floatMatrix[7];
        transformedVector[2] = z * floatMatrix[8] + z * floatMatrix[9] + z * floatMatrix[10] + z * floatMatrix[11];
        return transformedVector;
    }



    private Vector3f getCrossProduct(Vector3f v1, Vector3f v2) {
        Vector3f crossProduct = new Vector3f();
        crossProduct.x = v1.y * v2.z - v1.z * v2.y;
        crossProduct.y = v1.z * v2.x - v1.x * v2.z;
        crossProduct.z = v1.x * v2.y - v1.y * v2.x;
        return crossProduct;
    }

    public void disableInputListeners() {
        if (inputListenersActive) {
            inputListener.removeInputWaiter(EventType.Key_Typed, this);
            inputListener.removeInputWaiter(EventType.Mouse_Moved, this);
            inputListenersActive = false;
        }
    }

    public void enableInputListeners() {
        inputListener.addInputWaiter(EventType.Mouse_Moved, this);
        inputListener.addInputWaiter(EventType.Key_Typed, this);
        inputListenersActive = true;
    }

    public boolean isInputListenersActive() {
        return inputListenersActive;
    }

    public void reshapeCalled() {
        cameraMatrix = new Matrix4();

        cameraMatrix.makePerspective(50, (float) canvas.getWidth() / (float) canvas.getHeight(), 0.1f, 200f);
        cameraMatrix.translate(curPosition.x, curPosition.y, curPosition.z);
        cameraMatrix.rotate(3.14159265359f, 0,0,1);
        // check if angle * -1
        Vector3f unchangedZAxis =  new Vector3f(0,0,1);
        float angle = zAxis.angle(unchangedZAxis);
        Vector3f crossProduct = new Vector3f();
        crossProduct.cross(zAxis,unchangedZAxis);
        selfRotation(angle, crossProduct);

   /*     Vector3f crossProduct = new Vector3f();
        Vector3f axis = new Vector3f(1,0,0);
        crossProduct.cross(axis, xAxis);
        float angle = axis.angle(xAxis);
        Vector3f result = getRotatedAxis(angle, crossProduct, axis);
        Vector3f test = new Vector3f();
       test.cross(xAxis, axis);
        angle = xAxis.angle(axis);
        test = getRotatedAxis(angle, test, axis);
        axis.set(0,1,0);
     /*   test.sub(result,xAxis);
        if (test.length() > 0.1) {
            angle *= -1;
        }
        yAxis = getRotatedAxis(angle, crossProduct, yAxis);
        zAxis = getRotatedAxis(angle, crossProduct, zAxis);
        crossProduct.cross(axis, yAxis);
        angle = axis.angle(yAxis);*/
    }

    public Vector3f getCurPosition() {
        return curPosition;
    }

    public void setPosition(Vector3f position) {
        Vector3f translateVector = new Vector3f();
        translateVector.sub(curPosition, position);
        cameraMatrix.translate(translateVector.x, translateVector.y, translateVector.z);
        curPosition = position;
    }

    public Vector3f getCurrentLookVector() {
        return  zAxis;
    }

    public void resetCamera() {
        curPosition = new Vector3f(0,4,-60);
        cameraMatrix = new Matrix4();
        cameraMatrix.makePerspective(50, (float) canvas.getWidth() / (float) canvas.getHeight(), 0.1f, 200f);
        cameraMatrix.translate(curPosition.x, curPosition.y, curPosition.z);
        cameraMatrix.rotate(3.14159265359f, 0,0,1);
        xAxis = new Vector3f(1,0,0);
        yAxis = new Vector3f(0,1,0);
        zAxis = new Vector3f(0,0,1);
    }

    @Override
    public void inputEventHappened(InputEvent event) {
        if (event instanceof KeyEvent) {
            switch (((KeyEvent) event).getKeyChar()) {
                case 'w':
                    moveCamera(0.25f, zAxis);
                    break;
                case 's':
                    moveCamera(-0.25f, zAxis);
                    break;
                case 'a':
                    moveCamera(0.025f, xAxis);
                    break;
                case 'd':
                    moveCamera(-0.025f, xAxis);
                    break;
                case 'q':
                    rotateAroundZAxis(-0.025f);
                    break;
                case 'e':
                    rotateAroundZAxis(0.025f);
                    break;
                case KeyEvent.VK_BACK_SPACE:
                    resetCamera();
                    break;
            }
        } else if (event instanceof MouseEvent) {
            final float rotationScale = 0.002f;
            if (oldMouseX == null) {
                oldMouseX = ((MouseEvent)event).getX();
                oldYMouseY = ((MouseEvent)event).getY();
            } else {
                int newX = ((MouseEvent)event).getX();
                int newY = ((MouseEvent)event).getY();
                if(event.isAltDown()) {
                    if (newX != oldMouseX) {
                        Camera.this.rotateAroundYAxis((float) (newX - oldMouseX) * rotationScale);
                    }

                    if (newY != oldYMouseY) {
                        Camera.this.rotateAroundXAxis((newY - oldYMouseY) * rotationScale);
                    }
                }
                oldMouseX = newX;
                oldYMouseY = newY;
            }
        }
    }
}
