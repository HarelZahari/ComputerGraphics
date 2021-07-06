package edu.cg.models.Locomotive;

import edu.cg.models.IRenderable;
import edu.cg.util.glu.Cylinder;
import edu.cg.util.glu.Disk;

import static org.lwjgl.opengl.GL11.*;

/***
 * A 3D roof model renderer.
 * The roof is modeled using a cylinder bounded by disks that undergo a non-uniform scaling.
 */
public class Roof implements IRenderable {

    @Override
    public void render() {
        glPushMatrix();
        // TODO(7): Render the locomotive back body roof
        Materials.setMaterialRoof();
        glScalef((float) Specification.ROOF_WIDTH * (0.5f), (float) Specification.ROOF_HEIGHT * (0.5f), (float) Specification.BACK_BODY_DEPTH);
        new Cylinder().draw(1, 1, 1, 10, 1);
        glPopMatrix();
        drawRoofDisk();
        glTranslated(0, 0, Specification.ROOF_DEPTH);
        glRotated(180, 0, 1, 0);
        drawRoofDisk();
        glPopMatrix();
    }

    private void drawRoofDisk() {
        glPushMatrix();
        glRotated(180, 0, 1, 0);
        Materials.setMaterialRoof();
        glScalef((float) Specification.ROOF_WIDTH * (0.5f), (float) Specification.ROOF_HEIGHT * (0.5f), (float) Specification.BACK_BODY_DEPTH);
        new Disk().draw(0f, 1, 10, 1);
        glPopMatrix();
    }

    @Override
    public void init() {

    }
}
