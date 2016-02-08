package code.java;

import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.math.Matrix4;

import javax.vecmath.Vector3f;
import java.awt.event.*;

/**
 * @author peter
 * */
public class Camera {
    private Matrix4 cameraMatrix;
    private Vector3f xAxis;
    private Vector3f yAxis;
    private Vector3f zAxis;
    private GLCanvas canvas;
    private Vector3f curPosition;


    public Camera(GLCanvas canvas) {

        this.canvas = canvas;
        xAxis = new Vector3f(1,0,0);
        yAxis = new Vector3f(0,1,0);
        zAxis = new Vector3f(0,0,1);

        curPosition = new Vector3f(0,4,-60);

        cameraMatrix = new Matrix4();

        cameraMatrix.makePerspective(50, (float) canvas.getWidth() / (float) canvas.getHeight(), 0.1f, 100f);
        cameraMatrix.translate(curPosition.x, curPosition.y, curPosition.z);

        KeyListenerImpl keyListener = new KeyListenerImpl();
        canvas.addKeyListener(keyListener);
        MouseMotionListenerImpl mouseMotionListener = new MouseMotionListenerImpl();
        canvas.addMouseMotionListener(mouseMotionListener);

    }

    public Matrix4 getCameraMatrix() {
        return cameraMatrix;
    }

    private void moveCamera(float distance, Vector3f axis) {
        Vector3f changeVector = new Vector3f(distance*axis.x, distance* axis.y, distance*axis.z);
        cameraMatrix.translate(changeVector.x, changeVector.y, changeVector.z);
        curPosition.add(changeVector);
    }

    private Vector3f rotateAxis(float angle, Vector3f rotationAxis, Vector3f axis) {
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
        cameraMatrix.rotate(angle, xAxis.x, xAxis.y, xAxis.z);
        yAxis = rotateAxis(-angle,xAxis, yAxis);
        zAxis = rotateAxis(-angle, xAxis, zAxis);
    }

    private void rotateAroundYAxis(float angle) {
        cameraMatrix.rotate(angle, yAxis.x, yAxis.y, yAxis.z);
        xAxis = rotateAxis(-angle, yAxis, xAxis);
        zAxis = rotateAxis(-angle, yAxis, zAxis);
    }

    private void rotateAroundZAxis(float angle) {
        cameraMatrix.rotate(angle, zAxis.x, zAxis.y, zAxis.z);
        xAxis = rotateAxis(-angle, zAxis, xAxis);
        yAxis = rotateAxis(-angle, zAxis, yAxis);
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

    public void reshapeCalled() {
        cameraMatrix = new Matrix4();

        cameraMatrix.makePerspective(50, (float) canvas.getWidth() / (float) canvas.getHeight(), 0.1f, 100f);
        cameraMatrix.translate(curPosition.x, curPosition.y, curPosition.z);
        xAxis.set(1,0,0);
        yAxis.set(0,1,0);
        zAxis.set(0,0,1);

   /*     Vector3f crossProduct = new Vector3f();
        Vector3f axis = new Vector3f(1,0,0);
        crossProduct.cross(axis, xAxis);
        float angle = axis.angle(xAxis);
        Vector3f result = rotateAxis(angle, crossProduct, axis);
        Vector3f test = new Vector3f();
       test.cross(xAxis, axis);
        angle = xAxis.angle(axis);
        test = rotateAxis(angle, test, axis);
        axis.set(0,1,0);
     /*   test.sub(result,xAxis);
        if (test.length() > 0.1) {
            angle *= -1;
        }
        yAxis = rotateAxis(angle, crossProduct, yAxis);
        zAxis = rotateAxis(angle, crossProduct, zAxis);
        crossProduct.cross(axis, yAxis);
        angle = axis.angle(yAxis);*/
    }

    private class KeyListenerImpl implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            switch (e.getKeyChar()) {
                case 'w':
                    moveCamera(0.25f, zAxis);
                    break;
                case 's':
                    moveCamera(-0.25f, zAxis);
                    break;
                case 'a':
                    moveCamera(-0.025f, xAxis);
                    break;
                case 'd':
                    moveCamera(0.025f, xAxis);
                    break;
                case 'q':
                    rotateAroundZAxis(0.025f);
                    break;
                case 'e':
                    rotateAroundZAxis(-0.025f);
                    break;
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    }

    private class MouseMotionListenerImpl implements MouseMotionListener {
        Integer oldX;
        Integer oldY;

        public MouseMotionListenerImpl() {
            oldX = null;
            oldY = null;
        }

        @Override
        public void mouseDragged(MouseEvent e) {

        }

        @Override
        public void mouseMoved(MouseEvent e) {
           final float rotationScale = 0.004f;
           if (oldX == null) {
               oldX = e.getX();
               oldY = e.getY();
           } else {
               int newX = e.getX();
               if (newX != oldX) {
                   Camera.this.rotateAroundYAxis( (float)(newX - oldX) * rotationScale);
                   oldX = newX;
               }
               int newY = e.getY();
               if (newY != oldY) {
                    Camera.this.rotateAroundXAxis((newY - oldY) * rotationScale);
                    oldY = newY;
               }
           }
        }
    }
}
