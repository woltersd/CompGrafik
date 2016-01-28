package code.java;

import com.jogamp.opengl.util.GLBuffers;

import java.io.*;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.vecmath.Point3f;

/**
 *
 * @author Robert
 */
public class ObjFile {

    private final String VERTEX_NORMAL = "vn";
    private final String VERTEX = "v";
    private final String FACE = "f";

    private ArrayList<Point3f> vertexData;
    private ArrayList<Point3f> normalData;
    private ArrayList<Short> faceIndexData;
    private ArrayList<Short> normalIndexData;

    private LinkedList<Point3f> glVertexData;
    private LinkedList<Point3f> glNormalData;
    private LinkedList<Short> glIndexData;

    private static boolean normal = false;

    public ObjFile(String objFilename) throws FileNotFoundException, IOException {
        vertexData = new ArrayList();
        normalData = new ArrayList();
        faceIndexData = new ArrayList();
        normalIndexData = new ArrayList();

        parseObjFile(objFilename);

        glVertexData = new LinkedList();
        glNormalData = new LinkedList();
        glIndexData = new LinkedList();

        buildGLData();
    }

    private void parseObjFile(String objFilename) throws FileNotFoundException, IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(objFilename)));

        String line = null;
        while (true) {
            line = bufferedReader.readLine();
            if (null == line) {
                break;
            }
            if (line.length() == 0) {
                continue;
            }
            if (line.startsWith("#")) // comment
            {
                continue;
            } else if (line.startsWith(VERTEX_NORMAL)) {
                processVertexNormal(line);
            } else if (line.startsWith(VERTEX)) {
                processVertex(line);
            } else if (line.startsWith(FACE)) {
                processFace(line);
            }
        }
        bufferedReader.close();
    }

    private void processVertex(String line) {
        String[] values = line.split("\\s+");

        vertexData.add(new Point3f(Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3])));
    }

    private void processVertexNormal(String line) {
        normal = true;
        String[] values = line.split("\\s+");
        normalData.add(new Point3f(Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3])));
    }

    private void processFace(String line) {
        if (normal) {
            String[] values = line.split("\\s+"), val;
            for (int i = 1; i < values.length; i++) {
                val = values[i].split("//");
                faceIndexData.add((short) (Short.parseShort(val[0]) - 1));
                normalIndexData.add((short) (Short.parseShort(val[1]) - 1));
            }
        } else {
            String[] values = line.split("\\s+");
            for (int i = 1; i < values.length; i++) {
                faceIndexData.add((short) (Short.parseShort(values[i]) - 1));
            }
        }
    }

    private void buildGLData() {
        for (int i = 0; i < faceIndexData.size(); i++) {
            //TODO look again
            short y = (short) glVertexData.indexOf(vertexData.get(faceIndexData.get(i)));
            if (y == -1) {
                addGLVertex(i);
            } else {
                if (!normal()) {
                    glIndexData.add(y);
                }
                else if (glNormalData.get(y) == normalData.get(normalIndexData.get(i))) {
                    glIndexData.add(y);
                } else {
                    addGLVertex(i);
                }
            }

        }
    }

    private void addGLVertex(int i) {
        glVertexData.add(vertexData.get(faceIndexData.get(i)));

        if (normal) {
            glNormalData.add(normalData.get(normalIndexData.get(i)));
        }

        glIndexData.add((short) (glVertexData.size() - 1));
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
        FloatBuffer normalBuffer = FloatBuffer.allocate(getNomalCount());

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
        FloatBuffer comboBuffer = FloatBuffer.allocate(getVertexCount() + getNomalCount());

        for (Point3f each : glVertexData) {
            GLBuffers.putf(comboBuffer, each.x);
            GLBuffers.putf(comboBuffer, each.y);
            GLBuffers.putf(comboBuffer, each.z);
        }
        for (Point3f each : glNormalData) {
            GLBuffers.putf(comboBuffer, each.x);
            GLBuffers.putf(comboBuffer, each.y);
            GLBuffers.putf(comboBuffer, each.z);
        }
        comboBuffer.rewind();
        return comboBuffer;
    }

    public int getVertexCount() {
        return glVertexData.size() * 3;
    }

    public int getNomalCount() {
        return glNormalData.size() * 3;
    }

    public int getIndexCount() {
        return glIndexData.size();
    }

    boolean normal() {
        return normal;
    }

}