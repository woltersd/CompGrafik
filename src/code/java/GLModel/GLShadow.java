package code.java.GLModel;

import code.java.Shader;
import com.jogamp.opengl.GL3;

import java.util.Map;

/**
 * @author Robert
 * @author peter
 */
public class GLShadow implements GLObject {

    private GLModel model;
    private Shader shader;
    private boolean activeModel;

    public GLShadow(GL3 gl, GLModel model, Shader shader){
        this.model = model;
        this.shader = shader;
        this.activeModel = true;
    }

    public void display(GL3 gl) {
        model.bindBuffer(gl);
        //TODO separate shader uniforms
        for (Map.Entry<String, Object> entry : model.getShaderUniforms().entrySet()) {
            shader.setUniform(gl, entry.getKey(), entry.getValue());
        }
        // draw the triangles
        gl.glDrawElements(GL3.GL_TRIANGLES, model.getIndexCount(), GL3.GL_UNSIGNED_INT, 0);

        model.unbindBuffer(gl);
    }

    public Shader getShader() {return shader;}

    public void dispose(GL3 gl) {
        shader.destroy(gl);
    }

    @Override
    public void activateModel() {
        activeModel = true;
    }

    @Override
    public void disableModel() {
        activeModel = false;
    }

    @Override
    public void toggleModel() {
        activeModel = !activeModel;
    }

    @Override
    public boolean isActive() {
        return activeModel;
    }
}
