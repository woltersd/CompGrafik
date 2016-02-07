package code.java;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

/**
 * @author Robert
 */
//TODO :)
public class BackgroundSubtractor {

    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private Mat firstFrame = null;
    private Mat frameDelta = new Mat();
    private Mat thresh = new Mat();

    public BackgroundSubtractor(){

    }

    public Mat getForeground(Mat frame){
        if(firstFrame == null){
            firstFrame = frame;
        }

        CascadeClassifier cascade = new CascadeClassifier(System.getProperty("user.dir").replaceAll("\\\\", "/") + "/src/res/haarcascade_frontalface_default.xml");

        Mat gray = new Mat();

        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_BGR2GRAY);

        MatOfRect faceDetections = new MatOfRect();

        cascade.detectMultiScale(gray, faceDetections);

        Rect[] faces = faceDetections.toArray();

        for (int i = 0; i<faces.length && i<2; i++) {
            Imgproc.rectangle(frame, new Point(faces[i].x, faces[i].y), new Point(faces[i].x + faces[i].width, faces[i].y + faces[i].height), new Scalar(0, 255, 0));
        }



        /*


        Core.absdiff(firstFrame, frame, frameDelta);

        Imgproc.cvtColor(frameDelta, frameDelta, Imgproc.COLOR_BGR2GRAY);

        Imgproc.threshold(frameDelta, thresh, 11, 255, Imgproc.THRESH_BINARY);

        Imgproc.cvtColor(thresh, thresh, Imgproc.COLOR_GRAY2BGR);

        */
        return frame;
    }


}
