package code.java;

import code.java.GLModel.GLModel;
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

    private List<GLModel> modelList = null;

    private Shader shader = null;

    private float framecounter = 0;

    private Camera cameraMatrix;

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

        // initialize Shaders
        shader = new Shader(gl, "/src/code/glsl/","vertex_shader.glsl", "texture_FS.glsl");

        animator = new FPSAnimator(drawable, 30);
        animator.start();
        cameraMatrix = new Camera(canvas);
    }

    private void initializeModels (GL3 gl) {
        GLModel model;
        modelList = new LinkedList<>();
        model =  new GLModel(gl, "triangle.obj");
        model.setModelMatrixRotation(3.14159265359f, 0, 0, 1);
        model.setModelMatrixOffset(0f, 0f, -50f);
        modelList.add(model);

        model =  new GLModel(gl, "triangle.obj");
        model.setModelMatrixOffset(0f,-2f, -25f);
        modelList.add(model);

        model =  new GLModel(gl, "bunny_norm.obj");
        model.setModelMatrixOffset(0f,2f, -25f);
        model.setModelMatrixScale(10,10,10);
        modelList.add(model);

        model = new GLModel(gl, "bunny_norm.obj");
        model.setModelMatrixOffset(25f,2f, 0f);
        model.setModelMatrixScale(10,10,10);
        modelList.add(model);

       /* model = new GLCam(gl, 0);
        model.setModelMatrixOffset(-1f,1f, -15f);
        modelList.add(model);*/
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        //TODO fill me
    }

    @Override
    public void dispose(GLAutoDrawable glautodrawable) {
        animator.stop();
        shader.destroy(glautodrawable.getGL().getGL3());
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        framecounter += 0.001f;
        GL3 gl = drawable.getGL().getGL3();

        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);

        shader.bind(gl);

        // set light
        shader.setUniform(gl, "light.position", new float[] {0f, 0f, 10f}, 3);
        shader.setUniform(gl, "light.intensities", new float[] {1f, 1f, 1f}, 3);
        // set camera
        shader.setUniform(gl, "cameraMatrix", cameraMatrix.getCameraMatrix());

        for(GLModel each: modelList){
            each.display(gl, shader);
        }
        shader.unbind(gl);
    }

}
