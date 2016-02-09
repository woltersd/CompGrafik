package code.java;

import code.java.GLModel.*;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.vecmath.Vector3f;
import java.nio.IntBuffer;
import java.util.LinkedList;
import java.util.List;

/**
 * @author peter
 * @author robert
 */
public class GLEventListenerImpl implements GLEventListener{
    private FPSAnimator animator;

    private List<GLObject> modelList;
    private List<CameraMovingAction> cameraMovingActionList;

    private float framecounter = 0;

    private Camera camera;

    GLCanvas canvas;

    int[] vao;

    public GLEventListenerImpl(GLCanvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        initializeModels(gl);

        // generate Vertex Array Object
        vao = new int[1];
        gl.glGenVertexArrays(1, IntBuffer.wrap(vao));
        gl.glBindVertexArray(vao[0]);

        gl.glEnable(GL3.GL_DEPTH_TEST);

        animator = new FPSAnimator(drawable, 30);
        animator.start();
        camera = new Camera(canvas);
        addCameraMotionAction();
    }

    private void initializeModels (GL3 gl) {
        GLModel model;
        GLShadow shadow;
        BackgroundSubtractor subtractor;
        modelList = new LinkedList<>();
        Shader shader = new Shader(gl, "/src/code/glsl/","vertex_shader.glsl", "texture_FS.glsl");
        shader.setGlobalUniform("light.position", new float[] {20f, 20f, 0f});
        shader.setGlobalUniform("light.intensities", new float[] {1f, 1f, 1f});

        Shader shadowShader = new Shader(gl, "/src/code/glsl/","shadow_VS.glsl", "shadow_FS.glsl");
        shadowShader.setGlobalUniform("light.position", new float[] {20f, 20f, 0f});
        shadowShader.setGlobalUniform("light.intensities", new float[] {1f, 1f, 1f});
        Shader camShader = new Shader(gl, "/src/code/glsl/","vertex_shader.glsl", "subtractor_FS.glsl");
        camShader.setGlobalUniform("light.position", new float[] {20f, 20f, 0f});
        camShader.setGlobalUniform("light.intensities", new float[] {1f, 1f, 1f});

        model = new GLModel(gl, "field.obj", shader);
        modelList.add(model);
        model = new GLModel(gl, "field.obj", shader);
        model.setModelMatrixOffset(-8f, 0f, 0f);
        modelList.add(model);

        model = new GLModel(gl, "cylinder.obj", shader);
        model.setModelMatrixOffset(0f, 0f, 4f);
        modelList.add(model);
        //shadow = new GLShadow(gl, model, shadowShader);
        //modelList.add(shadow);

        model = new GLModel(gl, "cylinder.obj", shader);
        model.setModelMatrixOffset(0f, 0f, -4f);
        modelList.add(model);
        //shadow = new GLShadow(gl, model, shadowShader);
        //modelList.add(shadow);

        model = new GLModel(gl, "net.obj", shader);
        modelList.add(model);
        //shadow = new GLShadow(gl, model, shadowShader);
        //modelList.add(shadow);

    /*    model = new GLModel(gl, "ball.obj", shader);
        model.setModelMatrixOffset(2f, 2f, 0f);
        modelList.add(model);
        //shadow = new GLShadow(gl, model, shadowShader);
        //modelList.add(shadow);

        subtractor = new BackgroundSubtractor(gl, 0, canvas);
        modelList.add(subtractor);

        model = new GLCam(gl, subtractor, "player1.obj", camShader, -4f);
        model.setModelMatrixOffset(-4f, 0.0f, 0.0f);
        modelList.add(model);
        //shadow = new GLShadow(gl, model, shadowShader);
        //modelList.add(shadow);

        model = new GLCam(gl, subtractor, "player2.obj", camShader, 4);
        model.setModelMatrixOffset(4f, 0.0f, 0.0f);
        modelList.add(model);
        //shadow = new GLShadow(gl, model, shadowShader);
        //modelList.add(shadow)

        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        */


    }

    public void addCameraMotionAction() {
        cameraMovingActionList = new LinkedList<>();
        CameraMovingAction cameraMovingAction = new CameraMovingAction(camera);
       // cameraMovingAction.addWayPoint(30, new Vector3f(0,6,-10), new Vector3f(0,0,1), 0);
        cameraMovingAction.addWayPoint(120, new Vector3f(0,4,-60), new Vector3f(0,1,0),0);
        //cameraMovingAction.addWayPoint(120, new Vector3f(0,6,-10), new Vector3f(0,1,0),0f);
       // cameraMovingAction.addWayPoint(60, new Vector3f(-5,6,-30));
       // cameraMovingAction.addWayPoint(30, new Vector3f(-5,6,-30));
       // cameraMovingAction.addWayPoint(300, new Vector3f(5,4,-40));
        cameraMovingAction.setupMovingAction();
        cameraMovingActionList.add(cameraMovingAction);
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
        /*if (cameraMovingActionList.size() > 0) {
            if (cameraMovingActionList.get(0).isMovingActionActive()) {
                cameraMovingActionList.get(0).doStep();
            } else {
                cameraMovingActionList.remove(0);
            }
        }*/
        GL3 gl = drawable.getGL().getGL3();

        gl.glEnable(GL3.GL_BLEND);
        gl.glBlendFunc(GL3.GL_SRC_ALPHA, GL3.GL_ONE_MINUS_SRC_ALPHA);

        gl.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        for(GLObject each: modelList){
            each.getShader().bind(gl);
            each.getShader().setGlobalUniform("cameraMatrix", camera.getCameraMatrix());
            each.display(gl);
            each.getShader().unbind(gl);
        }
    }

}
