package edu.cg.models.Locomotive;

import edu.cg.algebra.Vec;

import static org.lwjgl.opengl.GL11.glColor3fv;
import static org.lwjgl.opengl.GL11.glMaterialfv;
import static org.lwjgl.opengl.GL21.*;

/**
 * TODO(0): Read instructions below.
 * This class contains different material properties that can be used to color different surfaces of the locomotive
 * model. You need to use the static methods of the class inorder to define the color of each surface. Note, defining
 * the color using these static methods will be useful in the next assignment.
 * <p>
 * For example:
 * If you want to render the locomotive back chassis, then the right way to do this would be:
 * Materials.setMaterialChassis();
 * chassis.render();
 * Instead of:
 * glColor3d(r,g,b,1.0);
 * chassis.render();
 * <p>
 * <p>
 * Note: you would still want to call glColor3d(r,g,b,1.0);  in the definition of Materials.setMaterialChassis().
 */
public final class Materials {
    // TODO: We defined some colors that you can use in-order to reproduce our result.
    //       you can use them or define your own color. Note that the colors are stored in Vec objects.
    //       We added a new method called toGLColor() that can be used to return the color in a 4-element float array.
    //       This array can be passed to glColor4fv.
    private static final Vec BLACK = new Vec(0f);
    private static final Vec WHITE = new Vec(1f);
    private static final Vec DARK_RED = new Vec(0.5f, 0.f, 0f);
    private static final Vec DARK_GREY = new Vec(25f / 255f, 25f / 255f, 25f / 255f);
    private static final Vec GREY = new Vec(125f / 255f, 125f / 255f, 125f / 255f);
    private static final Vec LIGHT_GREY = new Vec(225f / 255f, 225f / 255f, 225f / 255f);
    private static final Vec DARK_BLUE = new Vec(0f, 0f, 25f / 255f);
    private static final Vec LIGHT_BROWN = new Vec(152f / 255f, 102f / 255f, 0f / 255f);
    private static final Vec DARK_BROWN = new Vec(204f / 255f, 51f / 255f, 0f / 255f);
    private static final Vec LIGHT_BRONZE = new Vec(229f / 255f, 189f / 255f, 150f / 255f);

    private Materials() {
    }

    public static void setMaterialRoof() {
        // TODO(7): Use this method to define the color of the locomotive roof.
        glColor3fv(BLACK.toGLColor());
        glMaterialfv(GL_FRONT, GL_AMBIENT, BLACK.mult(0.2).toGLColor()); // Ambient
        glMaterialfv(GL_FRONT, GL_DIFFUSE, BLACK.toGLColor()); // Diffuse
        glMaterialfv(GL_FRONT, GL_SPECULAR, BLACK.mult(0.7).toGLColor()); // Specular
        glMaterialfv(GL_FRONT, GL_SHININESS, BLACK.toGLColor()); // Shininess
    }

    public static void setMaterialChassis() {
        // TODO(4): Use this method to define the color of the locomotive chassis.
        glColor3fv(DARK_RED.toGLColor());
        glMaterialfv(GL_FRONT, GL_AMBIENT, DARK_RED.mult(0.2).toGLColor()); // Ambient
        glMaterialfv(GL_FRONT, GL_DIFFUSE, DARK_RED.toGLColor()); // Diffuse
        glMaterialfv(GL_FRONT, GL_SPECULAR, DARK_RED.mult(0.7).toGLColor()); // Specular
        glMaterialfv(GL_FRONT, GL_SHININESS, DARK_RED.toGLColor()); // Shininess
    }

    public static void setMaterialWheelTire() {
        glColor3fv(BLACK.toGLColor());
        glMaterialfv(GL_FRONT, GL_AMBIENT, BLACK.mult(0.2).toGLColor()); // Ambient
        glMaterialfv(GL_FRONT, GL_DIFFUSE, BLACK.toGLColor()); // Diffuse
        glMaterialfv(GL_FRONT, GL_SPECULAR, BLACK.mult(0.7).toGLColor()); // Specular
        glMaterialfv(GL_FRONT, GL_SHININESS, BLACK.toGLColor()); // Shininess
    }

    public static void setMaterialWheelRim() {
        // TODO(3): Use this method to define the color of the wheel rim.
        glColor3fv(GREY.toGLColor());
        glMaterialfv(GL_FRONT, GL_AMBIENT, GREY.mult(0.2).toGLColor()); // Ambient
        glMaterialfv(GL_FRONT, GL_DIFFUSE, GREY.toGLColor()); // Diffuse
        glMaterialfv(GL_FRONT, GL_SPECULAR, GREY.mult(0.7).toGLColor()); // Specular
        glMaterialfv(GL_FRONT, GL_SHININESS, GREY.toGLColor()); // Shininess
    }

    public static void setMaterialLightCase() {
        // TODO(4): See how we used this function to define the color of the car light case.
        glColor4fv(DARK_GREY.toGLColor());
        glMaterialfv(GL_FRONT, GL_AMBIENT, DARK_GREY.mult(0.2).toGLColor()); // Ambient
        glMaterialfv(GL_FRONT, GL_DIFFUSE, DARK_GREY.toGLColor()); // Diffuse
        glMaterialfv(GL_FRONT, GL_SPECULAR, DARK_GREY.mult(0.7).toGLColor()); // Specular
        glMaterialfv(GL_FRONT, GL_EMISSION, BLACK.toGLColor()); // Emission
        glMaterialfv(GL_FRONT, GL_SHININESS, DARK_GREY.toGLColor()); // Shininess
    }

    public static void setMaterialFrontLight() {
        // TODO(4): See how we used this function to define the color of the car light glass.
        float[] col = LIGHT_GREY.toGLColor();
        glColor4fv(col);
        glColor3fv(LIGHT_BROWN.toGLColor());
        glMaterialfv(GL_FRONT, GL_AMBIENT, LIGHT_GREY.mult(0.2).toGLColor()); // Ambient
        glMaterialfv(GL_FRONT, GL_DIFFUSE, LIGHT_GREY.toGLColor()); // Diffuse
        glMaterialfv(GL_FRONT, GL_SPECULAR, LIGHT_GREY.mult(0.7).toGLColor()); // Specular
        glMaterialfv(GL_FRONT, GL_EMISSION, BLACK.toGLColor()); // Emission
        glMaterialfv(GL_FRONT, GL_SHININESS, LIGHT_GREY.toGLColor()); // Shininess
    }

    public static void setMaterialWindow() {
        glColor3fv(LIGHT_BROWN.toGLColor());
        glMaterialfv(GL_FRONT, GL_AMBIENT, LIGHT_BROWN.mult(0.2).toGLColor()); // Ambient
        glMaterialfv(GL_FRONT, GL_DIFFUSE, LIGHT_BROWN.toGLColor()); // Diffuse
        glMaterialfv(GL_FRONT, GL_SPECULAR, LIGHT_BROWN.mult(0.7).toGLColor()); // Specular
        glMaterialfv(GL_FRONT, GL_SHININESS, LIGHT_BROWN.toGLColor()); // Shininess
    }

    public static void setMaterialDoor() {
        glColor3fv(DARK_BROWN.toGLColor());
        glMaterialfv(GL_FRONT, GL_AMBIENT, DARK_BROWN.mult(0.2).toGLColor()); // Ambient
        glMaterialfv(GL_FRONT, GL_DIFFUSE, DARK_BROWN.toGLColor()); // Diffuse
        glMaterialfv(GL_FRONT, GL_SPECULAR, DARK_BROWN.mult(0.7).toGLColor()); // Specular
        glMaterialfv(GL_FRONT, GL_SHININESS, DARK_BROWN.toGLColor()); // Shininess
    }

    public static void setMaterialStep() {
        glColor3fv(LIGHT_BRONZE.toGLColor());
        glMaterialfv(GL_FRONT, GL_AMBIENT, LIGHT_BRONZE.mult(0.2).toGLColor()); // Ambient
        glMaterialfv(GL_FRONT, GL_DIFFUSE, LIGHT_BRONZE.toGLColor()); // Diffuse
        glMaterialfv(GL_FRONT, GL_SPECULAR, LIGHT_BRONZE.mult(0.7).toGLColor()); // Specular
        glMaterialfv(GL_FRONT, GL_SHININESS, LIGHT_BRONZE.toGLColor()); // Shininess
    }

    public static void setMaterialExhaust() {
        glColor3fv(BLACK.toGLColor());
        glMaterialfv(GL_FRONT, GL_AMBIENT, BLACK.mult(0.2).toGLColor()); // Ambient
        glMaterialfv(GL_FRONT, GL_DIFFUSE, BLACK.toGLColor()); // Diffuse
        glMaterialfv(GL_FRONT, GL_SPECULAR, BLACK.mult(0.7).toGLColor()); // Specular
        glMaterialfv(GL_FRONT, GL_SHININESS, BLACK.toGLColor()); // Shininess
    }

}
