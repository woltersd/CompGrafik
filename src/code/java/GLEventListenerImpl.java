package code.java;

import code.java.GLModel.*;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.vecmath.Vector3f;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.nio.IntBuffer;
import java.util.List;

/**
 * @author peter
 * @author robert
 */
public class GLEventListenerImpl implements GLEventListener, InputWaiter{
    private FPSAnimator animator;

    private List<GLObject> modelList;
    private CameraMovingAction cameraMovingAction;
    private CameraMovingAction cameraMovingActionSave;

    private Camera camera;

    GLCanvas canvas;

    int[] vao;

    public GLEventListenerImpl(GLCanvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        InputListener inputListener =  new InputListener(canvas);
        initializeModels(gl, inputListener);

        // generate Vertex Array Object
        vao = new int[1];
        gl.glGenVertexArrays(1, IntBuffer.wrap(vao));
        gl.glBindVertexArray(vao[0]);

        gl.glEnable(GL3.GL_DEPTH_TEST);

        animator = new FPSAnimator(drawable, 30);
        animator.start();

        camera = new Camera(canvas,inputListener);
        setupCameraMotionAction();
        inputListener.addInputWaiter(EventType.Key_Typed, this);
    }

    private void initializeModels (GL3 gl, InputListener inputListener) {
        modelList = ModelLoader.loadModelList(gl, canvas, inputListener);
    }

    public void setupCameraMotionAction() {
        cameraMovingAction = null;
        cameraMovingActionSave = new CameraMovingAction(camera);
        cameraMovingActionSave.addWayPoint(180, new Vector3f(0,6,-35), new Vector3f(0,0,1), 0);

        cameraMovingActionSave.addWayPoint(120, new Vector3f(15,4,-20), new Vector3f(0,0,1),0);
        cameraMovingActionSave.addWayPoint(60, new Vector3f(15,-8,-20), new Vector3f(0,0,1),0);
        cameraMovingActionSave.addWayPoint(90, new Vector3f(15,4,-20), new Vector3f(0,1,0),0.02f);
        cameraMovingActionSave.addWayPoint(60, new Vector3f(-5,-2,-30), new Vector3f(0,1,0),-0.02f);

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        camera.reshapeCalled();
    }

    @Override
    public void dispose(GLAutoDrawable glautodrawable) {
        animator.stop();
        for(GLObject each: modelList){
            each.dispose(glautodrawable.getGL().getGL3());
        }
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        if (cameraMovingAction != null) {
            if (cameraMovingAction.isMovingActionActive()) {
                cameraMovingAction.doStep();
            } else {
                cameraMovingAction = null;
                camera.enableInputListeners();
            }
        }

        GL3 gl = drawable.getGL().getGL3();

        gl.glEnable(GL3.GL_BLEND);
        gl.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);

        gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);


        for(GLObject each: modelList){
            if (!each.isActive()) {
                continue;
            }
            each.getShader().bind(gl);
            if (!(each instanceof GLHelpDialog)) {
                each.getShader().setGlobalUniform("cameraMatrix", camera.getCameraMatrix());
            }

            each.display(gl);
            each.getShader().unbind(gl);
        }
    }

    @Override
    public void inputEventHappened(InputEvent event) {
        if (event instanceof KeyEvent) {
            switch (((KeyEvent)event).getKeyChar()) {
                case '2':
                    camera.resetCamera();
                    cameraMovingAction = cameraMovingActionSave;
                    cameraMovingActionSave.setupMovingAction();
                    break;
            }
        }
    }
}
