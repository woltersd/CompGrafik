package code.java;

import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.math.Matrix4;

import java.awt.event.*;

/**
 * @author peter
 * */
public class Camera {
    private Matrix4 cameraMatrix;
    private KeyListenerImpl keyListener;
 //   private MouseMotionListenerImpl mouseMotionListener;

    public Camera(GLCanvas canvas) {
        cameraMatrix = new Matrix4();
        cameraMatrix.makePerspective(-50, 0.66f, 0.1f, 100f);
        cameraMatrix.translate(0,0,-10);
        keyListener = new KeyListenerImpl();
        canvas.addKeyListener(keyListener);
      //  mouseMotionListener = new MouseMotionListenerImpl();
     //   canvas.addMouseMotionListener(mouseMotionListener);
    }

    public Matrix4 getCameraMatrix() {
        return cameraMatrix;
    }

    private void moveCamera(float x, float y, float z) {
        cameraMatrix.translate(x,y,z);
    }

    private class KeyListenerImpl implements KeyListener {
        @Override
        public void keyTyped(KeyEvent e) {
            switch (e.getKeyChar()) {
                case 'w':
                    moveCamera(0,0,0.25f);
                    break;
                case 's':
                    moveCamera(0, 0, -0.25f);
                    break;
                case 'a':
                    moveCamera(-0.025f,0, 0);
                    break;
                case 'd':
                    moveCamera(0.025f,0, 0);
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

  /*  private class MouseMotionListenerImpl implements MouseMotionListener {
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
           if (oldX == null) {
               oldX = e.getX();
               oldY = e.getY();
           } else {
               int newX = e.getX();
               if (newX != oldX) {
                   //float degree = ;
                   cameraMatrix.rotate((newX - oldX) / 300f, 0, 1, 0);
                   oldX = newX;
               }
               int newY = e.getY();
               if (newY != oldY) {
                //   cameraMatrix.rotate((newY - oldY) / 300f , 1, 0, 0);
                //   oldY = newY;
               }
           }
        }
    }*/
}
