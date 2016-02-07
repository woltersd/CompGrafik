package code.java;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Matrix4;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;

/**
 *
 * @author Robert
 * @author Peter
 */

//TODO Cleanup
//TODO Change file path
public class Shader{

    protected ArrayList<Integer> vertexShaders = new ArrayList<>();
    protected ArrayList<Integer> fragmentShaders = new ArrayList<>();
    private int  progId;

    public Shader(GL3 gl) {
        progId = 0;
    }

    public Shader(GL3 gl, String shadersFilepath, String vertexShader, String fragmentShader) {

        this(gl);

        attachVertexShader(gl, shadersFilepath + vertexShader);
        attachFragmentShader(gl, shadersFilepath + fragmentShader);

        initializeProgram(gl, true);
    }

    public Shader(GL3 gl, String shadersFilepath, String[] vertexShaders, String[] fragmentShaders) {

        this(gl);

        for (String vertexShader : vertexShaders) {
            attachVertexShader(gl, shadersFilepath + vertexShader);
        }
        for (String fragmentShader : fragmentShaders) {
            attachFragmentShader(gl, shadersFilepath + fragmentShader);
        }

        initializeProgram(gl, true);
    }

    public void destroy(GL3 gl) {
        for (Integer vertexShader : vertexShaders) {
            gl.glDeleteShader(vertexShader);
        }
        for (Integer fragmentShader : fragmentShaders) {
            gl.glDeleteShader(fragmentShader);
        }
        if (progId != 0) {
            gl.glDeleteProgram(progId);
        }
    }

    public void bind(GL3 gl) {
        gl.glUseProgram(progId);
    }

    public void unbind(GL3 gl) {
        gl.glUseProgram(0);
    }

    public final void attachVertexShader(GL3 gl, String filename) {
        //
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(System.getProperty("user.dir").replaceAll("\\\\", "/") + filename);
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find the shader file " + filename);
        }
        if (inputStream == null) {
            System.err.println("Problem with InputStream");
            return;
        }
        BufferedReader input = null;
        String content = "";
        try {
            input = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            while ((line = input.readLine()) != null) {
                content += line + "\n";
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Unable to find the shader file " + filename);
        } catch (IOException iOException) {
            System.err.println("Problem reading the shader file " + filename);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException iOException) {
                System.out.println("Problem closing the BufferedReader, " + filename);
            }
        }

        int iID = gl.glCreateShader(GL3.GL_VERTEX_SHADER);

        String[] akProgramText = new String[1];
        // find and replace program name with "main"
        akProgramText[0] = content;

        int[] params = new int[]{0};

        int[] aiLength = new int[1];
        aiLength[0] = akProgramText[0].length();
        int iCount = 1;

        gl.glShaderSource(iID, iCount, akProgramText, aiLength, 0);

        gl.glCompileShader(iID);

        gl.glGetShaderiv(iID, GL3.GL_COMPILE_STATUS, params, 0);

        if (params[0] != 1) {
            System.err.println(filename);
            System.err.println("compile status: " + params[0]);
            gl.glGetShaderiv(iID, GL3.GL_INFO_LOG_LENGTH, params, 0);
            System.err.println("log length: " + params[0]);
            byte[] abInfoLog = new byte[params[0]];
            gl.glGetShaderInfoLog(iID, params[0], params, 0, abInfoLog, 0);
            System.err.println(new String(abInfoLog));
            System.exit(-1);
        }
        vertexShaders.add(iID);
    }

    public final void attachFragmentShader(GL3 gl, String filename) {
        InputStream inputStream = null;

        try {
            inputStream = new FileInputStream(System.getProperty("user.dir").replaceAll("\\\\", "/") + filename);
        } catch (FileNotFoundException e) {
            System.err.println("Unable to find the shader file " + filename);
        }
        if (inputStream == null) {
            System.err.println("Problem with InputStream");
            return;
        }

        String content = "";
        BufferedReader input = null;

        try {
            input = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = input.readLine()) != null) {
                content += line + "\n";
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.err.println("Unable to find the shader file " + filename);
        } catch (IOException iOException) {
            System.err.println("Problem reading the shader file " + filename);
        } finally {
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException iOException) {
                System.out.println("Problem closing the BufferedReader, " + filename);
            }
        }

        int iID = gl.glCreateShader(GL3.GL_FRAGMENT_SHADER);

        String[] akProgramText = new String[1];
        // find and replace program name with "main"
        akProgramText[0] = content;

        int[] params = new int[]{0};

        int[] aiLength = new int[1];
        aiLength[0] = akProgramText[0].length();
        int iCount = 1;

        gl.glShaderSource(iID, iCount, akProgramText, aiLength, 0);

        gl.glCompileShader(iID);

        gl.glGetShaderiv(iID, GL3.GL_COMPILE_STATUS, params, 0);

        if (params[0] != 1) {
            System.err.println(filename);
            System.err.println("compile status: " + params[0]);
            gl.glGetShaderiv(iID, GL3.GL_INFO_LOG_LENGTH, params, 0);
            System.err.println("log length: " + params[0]);
            byte[] abInfoLog = new byte[params[0]];
            gl.glGetShaderInfoLog(iID, params[0], params, 0, abInfoLog, 0);
            System.err.println(new String(abInfoLog));
            System.exit(-1);
        }
        fragmentShaders.add(iID);
    }

    public void setUniform(GL3 gl, String name, Object object) {
        if (object instanceof float[]) {
            setUniform(gl, name,(float[]) object );
        } else if (object instanceof Integer) {
            setUniform(gl, name, (int) object);
        } else if (object instanceof Matrix4) {
            setUniform(gl, name, (Matrix4) object);
        } else {
            System.err.println("Warning: Invalid uniform parameter \"" + name +"\"");
        }

    }
    //TODO cache uniform location
    public void setUniform(GL3 gl3, String name, float[] val) {
        int id = gl3.glGetUniformLocation(progId, name);
        if (id == -1) {
            System.err.println("Warning: Invalid uniform parameter \"" + name +"\"");
            return;
        }
        switch (val.length) {
            case 1:
                gl3.glUniform1fv(id, 1, val, 0);
                break;
            case 2:
                gl3.glUniform2fv(id, 1, val, 0);
                break;
            case 3:
                gl3.glUniform3fv(id, 1, val, 0);
                break;
            case 4:
                gl3.glUniform4fv(id, 1, val, 0);
                break;
        }
    }

    public void setUniform(GL3 gl, String name, int val) {
        int id = gl.glGetUniformLocation(progId, name);

        if (id == -1) {
            System.err.println("Warning: Invalid uniform parameter " + name);
            return;
        }
        gl.glUniform1i(id, val);
    }

    public void setUniform(GL3 gl, String name, Matrix4 matrix) {
        int id = gl.glGetUniformLocation(progId, name);

        if (id == -1) {
            System.err.println("Warning: Invalid uniform parameter " + name);
            return;
        }
        gl.glUniformMatrix4fv(id, 1, false, matrix.getMatrix(), 0);
    }

    public final void initializeProgram(GL3 gl, boolean cleanUp) {
        progId = gl.glCreateProgram();

        for (Integer vertexShader : vertexShaders) {
            gl.glAttachShader(progId, vertexShader);
        }

        for (Integer fragmentShader : fragmentShaders) {
            gl.glAttachShader(progId, fragmentShader);
        }

        gl.glLinkProgram(progId);

        int[] params = new int[]{0};
        gl.glGetProgramiv(progId, GL3.GL_LINK_STATUS, params, 0);

        if (params[0] != 1) {

            System.err.println("link status: " + params[0]);
            gl.glGetProgramiv(progId, GL3.GL_INFO_LOG_LENGTH, params, 0);
            System.err.println("log length: " + params[0]);

            byte[] abInfoLog = new byte[params[0]];
            gl.glGetProgramInfoLog(progId, params[0], params, 0, abInfoLog, 0);
            System.err.println(new String(abInfoLog));
        }

        gl.glValidateProgram(progId);

        if (cleanUp) {
            for (Integer vertexShader : vertexShaders) {
                gl.glDetachShader(progId, vertexShader);
                gl.glDeleteShader(vertexShader);
            }

            for (Integer fragmentShader : fragmentShaders) {
                gl.glDetachShader(progId, fragmentShader);
                gl.glDeleteShader(fragmentShader);
            }
        }
    }

    public Integer getProgramId() {
        return progId;
    }
}
