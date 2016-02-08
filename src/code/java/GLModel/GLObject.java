package code.java.GLModel;

import code.java.Shader;
import com.jogamp.opengl.GL3;

/**
 * Created by robert on 08.02.16.
 */
public interface GLObject {
    void display(GL3 gl);
    Shader getShader();
    void dispose(GL3 gl);
}
