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
    final float framesPerSecond = 30f;
    final float secondsPerFrame = 1 / framesPerSecond;
    final float airDragPerFrame = 0.001f / framesPerSecond;
    final float gravity = 0.0981f;
    final float abatement = 0.75f;


    LinkedList<GLCollision> collisions;

    public GLBall(GL3 gl, String objFile, Shader shader, float radius, Point3f pos) {
        super(gl, objFile, shader);
        this.radius = radius;
        this.pos = pos;
        this.pos.x += 2.2;
        velocity = new Vector3f(0.4f,0f,0);
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
            if (velocity.x > 0 ) {
                velocity.x += -airDragPerFrame;
                if (velocity.x < 0) {
                    velocity.x = 0;
                }
            } else {
                velocity.x += airDragPerFrame;
                if (velocity.x > 0) {
                    velocity.x = 0;
                }
            }
        }
        if (velocity.z != 0) {
            if (velocity.z > 0 ) {
                velocity.z += -airDragPerFrame;
                if (velocity.z < 0) {
                    velocity.z = 0;
                }
            } else {
                velocity.z += airDragPerFrame;
                if (velocity.z > 0) {
                    velocity.z = 0;
                }
            }
        }
    }

    private void checkCollisions(){
        for(GLCollision each: collisions){
            if(each.collides(pos, radius, velocity)){
                velocity.scale(abatement);
                if (!(velocity.y > 0.001f) && !(velocity.y < -0.001f)) {
                    gravityForce = false;
                    velocity.y = 0;
                    pos.y = 0;
                }
                break;
            }
        }
    }

}
