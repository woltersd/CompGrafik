package code.java.GLModel;

import code.java.Shader;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.GLBuffers;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

/**
 * @author peter
 */
public class GLSkyBoxModel extends GLModelAbstract {

    private ByteBuffer[] skyBoxFaces;
    private Mat[] images;
    private Matrix4 projection;
    private  GLCanvas canvas;

    /*static float vertices[] =
    {
            10.0f, -10.0f, 10.0f,
            10.0f, 10.0f, 10.0f,
            10.0f, -10.0f, -10.0f,
            10.0f, 10.0f, -10.0f,
            -10.0f, -10.0f, 10.0f,
            -10.0f, 10.0f, 10.0f,
            -10.0f, -10.0f, -10.0f,
            -10.0f, 10.0f, -10.0f
    };*/
    static float vertices[] =
            {
                    1.0f, -1.0f, 1.0f,
                    1.0f, 1.0f, 1.0f,
                    1.0f, -1.0f, -1.0f,
                    1.0f, 1.0f, -1.0f,
                    -1.0f, -1.0f, 1.0f,
                    -1.0f, 1.0f, 1.0f,
                    -1.0f, -1.0f, -1.0f,
                    -1.0f, 1.0f, -1.0f
            };

    static int indices[] = {
            1,2,3,
            2,3,4,
            5,6,7,
            6,7,8,
            2,4,6,
            4,6,8,
            1,3,5,
            3,5,7,
            3,4,7,
            4,7,8,
            1,2,5,
            2,5,6,
    };

    public GLSkyBoxModel(GL3 gl,GLCanvas canvas) {
        this(gl, canvas, new Shader(gl, "/src/code/glsl/", "skybox_vertex_shader.glsl", "skybox_fragment_shader.glsl"));
    }

    public GLSkyBoxModel(GL3 gl, GLCanvas canvas, Shader shader) {
        super();
        buildGLData();

        this.canvas = canvas;

        setShader(shader);
        projection = new Matrix4();
        projection.makePerspective(50, canvas.getWidth() / (float) canvas.getHeight(), 0.1f, 100f);
        //setShaderUniform("projection", projection);

        setVertexCount(glVertexData.size() * 3);
        setIndexCount(glIndexData.size());
        loadSkyBoxImages();
        initializeBuffers(gl);
    }

    private void buildGLData(){
        for (int i = 0; i < vertices.length / 3; i++) {
            int index = i * 3;
            Point3f vertexPoint = new Point3f(vertices[index], vertices[index+1], vertices[index+2]);
            glVertexData.add(vertexPoint);
        }
        for (int i = 0; i < indices.length / 3; i++) {
            glIndexData.add(indices[3*i] - 1);
            glIndexData.add(indices[3*i+1] - 1);
            glIndexData.add(indices[3*i+2] - 1);
        }

    }

    private void loadSkyBoxImages() {
        String [] imageNames = {"right.png", "left.png", "top.png", "bottom.png", "back.png", "front.png"};
        skyBoxFaces = new ByteBuffer[imageNames.length];
        images = new Mat[imageNames.length];
        for (int i = 0; i < skyBoxFaces.length; i++) {
            images[i] = Imgcodecs.imread(System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/SkyBox/" + imageNames[i], Imgcodecs.IMREAD_UNCHANGED);
            byte[] bytes = new byte[images[i].rows() * images[i].cols() * images[i].channels()];
            images[i].get(0, 0, bytes);
            skyBoxFaces[i] = GLBuffers.newDirectByteBuffer(bytes);
        }
    }

    protected void initializeBuffers(GL3 gl) {
        // generate Index Buffer Object
        gl.glGenBuffers(1, IntBuffer.wrap(ibo));
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        gl.glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, getIndexCount() * 4, getIndexBuffer(), GL3.GL_STATIC_DRAW);
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, 0);

        // generate vertex buffer object
        gl.glGenBuffers(1, IntBuffer.wrap(vbo));
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, (getVertexCount()) * 4, getVertexBuffer(), GL3.GL_STATIC_DRAW);
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);

        // skyBoxTexture
        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glGenTextures(1, IntBuffer.wrap(tbo));
        gl.glBindTexture(GL3.GL_TEXTURE_CUBE_MAP, tbo[0]);
        for (int i = 0; i < skyBoxFaces.length; i++) {
            gl.glTexImage2D(GL3.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL3.GL_RGBA, images[i].cols(), images[i].rows(), 0, GL3.GL_BGRA, GL3.GL_UNSIGNED_BYTE, skyBoxFaces[i]);
        }
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);

        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_R, GL3.GL_CLAMP_TO_EDGE);
        gl.glBindTexture(GL3.GL_TEXTURE_CUBE_MAP, 0);
    }

    public void bindBuffer(GL3 gl) {

        gl.glActiveTexture(GL3.GL_TEXTURE0);//
        gl.glBindTexture(GL3.GL_TEXTURE_CUBE_MAP, tbo[0]);

        // index buffer
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);

        // vertexBuffer
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);

        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 12, 0);
    }

    @Override
    public void display(GL3 gl) {
        gl.glDisable(GL3.GL_DEPTH_TEST);
        setShaderUniform("skybox",0);


        for (Map.Entry<String, Object> entry : getShaderUniforms().entrySet()) {
            getShader().setUniform(gl, entry.getKey(), entry.getValue());
        }

        bindBuffer(gl);
        // draw the triangles
        gl.glDrawElements(GL3.GL_TRIANGLES, getIndexCount(), GL3.GL_UNSIGNED_INT, 0);
      //  unbindBuffer(gl);
        gl.glEnable(GL3.GL_DEPTH_TEST);
    }

    public void unbindBuffer(GL3 gl) {
        // texture image buffer
        gl.glBindTexture(GL3.GL_TEXTURE_CUBE_MAP, 0);
        // index buffer
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, 0);
        // vertex buffer
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);

        gl.glDisableVertexAttribArray(0);
    }

    public FloatBuffer getVertexBuffer() {
        FloatBuffer vertexBuffer = FloatBuffer.allocate(getVertexCount());
        for (Point3f aGlVertexData : glVertexData) {
            GLBuffers.putf(vertexBuffer, aGlVertexData.x);
            GLBuffers.putf(vertexBuffer, aGlVertexData.y);
            GLBuffers.putf(vertexBuffer, aGlVertexData.z);
        }

        vertexBuffer.rewind();
        return vertexBuffer;
    }
}
