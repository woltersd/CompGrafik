package code.java.GLModel;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * @author Robert
 */
public interface GLCollision {
    boolean collides(Point3f pos, float radius, Vector3f velocity);
}
