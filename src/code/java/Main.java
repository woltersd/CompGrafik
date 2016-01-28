package code.java;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

import javax.swing.*;
import java.awt.*;

/**
 * @author peter
 */

public class Main {

    static GraphicsDevice device = GraphicsEnvironment
            .getLocalGraphicsEnvironment().getScreenDevices()[0];

    public static void main(String[] args) {
        int width = 1280;
        int height = 720;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-height") && args.length > i) {
                i++;
                height = Integer.parseInt(args[i]);

            } else if (args[i].equals("-width")&& args.length > i) {
                i++;
                width = Integer.parseInt(args[i]);
            } else {
                System.out.println("Invalid commandline parameters!");
                System.exit(-1);
            }
        }

        // New FullScreenWindow
        final JFrame frame = new JFrame("Display Mode");
        frame.setSize(width, height);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setUndecorated(true);

        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps);
        GLEventListenerImpl gLEventListener = new GLEventListenerImpl();
        canvas.addGLEventListener(gLEventListener);
        frame.getContentPane().add(canvas);

        device.setFullScreenWindow(frame);
        frame.setVisible(true);


    }
}
