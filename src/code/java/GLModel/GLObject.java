package code.java.GLModel;

import code.java.Shader;
import com.jogamp.opengl.GL3;

/**
 * @author  robert
 * @author peter
 */
public interface GLObject {
    void display(GL3 gl);
    Shader getShader();
    void dispose(GL3 gl);
    void activateModel();
    void disableModel();
    void toggleModel();
    boolean isActive();

}
