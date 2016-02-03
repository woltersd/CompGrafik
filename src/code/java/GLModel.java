package code.java;

import com.jogamp.opengl.util.GLBuffers;

import javax.vecmath.Point3f;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

/**
 * @author Robert
 */
public class GLModel {
    private ArrayList<Point3f> glVertexData;
    private ArrayList<Point3f> glNormalData;
    private ArrayList<Short> glIndexData;

    private int vertexCount = 0;
    private int normalCount = 0;
    private int indexCount = 0;

    private ObjFile objFile;

    private boolean normal = false;

    GLModel(String objPath) {
        //TODO better error handling
        try {
            this.objFile = new ObjFile(objPath);
        } catch (FileNotFoundException fnf) {
            System.err.println("FileNotFoundException " + objPath);
        } catch (IOException io) {
            System.err.println("IOException " + objPath);
        }

        glVertexData = new ArrayList<>();
        glNormalData = new ArrayList<>();
        glIndexData = new ArrayList<>();
        normal = objFile.normal();

        buildGLData();

        vertexCount = glVertexData.size() * 3;
        normalCount = glNormalData.size() * 3;
        indexCount = glIndexData.size();
    }

    private void buildGLData() {
        for (int i = 0; i < objFile.getFaceIndexData().size(); i++) {
            boolean found = false;
            Point3f vertex = objFile.getVertexData().get(objFile.getFaceIndexData().get(i));
            Point3f normal = new Point3f();
            if(normal()){
                normal = objFile.getNormalData().get(objFile.getNormalIndexData().get(i));
            }
            // Look if vertex+normal already saved
            for(int x = 0; x<glVertexData.size(); x++){
                if(glVertexData.get(x) == vertex){
                    if(!normal()){
                        found = true;
                        glIndexData.add((short) x);
                        break;
                    }
                    else if(glNormalData.get(x) == normal){
                        found = true;
                        glIndexData.add((short) x);
                        break;
                    }
                }
            }
            if(!found){
                glVertexData.add(vertex);
                if (normal()) {
                    glNormalData.add(normal);
                }
                glIndexData.add((short) (glVertexData.size() - 1));
            }
        }
    }

    public FloatBuffer getVertexBuffer() {
        FloatBuffer vertexBuffer = FloatBuffer.allocate(getVertexCount());

        for (Point3f each : glVertexData) {
            GLBuffers.putf(vertexBuffer, each.x);
            GLBuffers.putf(vertexBuffer, each.y);
            GLBuffers.putf(vertexBuffer, each.z);
        }
        vertexBuffer.rewind();
        return vertexBuffer;
    }

    public FloatBuffer getNormalBuffer() {
        FloatBuffer normalBuffer = FloatBuffer.allocate(getNormalCount());

        for (Point3f each : glNormalData) {
            GLBuffers.putf(normalBuffer, each.x);
            GLBuffers.putf(normalBuffer, each.y);
            GLBuffers.putf(normalBuffer, each.z);
        }
        normalBuffer.rewind();
        return normalBuffer;
    }

    public ShortBuffer getIndexBuffer() {
        ShortBuffer indexBuffer = ShortBuffer.allocate(glIndexData.size());

        for (short each : glIndexData) {
            GLBuffers.puts(indexBuffer, each);
        }
        indexBuffer.rewind();
        return indexBuffer;
    }

    public FloatBuffer getComboBuffer() {
        FloatBuffer comboBuffer = FloatBuffer.allocate(getVertexCount() + getNormalCount());

        for (int i = 0; i<glVertexData.size(); i++) {
            GLBuffers.putf(comboBuffer, glVertexData.get(i).x);
            GLBuffers.putf(comboBuffer, glVertexData.get(i).y);
            GLBuffers.putf(comboBuffer, glVertexData.get(i).z);
            GLBuffers.putf(comboBuffer, glNormalData.get(i).x);
            GLBuffers.putf(comboBuffer, glNormalData.get(i).y);
            GLBuffers.putf(comboBuffer, glNormalData.get(i).z);
        }

        comboBuffer.rewind();
        return comboBuffer;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public int getNormalCount(){
        return normalCount;
    }

    public int getIndexCount() {
        return indexCount;
    }

    public boolean bufferContains(float item, FloatBuffer buffer) {
        for (int i = 0; i < buffer.position(); i++) {
            if (buffer.get(i) == item) {
                return true;
            }
        }
        return false;
    }

    public boolean normal(){
        return normal;
    }

}