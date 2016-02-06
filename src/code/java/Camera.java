package code.java;

import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.math.Matrix4;

import java.awt.event.*;

/**
 * @author peter
 * */
public class Camera {
    private Matrix4 cameraMatrix;
    private float[] xAxis;
    private float[] yAxis;
    private float[] zAxis;


    public Camera(GLCanvas canvas) {
        cameraMatrix = new Matrix4();
        cameraMatrix.makePerspective(-50, 0.66f, 0.1f, 100f);
        cameraMatrix.translate(0,0,0);
        //cameraMatrix.rotate(1.57079632679f, -1,0,0);
        KeyListenerImpl keyListener = new KeyListenerImpl();
        canvas.addKeyListener(keyListener);
        MouseMotionListenerImpl mouseMotionListener = new MouseMotionListenerImpl();
        canvas.addMouseMotionListener(mouseMotionListener);
        xAxis = new float[3];
        xAxis[0] = 1f;
        yAxis = new float[3];
        yAxis[1] = 1f;
        zAxis = new float[3];
        zAxis[2] = 1f;
    }

    public Matrix4 getCameraMatrix() {
        return cameraMatrix;
    }

    private void moveCamera(float distance, float[] axis) {
        cameraMatrix.translate(distance*axis[0], distance* axis[1], distance*axis[2]);
    }

  /*  private void rotateCamera(float angle, float x, float y, float z) {
        cameraMatrix.rotate(angle, x,y,z);
    }*/

    private float[] rotateAxis(float angle, float[] rotationAxis, float[] axis) {
        float cosAngle = (float) Math.cos(angle);
        float sinAngle = (float) Math.sin(angle);
        float[] newAxis = new float[3];
        newAxis[0] = (rotationAxis[0] *rotationAxis[0] * (1 - cosAngle) + cosAngle) * axis[0] +
                (rotationAxis[0] * rotationAxis[1] * (1 - cosAngle) - rotationAxis[2] * sinAngle) * axis[1] +
                (rotationAxis[0] * rotationAxis[2] * (1 - cosAngle) + rotationAxis[1] * sinAngle) * axis[2];

        newAxis[1] = (rotationAxis[0] * rotationAxis[1] * (1 - cosAngle) + rotationAxis[2] * sinAngle) * axis[0] +
                (rotationAxis[1] * rotationAxis[1] * (1 - cosAngle) + cosAngle) * axis[1] +
                (rotationAxis[1] * rotationAxis[2] * (1 - cosAngle) - rotationAxis[0] * sinAngle) * axis[2];

        newAxis[2] = (rotationAxis[0] * rotationAxis[2] * (1 - cosAngle) - rotationAxis[1] * sinAngle) * axis[0] +
                (rotationAxis[1] * rotationAxis[2] * (1 - cosAngle) + rotationAxis[0] * sinAngle) * axis[1] +
                (rotationAxis[2] * rotationAxis[2] * (1 - cosAngle) + cosAngle) * axis[2];

        return newAxis;

    }

    private void rotateAroundXAxis(float angle) {
        cameraMatrix.rotate(angle, xAxis[0], xAxis[1], xAxis[2]);
        yAxis = rotateAxis(-angle,xAxis, yAxis);
        zAxis = rotateAxis(-angle, xAxis, zAxis);
    }

    private void rotateAroundYAxis(float angle) {
        cameraMatrix.rotate(angle, yAxis[0], yAxis[1], yAxis[2]);
        xAxis = rotateAxis(-angle, yAxis, xAxis);
        zAxis = rotateAxis(-angle, yAxis, zAxis);
    }

    private void rotateAroundZAxis(float angle) {
        cameraMatrix.rotate(angle, zAxis[0], zAxis[1], zAxis[2]);
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
                    moveCamera(0.025f, xAxis);
                    break;
                case 'd':
                    moveCamera(-0.025f, xAxis);
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
            final float rotationScale = 0.01f;
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
