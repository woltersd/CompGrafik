package code.java.GLModel;

import code.java.Shader;
import com.jogamp.opengl.GL3;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import java.util.LinkedList;

/**
 * @author Robert
 * @author Peter
 */
public class GLBall extends GLModel implements GLObject {

    float radius;
    Point3f pos;
    Vector3f velocity;
    boolean gravityForce;
    final float secondsPerFrame = 1 / 30f;
    final float airDrag = 0.0005f;
    final float gravity = 0.0981f;
    final float abatement = 0.75f;


    LinkedList<GLCollision> collisions;

    public GLBall(GL3 gl, String objFile, Shader shader, float radius, Point3f pos) {
        super(gl, objFile, shader);
        this.radius = radius;
        this.pos = pos;
        velocity = new Vector3f(0,-0.05f,0);
        gravityForce = true;
        collisions = new LinkedList<>();
    }

    public void addCollision(GLCollision collision){
        collisions.add(collision);
    }

    public void display(GL3 gl){
        updateVelocity();
        pos.add(velocity);
        setModelMatrixOffset(velocity.x, velocity.y, velocity.z);

        checkCollisions();

        super.display(gl);
    }

    private void updateVelocity() {
        if (gravityForce) {
            velocity.y = -gravity * secondsPerFrame + velocity.y;
        }
        if (velocity.x != 0) {
            velocity.x += velocity.x > 0 ? -airDrag : airDrag;
        }
        if (velocity.z != 0) {
            velocity.z += velocity.z > 0 ? -airDrag : airDrag;
        }
    }

    private void checkCollisions(){
        for(GLCollision each: collisions){
            if(each.collides(pos, radius, velocity)){
                velocity.scale(abatement);
                if (!(velocity.y > 0.001f) && !(velocity.y < -0.001f)) {
                    gravityForce = false;
                    velocity.y = 0;
                }
                break;
            }
        }
    }

}
