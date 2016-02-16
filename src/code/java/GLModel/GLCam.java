package code.java.GLModel;

import code.java.BackgroundSubtractor;
import code.java.InputListener;
import code.java.InputWaiter;
import code.java.Shader;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * @author Robert
 * @author peter
 */
public class GLCam extends GLModel implements InputWaiter{

    private VideoCapture camera;
    private BackgroundSubtractor subtractor;
    private Point3f initOffset;
    private Point2f playerOffset;
    private short player;
    private GLSphereCollision sphereCollision;

    public GLCam(GL3 gl, int cam, String objFile, Shader shader, InputListener inputListener) {
        super();
        String objPath = System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/" + objFile;
        init(objPath);
        camera = new VideoCapture(cam);
        image = new Mat();
        camera.set(3,1280);
        camera.set(4,720);

        getTextureImage();
        initializeBuffers(gl, false);

        setShader(shader);
        setShaderUniform("modelMatrix", getModelMatrix());
        setShaderUniform("tex_1", 0);

        inputListener.addInputWaiter(EventType.Key_Typed, this);
    }

    public GLCam(GL3 gl, BackgroundSubtractor subtractor, String objFile, Shader shader, float offset, GLSphereCollision sphereCollision, InputListener inputListener){
        super();
        this.subtractor = subtractor;
        setTbo(subtractor.getTbo());
        if(objFile.equals("player1.obj")){
            player = 1;
        }else {
            player = 2;
        }

        String objPath = System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/" + objFile;
        init(objPath);

        initializeBuffers(gl);

        initOffset = new Point3f(offset, 0, 0);
        playerOffset = new Point2f(0, 0);
        this.sphereCollision = sphereCollision;

        setShader(shader);
        setShaderUniform("modelMatrix", getModelMatrix());
        setShaderUniform("image", 0);
        setShaderUniform("thresh", 1);

        inputListener.addInputWaiter(EventType.Key_Typed, this);
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
    }

    public ByteBuffer getTextureImage() {
        camera.read(image);
        byte[] bytes = new byte[image.rows() * image.cols() * image.channels()];
        image.get(0, 0, bytes);
        return GLBuffers.newDirectByteBuffer(bytes);
    }

    @Override
    public void display(GL3 gl) {
        if(subtractor == null){
            // update cam texture
            gl.glBindTexture(GL3.GL_TEXTURE_2D, tbo[0]);
            gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA, image.cols(), image.rows(), 0, GL3.GL_BGR, GL3.GL_UNSIGNED_BYTE, getTextureImage());
            gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
        } else{
            playerOffset = subtractor.getPlayerOffset(player);
            Point3f position = new Point3f(initOffset.x + (playerOffset.x), initOffset.y ,initOffset.z);
            sphereCollision.setPos(position);
            getModelMatrix().loadIdentity();
            getModelMatrix().translate(position.x, position.y, position.z);
            //setShaderUniform("modelMatrix", getModelMatrix());

            // texture image buffer
            gl.glActiveTexture(GL3.GL_TEXTURE1);
            gl.glBindTexture(GL3.GL_TEXTURE_2D, tbo[1]);
        }
        super.display(gl);
    }

    public Point3f getPosition(){
        return new Point3f(initOffset.x + playerOffset.x, initOffset.y + playerOffset.y, initOffset.z);
    }

    @Override
    public void inputEventHappened(InputEvent event) {
        if (event instanceof KeyEvent) {
            switch (((KeyEvent) event).getKeyChar()) {
                case '1':
                    toggleModel();
                    break;
            }
        }
    }
}
