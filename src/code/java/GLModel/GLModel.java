package code.java.GLModel;

import code.java.ObjFile;
import code.java.Shader;
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
import java.util.Map;


import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * @author Robert
 * @author peter
 */
public class GLModel extends GLModelAbstract implements GLObject{

    protected ObjFile objFile;

    GLModel() {
        super();
    }

    public GLModel(GL3 gl, String objFile, Shader shader) {
        super();
        String objPath = System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/" + objFile;
        init(objPath);
        textureImage = loadTextureImage();
        initializeBuffers(gl, true);

        setShader(shader);
        setShaderUniform("modelMatrix", getModelMatrix());
        setShaderUniform("tex_1", 0);
    }

    protected void init(String objPath){
        try {
            this.objFile = new ObjFile(objPath);
        } catch (IOException io) {
            System.err.println("IOException " + objPath);
            return;
        }

        glVertexData = new ArrayList<>();
        glNormalData = new ArrayList<>();
        glTextureData = new ArrayList<>();
        glIndexData = new ArrayList<>();

        buildGLData();

        setVertexCount(glVertexData.size() * 3);
        setNormalCount(glNormalData.size() * 3);
        setTextureCount(glTextureData.size() * 2);
        setIndexCount(glIndexData.size());

        setModelMatrix(new Matrix4());
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
                    glIndexData.add(x);
                    found = true;
                }
            }
            if(!found){
                glVertexData.add(vertex);
                glNormalData.add(normal);
                glTextureData.add(texture);
                glIndexData.add(glVertexData.size() - 1);
            }
        }
    }

    protected void initializeBuffers(GL3 gl, boolean transparency) {
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
        if(transparency) gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA, image.cols(), image.rows(), 0, GL3.GL_BGRA, GL3.GL_UNSIGNED_BYTE, getTextureImage());
        else gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA, image.cols(), image.rows(), 0, GL3.GL_BGR, GL3.GL_UNSIGNED_BYTE, getTextureImage());
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
    }

    @Override
    public void display(GL3 gl) {
        bindBuffer(gl);

        for (Map.Entry<String, Object> entry : getShaderUniforms().entrySet()) {
            getShader().setUniform(gl, entry.getKey(), entry.getValue());
        }
        // draw the triangles
        gl.glDrawElements(GL3.GL_TRIANGLES, getIndexCount(), GL3.GL_UNSIGNED_INT, 0);

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
        gl.glVertexAttribPointer(2, 2, GL3.GL_FLOAT, false, 32, 24);
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

    private ByteBuffer loadTextureImage() {
        image = Imgcodecs.imread(System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/" + objFile.getObjName(), Imgcodecs.IMREAD_UNCHANGED);

        if(image.rows() == 0){
            image = Imgcodecs.imread(System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/replacement.png", Imgcodecs.IMREAD_UNCHANGED);
        }

        byte[] bytes = new byte[image.rows() * image.cols() * image.channels()];
        image.get(0, 0, bytes);
        return GLBuffers.newDirectByteBuffer(bytes);
    }
}