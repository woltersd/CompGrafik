package code.java.GLModel;

import code.java.Shader;
import com.jogamp.opengl.GL3;

import javax.vecmath.Point3f;
import java.util.LinkedList;

/**
 * @author Robert
 */
public class GLBall extends GLModel implements GLObject {

    float radius;
    Point3f pos;
    Point3f velocity;

    LinkedList<GLCollision> collisions;

    public GLBall(GL3 gl, String objFile, Shader shader, float radius, Point3f pos) {
        super(gl, objFile, shader);
        this.radius = radius;
        this.pos = pos;
        velocity = new Point3f(0,-0.05f,0);
        collisions = new LinkedList<>();
    }

    public void addCollision(GLCollision collision){
        collisions.add(collision);
    }

    public void display(GL3 gl){
        pos.add(velocity);
        setModelMatrixOffset(velocity.x, velocity.y, velocity.z);

        checkCollisions();

        super.display(gl);
    }

    private void checkCollisions(){
        for(GLCollision each: collisions){
            if(each.collides(pos, radius)){
                velocity.negate();
                break;
            }
        }
    }

}
