package code.java;

import code.java.GLModel.*;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

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

    }

    private void initializeModels (GL3 gl) {
        GLModel model;
        GLShadow shadow;
        modelList = new LinkedList<>();
        Shader shader = new Shader(gl, "/src/code/glsl/","vertex_shader.glsl", "texture_FS.glsl");
        shader.setGlobalUniform("light.position", new float[] {20f, 20f, 0f});
        shader.setGlobalUniform("light.intensities", new float[] {1f, 1f, 1f});

        Shader shadowShader = new Shader(gl, "/src/code/glsl/","shadow_VS.glsl", "shadow_FS.glsl");
        /*
        model = new GLModel(gl, "field.obj", shader);
        modelList.add(model);
        model = new GLModel(gl, "field.obj", shader);
        model.setModelMatrixOffset(-8f, 0f, 0f);
        modelList.add(model);
        */
        model = new GLModel(gl, "cylinder.obj", shader);
        model.setModelMatrixOffset(0f, 0f, 4f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "cylinder.obj", shader);
        model.setModelMatrixOffset(0f, 0f, -4f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "net.obj", shader);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);

        model = new GLModel(gl, "ball.obj", shader);
        model.setModelMatrixOffset(2f, 2f, 0f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        /*
        model = new GLCam(gl, 0, shader);
        model.setModelMatrixOffset(1f, 0.5f, 0f);
        modelList.add(model);
        shadow = new GLShadow(gl, model, shadowShader);
        modelList.add(shadow);
        */
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        //TODO fill me
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
        framecounter += 0.001f;
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
