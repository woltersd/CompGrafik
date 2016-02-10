package code.java.GLModel;

import javax.vecmath.Point3f;

/**
 * @author Robert
 */
public interface GLCollision {
    boolean collides(Point3f pos, float radius);
}
