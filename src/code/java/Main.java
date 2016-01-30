package code.java;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
        final JFrame frame = new JFrame("ComputerGrafik");

        GLProfile glp = GLProfile.getGL2GL3();
        GLCapabilities caps = new GLCapabilities(glp);
        final GLCanvas canvas = new GLCanvas(caps);
        GLEventListenerImpl gLEventListener = new GLEventListenerImpl();
        canvas.setSize(width, height);
        canvas.addGLEventListener(gLEventListener);
        frame.getContentPane().add(canvas);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing( WindowEvent windowevent ) {
                frame.remove(canvas);
                frame.dispose();
                System.exit( 0 );
            }
        });
        // makes problems
       // device.setFullScreenWindow(frame);
        frame.setSize(width, height);
        frame.setVisible(true);



    }
}
