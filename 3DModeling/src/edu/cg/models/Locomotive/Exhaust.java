package edu.cg.models.Locomotive;

import edu.cg.models.Box;
import edu.cg.models.IRenderable;
import edu.cg.util.glu.Cylinder;
import edu.cg.util.glu.Disk;

import static org.lwjgl.opengl.GL21.*;

public class Exhaust implements IRenderable {
    public void render() {
        glPushMatrix();
        // Exhaust rendering
        Materials.setMaterialExhaust();
        glScalef(0.01f,0.01f,0.07f);
        new Cylinder().draw((float) Specification.EXHAUST_RADIUS, (float) Specification.EXHAUST_RADIUS, (float) Specification.EXHAUST_DEPTH, 20, 1);
        glPopMatrix();
    }

    @Override
    public void init() {

    }
}
