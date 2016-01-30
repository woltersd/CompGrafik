package code.java;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.FPSAnimator;

import java.nio.IntBuffer;

/**
 * @author peter
 * @author robert
 */
public class GLEventListenerImpl implements GLEventListener{
    private FPSAnimator animator;

    private int[] vao = new int[1]; //Vertex Array Object

    private int[] vbo = new int[1]; //Vertex Buffer Object
    private int[] nbo = new int[1]; //Normal Buffer Object
    private int[] ibo = new int[1]; //Index  Buffer Object

    private GLModel model = new GLModel(System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/bunny_norm.obj");

    private Shader shader;

    private float framecounter = 0;

    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();

        initializeBuffers(gl);

        // generate Vertex Array Object
        gl.glGenVertexArrays(1, IntBuffer.wrap(vao));
        gl.glBindVertexArray(vao[0]);

        // initialize Shaders
        shader = new Shader(gl, "/src/code/glsl/","vertex_shader.glsl", "fragment_shader.glsl");

        animator = new FPSAnimator(drawable, 30);
        animator.start();

    }

    private void initializeBuffers(GL3 gl) {
        // generate Indice Buffer Object
        gl.glGenBuffers(1, IntBuffer.wrap(ibo));
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, model.getFaceIndexCount() * 4, model.getFaceIndexBuffer(), GL3.GL_STATIC_DRAW);

        // generate Vertex Buffer Object
        gl.glGenBuffers(1, IntBuffer.wrap(vbo));
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, model.getVertexCount() * 4, model.getVertexBuffer(), GL3.GL_STATIC_DRAW);

        // generate Normal Buffer Object
        if (model.getNormalCount() > 0) {
            gl.glGenBuffers(1, IntBuffer.wrap(nbo));
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, nbo[0]);
            gl.glBufferData(GL3.GL_ARRAY_BUFFER, model.getNormalCount() * 4, model.getNormalBuffer(), GL3.GL_STATIC_DRAW);
        }

    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

    }

    @Override
    public void dispose(GLAutoDrawable glautodrawable) {
        animator.stop();
        shader.destroy(glautodrawable.getGL().getGL3());
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        framecounter = framecounter + 0.01f;

        GL3 gl = drawable.getGL().getGL3();

        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT);

        bindBuffers(gl);

        shader.bind(gl);

        // shader.setUniform(gl, "offset", new float[]{framecounter}, 1);

        // set color
        shader.setUniform(gl, "vertexColor", new float[]{1.0f, 1.0f, 1.0f, 1f}, 4);

        // set light
        shader.setUniform(gl, "light.position", new float[] {10f, 10f, 0f}, 3);
        shader.setUniform(gl, "light.intensities", new float[] {1f, 1f, 1f}, 3);

        // View
        Matrix4 mat4 = new Matrix4();
        mat4.makePerspective(-50, 0.66f, 0.1f, 100f);
        mat4.translate(0, -0.1f, -2f);
        mat4.rotate(framecounter, 0, 1, 0);
        shader.setUniform(gl, "model", mat4);

        // draw the triangles
        gl.glDrawElements(GL3.GL_TRIANGLES, model.getFaceIndexCount(), GL3.GL_UNSIGNED_SHORT, 0);

        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);

        shader.unbind(gl);
    }

    public void bindBuffers(GL3 gl) {
        // index buffer
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);

        // vertex buffer
        gl.glEnableVertexAttribArray(0);
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);
        gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 0, 0);

        // normal buffer
        if (model.getNormalCount() > 0) {
            gl.glEnableVertexAttribArray(1);
            gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, nbo[0]);
            gl.glVertexAttribPointer(1, 3, GL3.GL_FLOAT, false, 0, 0);
        }
    }

}
