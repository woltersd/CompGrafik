package code.java;

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

    private int[] vao = null; //Vertex Array Object

    private int[] vbo = null; //Vertex Buffer Object
   // private int[] nbo = null; //Normal Buffer Object
    private int[] ibo = null; //Index  Buffer Object
    //private int[] tbo = new int[1]; //Texture Buffer Object

    //private GLCam model = new GLCam(0);

    private List<GLModel> modelList = null;

    private Shader shader = null;

    private float framecounter = 0;

    private Camera cameraMatrix;

    GLCanvas canvas;

    public GLEventListenerImpl(GLCanvas canvas) {
        this.canvas = canvas;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        initializeModels();
        initializeBuffers(gl);

        // generate Vertex Array Object
        gl.glGenVertexArrays(1, IntBuffer.wrap(vao));
        gl.glBindVertexArray(vao[0]);

        gl.glEnable(GL3.GL_DEPTH_TEST);

        // initialize Shaders
        shader = new Shader(gl, "/src/code/glsl/","vertex_shader.glsl", "fragment_shader.glsl");

        animator = new FPSAnimator(drawable, 30);
        animator.start();
        cameraMatrix = new Camera(canvas);
    }

    private void initializeModels () {
        GLModel model;
        modelList = new LinkedList<>();
        model =  new GLModel(System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/triangle.obj");
        model.setModelMatrixRotation(3.14159265359f, 0, 0, 1);
        model.setModelMatrixOffset(0f, 0f, -50f);
        modelList.add(model);
        model =  new GLModel(System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/triangle.obj");
        model.setModelMatrixOffset(0f,-2f, -25f);
        modelList.add(model);
        model =  new GLModel(System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/bunny_norm.obj");
        model.setModelMatrixOffset(0f,2f, -25f);
        model.setModelMatrixScale(10,10,10);
        modelList.add(model);
    }

    private void initializeBuffers(GL3 gl) {
        vao = new int[1];
        vbo =  new int[modelList.size()];
        ibo = new int[modelList.size()];

        gl.glGenBuffers(modelList.size(), IntBuffer.wrap(ibo));
        // generate Index Buffer Object
        for (int i = 0; i < modelList.size(); i++) {
            gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ibo[i]);
            gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, modelList.get(i).getIndexCount() * 4, modelList.get(i).getIndexBuffer(), GL3.GL_STATIC_DRAW);
        }

        gl.glGenBuffers(modelList.size(), IntBuffer.wrap(vbo));
        for (int i = 0; i < modelList.size(); i++) {
            // generate vertex buffer object
            if (modelList.get(0).normal()) {
                gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[i]);
                gl.glBufferData(GL3.GL_ARRAY_BUFFER, (modelList.get(i).getNormalCount() + modelList.get(i).getVertexCount()) * 4, modelList.get(i).getComboBuffer(), GL3.GL_STATIC_DRAW);
            } else {
                gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[i]);
                gl.glBufferData(GL3.GL_ARRAY_BUFFER, modelList.get(i).getVertexCount() * 4, modelList.get(i).getVertexBuffer(), GL3.GL_STATIC_DRAW);
            }
        }
        /*
        gl.glGenTextures(1, IntBuffer.wrap(tbo));
        gl.glBindTexture(GL3.GL_TEXTURE_2D, tbo[0]);
        gl.glTexImage2D(gl.GL_TEXTURE_2D, 0, gl.GL_BGR, 500, 500, 0, gl.GL_BGR, gl.GL_UNSIGNED_BYTE, model.getTexImage());
        */
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

        // set color
        shader.setUniform(gl, "vertexColor", new float[]{1.0f, 1.0f, 1.0f, 1f}, 4);

        // set light
        shader.setUniform(gl, "light.position", new float[] {0f, 0f, 10f}, 3);
        shader.setUniform(gl, "light.intensities", new float[] {1f, 1f, 1f}, 3);
        shader.setUniform(gl, "cameraMatrix", cameraMatrix.getCameraMatrix());

        for (int i = 0; i < modelList.size(); i++) {
            // View
            shader.setUniform(gl, "modelMatrix",  modelList.get(i).getModelMatrix());
            bindBuffer(gl, i);
            // draw the triangles
            gl.glDrawElements(GL3.GL_TRIANGLES,  modelList.get(i).getIndexCount(), GL3.GL_UNSIGNED_SHORT, 0);
            gl.glDisableVertexAttribArray(0);
            // TODO remove if condition
            if (modelList.get(i).normal()) {
                //normals
                gl.glDisableVertexAttribArray(1);
            }
        }
        shader.unbind(gl);
    }

    public void bindBuffer(GL3 gl, int index) {
        // index buffer
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ibo[index]);
        // vertex+normal buffer
        if (modelList.get(index).normal()) {
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[index]);
            gl.glEnableVertexAttribArray(0);
            gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 24, 0);
            gl.glEnableVertexAttribArray(1);
            gl.glVertexAttribPointer(1, 3, GL3.GL_FLOAT, false, 24, 12);
        }
        else{
            //TODO remove
            gl.glEnableVertexAttribArray(index);
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[index]);
            gl.glVertexAttribPointer(index, 3, GL3.GL_FLOAT, false, 0, 0);
        }
    }
}
