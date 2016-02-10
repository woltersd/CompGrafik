package code.java.GLModel;

import javax.vecmath.Point3f;

/**
 * @author Robert
 */
public class GLPlaneCollision implements GLCollision {

    private Point3f[] line;
    private String axis;

    public GLPlaneCollision(Point3f[] line, String axis){
        this.line = line;
        this.axis = axis;
    }

    public boolean collides(Point3f pos, float radius) {
        switch (axis){
            case "x":
                if(pos.x > line[0].x && pos.x < line[1].x) break;
                if(pos.distance(new Point3f(pos.x, line[0].y, 0)) <= radius){
                    return true;
                }
                break;
            case "y":
                if(pos.y > line[0].y && pos.y < line[1].y) break;
                if(pos.distance(new Point3f(line[0].x, pos.y, 0)) <= radius){
                    return true;
                }
                break;
        }
        return false;
    }
}
