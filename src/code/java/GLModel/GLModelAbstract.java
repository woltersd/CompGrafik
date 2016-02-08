package code.java.GLModel;

import code.java.ObjFile;
import code.java.Shader;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.GLBuffers;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class GLModelAbstract implements GLObject {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    protected Mat image;
    protected ByteBuffer textureImage;

    protected ArrayList<Point3f> glVertexData;
    protected ArrayList<Point3f> glNormalData;
    protected ArrayList<Point2f> glTextureData;
    protected ArrayList<Short> glIndexData;

    private Shader shader;
    private Map<String, Object> shaderUniforms;

    private int vertexCount;
    private int normalCount;
    private int textureCount;
    private int indexCount;

    private Matrix4 modelMatrix;


    protected int[] vbo; //Vertex Buffer Object
    protected int[] ibo; //Index  Buffer Object
    protected int[] tbo; //Texture Buffer Object

    public GLModelAbstract() {
        glVertexData = new ArrayList<>();
        glNormalData = new ArrayList<>();
        glTextureData = new ArrayList<>();
        glIndexData = new ArrayList<>();
        vertexCount = 0;
        normalCount = 0;
        textureCount = 0;
        indexCount = 0;
        shaderUniforms = new HashMap<>();
        vbo = new int[1]; //Vertex Buffer Object
        ibo = new int[1]; //Index  Buffer Object
        tbo = new int[1]; //Texture Buffer Object

    }

    public abstract void display(GL3 gl);

    public void dispose(GL3 gl) {
        shader.destroy(gl);
    }

    public void setModelMatrixOffset(float offsetX, float offsetY, float offsetZ) {
        modelMatrix.translate(offsetX, offsetY,offsetZ);
    }

    public void setModelMatrixRotation(float rotationAngle, float vectorX,float vectorY, float vectorZ) {
        modelMatrix.rotate(rotationAngle, vectorX, vectorY, vectorZ);
    }

    public void setModelMatrixScale(float xScale, float yScale, float zScale) {
        modelMatrix.scale(xScale, yScale, zScale);
    }

    public ByteBuffer getTextureImage() {
        return textureImage;
    }

    public ShortBuffer getIndexBuffer() {
        ShortBuffer indexBuffer = ShortBuffer.allocate(glIndexData.size());

        for (short each : glIndexData) {
            GLBuffers.puts(indexBuffer, each);
        }
        indexBuffer.rewind();
        return indexBuffer;
   }

    public FloatBuffer getComboBuffer() {
        FloatBuffer comboBuffer = FloatBuffer.allocate(getVertexCount() + getNormalCount() + getTextureCount());
        for (int i = 0; i<glVertexData.size(); i++) {
            GLBuffers.putf(comboBuffer, glVertexData.get(i).x);
            GLBuffers.putf(comboBuffer, glVertexData.get(i).y);
            GLBuffers.putf(comboBuffer, glVertexData.get(i).z);
            GLBuffers.putf(comboBuffer, glNormalData.get(i).x);
            GLBuffers.putf(comboBuffer, glNormalData.get(i).y);
            GLBuffers.putf(comboBuffer, glNormalData.get(i).z);
            GLBuffers.putf(comboBuffer, glTextureData.get(i).x);
            GLBuffers.putf(comboBuffer, glTextureData.get(i).y);
        }

        comboBuffer.rewind();
        return comboBuffer;
    }

    public Mat getImage() {
        return image;
    }

    public void setImage(Mat image) {
        this.image = image;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setVertexCount(int vertexCount) {
        this.vertexCount = vertexCount;
    }

    public int getNormalCount() {
        return normalCount;
    }

    public void setNormalCount(int normalCount) {
        this.normalCount = normalCount;
    }

    public int getTextureCount() {
        return textureCount;
    }

    public void setTextureCount(int textureCount) {
        this.textureCount = textureCount;
    }

    public int getIndexCount() {
        return indexCount;
    }

    public void setIndexCount(int indexCount) {
        this.indexCount = indexCount;
    }

    public Matrix4 getModelMatrix() {
        return modelMatrix;
    }

    public void setModelMatrix(Matrix4 modelMatrix) {
        this.modelMatrix = modelMatrix;
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
    }

    public void setShaderUniform(String name, Object object)  {
        shaderUniforms.put(name, object);
    }

    public Map<String, Object> getShaderUniforms() {
        return shaderUniforms;
    }

    public int[] getVbo() {
        return vbo;
    }

    public void setVbo(int[] vbo) {
        this.vbo = vbo;
    }

    public int[] getIbo() {
        return ibo;
    }

    public void setIbo(int[] ibo) {
        this.ibo = ibo;
    }

    public int[] getTbo() {
        return tbo;
    }

    public void setTbo(int[] tbo) {
        this.tbo = tbo;
    }
}
