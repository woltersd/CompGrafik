package code.java.GLModel;

import code.java.InputListener;
import code.java.InputWaiter;
import code.java.ObjFile;
import code.java.Shader;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4;
import com.jogamp.opengl.util.GLBuffers;
import org.opencv.imgcodecs.Imgcodecs;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Map;

/**
 * @author peter
 */
public class GLHelpDialog  extends GLModel implements InputWaiter{
    InputListener inputListener;


    public GLHelpDialog(GL3 gl, InputListener inputListener) {
        super();
        this.inputListener = inputListener;
        inputListener.addInputWaiter(EventType.Key_Typed, this);
        init();

        initializeBuffers(gl);
        setShader(new Shader(gl, "/src/code/glsl/","help_dialog_vertex.glsl", "help_dialog_fragment.glsl"));
        setShaderUniform("tex_1", 0);
    }

    private void init(){

        glVertexData = new ArrayList<>();
        glNormalData = new ArrayList<>();
        glTextureData = new ArrayList<>();
        glIndexData = new ArrayList<>();
        buildGLData();

        setVertexCount(glVertexData.size() * 3);
        setTextureCount(glTextureData.size() * 2);
        setIndexCount(glIndexData.size());
    }

    private void loadImageData(ObjFile objFile) {
        image = Imgcodecs.imread(System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/" + objFile.getObjName(), Imgcodecs.IMREAD_UNCHANGED);

        if(image.rows() == 0){
            image = Imgcodecs.imread(System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/replacement.png", Imgcodecs.IMREAD_UNCHANGED);
        }

        byte[] bytes = new byte[image.rows() * image.cols() * image.channels()];
        image.get(0, 0, bytes);
        textureImage = GLBuffers.newDirectByteBuffer(bytes);
    }

    private void buildGLData(){
        ObjFile objFile;
        try {
            objFile = new ObjFile(System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/" + "help.obj");
        } catch (IOException io) {
            System.err.println("IOException");
            return;
        }

        for (int i = 0; i < objFile.getFaceIndexData().size(); i++) {
            Point3f vertex = objFile.getVertexData().get(objFile.getFaceIndexData().get(i));

            Point2f texture;
            texture = objFile.getTextureData().get(objFile.getTextureIndexData().get(i));
            boolean newVertex = true;
            // Look if vertex+texture already saved
            for(int x = 0; x<glVertexData.size(); x++) {
                if ((glVertexData.get(x) == vertex) && (glTextureData.get(x) == texture)) {
                    glIndexData.add(x);
                    newVertex = false;
                    break;
                }
            }
            if (newVertex) {
                glVertexData.add(vertex);
                glTextureData.add(texture);
                glIndexData.add(glVertexData.size() - 1);
            }
        }
        loadImageData(objFile);
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
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, (getVertexCount() + getTextureCount()) * 4, getComboBuffer(), GL3.GL_STATIC_DRAW);
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);

        // texture
        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glGenTextures(1, IntBuffer.wrap(tbo));
        gl.glBindTexture(GL3.GL_TEXTURE_2D, tbo[0]);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
        gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA, image.cols(), image.rows(), 0, GL3.GL_BGRA, GL3.GL_UNSIGNED_BYTE, getTextureImage());
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
    }

    @Override
    public void display(GL3 gl) {
        bindBuffer(gl);
        // draw the triangles
        gl.glDrawElements(GL3.GL_TRIANGLES, getIndexCount(), GL3.GL_UNSIGNED_INT, 0);
        unbindBuffer(gl);
    }

    @Override
    public void inputEventHappened(InputEvent event) {
        if (event instanceof KeyEvent) {
            switch (((KeyEvent) event).getKeyChar()) {
                case 'h':
                    toggleModel();
                    break;
            }
        }
    }

    @Override
    public void bindBuffer(GL3 gl) {
        // texture image buffer
        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, tbo[0]);

        // index buffer
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);

        // vertex+normal+texture buffer
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, vbo[0]);

        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL3.GL_FLOAT, false, 20, 0);
        gl.glEnableVertexAttribArray(1);
        gl.glVertexAttribPointer(1, 2, GL3.GL_FLOAT, false, 20, 12);
    }

    @Override
    public void unbindBuffer(GL3 gl) {
        // texture image buffer
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
        // index buffer
        gl.glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, 0);
        // vertex+texture buffer
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, 0);

        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);
    }

    @Override
    public FloatBuffer getComboBuffer() {
        FloatBuffer comboBuffer = FloatBuffer.allocate(getVertexCount() + getNormalCount() + getTextureCount());
        for (int i = 0; i<glVertexData.size(); i++) {
            GLBuffers.putf(comboBuffer, glVertexData.get(i).x);
            GLBuffers.putf(comboBuffer, glVertexData.get(i).y);
            GLBuffers.putf(comboBuffer, glVertexData.get(i).z);
            GLBuffers.putf(comboBuffer, glTextureData.get(i).x);
            GLBuffers.putf(comboBuffer, glTextureData.get(i).y);
        }

        comboBuffer.rewind();
        return comboBuffer;
    }
}
