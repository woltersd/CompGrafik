package code.java;

import code.java.GLModel.GLObject;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.GLBuffers;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * @author Robert
 */
public class BackgroundSubtractor implements GLObject, KeyListener {

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    VideoCapture camera;
    private Mat firstFrame = null;
    private Mat frameDelta;

    private Mat image;
    private Mat thresh;
    private Mat low;
    private Mat gray;

    MatOfRect faceDetections;
    private Rect[] faces;

    private Point2f player1;
    private Point2f player2;

    private int[] tbo; //Texture Buffer Object


    public BackgroundSubtractor(GL3 gl, int cam, GLCanvas canvas){
        canvas.addKeyListener(this);

        camera = new VideoCapture(cam);

        frameDelta = new Mat();

        image = new Mat();
        thresh = new Mat();
        low = new Mat();
        gray = new Mat();

        faceDetections = new MatOfRect();

        player1 = new Point2f(0,0);
        player2 = new Point2f(0,0);

        camera.set(3,1280);
        camera.set(4,720);

        tbo = new int[2];

        camera.read(image);
        buildForeground();
        initializeBuffers(gl);
    }

    private void initializeBuffers(GL3 gl){
        // texture
        gl.glGenTextures(2, IntBuffer.wrap(tbo));

        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, tbo[0]);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
        gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA, image.cols(), image.rows(), 0, GL3.GL_BGR, GL3.GL_UNSIGNED_BYTE, getTextureImage(image));
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);

        gl.glActiveTexture(GL3.GL_TEXTURE1);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, tbo[1]);
        gl.glTexParameteri(GL3.GL_TEXTURE_2D, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);
        gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA, thresh.cols(), thresh.rows(), 0, GL3.GL_BGR, GL3.GL_UNSIGNED_BYTE, getTextureImage(thresh));
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
    }

    public void display(GL3 gl){
        camera.read(image);
        buildForeground();
        detectFaces();

        // update cam texture
        gl.glBindTexture(GL3.GL_TEXTURE_2D, tbo[0]);
        gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA, image.cols(), image.rows(), 0, GL3.GL_BGR, GL3.GL_UNSIGNED_BYTE, getTextureImage(image));
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);

        gl.glBindTexture(GL3.GL_TEXTURE_2D, tbo[1]);
        gl.glTexImage2D(GL3.GL_TEXTURE_2D, 0, GL3.GL_RGBA, thresh.cols(), thresh.rows(), 0, GL3.GL_BGR, GL3.GL_UNSIGNED_BYTE, getTextureImage(thresh));
        gl.glBindTexture(GL3.GL_TEXTURE_2D, 0);
    }

    public ByteBuffer getTextureImage(Mat image) {
        byte[] bytes = new byte[image.rows() * image.cols() * image.channels()];
        image.get(0, 0, bytes);
        return GLBuffers.newDirectByteBuffer(bytes);
    }

    private void buildForeground(){

        if(firstFrame == null){
            firstFrame = image.clone();
        }

        Core.absdiff(firstFrame, image, frameDelta);

        Imgproc.cvtColor(frameDelta, frameDelta, Imgproc.COLOR_BGR2GRAY);

        Imgproc.threshold(frameDelta, thresh, 11, 255, Imgproc.THRESH_BINARY);

        Imgproc.cvtColor(thresh, thresh, Imgproc.COLOR_GRAY2BGR);
    }

    //TODO return xy location
    private void detectFaces(){
        Imgproc.resize(image, low, new Size(640, 360));

        CascadeClassifier cascade = new CascadeClassifier(System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/haarcascade_frontalface_default.xml");

        Imgproc.cvtColor(low, gray, Imgproc.COLOR_BGR2GRAY);

        cascade.detectMultiScale(gray, faceDetections);

        faces = faceDetections.toArray();

        float faceX;
        float faceY;
        int half = low.cols()/2;
        int high = low.rows()/2;

        for (int i = 0; i<faces.length; i++) {
            faceX = (faces[i].x + (faces[i].width/2));
            faceY = (faces[i].y + (faces[i].height/2));

            if(faceX < half) player1 = new Point2f(faceX - (half/2), faceY - high);
            else {
                player2 = new Point2f(faceX - half - (half/2), faceY - high);
            }

            //Imgproc.rectangle(image, new Point(faces[i].x*2, faces[i].y*2), new Point(faces[i].x*2 + faces[i].width*2, faces[i].y*2 + faces[i].height*2), new Scalar(0, 255, 0));
        }
    }

    public Point2f getPlayerOffset(short player){
        float x;
        float y;

        if(player == 1){
            x = player1.x / (low.cols()/4f) * 4;
            y = player1.y / (low.rows()/2f) * 2;
            return new Point2f(x, y);
        }
        else {
            x = player2.x / (low.cols()/4f) * 4;
            y = player1.y / (low.rows()/2f) * 2;
            return new Point2f(x, y);
        }
    }

    public int[] getTbo(){
        return tbo;
    }

    @Override
    public Shader getShader() {
        return new Shader();
    }

    @Override
    public void dispose(GL3 gl){
        return;
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 'r':
                firstFrame = null;
                break;
        }
    }
}
