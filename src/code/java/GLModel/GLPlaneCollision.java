package code.java.GLModel;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

/**
 * @author Robert
 * @author Peter
 */
public class GLPlaneCollision implements GLCollision {

    private Point3f[] line;
    private String axis;

    public GLPlaneCollision(Point3f[] line, String axis){
        this.line = line;
        this.axis = axis;
    }

    public boolean collides(Point3f pos, float radius, Vector3f velocity) {
        switch (axis){
            case "x":
                if(pos.x > line[0].x && pos.x < line[1].x) break;
                if(pos.distance(new Point3f(pos.x, line[0].y, 0)) <= radius){
                    if (velocity.y > 0) {
                        pos.y = line[0].y - radius;
                    } else {
                        pos.y = line[0].y + radius;
                    }
                    velocity.y *= -1;
                    return true;

                }
                break;
            case "y":
                if(pos.y > line[0].y && pos.y < line[1].y) break;
                if(pos.distance(new Point3f(line[0].x, pos.y, 0)) <= radius){
                    if (velocity.x > 0) {
                        pos.x = line[0].x - radius;
                    } else {
                        pos.x = line[0].x + radius;
                    }
                    velocity.x *= -1;
                    return true;
                }
                break;
        }
        return false;
    }
}
