package code.java.GLModel;

import code.java.BackgroundSubtractor;
import code.java.Shader;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.GLBuffers;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * @author Robert
 */
public class GLCam extends GLModel implements GLObject{

    VideoCapture camera;
    BackgroundSubtractor subtractor = new BackgroundSubtractor();

    public GLCam(GL3 gl, int cam, Shader shader) {

        super();
        String objPath = System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/" + "plane_tex.obj";
        init(objPath);
        camera = new VideoCapture(cam);
        image = new Mat();
        camera.set(3,320);
        camera.set(4,320);

        getTextureImage();
        initializeBuffers(gl, false);

        setShader(shader);
        setShaderUniform("modelMatrix", getModelMatrix());
        setShaderUniform("tex_1", 0);
    }

    public ByteBuffer getTextureImage() {
        camera.read(image);
        image = subtractor.getForeground(image);
        byte[] bytes = new byte[image.rows() * image.cols() * image.channels()];
        image.get(0, 0, bytes);
        return GLBuffers.newDirectByteBuffer(bytes);
    }

    @Override
    public void display(GL3 gl) {
        // update cam texture
        gl.glBindTexture(GL3.GL_TEXTURE_2D, tbo[0]);
        gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA, image.cols(), image.rows(), 0, GL3.GL_BGR, GL3.GL_UNSIGNED_BYTE, getTextureImage());
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);

        super.display(gl);
    }

}
