package code.java.GLModel;

import code.java.Shader;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.nio.ByteBuffer;

/**
 * @author Robert
 */
public class GLCam extends GLModel {

    VideoCapture camera;

    public GLCam(GL3 gl, int cam){
        super("plane_tex.obj");

        camera = new VideoCapture(cam);
        image = new Mat();
        camera.set(3,1280);
        camera.set(4,720);

        getTextureImage();
        initializeBuffers(gl);
    }

    public ByteBuffer getTextureImage() {
        camera.read(image);
        byte[] bytes = new byte[image.rows() * image.cols() * image.channels()];
        image.get(0, 0, bytes);
        return GLBuffers.newDirectByteBuffer(bytes);
    }

    public void display(GL3 gl, Shader shader) {
        // update cam texture
        gl.glBindTexture(GL3.GL_TEXTURE_2D, tbo[0]);
        gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGB, image.cols(), image.rows(), 0, GL3.GL_BGR, GL3.GL_UNSIGNED_BYTE, getTextureImage());
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);

        super.display(gl, shader);
    }

}
