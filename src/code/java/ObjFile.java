/*
 *
 */
package code.java;

import java.io.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.vecmath.Point3f;

/**
 *
 * @author Robert
 */
public class ObjFile {

    private ArrayList<Point3f> vertexData;
    private ArrayList<Point3f> normalData;
    private ArrayList<Short> faceIndexData;
    private ArrayList<Short> normalIndexData;

    private static boolean normal = false;

    public ObjFile(String objFilename) throws FileNotFoundException, IOException {
        vertexData = new ArrayList<>();
        normalData = new ArrayList<>();
        faceIndexData = new ArrayList<>();
        normalIndexData = new ArrayList<>();
        //TODO normalize?
        parseObjFile(objFilename);
    }

    private void parseObjFile(String objFilename) throws FileNotFoundException, IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(objFilename)));

        String line;
        String[] values;
        while (true) {
            line = bufferedReader.readLine();
            if (null == line) {
                break;
            }
            if (line.length() == 0 || line.startsWith("#")) {
                continue;
            }
            if (line.startsWith("vn")) {
                normal = true;
                values = line.split("\\s+");
                normalData.add(new Point3f(Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3])));
            } else if (line.startsWith("v")) {
                values = line.split("\\s+");
                vertexData.add(new Point3f(Float.parseFloat(values[1]), Float.parseFloat(values[2]), Float.parseFloat(values[3])));
            } else if (line.startsWith("f")) {
                processFace(line);
            }
        }
        bufferedReader.close();
    }

    private void processFace(String line) {
        if (normal) {
            String[] values = line.split("\\s+");
            String[] val;
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

    public ArrayList<Point3f> getVertexData(){
        return vertexData;
    }

    public ArrayList<Point3f> getNormalData(){
        return normalData;
    }

    public ArrayList<Short> getFaceIndexData(){
        return faceIndexData;
    }

    public ArrayList<Short> getNormalIndexData(){
        return normalIndexData;
    }

    boolean normal() {
        return normal;
    }

}
