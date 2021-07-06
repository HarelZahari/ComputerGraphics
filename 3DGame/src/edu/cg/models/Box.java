package edu.cg.models;

import static org.lwjgl.opengl.GL21.*;
import edu.cg.util.glu.Texture;

/**
 * A simple 3D Box renderer. The box is centered at the origin in its local coordinate system.
 * The box can have different lengths along each of the main axes.
 */
public class Box implements IRenderable {
    private double rx, ry, rz;
    // TODO : If you wish to support textures this class must change.
    private Texture textureBox;
    private boolean useTexture = false;
    // TODO: To support textures, you need to add a texture object.

    /**
     * Constructs an object that renders a 3D box centered at the origin, with lengths rx, ry and rz.
     * @param rx the length along the x-axis.
     * @param ry the length along the y-axis.
     * @param rz the length along the z-axis.
     */
    public Box(double rx, double ry, double rz) {
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
    }

    /**
     * Constructs an object that renders a 3D Square-box centered at the origin with length r.
     * @param r the edge length of the box.
     */
    public Box(double r) {
        this.rx = r;
        this.ry = r;
        this.rz = r;
    }

    /**
     * Constructs an object that renders a 3D Square-box centered at the origin with length r.
     * @param r the edge length of the box.
     * @param useTexture a boolean value indicating whether to render the box with textures.
     */
    public Box(double r, boolean useTexture) {
        this.rx = r;
        this.ry = r;
        this.rz = r;
        this.useTexture = useTexture;
    }

    @Override
    public void render() {
        // TODO : Copy your code from HW5.
        //  TODO : In order to support shading, you must add calls to glNormal() to your code.
        //  In addition, if you wish to support textures, you need to enable and bind the texture, and add calls to glTex().

        if(useTexture){
            textureBox.bind();
        }

        glBegin(GL_QUADS);

        // TODO(1): draw the face that lies on the plane X=-rx/2
        // X=-rx/2:
        glNormal3d(-1, 0, 0);
        setBoxTexture(1f,1f);
        glVertex3d(-rx / 2, ry / 2, -rz / 2);
        setBoxTexture(1f,0f);
        glVertex3d(-rx / 2, -ry / 2, -rz / 2);
        setBoxTexture(0f,0f);
        glVertex3d(-rx / 2, -ry / 2, rz / 2);
        setBoxTexture(0f,1f);
        glVertex3d(-rx / 2, ry / 2, rz / 2);

        // X=rx/2:
        glNormal3d(1, 0, 0);
        setBoxTexture(0f,0f);
        glVertex3d(rx / 2, -ry / 2, -rz / 2);
        setBoxTexture(0f,1f);
        glVertex3d(rx / 2, ry / 2, -rz / 2);
        setBoxTexture(1f,1f);
        glVertex3d(rx / 2, ry / 2, rz / 2);
        setBoxTexture(1f,0f);
        glVertex3d(rx / 2, -ry / 2, rz / 2);

        // Y=-ry/2
        glNormal3d(0, -1, 0);
        setBoxTexture(1f,0f);
        glVertex3d(rx / 2, -ry / 2, rz / 2);
        setBoxTexture(0f,0f);
        glVertex3d(-rx / 2, -ry / 2, rz / 2);
        setBoxTexture(0f,1f);
        glVertex3d(-rx / 2, -ry / 2, -rz / 2);
        setBoxTexture(1f,1f);
        glVertex3d(rx / 2, -ry / 2, -rz / 2);

        // TODO(1): draw the face that lies on the plane Y=ry/2
        // Y=ry/2
        glNormal3d(0, 1, 0);
        setBoxTexture(1f,0f);
        glVertex3d(rx / 2, ry / 2, rz / 2);
        setBoxTexture(1f,1f);
        glVertex3d(rx / 2, ry / 2, -rz / 2);
        setBoxTexture(0f,1f);
        glVertex3d(-rx / 2, ry / 2, -rz / 2);
        setBoxTexture(0f,0f);
        glVertex3d(-rx / 2, ry / 2, rz / 2);

        // TODO(1): draw the face that lies on the plane Z=-rz/2
        // Z=-rz/2:
        glNormal3d(0, 0, -1);
        setBoxTexture(1f,1f);
        glVertex3d(rx / 2, ry / 2, -rz / 2);
        setBoxTexture(1f,0f);
        glVertex3d(rx / 2, -ry / 2, -rz / 2);
        setBoxTexture(0f,0f);
        glVertex3d(-rx / 2, -ry / 2, -rz / 2);
        setBoxTexture(0f,1f);
        glVertex3d(-rx / 2, ry / 2, -rz / 2);

        // Z=rz/2:
        glNormal3d(0, 0, 1);
        setBoxTexture(0f,0f);
        glVertex3d(-rx / 2, -ry / 2, rz / 2);
        setBoxTexture(0f,1f);
        glVertex3d(rx / 2, -ry / 2, rz / 2);
        setBoxTexture(1f,1f);
        glVertex3d(rx / 2, ry / 2, rz / 2);
        setBoxTexture(1f,0f);
        glVertex3d(-rx / 2, ry / 2, rz / 2);

        glEnd();
    }

    @Override
    public String toString() {
        return "Box";
    }

    private void setBoxTexture(float firstCoordinate, float secondCoordinate){
        if(useTexture) {
            glTexCoord2f(firstCoordinate, secondCoordinate);
        }
    }

    @Override
    public void init() {
        // TODO: To support textures, include texture initialization here.
        try {
            this.textureBox = Texture.loadTexture("Textures/WoodBoxTexture.png");
        }
        catch (Exception e) {
            System.err.print("Unable to read box texture : " + e.getMessage());
        }

    }
}
