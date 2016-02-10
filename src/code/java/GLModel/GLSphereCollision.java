package code.java.GLModel;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * @author Robert
 */
public class GLSphereCollision implements GLCollision {

    private Point3f pos;
    private float radius;


    public GLSphereCollision(Point3f pos, float radius){
        this.pos = pos;
        this.radius = radius;
    }

    public boolean collides(Point3f pos, float radius, Vector3f velocity) {
        return this.pos.distance(pos) < (radius * 2);
    }

    public void setPos(Point3f position){
        pos = position;
    }
}
