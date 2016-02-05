package code.java;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.GLBuffers;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * @author Robert
 * @author peter
 */
public class GLModel {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    protected Mat image;
    protected ByteBuffer textureImage;

    private ObjFile objFile;

    protected ArrayList<Point3f> glVertexData;
    protected ArrayList<Point3f> glNormalData;
    protected ArrayList<Point2f> glTextureData;
    protected ArrayList<Short> glIndexData;

    private int vertexCount = 0;
    private int normalCount = 0;
    private int textureCount = 0;
    private int indexCount = 0;

    private Matrix4 modelMatrix;

    protected int[] vbo = new int[1]; //Vertex Buffer Object
    protected int[] ibo = new int[1]; //Index  Buffer Object
    protected int[] tbo = new int[1]; //Texture Buffer Object

    GLModel(String objFile) {
        String objPath = System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/" + objFile;
        init(objPath);
    }

    GLModel(GL3 gl, String objFile) {
        String objPath = System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/" + objFile;
        init(objPath);

        textureImage = loadTextureImage();

        initializeBuffers(gl);
    }

    private void init(String objPath){
        //TODO better error handling
        try {
            this.objFile = new ObjFile(objPath);
        } catch (IOException io) {
            System.err.println("IOException " + objPath);
        }

        glVertexData = new ArrayList<>();
        glNormalData = new ArrayList<>();
        glTextureData = new ArrayList<>();
        glIndexData = new ArrayList<>();

        buildGLData();

        vertexCount = glVertexData.size() * 3;
        normalCount = glNormalData.size() * 3;
        textureCount = glTextureData.size() * 2;
        indexCount = glIndexData.size();

        modelMatrix = new Matrix4();
    }

    private void buildGLData(){
        for (int i = 0; i < objFile.getFaceIndexData().size(); i++) {
            boolean found = false;
            Point3f vertex = objFile.getVertexData().get(objFile.getFaceIndexData().get(i));

            Point3f normal;
            if(objFile.normal()){
                normal = objFile.getNormalData().get(objFile.getNormalIndexData().get(i));
            } else {
                //TODO calculate normal
                normal = objFile.getVertexData().get(objFile.getFaceIndexData().get(i));
            }

            Point2f texture;
            if(objFile.texture()){
                texture = objFile.getTextureData().get(objFile.getTextureIndexData().get(i));
            }
            else {
                // generate texture coordinates
                if(i%3 == 0) texture = new Point2f(0.0f, 0.0f);
                else if(i%3 == 1) texture = new Point2f(1.0f, 1.0f);
                else texture = new Point2f(1.0f, 0.0f);
            }
            // Look if vertex+normal+texture already saved
            for(int x = 0; x<glVertexData.size(); x++) {
                if ((glVertexData.get(x) == vertex) && (glNormalData.get(x) == normal) && (glTextureData.get(x) == texture)) {
                    glIndexData.add((short) x);
                    found = true;
                }
            }
            if(!found){
                glVertexData.add(vertex);
                glNormalData.add(normal);
                glTextureData.add(texture);
                glIndexData.add((short) (glVertexData.size() - 1));
            }
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
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, (getNormalCount() + getVertexCount() + getTextureCount()) * 4, getComboBuffer(), GL3.GL_STATIC_DRAW);
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);

        // texture
        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glGenTextures(1, IntBuffer.wrap(tbo));
        gl.glBindTexture(GL3.GL_TEXTURE_2D, tbo[0]);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
        gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGB, image.cols(), image.rows(), 0, GL3.GL_BGR, GL3.GL_UNSIGNED_BYTE, getTextureImage());
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
    }

    public void display(GL3 gl, Shader shader) {
        bindBuffer(gl);

        // view
        shader.setUniform(gl, "modelMatrix", getModelMatrix());

        shader.setUniform(gl, "tex_1", 0);

        // draw the triangles
        gl.glDrawElements(GL3.GL_TRIANGLES, getIndexCount(), GL3.GL_UNSIGNED_SHORT, 0);

        unbindBuffer(gl);
    }

    public void bindBuffer(GL3 gl) {
        // texture image buffer
        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, tbo[0]);

        // index buffer
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);

        // vertex+normal+texture buffer
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);

        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 32, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 3, GL3.GL_FLOAT, false, 32, 12);
        gl.glEnableVertexAttribArray(2);
        gl.glVertexAttribPointer(2, 3, GL3.GL_FLOAT, false, 32, 24);
    }

    public void unbindBuffer(GL3 gl) {
        // texture image buffer
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
        // index buffer
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, 0);
        // vertex+normal+texture buffer
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);

        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);
        gl.glDisableVertexAttribArray(2);
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

    public ByteBuffer loadTextureImage() {
        image = Imgcodecs.imread(System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/" + objFile.getObjName());

        if(image.rows() == 0){
            image = Imgcodecs.imread(System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/replacement.png");
        }

        System.out.println(image);

        byte[] bytes = new byte[image.rows() * image.cols() * image.channels()];
        image.get(0, 0, bytes);
        return GLBuffers.newDirectByteBuffer(bytes);
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

    public int getVertexCount() {
        return vertexCount;
    }

    public int getNormalCount(){
        return normalCount;
    }

    public int getTextureCount(){
        return textureCount;
    }

    public int getIndexCount() {
        return indexCount;
    }

    public Matrix4 getModelMatrix() {
        return modelMatrix;
    }
}