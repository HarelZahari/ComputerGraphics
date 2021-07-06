package edu.cg.models.Locomotive;

import edu.cg.models.Box;
import edu.cg.models.IRenderable;

import static org.lwjgl.opengl.GL21.*;


/***
 * A 3D locomotive back body renderer. The back-body of the locomotive model is composed of a chassis, two back wheels,
 * , a roof, windows and a door.
 */
public class BackBody implements IRenderable {
    // The back body is composed of one box that represents the locomotive front body.
    private Box chassis = new Box(Specification.BACK_BODY_WIDTH, Specification.BACK_BODY_HEIGHT, Specification.BACK_BODY_DEPTH);
    // The back body is composed of two back wheels.
    private Wheel wheel = new Wheel();
    // The back body is composed of a roof that lies on-top of the locomotive chassis.
    private Roof roof = new Roof();
    private Window window = new Window();
    private Door door = new Door();
    private Step step= new Step();
    private CarLight carLight = new CarLight();
    private Exhaust exhaust = new Exhaust();

    // TODO (9): Define your window/door objects here. You are free to implement these models as you wish as long as you
    //           stick to the locomotive sketch.
    @Override
    public void render() {
        glPushMatrix();
        // TODO(8): render the back-body of the locomotive model. You need to combine the chassis, wheels and roof using
        //          affine transformations. In addition, you need to render the back-body windows and door. You can do
        //          that using simple QUADRATIC polygons (use GL_QUADS).

        // Chassis rendering
        Materials.setMaterialChassis();
        chassis.render();

        // Right wheel rendering
        glPushMatrix();
        glTranslated(Specification.BACK_BODY_WIDTH / 2, -(Specification.BACK_BODY_HEIGHT / 2), -(Specification.BACK_BODY_DEPTH * (0.25f)));
        wheel.render();
        glPopMatrix();

        // Left wheel rendering
        glPushMatrix();
        glTranslated(-(Specification.BACK_BODY_WIDTH / 2), -(Specification.BACK_BODY_HEIGHT / 2), -(Specification.BACK_BODY_DEPTH * (0.25f)));
        wheel.render();
        glPopMatrix();

        // Roof rendering
        glPushMatrix();
        glTranslated(0, (Specification.BACK_BODY_HEIGHT + Specification.ROOF_HEIGHT) * (0.45), -(Specification.ROOF_DEPTH / 2));
        roof.render();
        glPopMatrix();

        // Door rendering
        glPushMatrix();
        glTranslated(-(Specification.BACK_BODY_WIDTH / 2), 0, ((Specification.BACK_BODY_DEPTH/2) * 0.6));
        glScalef((float) Specification.EPS, 0.30f, 0.15f);
        glRotated(90, 0, 1, 0);
        door.render();
        glPopMatrix();

        // First window door side rendering
        glPushMatrix();
        glTranslated(-(Specification.BACK_BODY_WIDTH / 2), Specification.BACK_BODY_HEIGHT * 0.2, 0);
        glScalef((float) Specification.EPS, 0.15f, 0.15f);
        glRotated(90, 0, 1, 0);
        window.render();
        glPopMatrix();

        // Second window door side rendering
        glPushMatrix();
        glTranslated(-(Specification.BACK_BODY_WIDTH / 2), Specification.BACK_BODY_HEIGHT * 0.2, -(Specification.BACK_BODY_DEPTH/2) * 0.6);
        glScalef((float) Specification.EPS, 0.15f, 0.15f);
        glRotated(90, 0, 1, 0);
        window.render();
        glPopMatrix();

        // First window non door side rendering
        glPushMatrix();
        glTranslated(Specification.BACK_BODY_WIDTH / 2, Specification.BACK_BODY_HEIGHT * 0.2, ((Specification.BACK_BODY_DEPTH/2)* 0.6));
        glScalef((float) Specification.EPS, 0.15f, 0.15f);
        glRotated(90, 0, 1, 0);
        window.render();
        glPopMatrix();

        // Second window non door side rendering
        glPushMatrix();
        glTranslated(Specification.BACK_BODY_WIDTH / 2, Specification.BACK_BODY_HEIGHT * 0.2, 0);
        glScalef((float) Specification.EPS, 0.15f, 0.15f);
        glRotated(90, 0, 1, 0);
        window.render();
        glPopMatrix();

        // Third window non door side rendering
        glPushMatrix();
        glTranslated(Specification.BACK_BODY_WIDTH / 2, Specification.BACK_BODY_HEIGHT * 0.2, -((Specification.BACK_BODY_DEPTH/2) * 0.6));
        glScalef((float) Specification.EPS, 0.15f, 0.15f);
        glRotated(90, 0, 1, 0);
        window.render();
        glPopMatrix();

        // Back window
        glPushMatrix();
        glTranslated(0, Specification.BACK_BODY_HEIGHT * 0.2, -(Specification.BACK_BODY_DEPTH / 2));
        glScalef(0.3f, 0.15f, (float) Specification.EPS);
        window.render();
        glPopMatrix();

        // Front window
        glPushMatrix();
        glTranslated(0, Specification.BACK_BODY_HEIGHT * 0.2, Specification.BACK_BODY_DEPTH / 2);
        glScalef(0.3f, 0.15f, (float) Specification.EPS);
        window.render();
        glPopMatrix();

        // Step
        glPushMatrix();
        glTranslated(-(Specification.BACK_BODY_WIDTH/2), -((Specification.BACK_BODY_HEIGHT/2) * 0.80f), ((Specification.BACK_BODY_DEPTH/2) * 0.6));
        glScalef(0.1f, 0.01f, 0.2f);
        step.render();
        glPopMatrix();

        // Right backlight
        glPushMatrix();
        glTranslated((Specification.BACK_BODY_WIDTH/2)*0.7f, -((Specification.BACK_BODY_HEIGHT/2) * 0.65f), -(Specification.BACK_BODY_DEPTH / 2));
        glScalef(0.5f,0.5f,0.5f);
        glRotated(180, 0, 1, 0);
        carLight.render();
        glPopMatrix();

        // Left backlight
        glPushMatrix();
        glTranslated(-(Specification.BACK_BODY_WIDTH/2)*0.7f, -((Specification.BACK_BODY_HEIGHT/2) * 0.65f), -(Specification.BACK_BODY_DEPTH / 2));
        glScalef(0.5f,0.5f,0.5f);
        glRotated(180, 0, 1, 0);
        carLight.render();
        glPopMatrix();

        // Exhaust
        glPushMatrix();
        glTranslated(-(Specification.BACK_BODY_WIDTH/2)*0.85f, -((Specification.BACK_BODY_HEIGHT/2) * 0.88f), -((Specification.BACK_BODY_DEPTH / 2)*1.1));
        exhaust.render();
        glPopMatrix();

        glPopMatrix();
    }

    @Override
    public void init() {

    }
}
